package com.example.sand_clock_application;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BrickBreakerView extends View {
    private Paint paintBg, paintPaddle, paintBall, paintBrick, paintText;

    // 1. Cấu hình lưới gạch (3 hàng, 6 cột như thiết kế)
    private final int ROWS = 3;
    private final int COLS = 6;

    // Mảng 2 chiều quản lý gạch (true = chưa vỡ, false = đã vỡ)
    public boolean[][] bricks = new boolean[ROWS][COLS];

    // 5 Mã màu chuẩn xác từ bản thiết kế
    private final String[] BRICK_COLORS = {
            "#F17D6E", // Đỏ san hô
            "#F4CF57", // Vàng
            "#58D68D", // Xanh ngọc (Teal)
            "#6C9EEB", // Xanh lam
            "#A569BD"  // Tím
    };

    // 2. Thông số kích thước & tọa độ
    private float brickWidth, brickHeight;
    private float padding = 36f; // Viền cách khung bao ngoài
    private float gap = 16f;     // Khoảng cách giữa các viên gạch

    public float paddleX, paddleY;
    public float paddleWidth = 180f, paddleHeight = 24f;

    public float ballX, ballY;
    public float ballRadius = 14f;

    public BrickBreakerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Cọ vẽ nền game (Đen nhạt bo góc)
        paintBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBg.setColor(Color.parseColor("#151515"));
        paintBg.setStyle(Paint.Style.FILL);

        // Cọ vẽ thanh trượt (Vàng Neon)
        paintPaddle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPaddle.setColor(Color.parseColor("#E0FF31"));
        paintPaddle.setStyle(Paint.Style.FILL);

        // Cọ vẽ quả bóng
        paintBall = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBall.setColor(Color.WHITE);
        paintBall.setStyle(Paint.Style.FILL);

        // Cọ vẽ gạch (Màu sắc sẽ đổi trong lúc vẽ)
        paintBrick = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBrick.setStyle(Paint.Style.FILL);

        // Khởi tạo game: Đặt toàn bộ gạch ở trạng thái "chưa vỡ" (true)
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                bricks[r][c] = true;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Chia đều chiều rộng màn hình cho 6 cột gạch (tính cả khoảng trống gap)
        float totalGapWidth = (COLS - 1) * gap;
        float totalPadding = padding * 2;
        brickWidth = (w - totalPadding - totalGapWidth) / COLS;
        brickHeight = 48f;

        // Đặt thanh trượt ở giữa, sát đáy
        paddleX = w / 2f - paddleWidth / 2f;
        paddleY = h - padding - 100f;

        // Đặt bóng nằm ngay trên thanh trượt
        ballX = w / 2f;
        ballY = paddleY - ballRadius - 40f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1. Vẽ khung nền của khu vực game (Khối bo góc chứa toàn bộ bóng và gạch)
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), 48f, 48f, paintBg);

        // 2. Vẽ ma trận gạch (Chỉ vẽ những viên có giá trị true)
        float currentY = padding + 40f;
        for (int r = 0; r < ROWS; r++) {
            float currentX = padding;
            for (int c = 0; c < COLS; c++) {
                if (bricks[r][c]) {
                    // Thuật toán lấy màu đan chéo theo lưới
                    int colorIndex = (r + c) % BRICK_COLORS.length;
                    paintBrick.setColor(Color.parseColor(BRICK_COLORS[colorIndex]));

                    // Vẽ viên gạch bo góc nhẹ 12f
                    canvas.drawRoundRect(
                            currentX, currentY,
                            currentX + brickWidth, currentY + brickHeight,
                            12f, 12f, paintBrick
                    );
                }
                currentX += brickWidth + gap; // Cộng dồn tọa độ X
            }
            currentY += brickHeight + gap; // Cộng dồn tọa độ Y
        }

        // 4. Vẽ thanh trượt (Paddle)
        canvas.drawRoundRect(
                paddleX, paddleY,
                paddleX + paddleWidth, paddleY + paddleHeight,
                12f, 12f, paintPaddle
        );

        // 5. Vẽ quả bóng (Ball)
        canvas.drawCircle(ballX, ballY, ballRadius, paintBall);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX(); // Lấy tọa độ X của ngón tay

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // Khi vừa chạm ngón tay xuống
            case MotionEvent.ACTION_MOVE: // Khi kéo ngón tay lướt đi
                // Đưa thanh trượt đi theo ngón tay (canh ngón tay vào đúng giữa thanh)
                paddleX = touchX - (paddleWidth / 2f);

                // Chặn không cho thanh trượt bay ra khỏi mép trái màn hình
                if (paddleX < 0) {
                    paddleX = 0;
                }
                // Chặn không cho thanh trượt bay ra khỏi mép phải màn hình
                if (paddleX + paddleWidth > getWidth()) {
                    paddleX = getWidth() - paddleWidth;
                }

                invalidate(); // Bắt buộc phải gọi hàm này để Canvas vẽ lại vị trí mới!
                return true;
        }
        return super.onTouchEvent(event);
    }
}
