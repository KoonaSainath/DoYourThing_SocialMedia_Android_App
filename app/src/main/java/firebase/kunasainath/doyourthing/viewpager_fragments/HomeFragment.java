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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.adapters.PostAdapter;
import firebase.kunasainath.doyourthing.model_classes.Post;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerHome;
    private PostAdapter mPostAdapter;
    private ArrayList<Post> posts;
    private SwipeRefreshLayout refresh;
    private ProgressBar mProgressBar;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        displayPosts();

        mProgressBar.animate().alpha(0.0f).setDuration(5000).start();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayPosts();
                refresh.setRefreshing(false);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerHome = view.findViewById(R.id.recycler_home);
        refresh = view.findViewById(R.id.refresh_home);
        mProgressBar = view.findViewById(R.id.progress_home);
        return view;
    }


    private void displayPosts(){
        posts = new ArrayList<Post>();

        FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String date, time, description, imageUrl, userId;
                        date = snapshot.child("Date").getValue().toString();
                        time = snapshot.child("Time").getValue().toString();
                        description = snapshot.child("Description").getValue().toString();
                        imageUrl = snapshot.child("ImageUrl").getValue().toString();
                        userId = snapshot.child("UserId").getValue().toString();

                        Post post = new Post(userId, date, time, description, imageUrl);
                        posts.add(post);

                        mPostAdapter = new PostAdapter(posts, getActivity());
                        recyclerHome.setAdapter(mPostAdapter);
                        recyclerHome.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}