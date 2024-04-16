package kernel.hardware.keyboard;

public class ListenerPriorityMap {
    private IKeyboardEventListener[] _listeners;
    private int[] _priorities;

    public ListenerPriorityMap(int size) {
        _listeners = new IKeyboardEventListener[size];
        _priorities = new int[size];
    }

    public void addListener(IKeyboardEventListener listener, int priority) {
        for (int i = 0; i < _listeners.length; i++) {
            if (_listeners[i] == null) {
                _listeners[i] = listener;
                _priorities[i] = priority;
                return;
            }

            if (_priorities[i] < priority) {
                for (int j = _listeners.length - 1; j > i; j--) {
                    _listeners[j] = _listeners[j - 1];
                    _priorities[j] = _priorities[j - 1];
                }

                _listeners[i] = listener;
                _priorities[i] = priority;
                return;
            }
        }
    }

    public IKeyboardEventListener get(int index) {
        return _listeners[index];
    }

    public int size() {
        return _listeners.length;
    }
}
