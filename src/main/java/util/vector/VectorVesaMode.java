package util.vector;

import kernel.Kernel;
import kernel.display.vesa.VesaMode;

public class VectorVesaMode {
    private static final int DEFAULT_CAPACITY = 10;
    private VesaMode[] elements;
    private int size;

    public VectorVesaMode() {
        this.elements = new VesaMode[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public VectorVesaMode(int initialCapacity) {
        if (initialCapacity < 0)
            Kernel.panic("Illegal Capacity");
        this.elements = new VesaMode[initialCapacity];
        this.size = 0;
    }

    public void add(VesaMode element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
    }

    public VesaMode get(int index) {
        if (index < 0 || index >= size)
            Kernel.panic("Index out of bounds for vector access");
        return elements[index];
    }

    public int size() {
        return size;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }

            VesaMode[] newElements = new VesaMode[newCapacity];
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }
}
