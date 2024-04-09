package kernel.hardware.keyboard;

import gui.GUI;
import kernel.Logger;
import kernel.bios.BIOS;
import kernel.display.text.TM3;
import kernel.display.text.TM3Color;
import kernel.display.video.VM13;
import kernel.hardware.keyboard.layout.ALayout;
import util.BitHelper;

public class KeyboardController {
    private static final int PORT_KEYCODE = 0x60;

    private static final int KEYCODE_EXTEND1 = 0xE0;
    private static final int KEYCODE_EXTEND2 = 0xE1;

    private static RingBuffer inputBuffer;
    private static ALayout layout;

    // modifier keys
    private static boolean shift;
    private static boolean ctrl;
    private static boolean alt;
    private static boolean capsLock;
    private static boolean numLock;
    private static boolean scrollLock;

    public static void initialize(ALayout keyBoardLayout) {
        inputBuffer = new RingBuffer(32);
        layout = keyBoardLayout;
        Logger.info("KeyB: Initialized");
    }

    public static void handle() {
        byte code = MAGIC.rIOs8(PORT_KEYCODE);
        if (code >= 0xE2) {
            Logger.warning("KeyB: Ignoring ScanCode >0xE2");
            return;
        }
        inputBuffer.put(code);
    }

    public static void readEvent() {
        if (!inputBuffer.containsNewElements())
            return;

        byte c0 = inputBuffer.get();
        int keyCode = 0;
        if (c0 == KEYCODE_EXTEND1) {
            byte c1 = inputBuffer.get();
            // 0xE0_2A
            keyCode = BitHelper.setRange(keyCode, 8, 8, c0);
            keyCode = BitHelper.setRange(keyCode, 0, 8, c1);
        } else if (c0 == KEYCODE_EXTEND2) {
            byte c1 = inputBuffer.get();
            byte c2 = inputBuffer.get();
            // 0xE1_2A_2A
            keyCode = BitHelper.setRange(keyCode, 16, 8, c0);
            keyCode = BitHelper.setRange(keyCode, 8, 8, c1);
            keyCode = BitHelper.setRange(keyCode, 0, 8, c2);
        } else {
            // 0x2A
            keyCode = c0;
        }

        boolean isBreak = BitHelper.getFlag(keyCode, 7);
        if (isBreak) {
            Logger.info("KeyB: Break");
            // sets the break flag to false
            keyCode &= 0x7F; // 01111111
        }
        int logicalKey = layout.logicalKey(keyCode, shift, alt);

        if (!isBreak) {
            Logger.info("KeyB: "
                    .append(Key.name(logicalKey)));
        } else {
            Logger.info("KeyB: "
                    .append(Key.name(logicalKey))
                    .append(" released"));
        }

        switch (logicalKey) {
            case Key.LSHIFT:
            case Key.RSHIFT:
                if (isBreak) {
                    shift = false;
                } else {
                    shift = true;
                }
                break;
            case Key.LCTRL:
            case Key.RCTRL:
                if (isBreak) {
                    ctrl = false;
                } else {
                    ctrl = true;
                }
                break;
            case Key.LALT:
            case Key.RALT:
                if (isBreak) {
                    alt = false;
                } else {
                    alt = true;
                }
                break;
            case Key.CAPSLOCK:
                capsLock = !capsLock;
                break;
            case Key.NUMLOCK:
                numLock = !numLock;
                break;
            case Key.SCROLLLOCK:
                scrollLock = !scrollLock;
                break;
            default:
        }
    }

    public static boolean hasNewEvent() {
        return inputBuffer.containsNewElements();
    }
}
