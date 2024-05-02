package gui.displays.windows;

import kernel.Kernel;
import kernel.display.ADisplay;
import kernel.trace.logging.Logger;

/*
 * A ball in a window that bounces around the edges of the window.
 */
public class Bounce extends AWindow {
    private Square ball;
    private int ballRadius = 20;
    private int ballSpeedX = 3;
    private int ballSpeedY = 2;

    public Bounce(int x, int y, int z, int width, int height, String title) {
        super(x, y, z, width, height, title);
        this.ball = new Square(width / 2, height / 2, ballRadius, 0x8A66B4);

        if (ball._r_type == null || ball._r_type.name == null) {
            Logger.Error("Bounce", "ball._r_type is null");
        }
    }

    @Override
    public void DrawContent(ADisplay context) {
        Tick();
        context.Rectangle(ContentX, ContentY, ContentWidth, ContentHeight, 0);
        context.Rectangle(
                ContentX + ball.X,
                ContentY + ball.Y,
                ball.Size,
                ball.Size,
                ball.Color);
    }

    @Override
    public boolean NeedsRedraw() {
        return true;
    }

    public void Tick() {
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
