package kernel.hardware.keyboard;

import kernel.hardware.keyboard.layout.ALayout;
import kernel.trace.logging.Logger;
import util.BitHelper;
import util.queue.QueueByte;
import util.vector.VecKeyboardEventListener;

public class KeyboardController {
    private static final int PORT_KEYCODE = 0x60;
    private static final int KEYCODE_EXTEND1 = 0xE0;
    private static final int KEYCODE_EXTEND2 = 0xE1;

    private static final int MASK_10000000 = 1 << 7;
    private static final int MASK_01111111 = ~MASK_10000000;
    private static final int MASK_1000000010000000 = (1 << 7) | (1 << 15);
    private static final int MASK_0111111101111111 = ~MASK_1000000010000000;

    private static QueueByte _inputBuffer;
    private static ALayout _layout;

    private static boolean _shiftPressed;
    @SuppressWarnings("unused")
    private static boolean _ctrlPressed;
    private static boolean _altPressed;
    private static boolean _capsLocked;

    /*
     * Listeners can register to receive keyboard events.
     * They are called in order of priority.
     * A listener can consume an event, preventing other listeners from receiving
     * it.
     */
    private static VecKeyboardEventListener _listeners;

    public static void Initialize(ALayout keyBoardLayout) {
        _inputBuffer = new QueueByte(256);
        _layout = keyBoardLayout;
        _listeners = new VecKeyboardEventListener();
        Logger.Info("KeyC", "Initialized");
    }

    public static void AddListener(IKeyboardEventListener listener) {
        Logger.Info("KeyC", "Adding Listener");
        _listeners.Add(listener);
        _listeners.SortByPriority();
    }

    @SJC.Inline
    public static boolean HasNewEvent() {
        return _inputBuffer.ContainsNewElements();
    }

    public static void Handle() {
        byte code = MAGIC.rIOs8(PORT_KEYCODE);
        if (code >= 0xE2) {
            Logger.Warning("KeyC", "Ignoring ScanCode >0xE2");
            return;
        }
        _inputBuffer.Put(code);
    }

    public static void ReadEvent() {
        if (!_inputBuffer.ContainsNewElements())
            return;

        int keyCode = ReadKeyCode();
        boolean isBreak = IsBreakCode(keyCode);
        if (isBreak) {
            keyCode = UnsetBreakCode(keyCode);
        }

        int logicalKey = _layout.LogicalKey(keyCode, IsUpper(), _altPressed);

        if (!isBreak) {
            Logger.Trace("KeyC", "Pressed ".append(Key.Name(logicalKey)));
        } else {
            Logger.Trace("KeyC", "Release ".append(Key.Name(logicalKey)));
        }

        UpdateKeyboardState(logicalKey, isBreak);
        SendKeyEvent(logicalKey, isBreak);
    }

    private static void SendKeyEvent(int logicalKey, boolean isBreak) {
        for (int i = 0; i < _listeners.Size(); i++) {
            IKeyboardEventListener listener = _listeners.Get(i);
            if (listener == null) {
                break;
            }
            boolean consumed = false;
            if (isBreak) {
                consumed = listener.OnKeyReleased((char) logicalKey);
            } else {
                consumed = listener.OnKeyPressed((char) logicalKey);
            }
            if (consumed) {
                Logger.Trace("KeyC", "Event consumed by ".append(Integer.toString(i, 10)));
                break;
            }
        }
    }

    private static void UpdateKeyboardState(int logicalKey, boolean isBreak) {
        switch (logicalKey) {
            case Key.LSHIFT:
            case Key.RSHIFT:
                if (isBreak) {
                    _shiftPressed = false;
                } else {
                    _shiftPressed = true;
                }
                break;
            case Key.LCTRL:
            case Key.RCTRL:
                if (isBreak) {
                    _ctrlPressed = false;
                } else {
                    _ctrlPressed = true;
                }
                break;
            case Key.LALT:
            case Key.RALT:
                if (isBreak) {
                    _altPressed = false;
                } else {
                    _altPressed = true;
                }
                break;
            case Key.CAPSLOCK:
                if (isBreak) {
                    _capsLocked = false;
                } else {
                    _capsLocked = true;
                }
                break;
        }
    }

    private static int ReadKeyCode() {
        int c0 = Integer.ubyte(_inputBuffer.Get());
        int keyCode = 0;
        if (c0 == KEYCODE_EXTEND1) {
            int c1 = Integer.ubyte(_inputBuffer.Get());

            // 0xE0_2A
            keyCode = BitHelper.SetRange(keyCode, 8, 8, c0);
            keyCode = BitHelper.SetRange(keyCode, 0, 8, c1);
        } else if (c0 == KEYCODE_EXTEND2) {
            int c1 = Integer.ubyte(_inputBuffer.Get());
            int c2 = Integer.ubyte(_inputBuffer.Get());

            // 0xE1_2A_2A
            keyCode = BitHelper.SetRange(keyCode, 16, 8, c0);
            keyCode = BitHelper.SetRange(keyCode, 8, 8, c1);
            keyCode = BitHelper.SetRange(keyCode, 0, 8, c2);
        } else {
            // 0x2A
            keyCode = Integer.ubyte(c0);
        }
        return keyCode;
    }

    /*
     * Unset the break code bits.
     * For normal codes, the 8th bit is set.
     * For E0 codes, the 8th bit is set.
     * For E1 codes, the 8th and 16th bits are set.
     */
    @SJC.Inline
    private static int UnsetBreakCode(int keyCode) {
        if (keyCode > 0xE10000) {
            return keyCode & MASK_0111111101111111;
        } else {
            return keyCode & MASK_01111111;
        }
    }

    @SJC.Inline
    private static boolean IsBreakCode(int keyCode) {
        return (keyCode & MASK_10000000) != 0;
    }

    @SJC.Inline
    private static boolean IsUpper() {
        if (_shiftPressed) {
            return true;
        }
        if (_capsLocked) {
            return true;
        }
        return false;
    }
}
