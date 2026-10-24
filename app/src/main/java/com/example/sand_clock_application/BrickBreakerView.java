package com.example.sand_clock_application;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;

public class BrickBreakerView extends View {
    private Paint paintBg, paintPaddle, paintBall, paintBrick;

    private GameListener gameListener;
    private int score = 0;
    private final int ROWS = 6;
    private final int COLS = 7;
    private Ball ball;
    boolean isInteractive = true;

    public boolean[][] bricks = new boolean[ROWS][COLS];

    public void setGameListener(GameListener listener) {
        this.gameListener = listener;
    }

    private final String[] BRICK_COLORS = {
            "#F17D6E",
            "#F4CF57",
            "#58D68D",
            "#6C9EEB",
            "#A569BD"
    };

    private int[] parsedBrickColors;

    private float brickWidth, brickHeight;
    private float padding = 36f;
    private float gap = 16f;

    public float paddleX, paddleY;
    public float paddleWidth = 180f, paddleHeight = 24f;

    private boolean isRunning = false;

    private Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            if (!isRunning) {
                return;
            }
            if (ball != null) {
                ball.move();
                checkCollision();
            }
            invalidate();
            Choreographer.getInstance().postFrameCallback(this);
        }
    };

    public void checkCollision() {
        if (ball == null) {
            return;
        }

        if (ball.x - ball.radius < 0) {
            ball.x = ball.radius;
            ball.speedX = Math.abs(ball.speedX);
        } else if (ball.x + ball.radius >= getWidth()) {
            ball.x = getWidth() - ball.radius;
            ball.speedX = -Math.abs(ball.speedX);
        }

        if (ball.y - ball.radius < 0) {
            ball.y = ball.radius;
            ball.speedY = Math.abs(ball.speedY);
        }

        if (ball.y + ball.radius >= paddleY && ball.y - ball.radius <= paddleY + paddleHeight) {
            if (ball.x >= paddleX && ball.x <= paddleX + paddleWidth) {
                ball.y = paddleY - ball.radius;
                ball.speedY = -Math.abs(ball.speedY);
            }
        }

        if (ball.y - ball.radius > getHeight()) {
            stopGame();
            if (gameListener != null) {
                gameListener.onGameOver();
            }
            return;
        }

        float currentY = padding + 40f;
        boolean hasRemainingBricks = false;

        for (int r = 0; r < ROWS; r++) {
            float currentX = padding;
            for (int c = 0; c < COLS; c++) {
                if (bricks[r][c]) {
                    hasRemainingBricks = true;
                    if (ball.x + ball.radius >= currentX && ball.x - ball.radius <= currentX + brickWidth
                            && ball.y + ball.radius >= currentY && ball.y - ball.radius <= currentY + brickHeight) {
                        bricks[r][c] = false;
                        ball.speedY *= -1;

                        score += 10;
                        if (gameListener != null) {
                            gameListener.onScoreUpdated(score);
                        }
                        return;
                    }
                }
                currentX += brickWidth + gap;
            }
            currentY += brickHeight + gap;
        }

        if (!hasRemainingBricks) {
            stopGame();
            if (gameListener != null) {
                gameListener.onGameVictory();
            }
        }
    }

    public BrickBreakerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paintBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBg.setColor(Color.parseColor("#151515"));
        paintBg.setStyle(Paint.Style.FILL);

        paintPaddle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPaddle.setColor(Color.parseColor("#E0FF31"));
        paintPaddle.setStyle(Paint.Style.FILL);

        paintBall = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBall.setColor(Color.WHITE);
        paintBall.setStyle(Paint.Style.FILL);

        paintBrick = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBrick.setStyle(Paint.Style.FILL);

        parsedBrickColors = new int[BRICK_COLORS.length];
        for (int i = 0; i < BRICK_COLORS.length; i++) {
            parsedBrickColors[i] = Color.parseColor(BRICK_COLORS[i]);
        }

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                bricks[r][c] = true;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float totalGapWidth = (COLS - 1) * gap;
        float totalPadding = padding * 2;
        brickWidth = (w - totalPadding - totalGapWidth) / COLS;
        brickHeight = 48f;

        paddleX = w / 2f - paddleWidth / 2f;
        paddleY = h - padding - 100f;

        ball = new Ball(w / 2f, paddleY - 14f - 40f, 14f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), 48f, 48f, paintBg);

        float currentY = padding + 40f;
        for (int r = 0; r < ROWS; r++) {
            float currentX = padding;
            for (int c = 0; c < COLS; c++) {
                if (bricks[r][c]) {
                    int colorIndex = (r + c) % parsedBrickColors.length;
                    paintBrick.setColor(parsedBrickColors[colorIndex]);

                    canvas.drawRoundRect(
                            currentX, currentY,
                            currentX + brickWidth, currentY + brickHeight,
                            12f, 12f, paintBrick
                    );
                }
                currentX += brickWidth + gap;
            }
            currentY += brickHeight + gap;
        }

        canvas.drawRoundRect(
                paddleX, paddleY,
                paddleX + paddleWidth, paddleY + paddleHeight,
                12f, 12f, paintPaddle
        );

        if (ball != null) {
            canvas.drawCircle(ball.x, ball.y, ball.radius, paintBall);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isInteractive) {
            return false;
        }
        float touchX = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                paddleX = touchX - (paddleWidth / 2f);

                if (paddleX < 0) {
                    paddleX = 0;
                }

                if (paddleX + paddleWidth > getWidth()) {
                    paddleX = getWidth() - paddleWidth;
                }

                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void startGame() {
        if (!isRunning) {
            isRunning = true;
            Choreographer.getInstance().removeFrameCallback(frameCallback);
            Choreographer.getInstance().postFrameCallback(frameCallback);
        }
    }

    public void stopGame() {
        isRunning = false;
        Choreographer.getInstance().removeFrameCallback(frameCallback);
    }

    public void resetGame() {
        stopGame();
        score = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                bricks[r][c] = true;
            }
        }
        if (getWidth() > 0 && getHeight() > 0) {
            paddleX = getWidth() / 2f - paddleWidth / 2f;
            paddleY = getHeight() - padding - 100f;
            if (ball != null) {
                ball.x = getWidth() / 2f;
                ball.y = paddleY - ball.radius - 40f;
                ball.resetRandomDirection();
            }
        }
        invalidate();
    }
}
