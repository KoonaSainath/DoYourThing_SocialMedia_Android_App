package firebase.kunasainath.doyourthing.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

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

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("online");
    }

    @Override
    protected void onPause() {
        super.onPause();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("offline");
    }
}