package com.example.digitallibraryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    EditText editTextUsername,editTextPassword;
    Button buttonLogin;
    TextView textViewRegiistration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth=FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null)
        {
//            finish();
            startActivity(new Intent(this,Profile.class));
        }
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        editTextUsername=(EditText)findViewById(R.id.editTextEmail);
        buttonLogin=(Button)findViewById(R.id.buttonLogin);
        textViewRegiistration=(TextView)findViewById(R.id.textViewRegistration);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        textViewRegiistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRegistration();
            }
        });
    }

    private void userRegistration()
    {
        startActivity(new Intent(this,Registration.class));
        finish();

    }
    private void userLogin()
    {

        String username=editTextUsername.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Empty Field ",Toast.LENGTH_LONG).show();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    startActivity(new Intent(getApplicationContext(),Profile.class));
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Something Went Wrong!!!!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

