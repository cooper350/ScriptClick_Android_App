package com.example.mainscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.FragmentTransaction;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private Button createAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        login = findViewById(R.id.loginBtn);
        createAcc = findViewById(R.id.acctBtn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAcc.setVisibility(View.INVISIBLE); // make the create account button invisible
                login.setVisibility(View.INVISIBLE); // make the login button invisible
                ImageView scriptClickIcon = findViewById(R.id.imageTS);
                scriptClickIcon.setVisibility(View.GONE);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new Login());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}