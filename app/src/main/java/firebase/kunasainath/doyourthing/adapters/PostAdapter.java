package firebase.kunasainath.doyourthing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    public interface PostInterface{
        public void profileImage(String userId);
        public void postImage(String imageUrl);
    }

    private PostInterface mInterface;

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

        mInterface = (PostInterface) mContext;

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
                        try {
                            String imageUrl = snapshot.child("ProfilePicUrl").getValue().toString();
                            Glide.with(mContext).load(imageUrl).into(holder.getImgUserProfilePic());
                        }catch (Exception e){
                            holder.getImgUserProfilePic().setImageResource(R.drawable.profile_pic_place_holder);
                            holder.getImgUserProfilePic().setScaleType(ImageView.ScaleType.FIT_XY);
                        }

                        String username = snapshot.child("Username").getValue().toString();

                        holder.getTxtUsername().setText(username);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.img_post:
                        mInterface.postImage(posts.get(position).getImageUrl());
                        break;
                    case R.id.img_user_image:
                        mInterface.profileImage(posts.get(position).getUserId());
                        break;
                }
            }
        };

        holder.getImgUserProfilePic().setOnClickListener(listener);
        holder.getImgPost().setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}
