package com.example.sand_clock_application;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GamePlay extends AppCompatActivity {

    Button btnBack, btnPause;
    BrickBreakerView playGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        playGameView = findViewById(R.id.game_view);
        playGameView.isInteractive = true;

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnPause = findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPauseMessage();
            }
        });
    }


    public void showPauseMessage(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_pause);

        dialog.setCancelable(false);

        Button btnResume = dialog.findViewById(R.id.btnResume);
        Button btnBackHome = dialog.findViewById(R.id.btnBackHome);


        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(!isFinishing()){
            showPauseMessage();
        }
    }
}