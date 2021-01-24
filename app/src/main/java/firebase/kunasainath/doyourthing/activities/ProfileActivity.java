package firebase.kunasainath.doyourthing.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import firebase.kunasainath.doyourthing.R;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ACTION_PICK_INTENT_KEY = 777 ;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 888;
    private ImageView imgProfilePic;
    private EditText edtUsername, edtAbout, edtFavorites;
    private ImageButton imgbtnEditUsername, imgbtnEditAbout, imgbtnEditFavorites;
    private Bitmap mBitmap;

    private DatabaseReference database;
    private FirebaseAuth mAuth;

    private StorageReference mStorageReference;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mStorageReference = FirebaseStorage.getInstance().getReference();

        setTitle("Your Profile");

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        imgProfilePic = findViewById(R.id.img_profile_pic);
        edtUsername = findViewById(R.id.edt_username);
        edtAbout = findViewById(R.id.edt_about);
        edtFavorites = findViewById(R.id.edt_favorites);
        imgbtnEditUsername = findViewById(R.id.imgbtn_edit_username);
        imgbtnEditAbout = findViewById(R.id.imgbtn_edit_about);
        imgbtnEditFavorites = findViewById(R.id.imgbtn_edit_favorites);

        imgProfilePic.setOnClickListener(this);
        imgbtnEditUsername.setOnClickListener(this);
        imgbtnEditAbout.setOnClickListener(this);
        imgbtnEditFavorites.setOnClickListener(this);

        checkForProfileDetailsAndFillThemInEditTexts();


    }

    private void checkForProfileDetailsAndFillThemInEditTexts() {

        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setTitle("Profile");
        progressDialog.setMessage("Updating your profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("Username")){
                    edtUsername.setText(snapshot.child("Username").getValue().toString());
                }

                if(snapshot.hasChild("About")){
                    edtAbout.setText(snapshot.child("About").getValue().toString());
                }

                if(snapshot.hasChild("Favorites")){
                    edtFavorites.setText(snapshot.child("Favorites").getValue().toString());
                }

                if(snapshot.hasChild("ProfilePicUrl")){
                    imgProfilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(ProfileActivity.this).load(snapshot.child("ProfilePicUrl").getValue().toString()).into(imgProfilePic);
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        animateTransition();
    }

    public void animateTransition(){
        overridePendingTransition(R.anim.hold_animation, R.anim.activity_transition_animation);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.imgbtn_edit_username:
                updateProfile(edtUsername.getText().toString(), "Username");
                break;
            case R.id.imgbtn_edit_about:
                updateProfile(edtAbout.getText().toString(), "About");
                break;
            case R.id.imgbtn_edit_favorites:
                updateProfile(edtFavorites.getText().toString(), "Favorites");
                break;
            case R.id.img_profile_pic:
                selectImage();
                break;
        }
    }

    private void updateProfile(String data, String field){
        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setTitle("Profile update");
        progressDialog.setMessage("Updating your profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        switch(field){
            case "Username":
                edtUsername.setText(data);
                database.child("Username").setValue(data);
                progressDialog.dismiss();
                break;
            case "About":
                edtAbout.setText(data);
                database.child("About").setValue(data);
                progressDialog.dismiss();
                break;
            case "Favorites":
                edtFavorites.setText(data);
                database.child("Favorites").setValue(data);
                progressDialog.dismiss();
                break;
        }
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), field + " updated.", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
    }

    private void selectImage(){
        if(Build.VERSION.SDK_INT <= 23){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, ACTION_PICK_INTENT_KEY);
        }else{
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, ACTION_PICK_INTENT_KEY);
            }else{
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {

            if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE && permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            }

        }catch (Exception e){}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ACTION_PICK_INTENT_KEY && resultCode == Activity.RESULT_OK && data!=null){
            Uri uri = data.getData();
            try {

                ProgressDialog dialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                dialog.setTitle("Image upload");
                dialog.setMessage("Your image is being uploaded...");
                dialog.setCancelable(false);
                dialog.show();

                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imgProfilePic.setImageBitmap(mBitmap);
                imgProfilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);

                UUID randomUUID = UUID.randomUUID();
                String imageName = randomUUID.toString();
                StorageReference reference = mStorageReference.child("images").child(imageName+".jpg");


                // Get the data from an ImageView as bytes
                imgProfilePic.setDrawingCacheEnabled(true);
                imgProfilePic.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imgProfilePic.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteData = baos.toByteArray();

                UploadTask uploadTask = reference.putBytes(byteData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), exception.getMessage(), Snackbar.LENGTH_INDEFINITE);
                        snackbar.show();
                        snackbar.setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        dialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                imageUrl = task.getResult().toString();

                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ProfilePicUrl").setValue(imageUrl);

                                dialog.dismiss();
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Image upload is successful.", Snackbar.LENGTH_INDEFINITE);
                                snackbar.show();
                                snackbar.setAction("Ok", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        snackbar.dismiss();
                                    }
                                });

                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("online");
    }

    @Override
    protected void onPause() {
        super.onPause();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("offline");
    }
}