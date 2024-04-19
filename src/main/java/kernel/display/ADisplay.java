package kernel.display;

public abstract class ADisplay {

    public abstract void activate();

    public abstract int Width();

    public abstract int Height();

    public abstract int rgb(int r, int g, int b);

    public abstract void swap();

    public abstract void clearScreen();

    public abstract void setPixel(int x, int y, int col);

    public abstract void fillrect(int x, int y, int width, int height, int color);

    public abstract void setBitmap(int x, int y, int[][] bitmap);

}
