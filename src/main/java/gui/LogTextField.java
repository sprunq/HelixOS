package gui;

import kernel.Logger;
import kernel.display.video.font.IFont;

public class LogTextField extends TextField {

    public LogTextField(int x, int y, int width, int height, IFont font, int charSpacing, int lineSpacing,
            byte textColor, byte backGroundColor) {
        super(x, y, width, height, font, charSpacing, lineSpacing, textColor, backGroundColor);

    }

    public void draw() {
        clearText();
        for (int i = Logger.getLogBufferSize() - 1; i >= 0; i--) {
            String log = Logger.getChronologicalLog(i);
            if (log.length() > 0) {
                addString("-");
                addString(log);
                newLine();
            }

        }
        super.draw();
    }

}
