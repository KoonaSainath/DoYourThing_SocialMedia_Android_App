package firebase.kunasainath.doyourthing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.model_classes.User;
import firebase.kunasainath.doyourthing.viewholders.PeopleViewHolder;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleViewHolder> {
    private ArrayList<User> users;
    private Context mContext;



    public PeopleAdapter(ArrayList<User> users, Context context){
        this.users = users;
        mContext = context;
    }

    @NonNull
    @Override
    public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.each_user_in_people, parent, false);
        PeopleViewHolder viewHolder = new PeopleViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleViewHolder holder, int position) {

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()){
                            if(Boolean.parseBoolean(data.getValue().toString())){
                                holder.getImgFriendOrUnFriend().setImageResource(R.drawable.liked);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.getImgFriendOrUnFriend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final boolean[] done = {false};

                String currentFriend = users.get(position).getUserId();
                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Friends")
                        .child(currentFriend).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.getValue()!=null && Boolean.parseBoolean(snapshot.getValue().toString())){
                            holder.getImgFriendOrUnFriend().setImageResource(R.drawable.not_liked);

                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Friends").child(currentFriend).setValue(false);
                            notifyItemChanged(position);
                        }
                        else{
                            holder.getImgFriendOrUnFriend().setImageResource(R.drawable.liked);

                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Friends").child(currentFriend).setValue(true);
                            notifyItemChanged(position);
                        }
                        done[0] = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });



        /*FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Friends")
                .child(users.get(position).getUserId()).setValue(true);*/


        holder.getTxtPeopleUsername().setText(users.get(position).getUsername());

        String userId = users.get(position).getUserId();

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("ProfilePicUrl")){
                            Glide.with(mContext).load(snapshot.child("ProfilePicUrl").getValue().toString()).into(holder.getImgPeopleProfilePic());
                            holder.getImgPeopleProfilePic().setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }else{
                            holder.getImgPeopleProfilePic().setImageResource(R.drawable.profile_pic_place_holder);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
