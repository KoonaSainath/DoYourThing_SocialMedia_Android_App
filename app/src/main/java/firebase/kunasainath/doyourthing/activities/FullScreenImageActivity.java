package firebase.kunasainath.doyourthing.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import firebase.kunasainath.doyourthing.R;

public class FullScreenImageActivity extends AppCompatActivity {

    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        mImageView = findViewById(R.id.imageView);

        String url = getIntent().getStringExtra("Image");
        if(url == null){
            mImageView.setImageResource(R.drawable.profile_pic_place_holder);
        }else{
            Glide.with(this).load(url).into(mImageView);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.hold_animation , R.anim.activity_transition_animation);
    }
}