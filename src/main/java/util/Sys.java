package util;

import kernel.video.TextWriter;

public class Sys {
    public static void panic(String msg) {
        TextWriter.newline();
        TextWriter.print("panic! ");
        TextWriter.print(msg);
        while (true) {
        }
    }
}