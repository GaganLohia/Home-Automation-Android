package com.example.gaganlohia.homeautomationandsecurity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.lang.Thread.sleep;

public class RegisterPage extends AppCompatActivity {

    private  EditText emailEditText,passwordEditText,apiKeyEditText;
    private FirebaseAuth mAuth;
    private String TAG = "Gagan";
    private ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);
        pBar = (ProgressBar) findViewById(R.id.pBar);
        apiKeyEditText = (EditText) findViewById(R.id.apiKey);
        mAuth = FirebaseAuth.getInstance();
    }

    public void toLoginPage(View v){
        Intent in = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(in);
        overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
        finish();
    }

    public void registerUser(View v) {

        pBar.setVisibility(View.VISIBLE);
        final String email = emailEditText.getText().toString(), password = passwordEditText.getText().toString(), apiKey = apiKeyEditText.getText().toString();

        if(mAuth!=null)
            mAuth.signOut();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterPage.this, R.string.register_auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(RegisterPage.this, R.string.register_successful,
                                    Toast.LENGTH_SHORT).show();
                        }
                        pBar.setVisibility(View.INVISIBLE);
                        // ...
                    }
                });
        try {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference(mAuth.getCurrentUser().getUid());

                                myRef.setValue(apiKey);
                                Intent in = new Intent(getApplicationContext(),HomePage.class);
                                startActivity(in);
                            }
                        }
                    });

        }
        catch (Exception e){
            Log.i(TAG,e.getMessage());
            e.printStackTrace();
        }

    }
}
