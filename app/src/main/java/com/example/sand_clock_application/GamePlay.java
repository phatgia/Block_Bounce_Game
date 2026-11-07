package com.example.sand_clock_application;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GamePlay extends AppCompatActivity implements GameListener {
    private TextView txtLevel, txtPoint;
    private BrickBreakerView playGameView;
    private int currentLevel = 1;
    private boolean roundFinished = false;
    private boolean pauseWhenReturning = false;
    private Dialog activeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        txtLevel = findViewById(R.id.txtLevel);
        txtPoint = findViewById(R.id.textView2);
        playGameView = findViewById(R.id.game_view);
        playGameView.setInteractive(true);
        playGameView.setGameListener(this);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> finish());

        Button btnPause = findViewById(R.id.btnPause);
        btnPause.setOnClickListener(view -> {
            playGameView.stopGame();
            showPauseMessage();
        });

        int selectedLevel = getIntent().getIntExtra("SELECTED_LEVEL", 1);
        startLevel(selectedLevel, true);
    }

    private void startLevel(int level, boolean resetScore) {
        currentLevel = Math.max(1, Math.min(3, level));
        roundFinished = false;
        pauseWhenReturning = false;
        txtLevel.setText("Level " + currentLevel);
        if (resetScore) {
            playGameView.resetScore();
        }
        playGameView.loadLevel(currentLevel);
        playGameView.startGame();
    }

    @Override
    public void onScoreUpdated(int newScore) {
        runOnUiThread(() -> txtPoint.setText("Point: " + newScore));
    }

    @Override
    public void onGameOver() {
        runOnUiThread(() -> {
            if (isFinishing() || roundFinished) {
                return;
            }
            roundFinished = true;
            showFailDialog();
        });
    }

    @Override
    public void onGameVictory() {
        runOnUiThread(() -> {
            if (isFinishing() || roundFinished) {
                return;
            }
            roundFinished = true;
            showVictoryDialog();
        });
    }

    private void presentDialog(Dialog dialog) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        if (activeDialog != null && activeDialog.isShowing()) {
            return;
        }
        activeDialog = dialog;
        dialog.setCancelable(false);
        dialog.setOnDismissListener(d -> {
            if (activeDialog == dialog) {
                activeDialog = null;
            }
        });
        dialog.show();
    }

    public void showPauseMessage() {
        if (roundFinished) {
            return;
        }
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_pause);

        Button btnResume = dialog.findViewById(R.id.btnResume);
        Button btnBackHome = dialog.findViewById(R.id.btnBackHome);
        btnResume.setOnClickListener(view -> {
            dialog.dismiss();
            playGameView.startGame();
        });
        btnBackHome.setOnClickListener(view -> {
            dialog.dismiss();
            goHome();
        });
        presentDialog(dialog);
    }

    private void showFailDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_fail);
        Button btnPlayAgain = dialog.findViewById(R.id.btnPlayAgain1);
        Button btnBackHome = dialog.findViewById(R.id.btnBackHomeFromFail);

        btnPlayAgain.setOnClickListener(view -> {
            dialog.dismiss();
            // Replay the level that the player just lost.
            startLevel(currentLevel, true);
        });
        btnBackHome.setOnClickListener(view -> {
            dialog.dismiss();
            goHome();
        });
        presentDialog(dialog);
    }

    private void showVictoryDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_victory);
        Button btnPlayNewLevel = dialog.findViewById(R.id.btnPlayNewLevel);
        Button btnBackHome = dialog.findViewById(R.id.btnBackHomeFromVic);

        if (currentLevel == 3) {
            btnPlayNewLevel.setText("Chơi lại từ Level 1");
        }
        btnPlayNewLevel.setOnClickListener(view -> {
            dialog.dismiss();
            if (currentLevel < 3) {
                // Keep cumulative points when advancing to the next level.
                startLevel(currentLevel + 1, false);
            } else {
                startLevel(1, true);
            }
        });
        btnBackHome.setOnClickListener(view -> {
            dialog.dismiss();
            goHome();
        });
        presentDialog(dialog);
    }

    private void goHome() {
        playGameView.stopGame();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        if (playGameView != null) {
            boolean wasRunning = playGameView.isGameRunning();
            playGameView.stopGame();
            if (wasRunning && !isFinishing() && !isChangingConfigurations() && !roundFinished) {
                pauseWhenReturning = true;
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pauseWhenReturning) {
            pauseWhenReturning = false;
            if (!roundFinished && (activeDialog == null || !activeDialog.isShowing())) {
                showPauseMessage();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (playGameView != null) {
            playGameView.stopGame();
            playGameView.setGameListener(null);
        }
        if (activeDialog != null) {
            activeDialog.dismiss();
            activeDialog = null;
        }
        super.onDestroy();
    }
}
