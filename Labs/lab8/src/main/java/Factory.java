import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.Seat;
import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.SeatFactory;
import uk.ac.soton.ecs.comp1206.labtestlibrary.recursion.Tuple;

public class Factory implements SeatFactory {

    @Override
    public Tuple<Class<? extends Seat>, Class<? extends Seat>> getSeats() {

        seatType1 value1 = new seatType1();
        seatType2 value2 = new seatType2();

        //Tuple<Class<? extends Seat>, Class<? extends Seat>> tuple = new Tuple<>(seatType1.class, seatType2.class);
        //tuple.setFirstValue(seatType1.class);

        return new Tuple<>(value1.getClass(), value2.getClass());
    }
}
