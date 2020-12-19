package firebase.kunasainath.doyourthing.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import firebase.kunasainath.doyourthing.R;

public class ChatRoomViewHolder extends RecyclerView.ViewHolder{

    private TextView txtMessage;

    public ChatRoomViewHolder(@NonNull View itemView) {
        super(itemView);

        txtMessage = itemView.findViewById(R.id.txt_display_message);
    }

    public TextView getTxtMessage() {
        return txtMessage;
    }
}
