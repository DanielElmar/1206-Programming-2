import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.NumberQueue;

import java.util.NoSuchElementException;


public class CyclicQueue implements NumberQueue {

    protected int[] queue;
    protected int capacity;
    protected int head;
    protected int tail;
    protected Boolean isFull;
    protected Boolean isEmpty;

    public CyclicQueue(int capacity){
        this.isFull = false;
        this.isEmpty = true;
        this.capacity = capacity;
        queue = new int[capacity];
        head = 0;
        tail = 0;
    }

    public void enqueue(int i) {
        if ( !isFull ){
            queue[tail] = i;

            tail++;
            tail = tail % capacity;
            if ( head == tail ){ isFull = true; }


        }else{ throw new IndexOutOfBoundsException("Queue is Full"); }
    }

    public int dequeue() {
        if ( head != tail || isFull) {
            int returnNum = queue[head];
            head++;
            head = head % capacity;
            isFull = false;
            return returnNum;
        }else{ throw new IndexOutOfBoundsException("Queue is Empty"); }
    }

    public boolean isEmpty() {
        return ( head == tail && isFull == false);
    }

}
