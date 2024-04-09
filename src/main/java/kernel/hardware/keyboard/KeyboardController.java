package kernel.hardware.keyboard;

import kernel.Kernel;
import kernel.Logger;
import kernel.bios.BIOS;
import kernel.display.text.TM3;
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

        int c0 = Integer.ubyte(inputBuffer.get());
        int keyCode = 0;
        boolean isBreak = false;
        if (c0 == KEYCODE_EXTEND1) {
            // 0xE0_2A
            int c1 = Integer.ubyte(inputBuffer.get());

            if (isBreakCode(c1)) {
                c1 = clearBreakCode(c1);
                isBreak = true;
            }
            keyCode = BitHelper.setRange(keyCode, 8, 8, c0);
            keyCode = BitHelper.setRange(keyCode, 0, 8, c1);
        } else if (c0 == KEYCODE_EXTEND2) {
            // 0xE1_2A_2A
            int c1 = Integer.ubyte(inputBuffer.get());
            int c2 = Integer.ubyte(inputBuffer.get());

            if (isBreakCode(c2)) {
                c1 = clearBreakCode(c1);
                c2 = clearBreakCode(c2);
                isBreak = true;
            }

            keyCode = BitHelper.setRange(keyCode, 16, 8, c0);
            keyCode = BitHelper.setRange(keyCode, 8, 8, c1);
            keyCode = BitHelper.setRange(keyCode, 0, 8, c2);
        } else {
            // 0x2A
            if (isBreakCode(c0)) {
                c0 = clearBreakCode(c0);
                isBreak = true;
            }
            keyCode = Integer.ubyte(c0);
        }

        int logicalKey = layout.logicalKey(keyCode, shift, alt);

        if (!isBreak) {
            Logger.info("KeyB: "
                    .append(Key.name(logicalKey)));
        } else {
            Logger.info("KeyB: "
                    .append(Key.name(logicalKey))
                    .append(" release"));
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

    @SJC.Inline
    private static int clearBreakCode(int c1) {
        return BitHelper.setFlag(c1, 7, false);
    }

    @SJC.Inline
    private static boolean isBreakCode(int c2) {
        return BitHelper.getFlag(c2, 7);
    }

    @SJC.Inline
    public static boolean hasNewEvent() {
        return inputBuffer.containsNewElements();
    }
}
