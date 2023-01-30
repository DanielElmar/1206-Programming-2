import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.NumberQueue;

import java.util.NoSuchElementException;


public class CyclicQueue implements NumberQueue {

    private int[] queue;
    private int capacity;
    private int head;
    private int tail;
    private Boolean isFull;

    public CyclicQueue(int capacity){
        this.isFull = false;
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
        if ( head != tail || isFull == true) {
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
