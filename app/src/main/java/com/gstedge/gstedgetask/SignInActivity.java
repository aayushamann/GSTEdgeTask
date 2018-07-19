package com.gstedge.gstedgetask;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private String TAG = "SignInActivity";

    private EditText usernameText;
    private EditText passwordText;

    private Pattern pattern;
    private Matcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.sign_in_progress_bar);

        usernameText = findViewById(R.id.sign_in_username);
        passwordText = findViewById(R.id.sign_in_password);
    }

    public void onSignInButton(View view) {
        if (!isInternetConnected()) {
            Toast.makeText(SignInActivity.this, "Connection error",
                    Toast.LENGTH_SHORT).show();
        } else {
            final String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();
            String email = username + "@saluchan.com";

            if (validatePassword(password) && validateUsername(username)) {
                progressBar.setVisibility(View.VISIBLE);
                try {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent intent = new Intent(SignInActivity.this, DashboardActivity.class);
                                        intent.putExtra("username", username);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } catch (Exception e) {
                    Toast.makeText(SignInActivity.this, "Invalid Credentials.",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            } else {
                if (!validateUsername(username)) {
                    usernameText.setError("Invalid");
                    Toast.makeText(SignInActivity.this, "Username should be alphanumeric, max-length=20, min-length=8, " +
                            "no white spaces, should contain at least one alphabet and one number", Toast.LENGTH_SHORT).show();
                }
                if (!validatePassword(password)) {
                    passwordText.setError("Invalid");
                    Toast.makeText(SignInActivity.this,
                            "Password must be alphanumeric, max-length=15, min-length=8", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public boolean validateUsername(String username) {
        String USERNAME_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$";
        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public boolean validatePassword(String password) {
        String PASSWORD_PATTERN = "^[A-Za-z0-9]{8,15}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
