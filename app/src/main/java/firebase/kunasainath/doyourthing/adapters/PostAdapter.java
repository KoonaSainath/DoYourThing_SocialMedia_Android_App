package firebase.kunasainath.doyourthing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.model_classes.Post;
import firebase.kunasainath.doyourthing.viewholders.PostViewHolder;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder>{

    private ArrayList<Post> posts;
    private Context mContext;

    public PostAdapter(ArrayList<Post> posts, Context context){
        this.posts = posts;
        mContext = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.each_post, parent, false);
        PostViewHolder viewHolder = new PostViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Glide.with(mContext).load(posts.get(position).getImageUrl()).into(holder.getImgPost());

        holder.getTxtDate().setText(posts.get(position).getDate().toString());
        holder.getTxtTime().setText(posts.get(position).getTime().toString());
        holder.getTxtDescription().setText(posts.get(position).getDescription().toString());

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(posts.get(position).getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String imageUrl = snapshot.child("ProfilePicUrl").getValue().toString();
                        String username = snapshot.child("Username").getValue().toString();

                        Glide.with(mContext).load(imageUrl).into(holder.getImgUserProfilePic());
                        holder.getTxtUsername().setText(username);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
