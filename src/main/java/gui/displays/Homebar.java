package gui.displays;

import formats.images.Image;
import gui.ADisplayElement;
import gui.images.Logo25;
import kernel.Kernel;
import kernel.display.ADisplay;

public class Homebar extends ADisplayElement {
    private final int COL_HOMEBAR;
    private static final int HOMEBAR_HEIGHT = 45;
    private Image _logo;
    private TextClockTime _time;
    private TextClockDate _date;

    public Homebar(int displayWidth, int displayHeight) {
        super(0, displayHeight - HOMEBAR_HEIGHT, 20, displayWidth, HOMEBAR_HEIGHT);
        COL_HOMEBAR = Kernel.Display.rgb(0, 20, 50);
        _logo = Logo25.load();
        setLogoBackGround(_logo);

        // time over date
        _time = new TextClockTime(
                Width - 85,
                Y + (Height - 35) / 2,
                21,
                85,
                20,
                Kernel.Display.rgb(255, 255, 255),
                COL_HOMEBAR);

        _date = new TextClockDate(
                Width - 85,
                Y + (Height - 0) / 2,
                21,
                85,
                20,
                Kernel.Display.rgb(255, 255, 255),
                COL_HOMEBAR);

    }

    @Override
    public void draw(ADisplay display) {
        display.fillrect(X, Y, Width, Height, COL_HOMEBAR);
        display.setBitmap(X + 10, Y + 10, _logo.PixelData);
        _time.draw(display);
        _date.draw(display);
    }

    @Override
    public boolean needsRedraw() {
        return _time.needsRedraw() || _date.needsRedraw();
    }

    private void setLogoBackGround(Image logo) {
        for (int i = 0; i < logo.Width; i++) {
            for (int j = 0; j < logo.Height; j++) {
                if (logo.PixelData[i][j] == 0) {
                    logo.PixelData[i][j] = COL_HOMEBAR;
                }
            }
        }
    }
}
