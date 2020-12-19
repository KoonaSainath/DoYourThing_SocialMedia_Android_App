package firebase.kunasainath.doyourthing.viewpager_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.adapters.UsersChatAdapter;

public class PeopleFragment extends Fragment{
    private RecyclerView recyclerPeople;
    private ArrayList<String> users;
    private UsersChatAdapter mUsersChatAdapter;
    private SwipeRefreshLayout refreshPeople;
    private ProgressBar progresPeople;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        recyclerPeople = view.findViewById(R.id.recycler_people);
        refreshPeople = view.findViewById(R.id.refresh_people);
        progresPeople = view.findViewById(R.id.progress_people);
        return view;
    }

    private void showPeople(){
        users = new ArrayList<String>();

        progresPeople.setVisibility(View.VISIBLE);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        users.clear();

                        for(DataSnapshot data : snapshot.getChildren()){
                            String username, userId;
                            userId = data.getKey().toString();

                            if(!userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                users.add(userId);
                            }
                        }

                        mUsersChatAdapter = new UsersChatAdapter(users, getActivity(), "People");
                        recyclerPeople.setAdapter(mUsersChatAdapter);
                        recyclerPeople.setLayoutManager(new LinearLayoutManager(getActivity()));

                        progresPeople.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}