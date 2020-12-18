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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import firebase.kunasainath.doyourthing.R;

public class PostUploadActivity extends AppCompatActivity implements View.OnClickListener{

    private String date, time;
    private ImageView imgPost;
    private EditText edtDescription;
    private Button btnUploadPost;

    private Bitmap mBitmap;

    private static final int ACTION_PICK_INTENT_KEY = 1000;
    private static final int EXTERNAL_STORAGE_REQUEST_KEY = 1001;

    private StorageReference mStorageReference;
    private String imageUrl;

    boolean imageUploaded = false;

    private HashMap<String, String> dataToUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upload);
        imageUploaded = false;

        setTitle("Upload a post");

        mStorageReference = FirebaseStorage.getInstance().getReference();

        imgPost = findViewById(R.id.img_post_image);
        edtDescription = findViewById(R.id.edt_description);
        btnUploadPost = findViewById(R.id.btn_upload_post);

        imgPost.setOnClickListener(this);
        btnUploadPost.setOnClickListener(this);

        String[] datetime = getDateAndTime();
        date = datetime[0];
        time = datetime[1];

        dataToUpload = new HashMap<>();
        dataToUpload.put("Date", date);
        dataToUpload.put("Time", time);

    }

    private String[] getDateAndTime(){
        Date date = new Date();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        String result[] = dateFormat.format(date).split(" ");

        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.hold_animation, R.anim.activity_transition_animation);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.img_post_image:
                selectImage();
                break;
            case R.id.btn_upload_post:

                ProgressDialog dialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                dialog.setCancelable(false);
                dialog.setTitle("Post upload");
                dialog.setMessage("Your post is uploading...");
                dialog.show();

                if(!imageUploaded){
                    Snackbar.make(findViewById(android.R.id.content), "Upload an image please", Snackbar.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }
                if(edtDescription.getText().toString().length() == 0){
                    edtDescription.setError("Enter some description");
                    dialog.dismiss();
                    return;
                }

                dataToUpload.put("Description", edtDescription.getText().toString());
                dataToUpload.put("UserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                dataToUpload.put("ImageUrl", imageUrl);

                FirebaseDatabase.getInstance().getReference()
                        .child("Posts")
                        .push()
                        .setValue(dataToUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Post uploaded successfully", Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                            snackbar.setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });
                            dialog.dismiss();
                        }else{
                            Snackbar.make(findViewById(android.R.id.content), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });




                break;
        }
    }

    private void selectImage(){
        if(Build.VERSION.SDK_INT <= 23){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, ACTION_PICK_INTENT_KEY);
        }else{
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST_KEY);
            }else{
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, ACTION_PICK_INTENT_KEY);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == EXTERNAL_STORAGE_REQUEST_KEY && permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ACTION_PICK_INTENT_KEY && resultCode == Activity.RESULT_OK && data != null){
            Uri uri = data.getData();
            try {

                ProgressDialog dialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                dialog.setCancelable(false);
                dialog.setTitle("Image upload");
                dialog.setMessage("Your image is uploading...");
                dialog.show();

                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imgPost.setImageBitmap(mBitmap);
                imgPost.setScaleType(ImageView.ScaleType.CENTER_CROP);


                UUID randomUUID = UUID.randomUUID();
                String imageName = randomUUID.toString();
                StorageReference reference = mStorageReference.child("images").child(imageName+".jpg");


                // Get the data from an ImageView as bytes
                imgPost.setDrawingCacheEnabled(true);
                imgPost.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imgPost.getDrawable()).getBitmap();
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
                                dialog.dismiss();
                            }
                        });
                        //dialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                imageUploaded = true;
                                imageUrl = task.getResult().toString();
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
}