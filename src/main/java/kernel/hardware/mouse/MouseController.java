package kernel.hardware.mouse;

import kernel.Kernel;
import kernel.hardware.keyboard.KeyboardController;
import kernel.interrupt.IDT;
import kernel.interrupt.PIC;
import kernel.trace.logging.Logger;
import rte.SClassDesc;
import util.BitHelper;
import util.queue.QueueByte;

public class MouseController {
    private static final int BIT_MIDDLE_BTN = 2;
    private static final int BIT_RIGHT_BTN = 1;
    private static final int BIT_LEFT_BTN = 0;
    private static final int BIT_DATA_AVAILABLE = 0;
    private static final int BIT_FROM_MOUSE = 5;
    private static final int BIT_X_OVERFLOW = 7;
    private static final int BIT_Y_OVERFLOW = 6;
    private static final int BIT_ALWAYS_ONE = 3;

    public static final int IRQ_MOUSE = 12;

    private static final int MOUSE_PORT = 0x60;
    private static final int MOUSE_STATUS = 0x64;
    private static final int MOUSE_ABIT = 0x02;
    private static final int MOUSE_BBIT = 0x01;
    private static final int MOUSE_WRITE = 0xD4;

    private static QueueByte _inputBuffer;

    public static void Initialize() {
        Install();
        int dscAddr = MAGIC.cast2Ref((SClassDesc) MAGIC.clssDesc("MouseController"));
        int handlerOffset = IDT.CodeOffset(dscAddr, MAGIC.mthdOff("MouseController", "MouseHandler"));
        IDT.RegisterIrqHandler(IRQ_MOUSE, handlerOffset);
    }

    public static MouseEvent Event = new MouseEvent();

    @SJC.Interrupt
    public static void MouseHandler() {
        int cycle = 0;
        int packetMetaData = 0;
        int packetXMovement = 0;
        int packetYMovement = 0;

        byte status = MAGIC.rIOs8(MOUSE_STATUS);
        while (BitHelper.GetFlag(status, BIT_DATA_AVAILABLE)) {
            byte mouse_in = MAGIC.rIOs8(MOUSE_PORT);
            if (BitHelper.GetFlag(status, BIT_FROM_MOUSE)) {
                switch (cycle) {
                case 0:
                    packetMetaData = mouse_in;
                    if (!BitHelper.GetFlag(packetMetaData, BIT_ALWAYS_ONE)
                            || BitHelper.GetFlag(packetMetaData, BIT_Y_OVERFLOW)
                            || BitHelper.GetFlag(packetMetaData, BIT_X_OVERFLOW)) {
                        // bad packet
                        Logger.Warning("Mouse", "Bad packet received");
                        return;
                    }
                    cycle++;
                    break;
                case 1:
                    packetXMovement = mouse_in;
                    cycle++;
                    break;
                case 2:
                    packetYMovement = mouse_in;

                    int buttonState = 0;
                    if (BitHelper.GetFlag(packetMetaData, BIT_LEFT_BTN)) buttonState |= MouseEvent.LEFT_BUTTON;
                    if (BitHelper.GetFlag(packetMetaData, BIT_RIGHT_BTN)) buttonState |= MouseEvent.RIGHT_BUTTON;
                    if (BitHelper.GetFlag(packetMetaData, BIT_MIDDLE_BTN)) buttonState |= MouseEvent.MIDDLE_BUTTON;
                    Event.X_Delta = packetXMovement;
                    Event.Y_Delta = packetYMovement;
                    Event.ButtonState = buttonState;
                    break;
                }
            }
            status = MAGIC.rIOs8(MOUSE_STATUS);
        }
        PIC.Acknowledge(IRQ_MOUSE);
    }

    @SJC.Inline
    public static boolean HasNewEvent() {
        return _inputBuffer.ContainsNewElements();
    }

    private static byte ReadMouseStatus() {
        return MAGIC.rIOs8(MOUSE_STATUS);
    }

    public static void Install() {
        Logger.Info("Mouse", "Initializing PS/2 mouse interface");

        Wait(1);
        MAGIC.wIOs8(MOUSE_STATUS, (byte) 0xA8);
        Wait(1);
        MAGIC.wIOs8(MOUSE_STATUS, (byte) 0x20);
        Wait(0);
        int status = MAGIC.rIOs8(0x60) | 2;
        Wait(1);
        MAGIC.wIOs8(MOUSE_STATUS, (byte) 0x60);
        Wait(1);
        MAGIC.wIOs8(MOUSE_PORT, (byte) status);
        Write(0xF6);
        Read();
        Write(0xF4);
        Read();
    }

    private static void Write(int data) {
        Wait(1);
        MAGIC.wIOs8(MOUSE_STATUS, (byte) MOUSE_WRITE);
        Wait(1);
        MAGIC.wIOs8(MOUSE_PORT, (byte) data);
    }

    private static int Read() {
        Wait(0);
        return Integer.ubyte(MAGIC.rIOs8(MOUSE_PORT));
    }

    private static void Wait(int type) {
        int timeout = 100000;
        if (type == 0) {
            while (--timeout > 0) {
                if ((ReadMouseStatus() & MOUSE_BBIT) == 1) {
                    return;
                }
            }
            Logger.Warning("Mouse", "Mouse timeout");
            return;
        } else {
            while (--timeout > 0) {
                if ((ReadMouseStatus() & MOUSE_ABIT) == 0) {
                    return;
                }
            }
            Logger.Warning("Mouse", "Mouse timeout");
            return;
        }
    }
}
