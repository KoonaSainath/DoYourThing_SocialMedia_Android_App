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

import java.util.HashMap;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.model_classes.User;

public class PeopleProfileToFriendOrUnFriendActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgPic;
    private TextView txtName, txtAbout, txtFav, txtStatus;
    private ImageView imgFriend;
    private User user;


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

        user = (User) getIntent().getSerializableExtra("User");

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Friends")
                .child(user.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (Boolean.parseBoolean(snapshot.child("IsFriend").getValue().toString())) {
                                imgFriend.setImageResource(R.drawable.liked);
                                txtStatus.setText("This person is your friend");
                                txtStatus.setTextColor(getColor(R.color.green));
                            }else{
                                imgFriend.setImageResource(R.drawable.not_liked);
                                txtStatus.setText("This person is not your friend");
                                txtStatus.setTextColor(getColor(R.color.red));
                            }
                        }catch (Exception e){}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(user.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("ProfilePicUrl")){
                            Glide.with(PeopleProfileToFriendOrUnFriendActivity.this).load(snapshot.child("ProfilePicUrl").getValue()).into(imgPic);
                            imgPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }

                        txtName.setText(snapshot.child("Username").getValue().toString());

                        if(snapshot.hasChild("About")){
                            if(snapshot.child("About").getValue().toString().length() == 0){
                                txtAbout.setText("No About");
                            }else {
                                txtAbout.setText(snapshot.child("About").getValue().toString());
                            }
                        }else{
                            txtAbout.setText("No About");
                        }

                        if(snapshot.hasChild("Favorites")){
                            if(snapshot.child("Favorites").getValue().toString().length() == 0){
                                txtFav.setText("No favorites");
                            }else {
                                txtFav.setText(snapshot.child("Favorites").getValue().toString());
                            }
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
                        .child(user.getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.hasChild("ProfilePicUrl")){
                                    String imageUrl = snapshot.child("ProfilePicUrl").getValue().toString();
                                    Intent intent = new Intent(PeopleProfileToFriendOrUnFriendActivity.this, FullScreenImageActivity.class);
                                    intent.putExtra("Image", imageUrl);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.hold_animation, R.anim.activity_transition_animation);
                                }else{
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


                        if(snapshot.child(user.getId()).getValue() != null && Boolean.parseBoolean(snapshot.child(user.getId()).child("IsFriend").getValue().toString())){

                            //ALREADY FRIEND

                            HashMap<String, Object> data1 = new HashMap<>();
                            data1.put("UserId", user.getId());
                            data1.put("Username", user.getName());
                            data1.put("IsFriend", false);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("Friends")
                                    .child(user.getId())
                                    .child("IsFriend")
                                    .setValue(false);

                            HashMap<String, Object> data2 = new HashMap<>();
                            data2.put("UserId", FirebaseAuth.getInstance().getCurrentUser().getUid());

                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            HashMap<String, Object> data = (HashMap) snapshot.getValue();
                                            data2.put("Username", data.get("Username"));

                                            data2.put("IsFriend", false);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Users")
                                                    .child(user.getId())
                                                    .child("Friends")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child("IsFriend")
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
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });



                        }else{

                            HashMap<String, Object> data1 = new HashMap<>();
                            data1.put("UserId", user.getId());
                            data1.put("Username", user.getName());
                            data1.put("IsFriend", true);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("Friends")
                                    .child(user.getId())
                                    .updateChildren(data1);


                            HashMap<String, Object> data2 = new HashMap<>();
                            data2.put("UserId", FirebaseAuth.getInstance().getCurrentUser().getUid());

                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            HashMap<String, Object> data = (HashMap) snapshot.getValue();
                                            data2.put("Username", data.get("Username"));
                                            data2.put("IsFriend", true);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Users")
                                                    .child(user.getId())
                                                    .child("Friends")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .updateChildren(data2);

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

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

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