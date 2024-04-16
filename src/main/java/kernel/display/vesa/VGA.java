package kernel.display.vesa;

import kernel.bios.BIOS;

public class VGA extends GraphicsDriver {
  private static final int TEXTMODE_ADDRESS = 0xB8000;
  private static final int GRAPHICMODE_ADDRESS = 0xA0000;
  private static final int CRTCind = 0x03D4; // VGA-CRTC-index
  private static final int CRTCdat = 0x03D5; // VGA-CRTC-data
  private int colorFG;
  private int colorBG;

  private int textCols = 0;
  private int textLines = 0;
  private int cursorX = 0;
  private int cursorY = 0;

  public VGA() {
    textCols = 80;
    textLines = 25;
    reInitText();
  }

  public int getStartAddress() {
    return GRAPHICMODE_ADDRESS;
  }

  private void reInitText() {
    cursorX = cursorY = 0;
    colorFG = 0x07;
    colorBG = 0x00;
    enableCursor(true);
  }

  public void setTextMode() {
    BIOS.Registers.EAX = 0x0003;
    BIOS.rint(0x10);
    reInitText();
  }

  public void setGraphics320x200Mode() {
    BIOS.Registers.EAX = 0x0013;
    BIOS.rint(0x10);
  }

  public void setColor(int fg, int bg) {
    colorFG = fg;
    colorBG = bg;
  }

  public void putChar(int x, int y, char c) {
    int NV = ((int) c & 0xFF) | ((colorFG & 0xF) << 8) | ((colorBG & 7) << 12);
    int offset;

    offset = y * textCols + x;
    if (offset < 0 || offset >= textCols * textLines)
      return; // don't try to put characters outside screen
    MAGIC.wMem16(TEXTMODE_ADDRESS + (offset << 1), (short) NV);
  }

  public void enableCursor(boolean on) {
    int dummy;

    MAGIC.wIOs8(CRTCind, (byte) 0x0A); // cursor start register
    dummy = (int) MAGIC.rIOs8(CRTCdat) & 0xDF; // save other bits
    if (!on)
      dummy |= 0x20; // bit set means cursor off
    MAGIC.wIOs8(CRTCdat, (byte) dummy);
  }

  public void setCursor(int newX, int newY) {
    int newVal; // cursor position

    if (newX < 0 || newX >= textCols || newY < 0 || newY >= textLines)
      return;
    newVal = newX + newY * textCols;
    MAGIC.wIOs8(CRTCind, (byte) 0x0E); // cursor location high
    MAGIC.wIOs8(CRTCdat, (byte) (newVal >> 8));
    MAGIC.wIOs8(CRTCind, (byte) 0x0F); // cursor location low
    MAGIC.wIOs8(CRTCdat, (byte) (newVal & 0xFF));
    cursorX = newX;
    cursorY = newY;
  }

  private void clearLine(int y) {
    int i, offset = (y * textCols) >> 1;
    int NV = (int) ' ' | ((colorFG & 7) << 8) | ((colorBG & 3) << 12);
    int dummy = NV | (NV << 16); // write two displayed characters at once

    for (i = 0; i < (textCols >>> 1); i++) {
      MAGIC.wMem32(TEXTMODE_ADDRESS + ((offset + i) << 2), dummy);
    }
  }

  public void scroll() {
    int i, diff, max;

    diff = textCols << 1;
    max = TEXTMODE_ADDRESS + ((textCols * textLines) << 1);
    for (i = TEXTMODE_ADDRESS + diff; i < max; i += 4)
      MAGIC.wMem32(i - diff, MAGIC.rMem32(i));
    clearLine(textLines - 1);
  }

  public void cls() {
    int i;
    for (i = 0; i < textLines; i++)
      clearLine(i);
  }

  public void setPixel(int x, int y, int col) {
    MAGIC.wMem8(GRAPHICMODE_ADDRESS + y * 320 + x, (byte) col);
  }

  public void drawLine(int y, int[] col) {
    if (col == null)
      return;
    for (int x = 0; x < 320; x++)
      MAGIC.wMem8(GRAPHICMODE_ADDRESS + y * 320 + x, (byte) col[x]);
  }
}
