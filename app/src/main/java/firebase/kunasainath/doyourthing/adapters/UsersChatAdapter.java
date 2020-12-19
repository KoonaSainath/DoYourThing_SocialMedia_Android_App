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
import firebase.kunasainath.doyourthing.viewholders.PeopleViewHolder;

public class UsersChatAdapter extends RecyclerView.Adapter<PeopleViewHolder> {

    private ArrayList<String> users;
    private Context mContext;

    public UsersChatAdapter(ArrayList<String> users, Context context){
        this.users = users;
        this.mContext = context;
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
        String userId = users.get(position);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("ProfilePicUrl")){
                            Glide.with(mContext).load(snapshot.child("ProfilePicUrl").getValue().toString()).into(holder.getImgPeopleProfilePic());
                            holder.getImgPeopleProfilePic().setScaleType(ImageView.ScaleType.CENTER_CROP);

                            holder.getTxtPeopleUsername().setText(snapshot.child("Username").getValue().toString());
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
