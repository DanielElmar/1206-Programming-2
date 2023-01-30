import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.FactoryWorker;
import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.NumberQueue;

public class Consumer extends FactoryWorker {

    public Consumer( int id, NumberQueue belt) {
        super("Consumer", id, belt);
    }


    @Override
    public void message(int i) {
        System.out.println( "Consumer " + id +" picked " + i + " from the belt.");
    }

    @Override
    public int action() {
        return belt.dequeue();
    }

    @Override
    public void run() {
        while ( !Thread.currentThread().isInterrupted() ){
            try {
                action();
            }catch (Exception e){
                messageError();
            }
        }
    }
}
