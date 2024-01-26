import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.ArrayList;

public class Main {
    public static char plane;
    public static void main(String[] args) throws Exception {

        // String airfieldcode, Arraylist of long[2] time and weather
        HashMap< String, ArrayList<long[]> > weather = new HashMap<>();

        FileReader airportsFile = new FileReader(args[0]);
        FileReader directionsFile = new FileReader(args[1]);
        FileReader weatherFile = new FileReader(args[2]);
        FileReader missionFile = new FileReader(args[3]);

        // BufferedWriter
        BufferedWriter task1Writer = new BufferedWriter(new FileWriter(args[4]));
        BufferedWriter task2Writer = new BufferedWriter(new FileWriter(args[5]));


        // Create the AirportGraph
        AirportGraph airportGraph = new AirportGraph(weather, task1Writer, task2Writer);
        
        // Getting the plane type beforehand
        BufferedReader missionReader = new BufferedReader(missionFile);
        String line = missionReader.readLine().trim();
        plane = line.charAt(0);


        // Reading the weather file
        BufferedReader reader = new BufferedReader(weatherFile);

        // Skip the first line
        reader.readLine();

        line = reader.readLine();
        while (line != null) {
            String[] lineArray = line.split(",");

            String airfieldCode = lineArray[0].trim();
            long time = Long.parseLong(lineArray[1].trim());
            int weatherCondition = Integer.parseInt(lineArray[2].trim());

            // Update the weather map
            if (weather.containsKey(airfieldCode)) {
                weather.get(airfieldCode).add(new long[]{time, weatherCondition});
            } else {
                ArrayList<long[]> weatherConditionList = new ArrayList<>();
                weatherConditionList.add(new long[]{time, weatherCondition});
                weather.put(airfieldCode, weatherConditionList);
            }

            line = reader.readLine();
        }

        reader.close();


        // Reading the airports file (AirportCode,AirfieldName,Latitude,Longitude,ParkingCost)
        reader = new BufferedReader(airportsFile);

        // Skip the first line
        reader.readLine();

        line = reader.readLine();
        while (line != null) {
            String[] lineArray = line.split(",");

            String airportCode = lineArray[0].trim();
            String airfieldCode = lineArray[1].trim();
            double latitude = Double.parseDouble(lineArray[2].trim());
            double longitude = Double.parseDouble(lineArray[3].trim());
            double parkingCost = Double.parseDouble(lineArray[4].trim());

            // Add the new Airport to the AirportGraph
            airportGraph.addAirport(airportCode, airfieldCode, latitude, longitude, parkingCost);

            line = reader.readLine();
        }

        reader.close();

        // Reading the directions file
        reader = new BufferedReader(directionsFile);

        // Skip the first line
        reader.readLine();

        line = reader.readLine();
        while(line != null){
            String[] lineArray = line.split(",");

            String from = lineArray[0].trim();
            String to = lineArray[1].trim();

            // Connect two airports
            airportGraph.buildConnection(from, to);

            line = reader.readLine();
        }
        
        reader.close();


        // Read the mission file        
        line = missionReader.readLine();

        // Keep the missionID
        int missionID = 0;
        while(line != null){
            String[] lineArray = line.split(" ");

            String from = lineArray[0].trim();
            String to = lineArray[1].trim();
            long startTime = Long.parseLong(lineArray[2].trim());
            long deadline = Long.parseLong(lineArray[3].trim());

            // Find the shortest path
            airportGraph.findShortestPathTask1(from, to, startTime, missionID);
            airportGraph.findShortestPath(from, to, startTime, deadline, missionID);

            // Increment the missionID
            missionID++;

            line = missionReader.readLine();
        }

        missionReader.close();

        task1Writer.close();
        task2Writer.close();   
    }
}