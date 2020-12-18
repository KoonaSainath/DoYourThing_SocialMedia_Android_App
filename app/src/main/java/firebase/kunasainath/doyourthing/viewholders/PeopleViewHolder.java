package firebase.kunasainath.doyourthing.viewholders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import firebase.kunasainath.doyourthing.R;

public class PeopleViewHolder extends RecyclerView.ViewHolder {
    private ImageView imgPeopleProfilePic;
    private TextView txtPeopleUsername;
    private ImageView imgFriendOrUnFriend;
    public PeopleViewHolder(@NonNull View itemView) {
        super(itemView);

        imgPeopleProfilePic = itemView.findViewById(R.id.img_people_profile_pic);
        txtPeopleUsername = itemView.findViewById(R.id.txt_people_username);
        imgFriendOrUnFriend = itemView.findViewById(R.id.img_friend_or_unfriend);
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

    public ImageView getImgFriendOrUnFriend() {
        return imgFriendOrUnFriend;
    }

    public void setImgbtnFriendOrUnFriend(ImageButton imgbtnFriendOrUnFriend) {
        this.imgFriendOrUnFriend = imgbtnFriendOrUnFriend;
    }
}
