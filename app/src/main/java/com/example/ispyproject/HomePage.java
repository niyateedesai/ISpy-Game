package com.example.ispyproject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    TextView userInfo, winStatus;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseInvitation;
    private DatabaseReference mDatabaseGame;
    Button signOutButton, inviteButton;
    EditText inviteText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseInvitation = FirebaseDatabase.getInstance().getReference("invitations");
        mDatabaseGame = FirebaseDatabase.getInstance().getReference("games");


        userInfo = findViewById(R.id.id_userInfo);
        signOutButton = findViewById(R.id.id_signOutButton);
        inviteButton = findViewById(R.id.id_inviteButton);
        inviteText = findViewById(R.id.editText);
        winStatus = findViewById(R.id.winStatus);


        String input = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("winStatus.json")));
            input = reader.readLine();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        winStatus.setText(input);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent signInRegister = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(signInRegister);
            }
        });

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Invitation invitation = new Invitation(FirebaseAuth.getInstance().getCurrentUser().getUid(), inviteText.getText().toString(), userInfo.getText().toString());

                FirebaseDatabase.getInstance().getReference("invitations").child(inviteText.getText().toString()).setValue(invitation).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        } else {
                            Toast.makeText(HomePage.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userInfo.setText(dataSnapshot.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ValueEventListener gameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.child("player2").getValue().toString().equals(userInfo.getText())) {
                        Intent intentToStartGame = new Intent(HomePage.this, GameRoom.class);
                        intentToStartGame.putExtra("player2", userInfo.getText().toString());
                        intentToStartGame.putExtra("username", userInfo.getText().toString());
                        startActivity(intentToStartGame);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabaseGame.addValueEventListener(gameListener);


        ValueEventListener inviteListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
              //      Toast.makeText(getApplicationContext(), dataSnapshot.child("tee")., Toast.LENGTH_LONG).show();
                }
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.child("inviteeUsername").getValue().toString().equals(userInfo.getText())) {


                        final String inviter = ds.child("inviterUsername").getValue().toString();

                        AlertDialog.Builder alert = new AlertDialog.Builder(HomePage.this);
                        alert.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Game game = new Game(userInfo.getText().toString(), inviter);

                                FirebaseDatabase.getInstance().getReference("games").child(inviter).setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Intent intentToStartGame = new Intent(HomePage.this, GameRoom.class);
                                            intentToStartGame.putExtra("player2", inviter);
                                            intentToStartGame.putExtra("username", userInfo.getText().toString());
                                            startActivity(intentToStartGame);

                                        } else {
                                            Toast.makeText(HomePage.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }
                        });

                        alert.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        alert.setTitle("Invitation!");
                        alert.setMessage(inviter + " wants to play with you!");
                        AlertDialog myAlert = alert.create();
                        myAlert.show();

                        FirebaseDatabase.getInstance().getReference("invitations").child(userInfo.getText().toString()).removeValue();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mDatabaseInvitation.addValueEventListener(inviteListener);


    }

}
