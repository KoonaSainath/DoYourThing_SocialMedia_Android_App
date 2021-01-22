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
    private TextView txtPeopleUsername;
    private Button btnOnlineOffline;
    public PeopleViewHolder(@NonNull View itemView) {
        super(itemView);

        imgPeopleProfilePic = itemView.findViewById(R.id.img_people_profile_pic);
        txtPeopleUsername = itemView.findViewById(R.id.txt_people_username);
        btnOnlineOffline = itemView.findViewById(R.id.btn_online_offline);
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
}
