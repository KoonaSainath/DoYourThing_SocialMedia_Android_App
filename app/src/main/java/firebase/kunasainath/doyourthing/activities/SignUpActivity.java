package firebase.kunasainath.doyourthing.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.signup_fragments.LoginFragment;
import firebase.kunasainath.doyourthing.signup_fragments.SignupFragment;

public class SignUpActivity extends AppCompatActivity {

    private SignupFragment mSignupFragment;
    private LoginFragment mLoginFragment;

    private static final String SIGNUP_FRAGMENT_TAG = "signup fragment tag";
    private static final String LOGIN_FRAGMENT_TAG = "login fragment tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mSignupFragment = SignupFragment.newInstance();
        mLoginFragment = LoginFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mSignupFragment)
                .commit();
    }
}