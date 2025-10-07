package com.example.test.ui.login;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.test.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView roleText = findViewById(R.id.roleText);

        // Receive data from LoginActivity
        String accountType = getIntent().getStringExtra("accountType");
        String username = getIntent().getStringExtra("username");

        if (accountType != null && username != null) {
            roleText.setText("Welcome, " + accountType + " " + username + "!");
        } else if (accountType != null) {
            roleText.setText("Welcome, " + accountType + "!");
        } else {
            roleText.setText("Welcome!");
        }
    }
}
