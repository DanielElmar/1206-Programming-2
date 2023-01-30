public class Belt extends CyclicQueue{

    private final Object enqueueLock = new Object();
    private final Object dequeueLock = new Object();




    public Belt(int capacity) {
        super(capacity);
    }

    @Override
    public void enqueue(int i) {

        synchronized (enqueueLock) {
            if (!isEmpty && tail == head) {
                try {
                    threadWait();
                } catch (InterruptedException e) { e.printStackTrace(); }
            }

            if (head == (( tail + 1 ) % capacity)) {
                isEmpty = false;
            }

            queue[tail] = i;
            tail++;
            tail = tail % capacity;

            threadNotify();
        }
    }

    public synchronized void threadWait() throws InterruptedException {
        wait();
    }

    public synchronized void threadNotify()  {
        //System.out.println("Calling Notify!");
        notifyAll();
    }



    @Override
    public int dequeue() {



        synchronized (dequeueLock) {

            if (isEmpty) {
                try {
                    threadWait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            int returnNum = queue[head];
            head++;
            head = head % capacity;

            if (!isEmpty){
                isEmpty = true;
            }


            threadNotify();

            return returnNum;
        }
    }
}



/*public class Belt extends CyclicQueue{

    static private final Object enqueueLock = new Object();
    static private final Object dequeueLock = new Object();

    static private final Object enqueueThreadWaitingRoom = new Object();
    static private final Object dequeueThreadWaitingRoom = new Object();



    public Belt(int capacity) {
        super(capacity);
    }

    @Override
    public void enqueue(int i) {

        synchronized (enqueueLock) {
            if (isFull) {
                try {
                    //System.out.println("Waiting for value to be removed");
                    //enqueueLock.wait();
                    enqueueThreadWait();
                    //System.out.println("Value removed going to enqueue");
                } catch (InterruptedException ignored) { }
            }

            queue[tail] = i;

            tail++;
            tail = tail % capacity;
            if (head == tail) {
                isFull = true;
            }
        }



        /*try {
            dequeueThreadNotify();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }//*/

        /*synchronized (dequeueLock) {
            dequeueLock.notify();
        }//*//*

    }

    public void enqueueThreadWait() throws InterruptedException {
        synchronized (enqueueThreadWaitingRoom){
            wait();
        }
    }

    public void enqueueThreadNotify() throws InterruptedException {
        synchronized (enqueueThreadWaitingRoom){
            notify();
        }
    }

    public void dequeueThreadWait() throws InterruptedException {
        synchronized (dequeueThreadWaitingRoom){
            wait();
        }
    }

    public void dequeueThreadNotify() throws InterruptedException {
        synchronized (dequeueThreadWaitingRoom){
            notify();
        }
    }

    @Override
    public int dequeue() {

        int returnNum;

        synchronized (dequeueLock) {

            if (head == tail && !isFull) {
                try {
                    //System.out.println("dequeue Lock activated waiting for vale");
                    dequeueThreadWait();
                    //dequeueLock.wait();
                    //System.out.println("dequeue Lock released, going to dequeue");
                } catch (InterruptedException ignored) { }
            }

            returnNum = queue[head];
            head++;
            head = head % capacity;
            isFull = false;
        }



        try {
            enqueueThreadNotify();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*synchronized (enqueueLock) {
            try {
                //enqueueLock.notify();
            }catch (Exception ignored){}
        }*//*


        return returnNum;

    }
}*/
