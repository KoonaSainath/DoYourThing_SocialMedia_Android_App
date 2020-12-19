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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.adapters.ChatRoomAdapter;
import firebase.kunasainath.doyourthing.model_classes.Message;

public class ChatRoomActivity extends AppCompatActivity {

    private String receiverId, senderId;
    private EditText edtMessage;
    private Button btnSend;

    private ChatRoomAdapter mChatRoomAdapter;
    private ArrayList<Message> messages;
    private RecyclerView recyclerChat;

    private ProgressBar progressChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        edtMessage = findViewById(R.id.edt_message);
        btnSend = findViewById(R.id.btn_send_message);
        recyclerChat = findViewById(R.id.recycler_chat);
        progressChat = findViewById(R.id.progress_chatroom);


        showAllPreviousMessages();

        receiverId = getIntent().getStringExtra("UserId");
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
                            String sender, receiver, message;
                            sender = data.child("Sender").getValue().toString();
                            receiver = data.child("Receiver").getValue().toString();
                            message = data.child("Message").getValue().toString();

                            if( (sender.equals(senderId) && receiver.equals(receiverId)) || (sender.equals(receiverId) && receiver.equals(senderId)) ){
                                Message msg = new Message(sender, receiver, message);
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

        HashMap<String, String> messageData = new HashMap<>();

        messageData.put("Sender", senderId);
        messageData.put("Receiver", receiverId);
        messageData.put("Message", msgToSend);

        FirebaseDatabase.getInstance().getReference()
                .child("Chats")
                .push()
                .setValue(messageData);

        Message message = new Message(senderId, receiverId, msgToSend);
        messages.add(message);
        ChatRoomAdapter chatRoomAdapter = (ChatRoomAdapter) recyclerChat.getAdapter();
        chatRoomAdapter.addNewMessage(message);
        chatRoomAdapter.notifyItemInserted(chatRoomAdapter.getMessageArrayList().size()-1);

        recyclerChat.scrollToPosition(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.hold_animation, R.anim.activity_transition_animation);
    }
}