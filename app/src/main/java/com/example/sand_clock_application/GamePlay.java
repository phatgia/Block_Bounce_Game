package com.example.sand_clock_application;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GamePlay extends AppCompatActivity implements GameListener {

    private Button btnBack, btnPause;
    private TextView txtLevel, txtPoint;
    private BrickBreakerView playGameView;
    private int currentLevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        txtLevel = findViewById(R.id.txtLevel);
        txtPoint = findViewById(R.id.textView2);
        playGameView = findViewById(R.id.game_view);
        playGameView.isInteractive = true;
        playGameView.setGameListener(this);

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
                playGameView.stopGame();
                showPauseMessage();
            }
        });

        playGameView.startGame();
    }

    @Override
    public void onScoreUpdated(final int newScore) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtPoint.setText("Point: " + newScore);
            }
        });
    }

    @Override
    public void onGameOver() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showFailDialog();
            }
        });
    }

    @Override
    public void onGameVictory() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showVictoryDialog();
            }
        });
    }

    public void showPauseMessage() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_pause);
        dialog.setCancelable(false);

        Button btnResume = dialog.findViewById(R.id.btnResume);
        Button btnBackHome = dialog.findViewById(R.id.btnBackHome);

        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                playGameView.startGame();
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

    private void showFailDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_fail);
        dialog.setCancelable(false);

        Button btnPlayAgain = dialog.findViewById(R.id.btnPlayAgain1);
        Button btnBackHome = dialog.findViewById(R.id.btnBackHomeFromFail);

        btnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                playGameView.resetGame();
                txtPoint.setText("Point: 0");
                playGameView.startGame();
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

    private void showVictoryDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_victory);
        dialog.setCancelable(false);

        Button btnPlayNewLevel = dialog.findViewById(R.id.btnPlayNewLevel);
        Button btnBackHome = dialog.findViewById(R.id.btnBackHomeFromVic);

        btnPlayNewLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                currentLevel++;
                txtLevel.setText("Level " + currentLevel);
                playGameView.resetGame();
                txtPoint.setText("Point: 0");
                playGameView.startGame();
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
    protected void onPause() {
        super.onPause();
        playGameView.stopGame();
        if (!isFinishing()) {
            showPauseMessage();
        }
    }
}
