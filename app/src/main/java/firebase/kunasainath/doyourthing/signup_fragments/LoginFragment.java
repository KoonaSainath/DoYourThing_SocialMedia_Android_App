package firebase.kunasainath.doyourthing.signup_fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.activities.MainActivity;

public class LoginFragment extends Fragment implements View.OnClickListener{

    private EditText edtEmail, edtPassword;
    private TextView txtCanLogin;
    private Button btnLogin;

    private FirebaseAuth mAuth;

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

        mAuth = FirebaseAuth.getInstance();

        mInterface = (LoginInterface) getActivity();
        txtCanLogin.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        edtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == MotionEvent.ACTION_DOWN){
                    onClick(btnLogin);
                }
                return true;
            }
        });
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

                ProgressDialog dialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                dialog.setTitle("Log in");
                dialog.setMessage("Logging you in. Please wait...");
                dialog.show();

                mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(), "Log in successful" , Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getActivity(), MainActivity.class));
                                    getActivity().finish();
                                    dialog.dismiss();
                                }else{
                                    Toast.makeText(getActivity(), task.getException().toString() , Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }
                        });
        }
    }
}