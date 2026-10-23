package com.example.sand_clock_application;

public interface GameListener {
    void onScoreUpdated(int newScore);
    void onGameOver();
    void onGameVictory();
}