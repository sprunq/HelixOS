package gui.displays;

import gui.ADisplayElement;
import gui.images.BinImage;
import gui.images.Logo;
import gui.images.LogoText;
import kernel.Kernel;
import kernel.display.ADisplay;

public class Splashscreen extends ADisplayElement {
    private BinImage logo;
    private BinImage logoText;
    private int spaceBetween;
    private int combinedHeight;
    private int backColor;

    public Splashscreen(int x, int y, int z, int width, int height) {
        super(x, y, z, width, height);
        logo = Logo.load();
        logoText = LogoText.load();
        spaceBetween = 20;
        combinedHeight = logo.Height + logoText.Height + spaceBetween;
        backColor = Kernel.Display.rgb(0, 13, 40);
    }

    @Override
    public void draw(ADisplay display) {
        Kernel.Display.fillrect(0, 0, Kernel.Display.Width(), Kernel.Display.Height(), backColor);

        int x = Kernel.Display.Width() / 2 - logo.Width / 2;
        int y = Kernel.Display.Height() / 2 - combinedHeight / 2;
        Kernel.Display.setBitmap(x, y, logo.PixelData);

        int x_text = Kernel.Display.Width() / 2 - logoText.Width / 2;
        int y_text = y + logo.Height + spaceBetween;

        Kernel.Display.setBitmap(x_text, y_text, logoText.PixelData);
    }

    @Override
    public boolean needsRedraw() {
        return true;
    }
}
