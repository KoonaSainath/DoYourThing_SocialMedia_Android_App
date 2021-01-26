package firebase.kunasainath.doyourthing.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.adapters.ChatRoomAdapter;
import firebase.kunasainath.doyourthing.model_classes.Message;
import firebase.kunasainath.doyourthing.model_classes.User;
import firebase.kunasainath.doyourthing.notification.Client;
import firebase.kunasainath.doyourthing.notification.Data;
import firebase.kunasainath.doyourthing.notification.MyResponse;
import firebase.kunasainath.doyourthing.notification.Sender;
import firebase.kunasainath.doyourthing.notification.Token;
import firebase.kunasainath.doyourthing.viewpager_fragments.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomActivity extends AppCompatActivity {

    private String receiverId, senderId;
    private EditText edtMessage;
    private Button btnSend;

    private ChatRoomAdapter mChatRoomAdapter;
    private ArrayList<Message> messages;
    private RecyclerView recyclerChat;

    private ProgressBar progressChat;

    private ValueEventListener mValueEventListener;
    private DatabaseReference mReference;


    //VARIABLES FOR CHAT NOTIFICATION
    APIService apiService;
    boolean notify = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        edtMessage = findViewById(R.id.edt_message);
        btnSend = findViewById(R.id.btn_send_message);
        recyclerChat = findViewById(R.id.recycler_chat);
        progressChat = findViewById(R.id.progress_chatroom);


        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        showAllPreviousMessages();

        User user = (User) getIntent().getSerializableExtra("User");
        receiverId = user.getId();
        senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(receiverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        setTitle(snapshot.child("Username").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(edtMessage.getText().toString().length() == 0){
                    Snackbar.make(findViewById(android.R.id.content), "Cannot send an empty message", Snackbar.LENGTH_LONG).show();
                    return ;
                }

                notify = true;

                sendMessage();

            }
        });

        seenMessage(receiverId);
    }

    private void showAllPreviousMessages(){
        messages = new ArrayList<Message>();

        FirebaseDatabase.getInstance().getReference()
                .child("Chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot data : snapshot.getChildren()){
                            String sender, receiver, message, dateTime;
                            boolean seenOrNot;
                            sender = data.child("Sender").getValue().toString();
                            receiver = data.child("Receiver").getValue().toString();
                            message = data.child("Message").getValue().toString();
                            seenOrNot = Boolean.parseBoolean(data.child("Seen").getValue().toString());
                            dateTime = data.child("Date and time").getValue().toString();

                            if( (sender.equals(senderId) && receiver.equals(receiverId)) || (sender.equals(receiverId) && receiver.equals(senderId)) ){
                                Message msg = new Message(sender, receiver, message, dateTime, seenOrNot);
                                messages.add(msg);
                            }
                        }

                        mChatRoomAdapter = new ChatRoomAdapter(messages, ChatRoomActivity.this);
                        recyclerChat.setAdapter(mChatRoomAdapter);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatRoomActivity.this);
                        layoutManager.setStackFromEnd(true);

                        recyclerChat.scrollToPosition(0);

                        recyclerChat.setLayoutManager(layoutManager);

                        progressChat.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void sendMessage(){


        String msgToSend = edtMessage.getText().toString();
        edtMessage.setText("");

        String dateTime;

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date currentDate = new Date();

        dateTime = dateFormat.format(currentDate);

        FirebaseDatabase.getInstance().getReference("Users").child(senderId).child("Friends").child(receiverId)
                .child("LastMessageDateTime").setValue(dateTime);

        FirebaseDatabase.getInstance().getReference("Users").child(receiverId).child("Friends").child(senderId)
                .child("LastMessageDateTime").setValue(dateTime);

        HashMap<String, Object> messageData = new HashMap<>();

        messageData.put("Sender", senderId);
        messageData.put("Receiver", receiverId);
        messageData.put("Message", msgToSend);
        messageData.put("Seen", false);
        messageData.put("Date and time", dateTime);

        FirebaseDatabase.getInstance().getReference()
                .child("Chats")
                .push()
                .setValue(messageData);

        Message message = new Message(senderId, receiverId, msgToSend, dateTime, false);
        messages.add(message);
        ChatRoomAdapter chatRoomAdapter = (ChatRoomAdapter) recyclerChat.getAdapter();
        chatRoomAdapter.addNewMessage(message);
        chatRoomAdapter.notifyItemInserted(chatRoomAdapter.getMessageArrayList().size()-1);

        recyclerChat.scrollToPosition(0);



        //CODE FOR CHAT NOTIFICATION
        final String msg = msgToSend;
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(notify) {
                            String userName = snapshot.child("Username").getValue().toString();
                            sendNotification(receiverId, userName, msg);
                            notify = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


    //CHAT NOTIFICATION METHOD
    public void sendNotification(String receiverId, String userName, String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiverId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);

                    //LAST PARAMETER MAY BE receiverId
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), R.mipmap.ic_launcher, userName + ": " + message, "New message", receiverId);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            Toast.makeText(ChatRoomActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void seenMessage(String userId){
        mReference = FirebaseDatabase.getInstance().getReference("Chats");

        mValueEventListener = mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    String senderId, receiverId, message, dateTime;
                    boolean isSeen;
                    senderId = data.child("Sender").getValue().toString();
                    receiverId = data.child("Receiver").getValue().toString();
                    message = data.child("Message").getValue().toString();
                    dateTime = data.child("Date and time").getValue().toString();
                    isSeen = Boolean.parseBoolean(data.child("Seen").getValue().toString());

//                    Log.i("Receiver id", receiverId);
//                    Log.i("Current user", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    Log.i("User id", userId);
//                    Log.i("Sender id", senderId+"\n\n");

                    if(receiverId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && userId.equals(senderId)){
                        //Log.i("IMPORTANT", message);

                        data.getRef().child("Seen").setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.hold_animation, R.anim.activity_transition_animation);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mReference.removeEventListener(mValueEventListener);
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("online");
    }


}