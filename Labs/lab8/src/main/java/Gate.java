public class Gate implements Runnable{

    private Counter counter;
    private int guestsNo;

    public Gate(Counter counter, int guestsNo){
        this.counter = counter;
        this.guestsNo = guestsNo;
    }

    @Override
    public void run() {

        synchronized (counter) {
            for (int i = 0; i < guestsNo; i++) {
                counter.addOne();
            }
        }

    }
}
