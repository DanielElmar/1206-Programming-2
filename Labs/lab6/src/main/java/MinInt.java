import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.recursion.MinimumInArray;

public class MinInt implements MinimumInArray {

    public static void main(String[] numbers){
        int[] arr = {24,52,74,9,34,23,64,34};
        MinInt minInt = new MinInt();
        System.out.println("Minimum is: " + minInt.findMin(arr));
    }

    /*public int findMin(int[] ints){

		if (ints.length == 1){ return ints[0]; }

        var newInts = new int[ints.length - 1];

		if ( ints[0] >= ints[1] ){

            for (int i = 1; i < ints.length; i++) {
                newInts[i - 1] = ints[i];
            }

        }else{
            newInts[0] = ints[0];

            for (int i = 2; i < ints.length ; i++) {
                newInts[i - 1] = ints[i];
            }
        }

        return findMin(newInts);

    }*/


    public int findMin(int[] ints){

        if (ints.length == 1){ return ints[0]; }

        return Math.min( ints[ints.length - 1], findMinAux(ints.length - 2, ints) );
    }


    private int findMinAux(int index ,int[] ints){

        if (index == 0) { return ints[0]; }
        return Math.min( ints[index], findMinAux( index - 1, ints ));

    }

}