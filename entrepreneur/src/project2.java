import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
public class project2 {
    public static void main(String[] args) throws Exception{
        MyHashMap<String, Branch> branches = new MyHashMap<>(101);

        File initialFile = new File(args[0]);
        File changesFile = new File(args[1]);
        FileWriter fileWriter = new FileWriter(args[2]);

        Scanner sc1 = new Scanner(initialFile);
        Scanner sc2 = new Scanner(changesFile);

        while(sc1.hasNext()){
            String line = sc1.nextLine().strip();
            if(line.isEmpty()) continue;
            String[] lineList = line.split(",");
            if (branches.contains( lineList[0].strip() + lineList[1].strip() )){
                branches.get( lineList[0].strip() + lineList[1].strip() ).addEmployee( lineList[2].strip(), lineList[3].strip() );
            } else {
                branches.insert( lineList[0].strip() + lineList[1].strip(), new Branch( lineList[0].strip() , lineList[1].strip(), fileWriter) );
                branches.get( lineList[0].strip() + lineList[1].strip() ).addEmployee( lineList[2].strip(), lineList[3].strip() );
            }
        }
        sc1.close();

        String month = "";

        while(sc2.hasNext()){
            String line = sc2.nextLine().strip();
            String[] lineList = line.split(":");

            //if the line contains a function
            if(lineList.length > 1){
                lineList = lineList[1].split(",");
                for(int i = 0; i < lineList.length; i++){
                    lineList[i] = lineList[i].strip();
                }
            }

            if(line.startsWith("ADD")){
                String branchName = lineList[0] + lineList[1];
                branches.get( branchName ).addEmployee( lineList[2], lineList[3]);

            } else if (line.startsWith("LEAVE")) {
                String branchName = lineList[0] + lineList[1];
                branches.get( branchName ).leave( lineList[2], month);

            } else if (line.startsWith("PERFORMANCE_UPDATE")) {
                String branchName = lineList[0] + lineList[1];
                branches.get( branchName ).update( lineList[2], Integer.parseInt( lineList[3] ), month);

            } else if (line.startsWith("PRINT_MONTHLY_BONUSES:")) {
                String branchName = lineList[0] + lineList[1];
                branches.get( branchName ).printMonthlyBonus(month);

            } else if (line.startsWith("PRINT_OVERALL_BONUSES")) {
                String branchName = lineList[0] + lineList[1];
                branches.get( branchName ).printAllTimeBonus();

            } else if (line.startsWith("PRINT_MANAGER")) {
                String branchName = lineList[0] + lineList[1];
                branches.get( branchName ).printManager();

            } else if (!line.isEmpty()){
                month = line;
            }

        }
        sc2.close();
        fileWriter.close();
    }
}