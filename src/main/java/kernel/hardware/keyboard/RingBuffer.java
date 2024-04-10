package kernel.hardware.keyboard;

public class RingBuffer {
    private final int size;
    private final byte[] buffer;
    private int headIdx;
    private int tailIdx;
    private int count;

    public RingBuffer(int size) {
        this.size = size;
        buffer = new byte[size];
        headIdx = 0;
        tailIdx = 0;
        count = 0;
    }

    public void put(byte c) {
        buffer[headIdx] = c;
        incHead();
    }

    public byte get() {
        byte c = buffer[tailIdx];
        incTail();
        return c;
    }

    @SJC.Inline
    public int getCount() {
        return count;
    }

    @SJC.Inline
    public boolean isEmpty() {
        return count == 0;
    }

    @SJC.Inline
    public boolean containsNewElements() {
        return headIdx != tailIdx;
    }

    @SJC.Inline
    private void incHead() {
        headIdx = (headIdx + 1) % size;
        count++;
    }

    @SJC.Inline
    private void incTail() {
        tailIdx = (tailIdx + 1) % size;
        count--;
    }
}
