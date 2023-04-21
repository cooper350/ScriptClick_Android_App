package com.example.mainscreen.scriptclick;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.scriptclick.ui.login.Login;
import com.example.scriptclick.ui.login.RegisterActivity;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private Button createAcc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login=findViewById(R.id.loginBtn);
        createAcc=findViewById(R.id.acctBtn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, Login.class); //Login class represents the activity you want to switch to while Main represents the current activity
                startActivity(intent);
            }
        });
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class); //Login class represents the activity you want to switch to while Main represents the current activity
                startActivity(intent);
            }
        });

    }

}