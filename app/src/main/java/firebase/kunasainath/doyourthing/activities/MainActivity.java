package firebase.kunasainath.doyourthing.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.adapters.PostAdapter;
import firebase.kunasainath.doyourthing.adapters.UsersChatAdapter;
import firebase.kunasainath.doyourthing.adapters.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PostAdapter.PostInterface, UsersChatAdapter.ChatInterface, UsersChatAdapter.PeopleInterface {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FloatingActionButton fabMain, fabProfile, fabUploadPost, fabLogout;
    private boolean fabOpened = false;


    private static final int DURATION = 200;

    private ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fabOpened = false;

        mAuth = FirebaseAuth.getInstance();
        mTabLayout = findViewById(R.id.tablayout);
        mViewPager = findViewById(R.id.viewpager);
        mToolbar = findViewById(R.id.toolbar_custom);

        setSupportActionBar(mToolbar);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        mViewPager.setAdapter(mViewPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        fabMain = findViewById(R.id.fab_main);
        fabProfile = findViewById(R.id.fab_profile);
        fabUploadPost = findViewById(R.id.fab_post_upload);
        fabLogout = findViewById(R.id.fab_logout);

        fabMain.setOnClickListener(this);
        fabProfile.setOnClickListener(this);
        fabUploadPost.setOnClickListener(this);
        fabLogout.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_logout:
                logout();
                break;
            case R.id.menu_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                animateTransition();
                break;
            case R.id.menu_upload_post:
                startActivity(new Intent(this, PostUploadActivity.class));
                animateTransition();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_main:
                if(!fabOpened) {
                    fabLogout.animate().translationY(-200).setDuration(DURATION);
                    fabUploadPost.animate().translationY(-400).setDuration(DURATION);
                    fabProfile.animate().translationY(-600).setDuration(DURATION);
                    fabMain.animate().rotation(45).setDuration(DURATION);
                    fabOpened = true;
                }else{
                    fabLogout.animate().translationY(0).setDuration(DURATION);
                    fabUploadPost.animate().translationY(0).setDuration(DURATION);
                    fabProfile.animate().translationY(0).setDuration(DURATION);
                    fabMain.animate().rotation(0).setDuration(DURATION);
                    fabOpened = false;
                }
                break;
            case R.id.fab_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                animateTransition();
                break;
            case R.id.fab_post_upload:
                startActivity(new Intent(this, PostUploadActivity.class));
                animateTransition();
                break;
            case R.id.fab_logout:
                logout();
                break;
        }
    }

    private void logout(){
        mAuth.signOut();
        startActivity(new Intent(this, SignUpActivity.class));
        Toast.makeText(this, "Log out successful", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void animateTransition(){
        overridePendingTransition(R.anim.hold_animation, R.anim.activity_transition_animation);
    }

    @Override
    public void profileImage(String userId) {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            try {
                                String userImageUrl = snapshot.child("ProfilePicUrl").getValue().toString();
                                Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
                                intent.putExtra("Image", userImageUrl);
                                startActivity(intent);
                                overridePendingTransition(R.anim.hold_animation, R.anim.activity_transition_animation);
                            }catch (Exception e){
                                startActivity(new Intent(getApplicationContext(), FullScreenImageActivity.class));
                            }

                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void postImage(String imageUrl) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra("Image", imageUrl);
        startActivity(intent);
        overridePendingTransition(R.anim.hold_animation, R.anim.activity_transition_animation);
    }

    @Override
    public void showProfile(String userid) {
        Intent intent = new Intent(this, PeopleProfileToFriendOrUnFriendActivity.class);
        intent.putExtra("UserId", userid);
        startActivity(intent);
    }

    @Override
    public void startChatRoom(String userid) {
        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra("UserId", userid);
        startActivity(intent);
    }
}