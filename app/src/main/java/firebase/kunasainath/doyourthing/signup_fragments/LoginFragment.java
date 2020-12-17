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

public class LoginFragment extends Fragment implements View.OnClickListener{

    private EditText edtEmail, edtPassword;
    private TextView txtCanLogin;
    private Button btnLogin;

    public interface LoginInterface{
        public void wantToSignup();
    }

    private LoginInterface mInterface;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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

        mInterface = (LoginInterface) getActivity();
        txtCanLogin.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        edtEmail = view.findViewById(R.id.edt_email);
        edtPassword = view.findViewById(R.id.edt_password);
        txtCanLogin = view.findViewById(R.id.txt_no_account);
        btnLogin = view.findViewById(R.id.btn_login);
        return view;
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.txt_no_account:
                //move to signup fragment
                mInterface.wantToSignup();
                break;
            case R.id.btn_login:

                if(!isEmailValid(edtEmail.getText().toString())){
                    edtEmail.setError("The email you entered is invalid");
                    return;
                }

                if(edtPassword.getText().toString().length() < 6){
                    edtPassword.setError("The password must have atleast 6 characters");
                    return;
                }


                //LOGIN CODE

                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();

        }
    }
}