package util;

import kernel.video.OsWriter;

public class Sys {
    public static void panic(String msg) {
        OsWriter.println();
        OsWriter.print("panic! ");
        OsWriter.print(msg);
        while (true) {
        }
    }
}