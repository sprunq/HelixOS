package util;

import kernel.Kernel;

public class ByteVector {
    private static final int DEFAULT_CAPACITY = 10;
    private byte[] elements;
    private int size;

    public ByteVector() {
        this.elements = new byte[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public ByteVector(int initialCapacity) {
        if (initialCapacity < 0)
            Kernel.panic("Illegal Capacity");
        this.elements = new byte[initialCapacity];
        this.size = 0;
    }

    public void clearKeepSize() {
        size = 0;
        for (int i = 0; i < size; i++) {
            elements[i] = 0;
        }
    }

    public void add(byte element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
    }

    @SJC.Inline
    public byte get(int index) {
        if (index < 0 || index >= size)
            Kernel.panic("Index out of bounds for vector access");
        return elements[index];
    }

    @SJC.Inline
    public int size() {
        return size;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }

            byte[] newElements = new byte[newCapacity];
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }
}
