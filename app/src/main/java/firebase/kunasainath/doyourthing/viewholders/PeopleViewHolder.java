package firebase.kunasainath.doyourthing.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import firebase.kunasainath.doyourthing.R;

public class PeopleViewHolder extends RecyclerView.ViewHolder {
    private ImageView imgPeopleProfilePic;
    private TextView txtPeopleUsername, txtLastMessage, txtUnreadMsgCount;
    private Button btnOnlineOffline;

    public PeopleViewHolder(@NonNull View itemView) {
        super(itemView);

        imgPeopleProfilePic = itemView.findViewById(R.id.img_people_profile_pic);
        txtPeopleUsername = itemView.findViewById(R.id.txt_people_username);
        btnOnlineOffline = itemView.findViewById(R.id.btn_online_offline);
        txtLastMessage = itemView.findViewById(R.id.txt_last_message);
        txtUnreadMsgCount = itemView.findViewById(R.id.txt_unread_message_count);
    }

    public ImageView getImgPeopleProfilePic() {
        return imgPeopleProfilePic;
    }

    public void setImgPeopleProfilePic(ImageView imgPeopleProfilePic) {
        this.imgPeopleProfilePic = imgPeopleProfilePic;
    }

    public TextView getTxtPeopleUsername() {
        return txtPeopleUsername;
    }

    public void setTxtPeopleUsername(TextView txtPeopleUsername) {
        this.txtPeopleUsername = txtPeopleUsername;
    }

    public Button getBtnOnlineOffline() {
        return btnOnlineOffline;
    }

    public void setBtnOnlineOffline(Button btnOnlineOffline) {
        this.btnOnlineOffline = btnOnlineOffline;
    }

    public TextView getTxtLastMessage() {
        return txtLastMessage;
    }

    public void setTxtLastMessage(TextView txtLastMessage) {
        this.txtLastMessage = txtLastMessage;
    }

    public TextView getTxtUnreadMsgCount() {
        return txtUnreadMsgCount;
    }

    public void setTxtUnreadMsgCount(TextView txtUnreadMsgCount) {
        this.txtUnreadMsgCount = txtUnreadMsgCount;
    }
}
