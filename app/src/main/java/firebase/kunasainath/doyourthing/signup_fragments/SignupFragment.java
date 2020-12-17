package firebase.kunasainath.doyourthing.signup_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.regex.Pattern;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.activities.MainActivity;

public class SignupFragment extends Fragment implements View.OnClickListener{

    private EditText edtEmail, edtPassword, edtUsername;
    private TextView txtCanLogin;
    private Button btnSignUp;

    public interface SignupInterface{
        public void wantToLogin();
    }

    private SignupInterface mInterface;

    public SignupFragment() {
    }


    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mInterface = (SignupInterface) getActivity();
        txtCanLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        edtEmail = view.findViewById(R.id.edt_email);
        edtPassword = view.findViewById(R.id.edt_password);
        edtUsername = view.findViewById(R.id.edt_username);
        txtCanLogin = view.findViewById(R.id.txt_already_have_account);
        btnSignUp = view.findViewById(R.id.btn_signup);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.txt_already_have_account:
                //move to login fragment
                mInterface.wantToLogin();
                break;
            case R.id.btn_signup:

                if(!isEmailValid(edtEmail.getText().toString())){
                    edtEmail.setError("The email you entered is invalid");
                    return;
                }

                if(edtPassword.getText().toString().length() < 6){
                    edtPassword.setError("The password must have atleast 6 characters");
                    return;
                }

                if(edtUsername.getText().toString().length() < 3){
                    edtUsername.setError("The username must have atlease 3 characters");
                    return;
                }

                //SIGNUP CODE

                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();

        }
    }

    private boolean isEmailValid(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}