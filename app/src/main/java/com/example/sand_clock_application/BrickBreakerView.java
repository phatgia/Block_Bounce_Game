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
    private static final int ROWS = 6;
    private static final int COLS = 7;

    private Paint paintBg, paintPaddle, paintBall, paintBrick;
    private GameListener gameListener;
    private int score = 0;
    private int currentLevel = 1;
    private float currentSpeedMultiplier = 1f;
    private Ball ball;
    private boolean isInteractive = true;
    private boolean isRunning = false;
    private long lastFrameTimeNanos = 0L;

    private final Brick[][] bricks = new Brick[ROWS][COLS];
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
    private float paddleX, paddleY;
    private float paddleWidth = 180f, paddleHeight = 24f;

    private final Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            if (!isRunning) {
                return;
            }

            if (lastFrameTimeNanos == 0L) {
                lastFrameTimeNanos = frameTimeNanos;
            }
            float deltaSeconds = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000_000f;
            lastFrameTimeNanos = frameTimeNanos;
            deltaSeconds = Math.min(deltaSeconds, 0.033f);

            if (ball != null) {
                ball.move(deltaSeconds);
                checkCollision();
            }
            invalidate();
            if (isRunning) {
                Choreographer.getInstance().postFrameCallback(this);
            }
        }
    };

    public BrickBreakerView(Context context) {
        super(context);
        init();
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
    }

    public void setGameListener(GameListener listener) {
        gameListener = listener;
    }

    public void setInteractive(boolean interactive) {
        isInteractive = interactive;
    }

    public boolean isGameRunning() {
        return isRunning;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w <= 0 || h <= 0) {
            return;
        }
        padding = Math.min(36f, w * 0.06f);
        gap = Math.min(16f, w * 0.02f);
        brickWidth = Math.max(1f, (w - 2f * padding - (COLS - 1) * gap) / COLS);
        brickHeight = Math.min(48f, Math.max(24f, h * 0.05f));

        paddleWidth = Math.min(w, Math.min(180f, Math.max(80f, w * 0.35f)));
        paddleX = w / 2f - paddleWidth / 2f;
        paddleY = h - Math.max(60f, Math.min(136f, h * 0.15f));

        setupBricks();
        resetBall();
    }

    private void setupBricks() {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                boolean visible;
                if (currentLevel == 1) {
                    visible = r < 4;
                } else if (currentLevel == 2) {
                    visible = (r + c) % 2 == 0;
                } else {
                    visible = c != 3 && r < 5;
                }

                Brick brick = new Brick(
                        padding + c * (brickWidth + gap),
                        padding + 40f + r * (brickHeight + gap),
                        brickWidth,
                        brickHeight,
                        parsedBrickColors[(r + c) % parsedBrickColors.length]
                );
                brick.isVisible = visible;
                bricks[r][c] = brick;
            }
        }
    }

    private void resetBall() {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        float radius = Math.max(8f, Math.min(14f, getWidth() * 0.025f));
        ball = new Ball(getWidth() / 2f, paddleY - radius - 40f, radius);
        ball.resetRandomDirection(currentSpeedMultiplier);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), 48f, 48f, paintBg);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Brick brick = bricks[r][c];
                if (brick != null && brick.isVisible) {
                    paintBrick.setColor(brick.color);
                    canvas.drawRoundRect(
                            brick.x, brick.y,
                            brick.x + brick.width, brick.y + brick.height,
                            12f, 12f, paintBrick
                    );
                }
            }
        }

        canvas.drawRoundRect(paddleX, paddleY, paddleX + paddleWidth,
                paddleY + paddleHeight, 12f, 12f, paintPaddle);
        if (ball != null) {
            canvas.drawCircle(ball.x, ball.y, ball.radius, paintBall);
        }
    }

    private void checkCollision() {
        if (ball == null) {
            return;
        }
        if (ball.x - ball.radius < 0f) {
            ball.x = ball.radius;
            ball.speedX = Math.abs(ball.speedX);
        } else if (ball.x + ball.radius > getWidth()) {
            ball.x = getWidth() - ball.radius;
            ball.speedX = -Math.abs(ball.speedX);
        }

        if (ball.y - ball.radius < 0f) {
            ball.y = ball.radius;
            ball.speedY = Math.abs(ball.speedY);
        }

        if (ball.speedY > 0f
                && ball.y + ball.radius >= paddleY
                && ball.y - ball.radius <= paddleY + paddleHeight
                && ball.x + ball.radius >= paddleX
                && ball.x - ball.radius <= paddleX + paddleWidth) {
            ball.y = paddleY - ball.radius;
            ball.speedY = -Math.abs(ball.speedY);
        }

        if (ball.y - ball.radius > getHeight()) {
            stopGame();
            if (gameListener != null) {
                gameListener.onGameOver();
            }
            return;
        }

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Brick brick = bricks[r][c];
                if (brick != null && brick.checkCollision(ball)) {
                    float overlapLeft = ball.x + ball.radius - brick.x;
                    float overlapRight = brick.x + brick.width - (ball.x - ball.radius);
                    float overlapTop = ball.y + ball.radius - brick.y;
                    float overlapBottom = brick.y + brick.height - (ball.y - ball.radius);
                    float horizontalOverlap = Math.min(overlapLeft, overlapRight);
                    float verticalOverlap = Math.min(overlapTop, overlapBottom);

                    if (horizontalOverlap < verticalOverlap) {
                        ball.speedX = -ball.speedX;
                        if (ball.x < brick.x + brick.width / 2f) {
                            ball.x = brick.x - ball.radius;
                        } else {
                            ball.x = brick.x + brick.width + ball.radius;
                        }
                    } else {
                        ball.speedY = -ball.speedY;
                        if (ball.y < brick.y + brick.height / 2f) {
                            ball.y = brick.y - ball.radius;
                        } else {
                            ball.y = brick.y + brick.height + ball.radius;
                        }
                    }

                    score += 10;
                    if (gameListener != null) {
                        gameListener.onScoreUpdated(score);
                    }
                    if (!hasRemainingBricks()) {
                        stopGame();
                        if (gameListener != null) {
                            gameListener.onGameVictory();
                        }
                    }
                    return;
                }
            }
        }
    }

    private boolean hasRemainingBricks() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (bricks[r][c] != null && bricks[r][c].isVisible) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isInteractive) {
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                paddleX = Math.max(0f,
                        Math.min(getWidth() - paddleWidth, event.getX() - paddleWidth / 2f));
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                performClick();
                return true;
            case MotionEvent.ACTION_CANCEL:
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public void startGame() {
        if (!isRunning) {
            isRunning = true;
            lastFrameTimeNanos = 0L;
            Choreographer.getInstance().removeFrameCallback(frameCallback);
            Choreographer.getInstance().postFrameCallback(frameCallback);
        }
    }

    public void stopGame() {
        isRunning = false;
        lastFrameTimeNanos = 0L;
        Choreographer.getInstance().removeFrameCallback(frameCallback);
    }

    public void loadLevel(int level) {
        stopGame();
        currentLevel = Math.max(1, Math.min(3, level));
        if (currentLevel == 1) {
            currentSpeedMultiplier = 1.0f;
        } else if (currentLevel == 2) {
            currentSpeedMultiplier = 1.3f;
        } else {
            currentSpeedMultiplier = 1.6f;
        }
        if (getWidth() > 0 && getHeight() > 0) {
            paddleX = getWidth() / 2f - paddleWidth / 2f;
            setupBricks();
            resetBall();
        }
        invalidate();
    }

    public void resetScore() {
        score = 0;
        if (gameListener != null) {
            gameListener.onScoreUpdated(score);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopGame();
        super.onDetachedFromWindow();
    }
}
