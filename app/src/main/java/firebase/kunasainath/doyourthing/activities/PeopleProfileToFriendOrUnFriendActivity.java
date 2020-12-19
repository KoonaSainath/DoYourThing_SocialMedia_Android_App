package firebase.kunasainath.doyourthing.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import firebase.kunasainath.doyourthing.R;

public class PeopleProfileToFriendOrUnFriendActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgPic;
    private TextView txtName, txtAbout, txtFav, txtStatus;
    private ImageView imgFriend;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_profile_to_friend_or_un_friend);
        imgPic = findViewById(R.id.img_profile_pic);
        txtName = findViewById(R.id.txt_username);
        txtAbout = findViewById(R.id.txt_about);
        txtFav = findViewById(R.id.txt_favorites);
        imgFriend = findViewById(R.id.img_friend);
        txtStatus = findViewById(R.id.txt_friend_status);

        userId = getIntent().getStringExtra("UserId");

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (Boolean.parseBoolean(snapshot.child(userId).getValue().toString())) {
                                imgFriend.setImageResource(R.drawable.liked);
                                txtStatus.setText("This person is your friend");
                                txtStatus.setTextColor(getColor(R.color.green));
                            }
                        }catch (Exception e){}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("ProfilePicUrl")){
                            Glide.with(PeopleProfileToFriendOrUnFriendActivity.this).load(snapshot.child("ProfilePicUrl").getValue()).into(imgPic);
                            imgPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }

                        txtName.setText(snapshot.child("Username").getValue().toString());

                        if(snapshot.hasChild("About")){
                            txtAbout.setText(snapshot.child("About").getValue().toString());
                        }else{
                            txtAbout.setText("No About");
                        }

                        if(snapshot.hasChild("Favorites")){
                            txtFav.setText(snapshot.child("Favorites").getValue().toString());
                        }else{
                            txtFav.setText("No favorites");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        imgFriend.setOnClickListener(this);
        imgPic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.img_profile_pic:
                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(userId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    String imageUrl = snapshot.child("ImageUrl").getValue().toString();
                                    Intent intent = new Intent(PeopleProfileToFriendOrUnFriendActivity.this, FullScreenImageActivity.class);
                                    intent.putExtra("Image", imageUrl);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.hold_animation, R.anim.activity_transition_animation);
                                }catch (Exception e){
                                    Intent intent = new Intent(PeopleProfileToFriendOrUnFriendActivity.this, FullScreenImageActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.hold_animation, R.anim.activity_transition_animation);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                break;
            case R.id.img_friend:
                doTheBigThing();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.hold_animation, R.anim.activity_transition_animation);
    }

    private void doTheBigThing() {

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(Boolean.parseBoolean(snapshot.child(userId).getValue().toString())){

                            //ALREADY FRIEND

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("Friends")
                                    .child(userId)
                                    .setValue(false);

                            imgFriend.setImageResource(R.drawable.not_liked);
                            txtStatus.setText("This person is not your friend");
                            txtStatus.setTextColor(getColor(R.color.red));

                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Removed user from your friends list", Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                            snackbar.setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });

                        }else{
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("Friends")
                                    .child(userId)
                                    .setValue(true);

                            imgFriend.setImageResource(R.drawable.liked);
                            txtStatus.setText("This person is your friend");
                            txtStatus.setTextColor(getColor(R.color.green));

                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Added user to your friends list", Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                            snackbar.setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}