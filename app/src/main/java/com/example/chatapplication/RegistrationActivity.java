package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    TextView signin_txt;
    CircleImageView profile_image;
    EditText fullName;
    EditText email;
    EditText pass;
    EditText confirmPass;
    Button signUp;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    Uri imageUri;
    String imageURI;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading, Please wait...");
        progressDialog.setCancelable(false);

        signin_txt = (TextView) findViewById(R.id.SignIn);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        fullName = (EditText) findViewById(R.id.fullName);
        email = (EditText) findViewById(R.id.signupEmail);
        pass = (EditText) findViewById(R.id.signupPassword);
        confirmPass = (EditText) findViewById(R.id.confirmPassword);
        signUp = (Button) findViewById(R.id.signUpButton);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                String nameStr = fullName.getText().toString();
                String emailStr = email.getText().toString();
                String passStr = pass.getText().toString();
                String confirmPassStr = confirmPass.getText().toString();
                String status = "Hey there, I am using chat app!";


                if(TextUtils.isEmpty(nameStr) || TextUtils.isEmpty(emailStr) || TextUtils.isEmpty(passStr) || TextUtils.isEmpty(confirmPassStr))
                {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Please Enter valid data", Toast.LENGTH_SHORT).show();
                }
                else if(!emailStr.matches(emailPattern))
                {
                    progressDialog.dismiss();
                    email.setError("Please Enter Valid Email!");
                }
                else if(!passStr.equals(confirmPassStr))
                {
                    progressDialog.dismiss();
                    confirmPass.setError("Enter correct password");
                    Toast.makeText(RegistrationActivity.this, "Enter Correct password", Toast.LENGTH_SHORT).show();
                }
                else if(pass.length()<6)
                {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Password must have atleast 6 characters", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    auth.createUserWithEmailAndPassword(emailStr, passStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
                                StorageReference storageReference = storage.getReference().child("Upload").child(auth.getUid());

                                if(imageUri != null)
                                {
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful())
                                            {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI = uri.toString();
                                                        Users user = new Users(auth.getUid(), nameStr, emailStr, imageURI, status);
                                                        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                if(task.isSuccessful()) {
                                                                    progressDialog.dismiss();
                                                                    Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                                else {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(RegistrationActivity.this, "Error in adding data to database", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    String status = "Hey there, I am using chat app!";
                                    imageURI = "https://firebasestorage.googleapis.com/v0/b/mini-project--ii.appspot.com/o/displaypicture.png?alt=media&token=c371684a-8c1b-4988-b2e7-251cac680fc5";
                                    Users user = new Users(auth.getUid(), nameStr, emailStr, imageURI, status);
                                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                                finish();
                                            }
                                            else {
                                                progressDialog.dismiss();
                                                Toast.makeText(RegistrationActivity.this, "Error in adding data to database", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(RegistrationActivity.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        signin_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10)
        {
            if(data != null)
            {
                imageUri = data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
}