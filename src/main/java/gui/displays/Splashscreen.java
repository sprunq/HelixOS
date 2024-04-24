package gui.displays;

import formats.images.Image;
import gui.ADisplayElement;
import gui.images.LogoText;
import gui.images.Logo;
import kernel.Kernel;
import kernel.display.ADisplay;

public class Splashscreen extends ADisplayElement {
    private Image logo;
    private Image logoText;
    private int spaceBetween;
    private int combinedHeight;
    private int backColor;

    public Splashscreen(int x, int y, int z, int width, int height) {
        super(x, y, z, width, height);
        logo = Logo.Load();
        logoText = LogoText.Load();
        spaceBetween = 20;
        combinedHeight = logo.Height + logoText.Height + spaceBetween;
        backColor = Kernel.Display.Rgb(0, 13, 40);
    }

    @Override
    public void Draw(ADisplay display) {
        Kernel.Display.Rectangle(0, 0, Kernel.Display.Width(), Kernel.Display.Height(), backColor);

        int x = Kernel.Display.Width() / 2 - logo.Width / 2;
        int y = Kernel.Display.Height() / 2 - combinedHeight / 2;
        Kernel.Display.Bitmap(x, y, logo.PixelData);

        int x_text = Kernel.Display.Width() / 2 - logoText.Width / 2;
        int y_text = y + logo.Height + spaceBetween;

        Kernel.Display.Bitmap(x_text, y_text, logoText.PixelData);
    }

    @Override
    public boolean NeedsRedraw() {
        return true;
    }
}
