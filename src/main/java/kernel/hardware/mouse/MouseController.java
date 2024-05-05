package kernel.hardware.mouse;

import kernel.interrupt.IDT;
import kernel.interrupt.PIC;
import kernel.trace.logging.Logger;
import rte.SClassDesc;
import util.BitHelper;
import util.queue.QueueByte;

public class MouseController {
    public static final int IRQ_MOUSE = 12;

    // Status byte bits
    private static final int BIT_DATA_AVAILABLE = 0;
    private static final int BIT_FROM_MOUSE = 5;

    // Packet 0 byte bits
    private static final int BIT_LEFT_BTN = 0;
    private static final int BIT_RIGHT_BTN = 1;
    private static final int BIT_MIDDLE_BTN = 2;
    private static final int BIT_ALWAYS_ONE = 3;
    private static final int BIT_X_SIGN = 4;
    private static final int BIT_Y_SIGN = 5;
    private static final int BIT_Y_OVERFLOW = 6;
    private static final int BIT_X_OVERFLOW = 7;

    // Ports
    private static final int PORT_DATA = 0x60;
    private static final int PORT_STATUS = 0x64;

    // Commands
    private static final int CMD_WRITE = 0xD4;

    private static QueueByte _inputBuffer;

    public static void Initialize() {
        Install();
        SetSampleRate(60);
        int dscAddr = MAGIC.cast2Ref((SClassDesc) MAGIC.clssDesc("MouseController"));
        int handlerOffset = IDT.CodeOffset(dscAddr, MAGIC.mthdOff("MouseController", "MouseHandler"));
        IDT.RegisterIrqHandler(IRQ_MOUSE, handlerOffset);
    }

    public static MouseEvent Event = new MouseEvent();

    private static int cycle = 0;
    private static int packetMetaData = 0;
    private static int packetYMovement = 0;
    private static int packetXMovement = 0;
    private static int buttonState = 0;

    @SJC.Interrupt
    public static void MouseHandler() {
        byte status = MAGIC.rIOs8(PORT_STATUS);
        if (BitHelper.GetFlag(status, BIT_DATA_AVAILABLE)) {
            byte mouse_in = MAGIC.rIOs8(PORT_DATA);
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
                        if (BitHelper.GetFlag(packetMetaData, BIT_LEFT_BTN)) {
                            buttonState |= MouseEvent.LEFT_BUTTON;
                        }
                        if (BitHelper.GetFlag(packetMetaData, BIT_RIGHT_BTN)) {
                            buttonState |= MouseEvent.RIGHT_BUTTON;
                        }
                        if (BitHelper.GetFlag(packetMetaData, BIT_MIDDLE_BTN)) {
                            buttonState |= MouseEvent.MIDDLE_BUTTON;
                        }
                        cycle++;
                        break;
                    case 1:
                        packetXMovement = Integer.ubyte(mouse_in);
                        if (BitHelper.GetFlag(packetMetaData, BIT_X_SIGN)) {
                            packetXMovement |= 0xFFFFFF00;
                        }
                        cycle++;
                        break;
                    case 2:
                        packetYMovement = Integer.ubyte(mouse_in);
                        if (BitHelper.GetFlag(packetMetaData, BIT_Y_SIGN)) {
                            packetYMovement |= 0xFFFFFF00;
                        }

                        if (Math.Abs(packetXMovement) < 3)
                            packetXMovement = 0;
                        if (Math.Abs(packetYMovement) < 3)
                            packetYMovement = 0;

                        Event.X_Delta = packetXMovement;
                        Event.Y_Delta = packetYMovement;
                        Event.ButtonState = buttonState;

                        cycle = 0;
                        buttonState = 0;
                        packetMetaData = 0;
                        packetYMovement = 0;
                        packetXMovement = 0;

                        break;
                }
            }
        }
        PIC.Acknowledge(IRQ_MOUSE);
    }

    @SJC.Inline
    public static boolean HasNewEvent() {
        return _inputBuffer.ContainsNewElements();
    }

    public static void Install() {
        Wait(1);
        MAGIC.wIOs8(PORT_STATUS, (byte) 0xA8);
        Wait(1);
        MAGIC.wIOs8(PORT_STATUS, (byte) 0x20);
        Wait(0);
        int status = MAGIC.rIOs8(0x60) | 2;
        Wait(1);
        MAGIC.wIOs8(PORT_STATUS, (byte) 0x60);
        Wait(1);
        MAGIC.wIOs8(PORT_DATA, (byte) status);
        Write(0xF6);
        Read();
        Write(0xF4);
        Read();
    }

    private static void Write(int data) {
        Wait(1);
        MAGIC.wIOs8(PORT_STATUS, (byte) CMD_WRITE);
        Wait(1);
        MAGIC.wIOs8(PORT_DATA, (byte) data);
    }

    private static int Read() {
        Wait(0);
        return Integer.ubyte(MAGIC.rIOs8(PORT_DATA));
    }

    private static void Wait(int type) {
        int timeout = 100000;
        if (type == 0) {
            while (--timeout > 0) {
                if (BitHelper.GetFlag(MAGIC.rIOs8(PORT_STATUS), 0)) {
                    return;
                }
            }
            Logger.Warning("Mouse", "Mouse timeout");
            return;
        } else {
            while (--timeout > 0) {
                if (!BitHelper.GetFlag(MAGIC.rIOs8(PORT_STATUS), 1)) {
                    return;
                }
            }
            Logger.Warning("Mouse", "Mouse timeout");
            return;
        }
    }

    private static void SetSampleRate(int rate) {
        Write(0xF3);
        Read();
        Write(rate);
        Read();
    }
}
