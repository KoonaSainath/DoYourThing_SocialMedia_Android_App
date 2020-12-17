package firebase.kunasainath.doyourthing.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.signup_fragments.LoginFragment;
import firebase.kunasainath.doyourthing.signup_fragments.SignupFragment;

public class SignUpActivity extends AppCompatActivity implements SignupFragment.SignupInterface, LoginFragment.LoginInterface {

    private SignupFragment mSignupFragment;
    private LoginFragment mLoginFragment;

    private static final String SIGNUP_FRAGMENT_TAG = "signup fragment tag";
    private static final String LOGIN_FRAGMENT_TAG = "login fragment tag";

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        mSignupFragment = SignupFragment.newInstance();
        mLoginFragment = LoginFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mSignupFragment, SIGNUP_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void wantToSignup() {
        displaySignupFragment();
    }

    @Override
    public void wantToLogin() {
        displayLoginFragment();
    }


    private void displayLoginFragment(){
        getSupportFragmentManager().popBackStack();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mLoginFragment, LOGIN_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    private void displaySignupFragment(){
        getSupportFragmentManager().popBackStack();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mSignupFragment, SIGNUP_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Fragment fragment1 = getSupportFragmentManager().findFragmentByTag(SIGNUP_FRAGMENT_TAG);
        Fragment fragment2 = getSupportFragmentManager().findFragmentByTag(LOGIN_FRAGMENT_TAG);

        if(fragment1 == null && fragment2 == null){
            finish();
        }
    }
}