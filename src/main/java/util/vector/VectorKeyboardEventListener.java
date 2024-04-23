package util.vector;

import kernel.Kernel;
import kernel.hardware.keyboard.IKeyboardEventListener;

public class VectorKeyboardEventListener {
    private static final int DEFAULT_CAPACITY = 10;
    private IKeyboardEventListener[] elements;
    private int size;

    public VectorKeyboardEventListener() {
        this.elements = new IKeyboardEventListener[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public VectorKeyboardEventListener(int initialCapacity) {
        if (initialCapacity < 0)
            Kernel.panic("Illegal Capacity");
        this.elements = new IKeyboardEventListener[initialCapacity];
        this.size = 0;
    }

    public void add(IKeyboardEventListener element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
    }

    public IKeyboardEventListener get(int index) {
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

            IKeyboardEventListener[] newElements = new IKeyboardEventListener[newCapacity];
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }

    public void SortByPriority() {
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (elements[i].priority() > elements[j].priority()) {
                    IKeyboardEventListener temp = elements[i];
                    elements[i] = elements[j];
                    elements[j] = temp;
                }
            }
        }
    }
}
