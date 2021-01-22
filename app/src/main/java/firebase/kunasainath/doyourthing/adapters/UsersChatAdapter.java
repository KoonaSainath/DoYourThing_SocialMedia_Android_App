package firebase.kunasainath.doyourthing.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
    private String parent;

    public UsersChatAdapter(ArrayList<String> users, Context context, String parent){
        this.users = users;
        this.mContext = context;
        this.parent = parent;
    }

    public interface PeopleInterface{
        public void showProfile(String userid);
    }

    public interface ChatInterface{
        public void startChatRoom(String userid);
    }

    private PeopleInterface mPeopleInterface;
    private ChatInterface mChatInterface;

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
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        if(snapshot.child("status").getValue().equals("online")){
                            holder.getBtnOnlineOffline().setBackground(mContext.getDrawable(R.drawable.btn_online));
                        }else{
                            holder.getBtnOnlineOffline().setBackground(mContext.getDrawable(R.drawable.btn_offline));
                        }

                        holder.getTxtPeopleUsername().setText(snapshot.child("Username").getValue().toString());

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parent.equals("People")){
                    mPeopleInterface = (PeopleInterface) mContext;
                    mPeopleInterface.showProfile(userId);
                }else if(parent.equals("Chat")){
                    mChatInterface = (ChatInterface) mContext;
                    mChatInterface.startChatRoom(userId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
