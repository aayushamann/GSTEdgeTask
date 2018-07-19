package com.gstedge.gstedgetask;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private EditText usernameText;
    private EditText dobText;
    private EditText phoneText;
    private EditText passwordText;
    private EditText confirmPasswordText;

    private Pattern pattern;
    private Matcher matcher;

    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.sign_up_progress_bar);

        usernameText = findViewById(R.id.sign_up_username);
        dobText = findViewById(R.id.sign_up_dob);
        phoneText = findViewById(R.id.sign_up_phone);
        passwordText = findViewById(R.id.sign_up_password);
        confirmPasswordText = findViewById(R.id.sign_up_confirm_password);

        dobText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SignUpActivity.this, date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public void onOpenSignInButton(View view) {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
    }

    public void onSignupButton(View view) {
        final String username = usernameText.getText().toString();
        final String dob = dobText.getText().toString();
        final String phone = phoneText.getText().toString();
        String password = passwordText.getText().toString();
        String confirmPassword = confirmPasswordText.getText().toString();

        if (validateUsername(username) && validatePassword(password)) {
            String email = username + "@saluchan.com";
            if (isInternetConnected()) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    UserProfile userProfile = new UserProfile(username, dob, phone);
                                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                    database.child("user").child(user.getUid()).setValue(userProfile);
                                    Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            } else {
                Toast.makeText(SignUpActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!validateUsername(username)) {
                usernameText.setError("Invalid");
                Toast.makeText(SignUpActivity.this, "Username should be alphanumeric, max-length=20, min-length=8, " +
                        "no white spaces, should contain at least one alphabet and one number", Toast.LENGTH_SHORT).show();
            }
            if (!validatePassword(password)) {
                passwordText.setError("Invalid");
                Toast.makeText(SignUpActivity.this,
                        "Password must be alphanumeric, max-length=15, min-length=8", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this,
                        "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        }
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }
    };

    private void updateDateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dobText.setText(sdf.format(myCalendar.getTime()));
    }

    public boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean validateUsername(String username) {
        String USERNAME_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$";
        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public boolean validatePassword(String password) {
        String PASSWORD_PATTERN = "^[A-za-z0-9]{8,15}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
