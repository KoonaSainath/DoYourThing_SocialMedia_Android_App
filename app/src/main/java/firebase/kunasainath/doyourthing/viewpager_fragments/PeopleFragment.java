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
import java.util.HashMap;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.adapters.UsersChatAdapter;
import firebase.kunasainath.doyourthing.model_classes.User;

public class PeopleFragment extends Fragment{
    private RecyclerView recyclerPeople;
    private ArrayList<User> users;
    private UsersChatAdapter mUsersChatAdapter;
    private SwipeRefreshLayout refreshPeople;
    private ProgressBar progresPeople;
    private EditText edtSearch;
    public PeopleFragment() {
    }

    public static PeopleFragment newInstance() {
        PeopleFragment fragment = new PeopleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showPeople();

        refreshPeople.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showPeople();
                refreshPeople.setRefreshing(false);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                search(text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        recyclerPeople = view.findViewById(R.id.recycler_people);
        refreshPeople = view.findViewById(R.id.refresh_people);
        progresPeople = view.findViewById(R.id.progress_people);
        edtSearch = view.findViewById(R.id.edt_search);
        return view;
    }

    private void showPeople(){
        users = new ArrayList<User>();

        progresPeople.setVisibility(View.VISIBLE);

        if(edtSearch.getText().toString().equals("")) {

            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            users.clear();

                            for (DataSnapshot data : snapshot.getChildren()) {
                                String username, userId;
                                userId = data.getKey();

                                HashMap<String, Object> userdata = (HashMap) data.getValue();

                                username = userdata.get("Username").toString();

                                User user = new User(userId, username);

                                if (!userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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

                            mUsersChatAdapter = new UsersChatAdapter(users, getActivity(), "People");
                            recyclerPeople.setAdapter(mUsersChatAdapter);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

                            recyclerPeople.setLayoutManager(layoutManager);

                            progresPeople.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    public void search(String text){
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .orderByChild("Username")
                .startAt(text)
                .endAt(text + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                users.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    String username = data.child("Username").getValue().toString();
                    String userid = data.getKey();
                    User user = new User(userid, username);

                    if(!userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        users.add(user);
                    }
                }

                mUsersChatAdapter = new UsersChatAdapter(users, getActivity(), "People");
                recyclerPeople.setAdapter(mUsersChatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}