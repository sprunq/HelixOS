package gui.displays.windows;

import gui.Window;
import kernel.Kernel;
import kernel.display.GraphicsContext;

/*
 * A ball in a window that bounces around the edges of the window.
 */
public class BounceWindow extends Window {
    private Square ball;
    private int ballRadius = 20;
    private int ballSpeedX = 3;
    private int ballSpeedY = 2;
    private boolean ballNeedsRedraw;

    public BounceWindow(int x, int y, int z, int width, int height, String title) {
        super(x, y, z, width, height, title);
        this.ball = new Square(width / 2, height / 2, ballRadius, 0x8A66B4);
        ballNeedsRedraw = true;
    }

    @Override
    public void DrawContent(GraphicsContext ctx) {
        ctx.Rectangle(ContentX, ContentY, ContentWidth, ContentHeight, 0);
        ctx.Rectangle(
                ContentX + ball.X,
                ContentY + ball.Y,
                ball.Size,
                ball.Size,
                ball.Color);
        ballNeedsRedraw = false;
    }

    @Override
    public boolean NeedsRedraw() {
        return ballNeedsRedraw || super.NeedsRedraw();
    }

    public void UpdateBallPosition() {
        ball.X += ballSpeedX;
        ball.Y += ballSpeedY;

        if (ball.X <= 0 || ball.X >= ContentWidth - ball.Size - 1) {
            ballSpeedX = -ballSpeedX;
            NextColor();
        }

        if (ball.Y <= 0 || ball.Y >= ContentHeight - ball.Size - 1) {
            ballSpeedY = -ballSpeedY;
            NextColor();
        }
        ballNeedsRedraw = true;
    }

    private void NextColor() {
        int red = (ball.Color >> 16) & 0xFF;
        int green = (ball.Color >> 8) & 0xFF;
        int blue = ball.Color & 0xFF;

        ball.Color = Kernel.Display.Rgb(
                (ball.X + blue) % 256,
                (ball.Y + red) % 256,
                (ball.X + ball.Y + green) % 256);
    }
}
