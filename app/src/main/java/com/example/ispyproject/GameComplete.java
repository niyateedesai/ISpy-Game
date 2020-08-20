package com.example.ispyproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class GameComplete extends AppCompatActivity {

    TextView winner;
    Button returnHome;

    String lastGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_complete);

        winner = findViewById(R.id.textView_winner);
        returnHome = findViewById(R.id.button_return);

        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnHome = new Intent(GameComplete.this, HomePage.class);
                startActivity(returnHome);

                if(getIntent().getBooleanExtra("winner", false)){
                    FirebaseDatabase.getInstance().getReference("complete").child(getIntent().getStringExtra("player2")).removeValue();
                }
            }
        });


        if(getIntent().getBooleanExtra("winner", false)){
            FirebaseDatabase.getInstance().getReference("complete").child(getIntent().getStringExtra("player2")).setValue(getIntent().getStringExtra("winnerPlayer"));
            winner.setText("YOU WIN");
            lastGame = "win";
        }
        else{
            winner.setText("YOU LOSE");
            lastGame = "loss";
        }

        FirebaseDatabase.getInstance().getReference("games").child(getIntent().getStringExtra("player2")).removeValue();

        String status = lastGame;
        String fileName = "winStatus.json";

        try {
            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_PRIVATE));
            writer.write(status);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }





    }
}
