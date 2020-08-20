package com.example.ispyproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.util.ArrayList;
import java.util.List;


public class GameRoom extends AppCompatActivity {

    private CameraKitView cameraKitView;

    private DatabaseReference mDatabaseReference;

    Button takePictureButton;
    ArrayList<String> wordList;
    ArrayList<TextView> textViews;
    TextView word1, word2, word3, word4, word5, word6, word7, word8, word9;
    int score = 0;
    String player;
    String opponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);
        cameraKitView = findViewById(R.id.camera);
        takePictureButton = findViewById(R.id.id_button_takepicture);

        word1 = findViewById(R.id.textView);
        word2 = findViewById(R.id.textView2);
        word3 = findViewById(R.id.textView3);
        word4 = findViewById(R.id.textView4);
        word5 = findViewById(R.id.textView5);
        word6 = findViewById(R.id.textView6);
        word7 = findViewById(R.id.textView7);
        word8 = findViewById(R.id.textView8);
        word9 = findViewById(R.id.textView9);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.length);
                        getLabels(bitmap);

                    }
                });
            }
        });

        if(getIntent().getStringExtra("player2").equals(getIntent().getStringExtra("username"))){
            player = "player2";
            opponent = "player1";
        }
        else{
            player = "player1";
            opponent = "player2";
        }

        textViews = new ArrayList<>();
        wordList = new ArrayList<>();

        textViews.add(word1);
        textViews.add(word2);
        textViews.add(word3);
        textViews.add(word4);
        textViews.add(word5);
        textViews.add(word6);
        textViews.add(word7);
        textViews.add(word8);
        textViews.add(word9);


        ValueEventListener wordsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i = 0; i<9; i++){
                    textViews.get(i).setText(dataSnapshot.child("games").child(getIntent().getStringExtra("player2")).child("words").child(String.valueOf(i)).getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDatabaseReference.addListenerForSingleValueEvent(wordsListener);
        mDatabaseReference.removeEventListener(wordsListener);

        for(int i = 0; i<9; i++){
            wordList.add(textViews.get(i).getText().toString());
        }

        ValueEventListener winnerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.getValue().toString().equals(opponent)) {
                        Intent intentToEnd = new Intent(GameRoom.this, GameComplete.class);
                        intentToEnd.putExtra("winner", false);
                        intentToEnd.putExtra("username", getIntent().getStringExtra("username"));
                        intentToEnd.putExtra("player2", getIntent().getStringExtra("player2"));
                        startActivity(intentToEnd);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
                Log.d("TAG", databaseError.getMessage());
            }
        };


        FirebaseDatabase.getInstance().getReference("complete").addValueEventListener(winnerListener);

    }

    private void getLabels (Bitmap bitmap){
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        final FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();

        labeler.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {

                        for (FirebaseVisionImageLabel label: labels) {
                            String text = label.getText();
                            String entityId = label.getEntityId();
                            float confidence = label.getConfidence();

                            for(TextView textView: textViews){
                                if(confidence>0.5 && text.equals(textView.getText()) && textView.getCurrentTextColor()!=Color.BLACK){
                                    textView.setTextColor(Color.BLACK);
                                    score++;
                                    if(score == 9){
                                        finishGame();
                                    }
                                }
                            }

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });
    }

    public void finishGame(){
        Intent intentToEnd = new Intent(GameRoom.this, GameComplete.class);
        intentToEnd.putExtra("winner", true);
        intentToEnd.putExtra("winnerPlayer", player);
        intentToEnd.putExtra("username", getIntent().getStringExtra("username"));
        intentToEnd.putExtra("player2", getIntent().getStringExtra("player2"));
        startActivity(intentToEnd);

    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }
    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }
    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
