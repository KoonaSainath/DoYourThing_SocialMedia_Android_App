package firebase.kunasainath.doyourthing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.model_classes.Message;
import firebase.kunasainath.doyourthing.viewholders.ChatRoomViewHolder;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomViewHolder> {
    public static final int VIEW_TYPE_LEFT = 1;
    public static final int VIEW_TYPE_RIGHT = 2;
    private ArrayList<Message> messages;
    private Context mContext;

    public ChatRoomAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        mContext = context;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        if(viewType == VIEW_TYPE_RIGHT){
            View view = layoutInflater.inflate(R.layout.chat_item_right, parent, false);
            return new ChatRoomViewHolder(view);
        }else{
            View view = layoutInflater.inflate(R.layout.chat_item_left, parent, false);
            return new ChatRoomViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {


        String message = messages.get(position).getMessage();
        String dateTime = messages.get(position).getDateTime();
        String seenOrNot = messages.get(position).getSeenOrDelivered();

        holder.getTxtMessage().setText(message);

        holder.getTxtDateTime().setText(dateTime);


        if(seenOrNot.equals("Seen")){
            holder.getTxtSeenOrNot().setText("seen");
        }else{
            holder.getTxtSeenOrNot().setText("delivered");
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_RIGHT;
        }

        return VIEW_TYPE_LEFT;
    }

    public void addNewMessage(Message message){
        messages.add(message);
    }

    public ArrayList<Message> getMessageArrayList(){
        return messages;
    }
}
