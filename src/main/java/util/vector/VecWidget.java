package util.vector;

import gui.Widget;
import kernel.Kernel;

public class VecWidget {
    private static final int DEFAULT_CAPACITY = 10;
    private Widget[] elements;
    private int size;

    public VecWidget() {
        this.elements = new Widget[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public VecWidget(int initialCapacity) {
        if (initialCapacity < 0)
            Kernel.panic("Illegal Capacity");
        this.elements = new Widget[initialCapacity];
        this.size = 0;
    }

    public void add(Widget element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
    }

    public void remove(Widget element) {
        for (int i = 0; i < size; i++) {
            if (elements[i] == element) {
                remove(i);
                return;
            }
        }
    }

    public void remove(int index) {
        if (index < 0 || index >= size)
            Kernel.panic("Index out of bounds for vector removal");
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        size--;
    }

    public Widget get(int index) {
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

            Widget[] newElements = new Widget[newCapacity];
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }

    public Widget MaxSelectable() {
        for (int i = size - 1; i >= 0; i--) {
            if (elements[i].IsSelectable()) {
                return elements[i];
            }
        }

        return null;
    }

    public Widget NextSelectable(Widget current) {
        if (current == null) {
            return null;
        } else {
            for (int i = 0; i < size; i++) {
                if (elements[i] == current) {
                    for (int j = 1; j < size; j++) {
                        int next = (i + j) % (size);
                        Widget widget = elements[next];
                        if (widget.IsSelectable()) {
                            return elements[next];
                        }
                    }
                }
            }
        }
        return null;
    }
}
