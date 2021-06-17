package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView signup;
    EditText email;
    EditText password;
    Button signIn;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth auth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading, Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();

        signup = (TextView) findViewById(R.id.signUpText);
        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
        signIn = (Button) findViewById(R.id.signInButton);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                String emailid = email.getText().toString();
                String pass = password.getText().toString();

                if(TextUtils.isEmpty(emailid) || TextUtils.isEmpty(pass)) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Please Enter the credentials!", Toast.LENGTH_SHORT).show();
                }
                else if(!emailid.matches(emailPattern)) {
                    progressDialog.dismiss();
                    email.setError("Invalid Email!");
                    Toast.makeText(LoginActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
                }
                else if(pass.length() < 6) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Password must be atlead 6 characters", Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.signInWithEmailAndPassword(emailid, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                            if(task.isSuccessful() == true)
                            {
                                progressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Error in login!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });

    }
}