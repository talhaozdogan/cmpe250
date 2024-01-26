import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception{
        String inputFileName = args[0];
        String outputFileName = args[1];

        File file = new File(inputFileName);
        FileWriter fileWriter = new FileWriter(outputFileName);

        Scanner sc = new Scanner(file);

        AvlTree tree = new AvlTree(fileWriter);

        //inserting the root
        String line = sc.nextLine();
        String[] lineElements = line.split(" ");
        tree.insert( lineElements[0], Double.parseDouble(lineElements[1]) );

        while (sc.hasNextLine()){
            line = sc.nextLine();
            lineElements = line.split(" ");
            if(line.startsWith("MEMBER_IN")){
                tree.insert( lineElements[1], Double.parseDouble(lineElements[2]) );
            }
            else if(line.startsWith("INTEL_DIVIDE")){
                tree.divide();
            }
            else if(line.startsWith("MEMBER_OUT")){
                tree.remove( Double.parseDouble(lineElements[2]) );
            }
            else if(line.startsWith("INTEL_TARGET")){
                tree.target( Double.parseDouble(lineElements[2]), Double.parseDouble(lineElements[4]) );
            }
            else if(line.startsWith("INTEL_RANK")){
                tree.rank( Double.parseDouble(lineElements[2]) );
            }
        }

        fileWriter.close();
        sc.close();
    }
}