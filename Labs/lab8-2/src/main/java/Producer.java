import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.FactoryWorker;
import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.NumberQueue;

public class Producer extends FactoryWorker {

    public Producer( int id, NumberQueue belt) {
        super("Producer", id, belt);
    }

    @Override
    public void message(int i) {
        System.out.println( "Producer " + id + " produced " + i );
    }

    @Override
    public int action() {
        belt.enqueue((int) Math.round(Math.random() * 1000 ));
        return 0;
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
