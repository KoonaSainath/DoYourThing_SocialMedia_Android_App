package firebase.kunasainath.doyourthing.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import firebase.kunasainath.doyourthing.R;

public class PostViewHolder extends RecyclerView.ViewHolder{

    private ImageView imgPost, imgUserProfilePic;
    private TextView txtDate, txtTime, txtDescription, txtUsername;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);

        imgPost = itemView.findViewById(R.id.img_post);
        imgUserProfilePic = itemView.findViewById(R.id.img_user_image);
        txtDate = itemView.findViewById(R.id.txt_date);
        txtTime = itemView.findViewById(R.id.txt_time);
        txtDescription = itemView.findViewById(R.id.txt_description);
        txtUsername = itemView.findViewById(R.id.txt_username);

    }

    public ImageView getImgPost() {
        return imgPost;
    }

    public void setImgPost(ImageView imgPost) {
        this.imgPost = imgPost;
    }

    public ImageView getImgUserProfilePic() {
        return imgUserProfilePic;
    }

    public void setImgUserProfilePic(ImageView imgUserProfilePic) {
        this.imgUserProfilePic = imgUserProfilePic;
    }

    public TextView getTxtDate() {
        return txtDate;
    }

    public void setTxtDate(TextView txtDate) {
        this.txtDate = txtDate;
    }

    public TextView getTxtTime() {
        return txtTime;
    }

    public void setTxtTime(TextView txtTime) {
        this.txtTime = txtTime;
    }

    public TextView getTxtDescription() {
        return txtDescription;
    }

    public void setTxtDescription(TextView txtDescription) {
        this.txtDescription = txtDescription;
    }

    public TextView getTxtUsername() {
        return txtUsername;
    }

    public void setTxtUsername(TextView txtUsername) {
        this.txtUsername = txtUsername;
    }
}

