import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.Seat;

import java.util.concurrent.locks.ReentrantLock;

// 1 seat
public class seatType2 implements Seat {

    private ReentrantLock leftFork;
    private ReentrantLock rightFork;

    // Asked FIRST
    @Override
    public void askFork1() {
        rightFork.lock();
    }

    // Asked SECOND
    @Override
    public void askFork2() {
        leftFork.lock();
    }

    // called after seating, assigns forks to the seats
    @Override
    public void assignForks(ReentrantLock reentrantLock, ReentrantLock reentrantLock1) {
        leftFork = reentrantLock;
        rightFork = reentrantLock1;
    }
}
