import org.checkerframework.checker.units.qual.C;

public class Zoo {


    public static void main(String[] args) throws InterruptedException {

        Counter counter = new Counter();

        var temp = 4000;

        Gate gate1 = new Gate(counter, temp);
        Gate gate2 = new Gate(counter, temp);
        Gate gate3 = new Gate(counter, temp);

        var t1 = new Thread(gate1);
        var t2 = new Thread(gate2);
        var t3 = new Thread(gate3);


        t1.start();
        t2.start();
        t3.start();


        t3.join();
        t2.join();
        t1.join();


        System.out.println("Exspected: " + temp * 3 + " actuall: " + counter.getCounter() );


    }

}
