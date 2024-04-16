package kernel.display.vesa;

public abstract class GraphicsDriver {
  public abstract void setTextMode();

  public abstract void setGraphics320x200Mode();

  public abstract void setPixel(int x, int y, int col);

  public abstract void drawLine(int y, int[] col);

  public abstract int getStartAddress();

  public boolean setMode(int xRes, int yRes, int colDepth, boolean graphical) {
    return false; // default: mode not supported
  }
}
