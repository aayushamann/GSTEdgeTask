package com.gstedge.gstedgetask;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DashboardActivity extends AppCompatActivity {

    private EditText ifscText;
    private ProgressBar progressBar;

    private Pattern pattern;
    private Matcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ifscText = findViewById(R.id.dashboard_ifsc);
        TextView message = findViewById(R.id.dashboard_message);
        progressBar = findViewById(R.id.dashboard_progress_bar);
        ImageView logout = findViewById(R.id.logout_button);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String welcomeMsg = "Welcome, " + username;
        message.setText(welcomeMsg);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DashboardActivity.this, SignInActivity.class));
                finish();
            }
        });
    }

    public void onSubmitIFSC(View view) {
        String ifsc = ifscText.getText().toString();
        String url = "https://ifsc.razorpay.com/" + ifsc;

        if (!validateIFSC(ifsc)) {
            ifscText.setError("Invalid");
            Toast.makeText(DashboardActivity.this,
                    "IFSc must be of 11 characters and alphabets and digits", Toast.LENGTH_SHORT).show();
        } else {
            if (isInternetConnected()) {
                progressBar.setVisibility(View.VISIBLE);
                RequestQueue queue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("Not Found")) {
                                    Toast.makeText(DashboardActivity.this,
                                            "IFSC Not Found! Try again.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(DashboardActivity.this, IFSCdetailsActivity.class);
                                    intent.putExtra("response", response);
                                    startActivity(intent);
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DashboardActivity.this, "Not Found! Try again.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                queue.add(stringRequest);
            } else {
                Toast.makeText(DashboardActivity.this, "Connection Problem", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean validateIFSC(String ifsc) {
        String IFSC_PATTERN = "^[A-Z0-9]{11}$";
        pattern = Pattern.compile(IFSC_PATTERN);
        matcher = pattern.matcher(ifsc);
        return matcher.matches();
    }

    public boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
