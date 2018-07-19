package com.gstedge.gstedgetask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class IFSCdetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ifscdetails);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView branch = findViewById(R.id.branch);
        TextView address = findViewById(R.id.address);
        TextView contact = findViewById(R.id.contact);
        TextView city = findViewById(R.id.city);
        TextView district = findViewById(R.id.district);
        TextView state = findViewById(R.id.state);
        TextView bank = findViewById(R.id.bank);
        TextView bankCode = findViewById(R.id.bank_code);
        TextView ifsc = findViewById(R.id.ifsc);

        Intent intent = getIntent();
        String json = intent.getStringExtra("response");
        try {
            JSONObject details = new JSONObject(json);
            branch.setText(details.getString("BRANCH"));
            address.setText(details.getString("ADDRESS"));
            contact.setText(details.getString("CONTACT"));
            city.setText(details.getString("CITY"));
            district.setText(details.getString("DISTRICT"));
            state.setText(details.getString("STATE"));
            bank.setText(details.getString("BANK"));
            bankCode.setText(details.getString("BANKCODE"));
            ifsc.setText(details.getString("IFSC"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
