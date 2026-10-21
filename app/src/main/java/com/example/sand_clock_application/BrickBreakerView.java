package com.example.sand_clock_application;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BrickBreakerView extends View {
    private Paint paintBg, paintPaddle, paintBall, paintBrick;

    private final int ROWS = 6;
    private final int COLS = 7;

    boolean isInteractive = true;

    public boolean[][] bricks = new boolean[ROWS][COLS];

    // 5 Mã màu chuẩn xác từ bản thiết kế
    private final String[] BRICK_COLORS = {
            "#F17D6E", // Đỏ san hô
            "#F4CF57", // Vàng
            "#58D68D", // Xanh ngọc
            "#6C9EEB", // Xanh lam
            "#A569BD"  // Tím
    };

    private float brickWidth, brickHeight;
    private float padding = 36f;
    private float gap = 16f;

    public float paddleX, paddleY;
    public float paddleWidth = 180f, paddleHeight = 24f;

    public float ballX, ballY;
    public float ballRadius = 14f;

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

        // Đặt bóng nằm ngay trên thanh trượt
        ballX = w / 2f;
        ballY = paddleY - ballRadius - 40f;
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
                    int colorIndex = (r + c) % BRICK_COLORS.length;
                    paintBrick.setColor(Color.parseColor(BRICK_COLORS[colorIndex]));

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

        canvas.drawCircle(ballX, ballY, ballRadius, paintBall);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isInteractive){
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


}
