import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.UnitCounter;

public class Counter implements UnitCounter{

    private int count;

    @Override
    public void addOne() {
        count++;
    }

    @Override
    public int getCounter() {
        return count;
    }
}
