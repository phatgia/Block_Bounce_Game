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

        if (ball.x + ball.radius >= x && ball.x - ball.radius <= x + width
                && ball.y + ball.radius >= y && ball.y - ball.radius <= y + height) {
            isVisible = false;
            return true;
        }
        return false;
    }
}
