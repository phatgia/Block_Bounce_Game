package com.example.sand_clock_application;

import java.util.Random;

public class Ball {
    public float x, y;
    public float radius;
    // Speed values represent pixels per frame at the reference rate of 60 FPS.
    public float speedX, speedY;

    private static final Random RANDOM = new Random();

    public Ball(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        resetRandomDirection(1.0f);
    }

    public void resetRandomDirection(float multiplier) {
        speedX = (RANDOM.nextBoolean() ? 1f : -1f)
                * (6f + RANDOM.nextFloat() * 6f) * multiplier;
        speedY = -12f * multiplier;
    }

    public void move(float deltaSeconds) {
        float frameScale = deltaSeconds * 60f;
        x += speedX * frameScale;
        y += speedY * frameScale;
    }
}
