package kernel.display;

public abstract class ADisplay {

    public abstract void Activate();

    public abstract int Width();

    public abstract int Height();

    public abstract int Rgb(int r, int g, int b);

    public abstract void Swap();

    public abstract void ClearScreen();

    public abstract void Pixel(int x, int y, int col);

    public abstract void Rectangle(int x, int y, int width, int height, int color);

    public abstract void Bitmap(int x, int y, int[][] bitmap);

}
