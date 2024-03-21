package kernel;

import kernel.video.VidColor;
import kernel.video.VidMem;

@SuppressWarnings("unused")
public class Kernel {
    private static final VidMem vidMem = (VidMem) MAGIC.cast2Struct(0xB8000);
    private static int vidPos;

    public static void main() {
        clearScreen();

        byte col = 0;
        int round = 0;
        while (true) {
            for (int i = 0; i < 2000; i++) {
                int index = (i + round) % 2000;
                vidMem.cells[index].color = VidColor.setBg(vidMem.cells[index].color , col);
                if (i % (2000 / 16) == 0) {
                    col = (byte) ((col + 1) % 8);
                }
                if (index % 3 == 0){
                    vidMem.cells[i].color = VidColor.setBgBright(vidMem.cells[i].color , true);
                } else {
                    vidMem.cells[i].color = VidColor.setBgBright(vidMem.cells[i].color , false);
                }
            }
            round += 1;

            for (int i = 0; i < 5000000; i++) {
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
        vidMem.cells[vidPos].character = (byte) c;

        byte color = vidMem.cells[vidPos].color;
        vidMem.cells[vidPos].color = VidColor.setFg(color, VidColor.GREY);
        vidPos += 1;
    }

    public static void clearScreen() {
        for (int i = 0; i < 2000; i++) {
            vidMem.cells[i].character = ' ';
            vidMem.cells[i].color = VidColor.BLACK;
        }
        vidPos = 0;
    }
}
