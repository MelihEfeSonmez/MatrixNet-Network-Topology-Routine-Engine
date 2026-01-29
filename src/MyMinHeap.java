public class MyMinHeap<E> {

    // ---DATA FIELDS---
    private Object[] heap; // Array-based heap
    private int size;
    private int capacity;

    // ---CONSTRUCTORS---
    public MyMinHeap(int initialCapacity) {
        this.capacity = initialCapacity;
        this.size = 0;
        this.heap = new Object[initialCapacity];
    }

    // ---METHODS---
    // Adds a new element
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException("MyMinHeap does not permit null elements.");
        }

        // Checks if capacity is full
        if (size >= capacity) {
            grow();
        }

        // Hole strategy
        int current = size;
        heap[size] = element;
        size++;

        percUp(current, element); // Keeps heap property valid
        return true;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked") // Prevents compiler warnings
    public E poll() {
        if (size == 0) return null;

        E root = (E) heap[0];
        size--;

        E last = (E) heap[size];
        heap[size] = null;

        if (size > 0) {
            percDown(0, last); // Keeps heap property valid
        }

        return root;
    }

    // ---GENERAL HELPERS---
    // Grows heap (array)
    private void grow() {
        int newCapacity = capacity * 2;

        // Creates new heap
        Object[] newHeap = new Object[newCapacity];

        // Moves old elements
        for (int i = 0; i < capacity; i++) {
            newHeap[i] = heap[i];
        }

        // Updates
        heap = newHeap;
        capacity = newCapacity;
    }

    // Helps to keep heap property valid by percolating up
    @SuppressWarnings("unchecked") // Prevents compiler warnings
    private void percUp(int index, E element) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            E parent = (E) heap[parentIndex];

            // Hole strategy
            if (compare(element, parent) < 0) {
                heap[index] = parent;
                index = parentIndex;
            } else {
                break;
            }
        }
        heap[index] = element;
    }

    // Helps to keep heap property valid by percolating down
    @SuppressWarnings("unchecked")
    private void percDown(int index, E element) {
        int half = size / 2; // Nodes that have children
        while (index < half) {
            int leftChild = 2 * index + 1;
            int rightChild = leftChild + 1;
            int smallestChild = leftChild;
            E childVal = (E) heap[leftChild];

            // Compares children
            if (rightChild < size) {
                E rightVal = (E) heap[rightChild];
                if (compare(rightVal, childVal) < 0) {
                    smallestChild = rightChild;
                    childVal = rightVal;
                }
            }

            // Checks if child is hte minimum
            if (compare(element, childVal) <= 0) {
                break;
            }

            heap[index] = childVal;
            index = smallestChild;
        }
        heap[index] = element;
    }

    // Compares two elements (assuming objects are Router objects)
    @SuppressWarnings("unchecked") // Prevents compiler warnings
    private int compare(E e1, E e2) {
        // Router casting
        Router r1 = (Router) e1;
        Router r2 = (Router) e2;

        // 1) Total latency comparison
        if (r1.getTotalLatency() != r2.getTotalLatency()) {
            return (r1.getTotalLatency() < r2.getTotalLatency()) ? -1 : 1;
        }
        // 2) Number of segments comparison
        if (r1.getNumSegments() != r2.getNumSegments()) {
            return (r1.getNumSegments() < r2.getNumSegments()) ? -1 : 1;
        }
        // 3) Lexicographical comparison
        return r1.getHost().getHostID().compareTo(r2.getHost().getHostID());
    }

}
