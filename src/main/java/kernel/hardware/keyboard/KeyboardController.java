package kernel.hardware.keyboard;

import kernel.Logger;
import kernel.interrupt.PIC;

public class KeyboardController {
    private static final int PORT_KEYCODE = 0x60;

    public static void handle() {
        int c0 = readCode();
        String s = Integer.toString(c0, 16);
        Logger.info("Keycode: 0x".append(s));
        if (c0 <= 0xDF) {
            Logger.info("Normal Keycode");
        } else if (c0 == 0xE0) {
            Logger.info("ScanCode - ext0");
        } else if (c0 == 0xE1) {
            Logger.info("ScanCode - ext1");
        } else if (c0 >= 0xE2) {
            Logger.warning("Ignoring ScanCode >0xE2");
        } else {
            Logger.error("Unrecognized Keycode");
        }

    }

    private static int readCode() {
        int code = MAGIC.rIOs8(PORT_KEYCODE);
        PIC.acknowledge(1);
        return code;
    }
}
