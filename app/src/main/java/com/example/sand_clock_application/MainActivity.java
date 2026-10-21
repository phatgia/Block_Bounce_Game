package com.example.sand_clock_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    public static int REQUEST_CODE_PLAY= 1000;
    BrickBreakerView homeGameView;
    Button btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeGameView = findViewById(R.id.game_view);
        homeGameView.isInteractive = false;
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_to_play = new Intent(MainActivity.this, GamePlay.class);
                startActivityForResult(intent_to_play, REQUEST_CODE_PLAY);
            }
        });
    }
}