import org.checkerframework.checker.units.qual.C;

public class Zoo {


    public static void main(String[] args) throws InterruptedException {

        Counter counter = new Counter();

        Gate gate1 = new Gate(counter, 2);
        Gate gate2 = new Gate(counter, 5);
        Gate gate3 = new Gate(counter, 9);

        var t1 = new Thread(gate1);
        var t2 = new Thread(gate2);
        var t3 = new Thread(gate3);

        t1.start();
        t1.join();
        t2.start();
        t2.join();
        t3.start();
        t3.join();

        System.out.println("Exspected: " + 16 + " actuall: " + counter.getCounter() );


    }

}
