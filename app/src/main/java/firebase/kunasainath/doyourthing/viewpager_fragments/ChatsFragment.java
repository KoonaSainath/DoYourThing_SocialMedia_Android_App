package firebase.kunasainath.doyourthing.viewpager_fragments;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.adapters.UsersChatAdapter;
import firebase.kunasainath.doyourthing.model_classes.User;
import firebase.kunasainath.doyourthing.notification.Token;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerUsersChat;
    private ArrayList<User> users;
    private UsersChatAdapter mUsersChatAdapter;
    private SwipeRefreshLayout refreshUserChats;
    private ProgressBar progressUserChats;
    private EditText edtSearch;

    public static ValueEventListener mValueEventListener;
    public static DatabaseReference sDatabaseReference;

    public static ValueEventListener mainValueEventListener;
    public static DatabaseReference mainReference;


    ProgressDialog dialog;

    public ChatsFragment() {
    }
    public static ChatsFragment newInstance() {
        ChatsFragment fragment = new ChatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_DEVICE_DEFAULT_DARK);

        updateUnreadMessagesCount();

        showUserChats();

        refreshUserChats.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showUserChats();
                refreshUserChats.setRefreshing(false);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString();
                updateChats(searchText);
            }
        });


    }

    public void updateUnreadMessagesCount(){

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mainReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser).child("Friends");

        mainValueEventListener = mainReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    boolean isFriend = Boolean.parseBoolean(data.child("IsFriend").getValue().toString());
                    String friendUserId = data.child("UserId").getValue().toString();

                    if(isFriend) {
                        sDatabaseReference = FirebaseDatabase.getInstance().getReference("Chats");
                        mValueEventListener = sDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int count = 0;
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    String senderId = dataSnapshot.child("Sender").getValue().toString();
                                    String receiverId = dataSnapshot.child("Receiver").getValue().toString();

                                    boolean seen = Boolean.parseBoolean(dataSnapshot.child("Seen").getValue().toString());

                                    if(senderId.equals(friendUserId) && receiverId.equals(currentUser) && !seen){
                                        count++;
                                    }
                                }
                                data.getRef().child("UnreadMessageCount").setValue(count);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerUsersChat = view.findViewById(R.id.recycler_users_in_chat);
        refreshUserChats = view.findViewById(R.id.refresh_chats);
        progressUserChats = view.findViewById(R.id.progress_chat);
        edtSearch = view.findViewById(R.id.edt_search);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showUserChats(){
        users = new ArrayList<User>();

        progressUserChats.setVisibility(View.VISIBLE);

        if(edtSearch.getText().toString().equals("")) {

            displayProgressDialog();

            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Friends")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            users.clear();

                            for (DataSnapshot data : snapshot.getChildren()) {

                                if (Boolean.parseBoolean(data.child("IsFriend").getValue().toString())) {
                                    String userId = data.child("UserId").getValue().toString();
                                    String username = data.child("Username").getValue().toString();
                                    String dateTime = "";
                                    if(data.hasChild("LastMessageDateTime")) {
                                        dateTime = data.child("LastMessageDateTime").getValue().toString();
                                    }
                                    int unreadMsgCount = 0;
                                    if(data.hasChild("UnreadMessageCount")) {
                                        unreadMsgCount = Integer.parseInt(data.child("UnreadMessageCount").getValue().toString());
                                    }

                                    User user = new User(userId, username, unreadMsgCount, dateTime);

                                    users.add(user);
                                }
                            }

                            Collections.sort(users, new ChatSorter());

                            mUsersChatAdapter = new UsersChatAdapter(users, getActivity(), "Chat");
                            recyclerUsersChat.setAdapter(mUsersChatAdapter);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            recyclerUsersChat.setLayoutManager(layoutManager);

                            progressUserChats.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            dismissProgressDialog();
        }else{
            progressUserChats.setVisibility(View.INVISIBLE);
        }

        //CHAT NOTIFICATIONS

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    //CHAT NOTIFICATIONS

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token1);
    }

    public void updateChats(String searchText){
        users.clear();
        Query query = FirebaseDatabase.getInstance().getReference().
                child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Friends")
                .orderByChild("Username")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    String id = data.child("UserId").getValue().toString();
                    String name = data.child("Username").getValue().toString();
                    String dateTime = "";
                    if(data.hasChild("LastMessageDateTime")) {
                        dateTime = data.child("LastMessageDateTime").getValue().toString();
                    }

                    boolean isFriend = Boolean.parseBoolean(data.child("IsFriend").getValue().toString());
                    int unreadMsgCount = 0;
                    if(data.hasChild("UnreadMessageCount")) {
                        unreadMsgCount = Integer.parseInt(data.child("UnreadMessageCount").getValue().toString());
                    }

                    if(isFriend) {

                        User user = new User(id, name, unreadMsgCount, dateTime);
                        users.add(user);
                    }
                }

                Collections.sort(users, new ChatSorter());

                mUsersChatAdapter = new UsersChatAdapter(users, getActivity(), "Chat");
                recyclerUsersChat.setAdapter(mUsersChatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void displayProgressDialog(){
        dialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        dialog.setTitle("Wait");
        dialog.setMessage("Your chats are getting updated...");
        dialog.setCancelable(false);
        dialog.create();
        dialog.show();
    }

    public void dismissProgressDialog(){
        dialog.dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        /*
        mainReference.removeEventListener(mainValueEventListener);
        sDatabaseReference.removeEventListener(mValueEventListener);
         */
    }
}