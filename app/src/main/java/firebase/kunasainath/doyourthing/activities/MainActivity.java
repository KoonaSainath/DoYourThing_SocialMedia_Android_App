package firebase.kunasainath.doyourthing.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.adapters.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FloatingActionButton fabMain, fabProfile, fabUploadPost, fabLogout;

    private ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                mAuth.signOut();
                startActivity(new Intent(this, SignUpActivity.class));
                Toast.makeText(this, "Logging you out", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return true;
    }
}