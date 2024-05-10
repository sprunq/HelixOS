package kernel.hardware.mouse;

import kernel.interrupt.IDT;
import kernel.interrupt.PIC;
import kernel.trace.logging.Logger;
import util.BitHelper;
import util.queue.QueueMouseEvent;

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

    private static QueueMouseEvent _eventQueue;
    private static byte[] _packet;
    private static byte[] _workingPacket;
    private static int _cycle;

    public static void Initialize() {
        Install();
        SetSampleRate(100);
        SetResolution(4);
        SetScaling(1);

        _cycle = 0;
        _packet = new byte[3];
        _workingPacket = new byte[3];
        _eventQueue = new QueueMouseEvent(1024);

        int dscAddr = MAGIC.cast2Ref(MAGIC.clssDesc("MouseController"));
        int handlerOffset = IDT.CodeOffset(dscAddr, MAGIC.mthdOff("MouseController", "MouseHandler"));
        IDT.RegisterIrqHandler(IRQ_MOUSE, handlerOffset);
    }

    @SJC.Interrupt
    public static void MouseHandler() {
        byte status = MAGIC.rIOs8(PORT_STATUS);
        if (BitHelper.GetFlag(status, BIT_DATA_AVAILABLE)) {
            byte mouse_in = MAGIC.rIOs8(PORT_DATA);
            if (BitHelper.GetFlag(status, BIT_FROM_MOUSE)) {
                switch (_cycle) {
                    case 0:
                        _workingPacket[0] = mouse_in;
                        _cycle++;
                        break;
                    case 1:
                        _workingPacket[1] = mouse_in;
                        _cycle++;
                        break;
                    case 2:
                        _workingPacket[2] = mouse_in;
                        _packet[0] = _workingPacket[0];
                        _packet[1] = _workingPacket[1];
                        _packet[2] = _workingPacket[2];
                        ProcessPacket();
                        _cycle = 0;
                        break;
                }
            }
        }
        PIC.Acknowledge(IRQ_MOUSE);
    }

    private static boolean ProcessPacket() {
        int packetMetaData = _packet[0];
        int packetXMovement = _packet[1];
        int packetYMovement = _packet[2];

        if (!BitHelper.GetFlag(packetMetaData, BIT_ALWAYS_ONE)
                || BitHelper.GetFlag(packetMetaData, BIT_Y_OVERFLOW)
                || BitHelper.GetFlag(packetMetaData, BIT_X_OVERFLOW)) {
            Logger.Warning("Mouse", "Bad packet received");
            return false;
        }

        if (BitHelper.GetFlag(packetMetaData, BIT_X_SIGN)) {
            packetXMovement |= 0xFFFFFF00;
        }

        if (BitHelper.GetFlag(packetMetaData, BIT_Y_SIGN)) {
            packetYMovement |= 0xFFFFFF00;
        }

        int buttonState = 0;
        if (BitHelper.GetFlag(packetMetaData, BIT_LEFT_BTN)) {
            buttonState |= MouseEvent.LEFT_BUTTON;
        }
        if (BitHelper.GetFlag(packetMetaData, BIT_RIGHT_BTN)) {
            buttonState |= MouseEvent.RIGHT_BUTTON;
        }
        if (BitHelper.GetFlag(packetMetaData, BIT_MIDDLE_BTN)) {
            buttonState |= MouseEvent.MIDDLE_BUTTON;
        }

        MouseEvent event = _eventQueue.Peek();
        event.X_Delta = packetXMovement;
        event.Y_Delta = packetYMovement;
        event.ButtonState = buttonState;
        _eventQueue.Put(event);

        return true;
    }

    public static MouseEvent ReadEvent() {
        if (_eventQueue.IsEmpty()) {
            return null;
        }
        return _eventQueue.Get();
    }

    private static void Install() {
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

    private static void SetSampleRate(int rate) {
        Write(0xF3);
        Read();
        Write(rate);
        Read();
    }

    private static void SetResolution(int resolution) {
        Write(0xE8);
        Read();
        Write(resolution);
        Read();
    }

    private static void SetScaling(int scaling) {
        Write(0xE6);
        Read();
        Write(scaling);
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

}
