
public class Belt extends CyclicQueue{

    private final Object enqueueLock = new Object();
    private final Object dequeueLock = new Object();


    public Belt(int capacity) {
        super(capacity);
    }

    @Override
    public void enqueue(int i) {

        synchronized (enqueueLock) {
            if (isFull) {
                try {
                    threadWait();
                } catch (InterruptedException ignored) { }
            }

            queue[tail] = i;

            tail++;
            tail = tail % capacity;
            if (head == tail) {
                isFull = true;
            }
        }

        try {
            threadNotify();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void threadWait() throws InterruptedException {
            wait();
    }

    public synchronized void threadNotify() throws InterruptedException {
            notifyAll();
    }

    @Override
    public int dequeue() {

        int returnNum;

        synchronized (dequeueLock) {

            if (head == tail && !isFull) {
                try {
                    threadWait();
                } catch (InterruptedException ignored) { }
            }

            returnNum = queue[head];
            head++;
            head = head % capacity;
            isFull = false;
        }


        try {
            threadNotify();
        } catch (InterruptedException ignored) { }

        return returnNum;

    }
}
