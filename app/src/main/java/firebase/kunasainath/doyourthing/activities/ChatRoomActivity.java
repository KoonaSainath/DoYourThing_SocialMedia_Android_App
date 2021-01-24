package firebase.kunasainath.doyourthing.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        mReference = FirebaseDatabase.getInstance().getReference().child("Chats");

        edtMessage = findViewById(R.id.edt_message);
        btnSend = findViewById(R.id.btn_send_message);
        recyclerChat = findViewById(R.id.recycler_chat);
        progressChat = findViewById(R.id.progress_chatroom);


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
            @Override
            public void onClick(View v) {
                if(edtMessage.getText().toString().length() == 0){
                    Snackbar.make(findViewById(android.R.id.content), "Cannot send an empty message", Snackbar.LENGTH_LONG).show();
                    return ;
                }

                sendMessage();

            }
        });

        seenTheMessage(senderId);

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
                            String sender, receiver, message, seenOrNot, dateTime;
                            sender = data.child("Sender").getValue().toString();
                            receiver = data.child("Receiver").getValue().toString();
                            message = data.child("Message").getValue().toString();
                            seenOrNot = data.child("Seen").getValue().toString();
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

    private void sendMessage(){
        String msgToSend = edtMessage.getText().toString();
        edtMessage.setText("");

        String dateTime, seenOrNot = "sent";

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date currentDate = new Date();

        dateTime = dateFormat.format(currentDate);


        HashMap<String, String> messageData = new HashMap<>();

        messageData.put("Sender", senderId);
        messageData.put("Receiver", receiverId);
        messageData.put("Message", msgToSend);
        messageData.put("Seen", seenOrNot);
        messageData.put("Date and time", dateTime);

        FirebaseDatabase.getInstance().getReference()
                .child("Chats")
                .push()
                .setValue(messageData);

        Message message = new Message(senderId, receiverId, msgToSend, dateTime, seenOrNot);
        messages.add(message);
        ChatRoomAdapter chatRoomAdapter = (ChatRoomAdapter) recyclerChat.getAdapter();
        chatRoomAdapter.addNewMessage(message);
        chatRoomAdapter.notifyItemInserted(chatRoomAdapter.getMessageArrayList().size()-1);

        recyclerChat.scrollToPosition(0);

    }


    public void seenTheMessage(String userId){

        mReference = FirebaseDatabase.getInstance().getReference().child("Chats");

        mValueEventListener = mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    String receiverId = data.child("Receiver").getValue().toString();
                    String senderId = data.child("Sender").getValue().toString();
                    if(receiverId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && senderId.equals(userId)){
                        data.getRef().child("Seen").setValue("Seen");
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
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("offline");
        mReference.removeEventListener(mValueEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("online");
    }

}