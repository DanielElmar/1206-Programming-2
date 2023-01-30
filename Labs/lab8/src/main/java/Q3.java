import java.util.ArrayList;

public class Q3 {


    public static void main(String[] args) {

       var queue = new Belt(50000);

       var producers = new ArrayList<>();
       var consumers = new ArrayList<>();

        var producerThreads = new ArrayList<>();
        var consumerThreads = new ArrayList<>();



        for (int i = 0; i < 5; i++) {
            producers.add( new Producer(i, queue) );
            consumers.add( new Consumer(i, queue) );

            producerThreads.add( new Thread( (Runnable) producers.get(i)) );
            consumerThreads.add( new Thread( (Runnable) consumers.get(i)) );


            ((Thread)consumerThreads.get(i)).start();
            ((Thread)producerThreads.get(i)).start();

        }

    }

}
