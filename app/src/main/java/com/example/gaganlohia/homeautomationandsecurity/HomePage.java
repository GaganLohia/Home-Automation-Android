package com.example.gaganlohia.homeautomationandsecurity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "Gagan", apiKey="";
    private LinearLayout  linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent in = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(in);
                    finish();
                }
            }

        };

        retrieveKey();

    }

    public void retrieveKey()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(mAuth.getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                apiKey = dataSnapshot.getValue(String.class);
                //Http task = new Http();
                //task.execute("https://api.thingspeak.com/update?api_key="+apiKey+"&field1=0");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    public void logOut(View v){

        AlertDialog.Builder adb = new AlertDialog.Builder(this);


        adb.setView(linearLayout);


        adb.setTitle("Are you sure you want to logout?");


        adb.setIcon(android.R.drawable.ic_dialog_alert);


        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                Intent in = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(in);
                finish();
            } });


        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            } });
        adb.show();
    }

    public class Http extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1)
                {
                    result += (char)data;
                    data = reader.read();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}

