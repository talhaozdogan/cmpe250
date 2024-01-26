import java.io.FileWriter;

public class AvlTree {
    private AvlNode root;
    FileWriter outFile;
    public AvlTree( FileWriter outFile ) {
        this.outFile = outFile;
        root = null;
    }

    public void insert( String name, double gms ) throws Exception
    {
        root = insert( name, gms, root );
    }
    private AvlNode insert( String name, double gms, AvlNode t ) throws Exception
    {
        if( t == null ) {
            //adding the new node
            return new AvlNode(name, gms, null, null);
        }
        if( gms < t.gms ) {
            outFile.write(t.name + " welcomed " + name + "\n");
            //recursive call for the left node if the gms to be inserted is on the left side
            t.left = insert(name, gms, t.left);
        }
        else if( gms > t.gms ){
            //recursive call for the right node if the gms to be inserted is on the right side
            outFile.write(t.name + " welcomed " + name + "\n");
            t.right = insert( name, gms, t.right );
        }

        return balance( t );
    }

    public void remove( double x ) throws Exception
    {
        root = remove( x, root, true );
    }
    private AvlNode remove( double gms, AvlNode t, boolean print ) throws Exception
    {
        if( t == null )
            return t;

        if( gms < t.gms )
            //recursive call for the left node if the gms to be removed is on the left side
            t.left = remove( gms, t.left , print );
        else if( gms > t.gms )
            //recursive call for the right node if the gms to be removed is on the right side
            t.right = remove( gms, t.right, print );

        //two children case
        else if( t.left != null && t.right != null )
        {
            AvlNode tReplace = findMin( t.right );

            if (print) outFile.write(t.name + " left the family, replaced by " + tReplace.name + "\n");

            t.gms = tReplace.gms;
            t.name = tReplace.name;
            t.right = remove( t.gms, t.right, false );
        }
        //no child case
        else if(t.left == null && t.right == null)
        {
            if (print) outFile.write(t.name + " left the family, replaced by nobody" + "\n");
            t = null;
        }
        //one child case
        else
        {
            AvlNode tReplace = (t.left != null) ? t.left : t.right;
            if (print) outFile.write(t.name + " left the family, replaced by " + tReplace.name + "\n");
            t = tReplace;
        }
        //balancing the tree after removing the member
        return balance( t );
    }

    //a method for finding the minimum member after the node t
    private AvlNode findMin( AvlNode t )
    {
        if( t == null )
            return t;
        while( t.left != null )
            t = t.left;
        return t;
    }

    //a method to find the height of a given node
    private int height( AvlNode t )
    {
        if (t == null) return -1;
        return t.height;
    }

    //rotation methods
    private AvlNode rotateWithLeftChild( AvlNode k2 )
    {
        AvlNode k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = Math.max( height( k2.left ), height( k2.right ) ) + 1;
        k1.height = Math.max( height( k1.left ), k2.height ) + 1;
        return k1;
    }

    private AvlNode rotateWithRightChild( AvlNode k1 )
    {
        AvlNode k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = Math.max( height( k1.left ), height( k1.right ) ) + 1;
        k2.height = Math.max( height( k2.right ), k1.height ) + 1;
        return k2;
    }
    private AvlNode doubleWithLeftChild( AvlNode k3 )
    {
        k3.left = rotateWithRightChild( k3.left );
        return rotateWithLeftChild( k3 );
    }
    private AvlNode doubleWithRightChild( AvlNode k1 )
    {
        k1.right = rotateWithLeftChild( k1.right );
        return rotateWithRightChild( k1 );
    }
    private AvlNode balance( AvlNode t )
    {
        if( t == null )
            return t;

        //if the left subtree is taller than the right subtree
        if( height( t.left ) - height( t.right ) > 1 )
            if( height( t.left.left ) >= height( t.left.right ) )
                //perform single rotation
                t = rotateWithLeftChild( t );
            else
                //perform double rotation
                t = doubleWithLeftChild( t );
        else
        if( height( t.right ) - height( t.left ) > 1 )
            if( height( t.right.right ) >= height( t.right.left ) )
                //perform single rotation
                t = rotateWithRightChild( t );
            else
                //perform double rotation
                t = doubleWithRightChild( t );

        // Update the height of the current node after balancing
        t.height = Math.max( height( t.left ), height( t.right ) ) + 1;
        return t;
    }

    public void target(double gms1, double gms2) throws Exception
    {
        AvlNode tempNode = root;

        while(true){
            double tempGMS = tempNode.gms;
            if(tempGMS > gms1 && tempGMS > gms2) tempNode = tempNode.left;
            else if(tempGMS < gms1 && tempGMS < gms2) tempNode = tempNode.right;
            //when they are not in the same side of the tree or we are on one of the nodes
            else{
                outFile.write("Target Analysis Result: " + tempNode.name + " " +  String.format("%.3f", tempNode.gms) + "\n");
                break;
            }
        }
    }

    public void divide() throws Exception
    {
        int[] result =  divideHelper(root);
        outFile.write( "Division Analysis Result: " + Math.max(result[0], result[1]) + "\n" );
    }
    private int[] divideHelper(AvlNode node){
        //if leaf, then 1 if we take it, 0 if we dont take it
        if(node.left == null && node.right == null)
            return new int[]{1,0};

        int[] left = {0,0};
        int[] right = {0,0};

        if(node.left != null) left = divideHelper(node.left);
        if(node.right != null) right = divideHelper(node.right);

        //num of independents if we take the current node
        int select = 1 + left[1] + right[1];
        //num of independents if we do not take the current node
        int notSelect = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);

        return new int[]{select, notSelect};
    }

    public void rank(double gms) throws Exception
    {
        //finding the rank of the given node
        int rank = findRank(gms, root, 0);
        //showing the analysis result
        outFile.write( "Rank Analysis Result:");
        rankHelper(root, rank);
        outFile.write( "\n");
    }

    private void rankHelper(AvlNode node, int rank) throws Exception
    {
        if(node == null) return;

        //we reached the given rank(at first call it was rank and now we reached the rank so rank became 0)
        if(rank == 0) {
            outFile.write(" " + node.name + " " + String.format("%.3f", node.gms));
        }
        else{
            //firstly calling the left child then the right child to ensure the order is from left to right
            rankHelper(node.left, rank-1);
            rankHelper(node.right, rank-1);
        }
    }

    //a method finds the rank of a gms value
    private int findRank(double gms, AvlNode startNode, int rankCount)
    {
        if (startNode == null) return -1;

        if(gms > startNode.gms)
            return findRank(gms, startNode.right, rankCount+1);
        else if(gms < startNode.gms)
            return findRank(gms, startNode.left, rankCount+1);
        else
            return rankCount;
    }


}
