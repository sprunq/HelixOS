package gui;

import kernel.Kernel;
import kernel.hardware.Timer;
import util.images.BinImage;
import util.images.Logo;
import util.images.LogoText;

public class Splashscreen {
    public static void show() {
        BinImage logo = Logo.load();
        BinImage logoText = LogoText.load();
        int spaceBetween = 10;
        int combinedHeight = logo.Height + logoText.Height + spaceBetween;

        int backColor = Kernel.Display.rgb(0, 13, 40);
        Kernel.Display.fillrect(0, 0, Kernel.Display.Width(), Kernel.Display.Height(), backColor);

        int x = Kernel.Display.Width() / 2 - logo.Width / 2;
        int y = Kernel.Display.Height() / 2 - combinedHeight / 2;
        Kernel.Display.setBitmap(x, y, logo.PixelData);

        int x_text = Kernel.Display.Width() / 2 - logoText.Width / 2;
        int y_text = y + logo.Height + spaceBetween;

        Kernel.Display.setBitmap(x_text, y_text, logoText.PixelData);

        Kernel.Display.swap();
        Timer.sleep(4000);
        Kernel.Display.fillrect(0, 0, Kernel.Display.Width(), Kernel.Display.Height(), backColor);
        Kernel.Display.swap();
    }
}
