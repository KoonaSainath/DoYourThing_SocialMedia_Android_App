package firebase.kunasainath.doyourthing.viewpager_fragments;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.adapters.UsersChatAdapter;
import firebase.kunasainath.doyourthing.model_classes.User;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerUsersChat;
    private ArrayList<User> users;
    private UsersChatAdapter mUsersChatAdapter;
    private SwipeRefreshLayout refreshUserChats;
    private ProgressBar progressUserChats;
    private EditText edtSearch;

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

    private void showUserChats(){
        users = new ArrayList<User>();

        progressUserChats.setVisibility(View.VISIBLE);

        if(edtSearch.getText().toString().equals("")) {

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

                                    User user = new User(userId, username);

                                    users.add(user);
                                }
                            }

                            Comparator<User> sorter = new Comparator<User>() {
                                @Override
                                public int compare(User a, User b) {
                                    return -1;
                                }
                            };

                            Collections.sort(users, sorter);

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
        }else{
            progressUserChats.setVisibility(View.INVISIBLE);
        }
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
                    User user = new User(id, name);
                    users.add(user);
                }

                mUsersChatAdapter = new UsersChatAdapter(users, getActivity(), "Chat");
                recyclerUsersChat.setAdapter(mUsersChatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
}