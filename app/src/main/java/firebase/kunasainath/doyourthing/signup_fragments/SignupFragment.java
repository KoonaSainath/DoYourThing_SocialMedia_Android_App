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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import firebase.kunasainath.doyourthing.R;
import firebase.kunasainath.doyourthing.activities.MainActivity;

public class SignupFragment extends Fragment implements View.OnClickListener{

    private EditText edtEmail, edtPassword, edtUsername;
    private TextView txtCanLogin;
    private Button btnSignUp;

    private FirebaseAuth mAuth;

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

        mAuth = FirebaseAuth.getInstance();

        mInterface = (SignupInterface) getActivity();
        txtCanLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        edtUsername.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == MotionEvent.ACTION_DOWN){
                    onClick(btnSignUp);
                }
                return true;
            }
        });
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

                ProgressDialog dialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                dialog.setTitle("Sign up");
                dialog.setMessage("Creating your account...");
                dialog.setCancelable(false);
                dialog.show();

                mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString() , edtPassword.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(), "Sign up successful. " + "\n  WELCOME " + edtUsername.getText().toString(), Toast.LENGTH_LONG).show();
                                    saveUsernameToFirebase();
                                    startActivity(new Intent(getActivity(), MainActivity.class));
                                    getActivity().finish();
                                    dialog.dismiss();
                                }else{
                                    Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });

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

    private void saveUsernameToFirebase(){
        String username = edtUsername.getText().toString();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").setValue(username);
    }
}