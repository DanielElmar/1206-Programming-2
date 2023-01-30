import uk.ac.soton.ecs.comp1206.labtestlibrary.datastructure.Tree;
import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.recursion.MinimumInTree;

public class MinTree implements MinimumInTree {

    public static void main(String[] args){
        Tree tree = new Tree( 24,
                new Tree( 45,
                        null ,
                        new Tree(8, null , null) ) ,
                new Tree ( 17,
                        new Tree (74 , null , null ) ,
                        null ) );
        MinTree minTree = new MinTree();
        System.out.println("Minimum is: " + minTree.findMin(tree));
    }

    public int findMin(Tree tree){

        if ( tree.left() == null && tree.right() == null ){ return tree.getVal(); }
        if ( tree.left() != null && tree.right() != null ){ return Math.min( findMin(tree.left()), Math.min( tree.getVal(), findMin(tree.right())) ); }
        if ( tree.left() != null ){ return Math.min( tree.getVal(), findMin(tree.left())); }
        return Math.min( tree.getVal(), findMin(tree.right()));
    }

}