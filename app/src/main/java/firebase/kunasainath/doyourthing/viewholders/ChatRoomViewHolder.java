package firebase.kunasainath.doyourthing.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import firebase.kunasainath.doyourthing.R;

public class ChatRoomViewHolder extends RecyclerView.ViewHolder{

    private TextView txtMessage, txtDateTime, txtSeenOrNot;

    public ChatRoomViewHolder(@NonNull View itemView) {
        super(itemView);

        txtMessage = itemView.findViewById(R.id.txt_display_message);
        txtDateTime = itemView.findViewById(R.id.txt_date_time);
        txtSeenOrNot = itemView.findViewById(R.id.txt_sent_delivered_seen);
    }

    public TextView getTxtMessage() {
        return txtMessage;
    }

    public TextView getTxtDateTime() {
        return txtDateTime;
    }

    public TextView getTxtSeenOrNot() {
        return txtSeenOrNot;
    }
}
