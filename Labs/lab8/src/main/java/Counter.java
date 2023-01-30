import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.UnitCounter;

public class Counter implements UnitCounter{

    private int count;
    private final Object lock = new Object();


    @Override
    public void addOne() {
        //synchronized (lock) {
            count++;

        //}
    }

    @Override
    public int getCounter() {
        return count;
    }
}
