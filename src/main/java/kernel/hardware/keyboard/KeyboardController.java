package kernel.hardware.keyboard;

import kernel.Logger;
import kernel.hardware.keyboard.layout.ALayout;
import util.BitHelper;

public class KeyboardController {
    private static final int PORT_KEYCODE = 0x60;
    private static final int KEYCODE_EXTEND1 = 0xE0;
    private static final int KEYCODE_EXTEND2 = 0xE1;

    private static RingBuffer inputBuffer;
    private static ALayout layout;

    private static boolean shiftPressed;
    @SuppressWarnings("unused")
    private static boolean ctrlPressed;
    private static boolean altPressed;
    private static boolean capsLocked;

    /*
     * Listeners can register to receive keyboard events.
     * They are called in order of priority.
     * A listener can consume an event, preventing other listeners from receiving
     * it.
     */
    private static ListenerPriorityMap listeners;

    public static void initialize(ALayout keyBoardLayout) {
        inputBuffer = new RingBuffer(256);
        layout = keyBoardLayout;
        listeners = new ListenerPriorityMap(19);
        Logger.info("KeyB", "Initialized");
    }

    public static void addListener(IKeyboardEventListener listener, int priority) {
        KeyboardController.listeners.addListener(listener, priority);
    }

    @SJC.Inline
    public static boolean hasNewEvent() {
        return inputBuffer.containsNewElements();
    }

    public static void handle() {
        byte code = MAGIC.rIOs8(PORT_KEYCODE);
        if (code >= 0xE2) {
            Logger.warning("KeyB", "Ignoring ScanCode >0xE2");
            return;
        }
        inputBuffer.put(code);
    }

    public static void readEvent() {
        if (!inputBuffer.containsNewElements())
            return;

        int keyCode = readKeyCode();
        boolean isBreak = isBreakCode(keyCode);
        if (isBreak) {
            keyCode = unsetBreakCode(keyCode);
        }

        int logicalKey = layout.logicalKey(keyCode, isUpper(), altPressed);

        if (!isBreak) {
            Logger.trace("KeyB", "Pressed ".append(Key.name(logicalKey)));
        } else {
            Logger.trace("KeyB", "Release ".append(Key.name(logicalKey)));
        }

        updateKeyboardState(logicalKey, isBreak);
        sendKeyEvent(logicalKey, isBreak);
    }

    private static void sendKeyEvent(int logicalKey, boolean isBreak) {
        for (int i = 0; i < listeners.size(); i++) {
            IKeyboardEventListener listener = listeners.get(i);
            if (listener == null) {
                break;
            }
            boolean consumed = false;
            if (isBreak) {
                consumed = listener.onKeyReleased((char) logicalKey);
            } else {
                consumed = listener.onKeyPressed((char) logicalKey);
            }
            if (consumed) {
                Logger.trace("KeyB", "Event consumed by ".append(Integer.toString(i, 10)));
                break;
            }
        }
    }

    private static void updateKeyboardState(int logicalKey, boolean isBreak) {
        switch (logicalKey) {
            case Key.LSHIFT:
            case Key.RSHIFT:
                if (isBreak) {
                    shiftPressed = false;
                } else {
                    shiftPressed = true;
                }
                break;
            case Key.LCTRL:
            case Key.RCTRL:
                if (isBreak) {
                    ctrlPressed = false;
                } else {
                    ctrlPressed = true;
                }
                break;
            case Key.LALT:
            case Key.RALT:
                if (isBreak) {
                    altPressed = false;
                } else {
                    altPressed = true;
                }
                break;
            case Key.CAPSLOCK:
                if (isBreak) {
                    capsLocked = false;
                } else {
                    capsLocked = true;
                }
                break;
        }
    }

    private static int readKeyCode() {
        int c0 = Integer.ubyte(inputBuffer.get());
        int keyCode = 0;
        if (c0 == KEYCODE_EXTEND1) {
            int c1 = Integer.ubyte(inputBuffer.get());

            // 0xE0_2A
            keyCode = BitHelper.setRange(keyCode, 8, 8, c0);
            keyCode = BitHelper.setRange(keyCode, 0, 8, c1);
        } else if (c0 == KEYCODE_EXTEND2) {
            int c1 = Integer.ubyte(inputBuffer.get());
            int c2 = Integer.ubyte(inputBuffer.get());

            // 0xE1_2A_2A
            keyCode = BitHelper.setRange(keyCode, 16, 8, c0);
            keyCode = BitHelper.setRange(keyCode, 8, 8, c1);
            keyCode = BitHelper.setRange(keyCode, 0, 8, c2);
        } else {
            // 0x2A
            keyCode = Integer.ubyte(c0);
        }
        return keyCode;
    }

    private static final int MASK_10000000 = 1 << 7;
    private static final int MASK_01111111 = ~MASK_10000000;
    private static final int MASK_1000000010000000 = (1 << 7) | (1 << 15);
    private static final int MASK_0111111101111111 = ~MASK_1000000010000000;

    /*
     * Unset the break code bits.
     * For normal codes, the 8th bit is set.
     * For E0 codes, the 8th bit is set.
     * For E1 codes, the 8th and 16th bits are set.
     */
    @SJC.Inline
    private static int unsetBreakCode(int keyCode) {
        if (keyCode > 0xE10000) {
            return keyCode & MASK_0111111101111111;
        } else {
            return keyCode & MASK_01111111;
        }
    }

    @SJC.Inline
    private static boolean isBreakCode(int keyCode) {
        return (keyCode & MASK_10000000) != 0;
    }

    @SJC.Inline
    private static boolean isUpper() {
        if (shiftPressed) {
            return true;
        }
        if (capsLocked) {
            return true;
        }
        return false;
    }
}
