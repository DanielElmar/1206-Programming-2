import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DAGSortTest {

    DAGSort DAG;

    @BeforeEach
    void setUp() {
        DAG = new DAGSort();
    }

    @AfterEach
    void tearDown() {
        DAG = null;
    }

    @Test
    void testSortDAGReturn() throws CycleDetectedException, InvalidNodeException {

        int[][] graph = new int[][]{{3},{3,4},{4,7},{5,6,7},{6},{},{},{}};

        assertTrue(  checkArray(DAG.sortDAG( graph ) , graph ));
    }

    private Boolean checkArray(int[] sort, int[][] graph){
        Boolean found;
        if (sort.length != graph.length){ return false; }
        for (int i = 0; i < sort.length; i++) {
            found = false;
            for (int j = 0; j < graph.length; j++) {
                for (int k = 0; k < graph[j].length; k++) {
                    if ( graph[j][k] == sort[i] ){ return false; }
                }

                // is every node given by i in sort??
                if (sort[j] == i){ found = true; }

            }
            graph[sort[i]] = new int[] {};
            if ( !found ){ return false; }
        }
        return true;
    }

    @Test
    void testSortDAGCycleDetected() throws CycleDetectedException, InvalidNodeException {
        assertThrows(CycleDetectedException.class , () -> DAG.sortDAG( new int[][]{{3},{3,4},{4,7},{5,6,7},{6},{},{},{1}} ) , "Test 2");
    }

    @Test
    void testSortDAGInvalidNode() throws CycleDetectedException, InvalidNodeException {
        assertThrows(InvalidNodeException.class , () -> DAG.sortDAG( new int[][]{{3},{3,4},{4,7},{5,6,7},{6},{},{},{9}} ) , "Test 3");
    }

    @Test
    void testSortDAGNullPointer() throws CycleDetectedException, InvalidNodeException {
        assertThrows(NullPointerException.class , () -> DAG.sortDAG( null ) , "Test 4");
    }

}