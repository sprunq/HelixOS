package gui.displays;

import formats.images.Image;
import gui.ADisplayElement;
import gui.images.LogoText;
import gui.images.Logo;
import kernel.Kernel;
import kernel.display.ADisplay;

public class Splashscreen extends ADisplayElement {
    private Image _logo;
    private Image _logoText;
    private int _spaceBetween;
    private int _combinedHeight;
    private int _backColor;

    public Splashscreen(int x, int y, int z, int width, int height) {
        super(x, y, z, width, height);
        _logo = Logo.Load();
        _logoText = LogoText.Load();
        _spaceBetween = 20;
        _combinedHeight = _logo.Height + _logoText.Height + _spaceBetween;
        _backColor = Kernel.Display.Rgb(0, 13, 40);
    }

    @Override
    public void Draw(ADisplay display) {
        Kernel.Display.Rectangle(0, 0, Kernel.Display.Width(), Kernel.Display.Height(), _backColor);

        int x = Kernel.Display.Width() / 2 - _logo.Width / 2;
        int y = Kernel.Display.Height() / 2 - _combinedHeight / 2;
        Kernel.Display.Bitmap(x, y, _logo.PixelData);

        int x_text = Kernel.Display.Width() / 2 - _logoText.Width / 2;
        int y_text = y + _logo.Height + _spaceBetween;

        Kernel.Display.Bitmap(x_text, y_text, _logoText.PixelData);
    }

    @Override
    public boolean NeedsRedraw() {
        return true;
    }
}
