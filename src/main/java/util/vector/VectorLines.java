package util.vector;

import kernel.Kernel;

public class VectorLines {
    private static final int DEFAULT_CAPACITY = 10;
    private VectorChar[] elements;
    private int size;

    public VectorLines() {
        this.elements = new VectorChar[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public VectorLines(int initialCapacity) {
        if (initialCapacity < 0)
            Kernel.panic("Illegal Capacity");
        this.elements = new VectorChar[initialCapacity];
        this.size = 0;
    }

    public void clearKeepCapacity() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    @SJC.Inline
    public void add(VectorChar element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
    }

    @SJC.Inline
    public VectorChar get(int index) {
        if (index < 0 || index >= size)
            Kernel.panic("Index out of bounds for vector access");
        return elements[index];
    }

    @SJC.Inline
    public int size() {
        return size;
    }

    @SJC.Inline
    public int capacity() {
        return elements.length;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }

            VectorChar[] newElements = new VectorChar[newCapacity];
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }
}
