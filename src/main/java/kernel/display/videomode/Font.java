package kernel.display.videomode;

import kernel.Kernel;

public class Font {

    public static String FONT = MAGIC.getNamedString(
            "C:\\Users\\Tobias\\Documents\\Projects\\ToOs\\src\\main\\java\\kernel\\display\\videomode\\font.bim");

    public static int getByte(int ch, int offset) {
        int line_len = 8;
        int b = FONT.charAt(ch * line_len + offset);
        return b;
    }
}