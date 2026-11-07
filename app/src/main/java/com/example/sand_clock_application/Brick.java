package com.example.sand_clock_application;

public class Brick {
    public float x, y;
    public float width, height;
    public boolean isVisible = true;
    public int color;

    public Brick(float x, float y, float width, float height, int color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public boolean checkCollision(Ball ball) {
        if (!isVisible) {
            return false;
        }

        float nearestX = Math.max(x, Math.min(ball.x, x + width));
        float nearestY = Math.max(y, Math.min(ball.y, y + height));
        float dx = ball.x - nearestX;
        float dy = ball.y - nearestY;

        if (dx * dx + dy * dy <= ball.radius * ball.radius) {
            isVisible = false;
            return true;
        }
        return false;
    }
}
