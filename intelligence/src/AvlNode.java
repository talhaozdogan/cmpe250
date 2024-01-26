public class AvlNode {
    String name;
    double gms;
    AvlNode left;
    AvlNode right;
    int height = 0;

    AvlNode(){}
    AvlNode(String name, double gms, AvlNode leftNode, AvlNode rightNode){
        this.name = name;
        this.gms = gms;
        this.left = leftNode;
        this.right = rightNode;
    }


}