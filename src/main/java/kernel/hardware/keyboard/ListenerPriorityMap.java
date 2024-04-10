package kernel.hardware.keyboard;

public class ListenerPriorityMap {
    private IKeyboardEventListener[] listeners;
    private int[] priorities;

    public ListenerPriorityMap(int size) {
        listeners = new IKeyboardEventListener[size];
        priorities = new int[size];
    }

    public void addListener(IKeyboardEventListener listener, int priority) {
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == null) {
                listeners[i] = listener;
                priorities[i] = priority;
                return;
            }

            if (priorities[i] < priority) {
                for (int j = listeners.length - 1; j > i; j--) {
                    listeners[j] = listeners[j - 1];
                    priorities[j] = priorities[j - 1];
                }

                listeners[i] = listener;
                priorities[i] = priority;
                return;
            }
        }
    }

    public IKeyboardEventListener get(int index) {
        return listeners[index];
    }

    public int size() {
        return listeners.length;
    }
}
