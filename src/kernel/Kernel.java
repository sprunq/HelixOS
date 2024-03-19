package kernel;

import kernel.video.VidColor;
import kernel.video.VidMem;

public class Kernel {
    private static final VidMem vidMem = (VidMem) MAGIC.cast2Struct(0xB8000);
    private static int vidPos;

    @SuppressWarnings("unused")
    public static void main() {
        clearScreen();

        byte col = 0;
        int round = 0;
        while (true) {
            for (int i = 0; i < 2000; i++) {
                int index = (i + round) % 2000;
                vidMem.digit[index].color = VidColor.withBg(VidColor.BLACK, col);
                if (i % (2000 / 8) == 0) {
                    col = (byte) ((col + 1) % 8);
                }
            }
            round += 1;

            for (int i = 0; i < 1000000; i++) {
            }
        }
    }

    public static void print(String str) {
        for (int i = 0; i < str.length(); i++) {
            print(str.charAt(i));
        }
    }

    public static void print(char c) {
        if (vidPos < 0 || vidPos >= 2000) {
            vidPos = 0;
        }
        vidMem.digit[vidPos].ascii = (byte) c;
        vidMem.digit[vidPos++].color = VidColor.GREEN;
    }

    public static void clearScreen() {
        for (int i = 0; i < 2000; i++) {
            vidMem.digit[i].ascii = ' ';
            vidMem.digit[i].color = VidColor.BLACK;
        }
        vidPos = 0;
    }
}
