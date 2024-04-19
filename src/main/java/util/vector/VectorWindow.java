package util.vector;

import gui.ADisplayElement;
import kernel.Kernel;

public class VectorWindow {
    private static final int DEFAULT_CAPACITY = 10;
    private ADisplayElement[] elements;
    private int size;

    public VectorWindow() {
        this.elements = new ADisplayElement[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public VectorWindow(int initialCapacity) {
        if (initialCapacity < 0)
            Kernel.panic("Illegal Capacity");
        this.elements = new ADisplayElement[initialCapacity];
        this.size = 0;
    }

    public void add(ADisplayElement element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
    }

    public ADisplayElement get(int index) {
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

            ADisplayElement[] newElements = new ADisplayElement[newCapacity];
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }

    public void SortByZ() {
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (elements[i].Z > elements[j].Z) {
                    ADisplayElement temp = elements[i];
                    elements[i] = elements[j];
                    elements[j] = temp;
                }
            }
        }
    }
}
