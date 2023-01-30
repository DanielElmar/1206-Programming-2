import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.Seat;

import java.util.concurrent.locks.ReentrantLock;

// 4 seats
public class seatType1 implements Seat {

    private ReentrantLock leftFork;
    private ReentrantLock rightFork;



    // Asked FIRST
    @Override
    public void askFork1() {
        leftFork.lock();

        //if (leftFork.isLocked() && rightFork.isLocked()){}
        //else{}
    }

    // Asked SECOND
    @Override
    public void askFork2() {
        rightFork.lock();

        /*if (leftFork.isHeldByCurrentThread() || rightFork.isHeldByCurrentThread()){
            leftFork.lock();
            rightFork.lock();
        }*/
    }

    // called after seating, assigns forks to the seats
    @Override
    public void assignForks(ReentrantLock reentrantLock, ReentrantLock reentrantLock1) {
        leftFork = reentrantLock;
        rightFork = reentrantLock1;
    }
}
