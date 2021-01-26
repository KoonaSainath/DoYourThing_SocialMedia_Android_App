package firebase.kunasainath.doyourthing.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

public class UsersChatAdapter extends RecyclerView.Adapter<PeopleViewHolder> {

    private ArrayList<User> users;
    private Context mContext;
    private String parent;

    public UsersChatAdapter(ArrayList<User> users, Context context, String parent){
        this.users = users;
        this.mContext = context;
        this.parent = parent;
    }

    public interface PeopleInterface{
        public void showProfile(User user);
    }

    public interface ChatInterface{
        public void startChatRoom(User user);
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

        User user = users.get(position);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(user.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        if(snapshot.child("status").getValue().equals("online")){
                            holder.getBtnOnlineOffline().setBackground(mContext.getDrawable(R.drawable.btn_online));
                            notifyItemChanged(position);
                        }else{
                            holder.getBtnOnlineOffline().setBackground(mContext.getDrawable(R.drawable.btn_offline));
                            notifyItemChanged(position);
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
                    mPeopleInterface.showProfile(user);
                }else if(parent.equals("Chat")){
                    mChatInterface = (ChatInterface) mContext;
                    mChatInterface.startChatRoom(user);



                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Friends")
                            .child(user.getId())
                            .child("UnreadMessageCount").setValue(0);
                }
            }
        });


        if(parent.equals("Chat")){
            try {
                displayLastMessage(user.getId(), holder);

                if(user.getUnreadMsgCount() == 0){
                    holder.getTxtUnreadMsgCount().setVisibility(View.GONE);
                }else{
                    holder.getTxtUnreadMsgCount().setVisibility(View.VISIBLE);
                    holder.getTxtUnreadMsgCount().setText(Integer.toString(user.getUnreadMsgCount()));
                }

            } catch (Exception exception1) {
                exception1.printStackTrace();
            }
        }else{
            holder.getTxtLastMessage().setVisibility(View.GONE);
            holder.getTxtUnreadMsgCount().setVisibility(View.GONE);
        }

    }

    private void displayLastMessage(String userId, PeopleViewHolder holder) throws Exception{

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String otherUser = userId;


        FirebaseDatabase.getInstance().getReference().child("Chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String lastMessage = "No messages";

                        for(DataSnapshot data : snapshot.getChildren()){
                            String senderId = data.child("Sender").getValue().toString();
                            String receiverId = data.child("Receiver").getValue().toString();

                            if((senderId.equals(currentUser) && receiverId.equals(otherUser)) || (senderId.equals(otherUser) && receiverId.equals(currentUser))){
                                lastMessage = data.child("Message").getValue().toString();
                            }
                        }

                        holder.getTxtLastMessage().setText(lastMessage);
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
