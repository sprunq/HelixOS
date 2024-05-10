package util.queue;

import kernel.hardware.mouse.MouseEvent;
import kernel.trace.logging.Logger;

public class QueueMouseEvent {
    private final int _size;
    private final MouseEvent[] _buffer;
    private int _headIdx;
    private int _tailIdx;
    private int _count;

    public QueueMouseEvent(int size) {
        this._size = size;
        _buffer = new MouseEvent[size];
        for (int i = 0; i < size; i++) {
            _buffer[i] = new MouseEvent(0, 0, 0);
        }
        _headIdx = 0;
        _tailIdx = 0;
        _count = 0;
    }

    public void Put(MouseEvent c) {
        _buffer[_headIdx] = c;
        IncHead();

        if (_headIdx == _tailIdx) {
            IncTail();
        }
    }

    public MouseEvent Get() {
        MouseEvent c = _buffer[_tailIdx];
        IncTail();
        return c;
    }

    public MouseEvent Peek() {
        return _buffer[_tailIdx];
    }

    @SJC.Inline
    public int Count() {
        return _count;
    }

    @SJC.Inline
    public int Capacity() {
        return _size;
    }

    @SJC.Inline
    public boolean IsEmpty() {
        return _count == 0;
    }

    @SJC.Inline
    public boolean ContainsNewElements() {
        return _headIdx != _tailIdx;
    }

    @SJC.Inline
    public void IncHead() {
        _headIdx = (_headIdx + 1) % _size;
        _count++;

        if (_headIdx == _tailIdx) {
            Logger.Warning("Queue", "Queue is full");
            IncTail();
        }
    }

    @SJC.Inline
    public void IncTail() {
        _tailIdx = (_tailIdx + 1) % _size;
        _count--;
    }
}
