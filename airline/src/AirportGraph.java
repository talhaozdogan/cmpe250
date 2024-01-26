import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class AirportGraph {

    // String airport name, airport
    HashMap< String, Airport > airportsHashMap = new HashMap<>();

    // String Airport name, LinkedList for GraphEdges
    HashMap< String, LinkedList<GraphEdge> > adjacencies = new HashMap<>();

    // String airfieldcode, HashMap of <long time, int weather>
    HashMap< String, ArrayList<long[]> > weatherConditons;

    BufferedWriter task1Writer;
    BufferedWriter task2Writer;

    // Constructor
    AirportGraph(HashMap< String, ArrayList<long[]> > weatherConditons, BufferedWriter task1Writer, BufferedWriter task2Writer) {
        this.weatherConditons = weatherConditons;
        this.task1Writer = task1Writer;
        this.task2Writer = task2Writer;
    }

    // Add an airport to the graph
    public void addAirport(String airportCode, String airfieldCode, double latitude, double longitude, double parkingCost){

        // Create a new Airport
        Airport airport = new Airport(airportCode, airfieldCode, latitude, longitude, parkingCost, weatherConditons.get(airfieldCode));
        
        // Add the new Airport to the AirportGraph
        airportsHashMap.put(airportCode, airport);

    }
    
    public void buildConnection(String fromName, String toName){
        // Finding the airports
        Airport from = airportsHashMap.get(fromName);
        Airport to = airportsHashMap.get(toName);

        // Calculating the time delay
        double distance = calculateDistance(fromName, toName);
        int timeDelay = 0;

        if(Main.plane == 'C'){
            if(distance <= 175) timeDelay = 6*3600;
            else if (distance <= 350) timeDelay = 12*3600;
            else timeDelay = 18*3600;

        } else if(Main.plane == 'O'){
            if(distance <= 1500) timeDelay = 6*3600;
            else if (distance <= 3000) timeDelay = 12*3600;
            else timeDelay = 18*3600;

        } else if(Main.plane == 'S'){
            if(distance <= 500) timeDelay = 6*3600;
            else if (distance <= 1000) timeDelay = 12*3600;
            else timeDelay = 18*3600;

        } else if(Main.plane == 'T'){
            if(distance <= 2500) timeDelay = 6*3600;
            else if (distance <= 5000) timeDelay = 12*3600;
            else timeDelay = 18*3600;
        } else {
            //do nothing
        }

        // Build the connection
        if(adjacencies.containsKey(fromName) == false){
            LinkedList<GraphEdge> tempLinkedList = new LinkedList<>();
            tempLinkedList.add(new GraphEdge(from, to, distance, timeDelay));
            adjacencies.put(fromName, tempLinkedList);
        } else {
            adjacencies.get(fromName).add(new GraphEdge(from, to, distance, timeDelay));
        }

    }

    public void findShortestPath(String fromName, String toName, long startTime, long deadline, int missionID) throws Exception{
        // Finding the start instance and setting its cost to 0
        AirportInstance startInstance = airportsHashMap.get(fromName).instances.get(startTime);
        startInstance.setCost(missionID, 0);

        // Creating the priority queue
        MyMinHeap priorityQueue = new MyMinHeap(missionID);

        // Adding the start instance to the priority queue
        priorityQueue.add(startInstance);

        // While the priority queue is not empty
        while(!priorityQueue.isEmpty()){
            AirportInstance currentInstance = priorityQueue.poll();

            currentInstance.setSettled(missionID, true);
            double currentCost = currentInstance.getCost(missionID);
            String airportName = currentInstance.airport.airportName;
            
            // Traversing the adjacencies
            for(GraphEdge edge: adjacencies.get(airportName)){
                AirportInstance nextInstance = edge.to.instances.get(currentInstance.time + edge.timeDelay);
                
                if(nextInstance == null) continue;

                // Calculating the new cost
                double nextCost = currentCost + calculateCost(currentInstance, nextInstance, edge.distance);
                
                // If the new cost is less than the current cost, update the cost and add the next instance to the priority queue
                if(nextCost < nextInstance.getCost(missionID) && nextInstance.time <= deadline){
                    nextInstance.setCost(missionID, nextCost);
                    nextInstance.setPrevious(missionID, currentInstance);

                    // Add the next instance to the priority queue
                    if(nextInstance.isInHeap(missionID)){
                        priorityQueue.updateHeap(nextInstance);
                    } else {
                        priorityQueue.add(nextInstance);
                    }


                }

            }

            // Next instance may also be available for parking
            if(currentInstance.time + 6*3600 <= deadline){
                AirportInstance nextInstance = currentInstance.airport.instances.get(currentInstance.time + 6*3600);
                double nextCost = currentCost + currentInstance.airport.parkingCost;
                if(nextCost < nextInstance.getCost(missionID)){
                    nextInstance.setCost(missionID, nextCost);
                    nextInstance.setPrevious(missionID, currentInstance);

                    // Add the next instance to the priority queue
                    if(nextInstance.isInHeap(missionID)){
                        priorityQueue.updateHeap(nextInstance);
                    } else {
                        priorityQueue.add(nextInstance);
                    }

                }
            }
        }    

        // Printing the resulting path
        PriorityQueue<AirportInstance> possibleEnds = new PriorityQueue<>(Comparator.comparingDouble(instance -> instance.getCost(missionID)));
        for(AirportInstance instance : airportsHashMap.get(toName).instances.values()){
            if(instance.mission == missionID && instance.time <= deadline){
                possibleEnds.add(instance);
            }
        }

        if(possibleEnds.isEmpty()){
            //System.out.println("No possible solution.");
            task2Writer.write("No possible solution.\n");
        } else {
            ArrayList<AirportInstance> path = new ArrayList<>();

            // Add the end instance to the path
            AirportInstance endInstance = possibleEnds.poll();
            while(endInstance != null){
                path.add(endInstance);
                
                if(endInstance.airport.airportName.equals(fromName) && endInstance.time == startTime) break;
                
                endInstance = endInstance.getPrevious(missionID);
            }
            for (int i = path.size() - 1; i >= 0; i--) {
                AirportInstance instance = path.get(i);
                if(i + 1 <= path.size() -1 && instance.airport.airportName.equals(path.get(i+1).airport.airportName)){
                    //System.out.print("PARK ");
                    task2Writer.write("PARK ");
                } else {
                    //System.out.print(instance.airport.airportName + " ");
                    task2Writer.write(instance.airport.airportName + " ");
                }
            }
            //System.out.printf("%.5f\n", path.get(0).getCost(missionID));
            task2Writer.write(String.format("%.5f\n", path.get(0).getCost(missionID)));

        }

    }

    public void findShortestPathTask1(String fromName, String toName, long time, int missionID) throws Exception{
        // Give different mission IDs to different tasks
        missionID += 10000000;
       
        // Finding the start instance and setting its cost to 0
        AirportInstance startInstance = airportsHashMap.get(fromName).instances.get(time);
        startInstance.setCost(missionID, 0);

        // Creating the priority queue
        MyMinHeap priorityQueue = new MyMinHeap(missionID);

        // Adding the start instance to the priority queue
        priorityQueue.add(startInstance);

        // While the priority queue is not empty
        while(!priorityQueue.isEmpty()){
            AirportInstance currentInstance = priorityQueue.poll();

            currentInstance.setSettled(missionID, true);
            double currentCost = currentInstance.getCost(missionID);
            String airportName = currentInstance.airport.airportName;
            
            // Traversing the adjacencies
            for(GraphEdge edge: adjacencies.get(airportName)){
                AirportInstance nextInstance = edge.to.instances.get(currentInstance.time);
                
                if(nextInstance == null) continue;

                // Calculating the new cost
                double nextCost = currentCost + calculateCost(currentInstance, nextInstance, edge.distance);
                
                // If the new cost is less than the current cost, update the cost and add the next instance to the priority queue
                if(nextCost < nextInstance.getCost(missionID)){
                    nextInstance.setCost(missionID, nextCost);
                    nextInstance.setPrevious(missionID, currentInstance);

                    // Add the next instance to the priority queue
                    if(nextInstance.isInHeap(missionID)){
                        priorityQueue.updateHeap(nextInstance);
                    } else {
                        priorityQueue.add(nextInstance);
                    }
                }
            }
        }    

        ArrayList<AirportInstance> path = new ArrayList<>();
        // Add the end instance to the path
        AirportInstance endInstance = airportsHashMap.get(toName).instances.get(time);

        while(endInstance != null){
            path.add(endInstance);
                
            if(endInstance.airport.airportName.equals(fromName)) break;
                
            endInstance = endInstance.getPrevious(missionID);
        }
        for (int i = path.size() - 1; i >= 0; i--) {
            AirportInstance instance = path.get(i);
            //System.out.print(instance.airport.airportName + " ");
            task1Writer.write(instance.airport.airportName + " ");
        
        }    
        //System.out.printf("%.5f\n", path.get(0).getCost(missionID));
        task1Writer.write(String.format("%.5f\n", path.get(0).getCost(missionID)));
        
    }
    
    private double calculateDistance(String airport1Name, String airport2Name){
        // Get the ArrayList of AirportInstances for the airport1
        Airport airport1 = airportsHashMap.get(airport1Name);
        Airport airport2 = airportsHashMap.get(airport2Name);
        double lat1 = Math.toRadians(airport1.latitude);
        double lat2 = Math.toRadians(airport2.latitude);
        double lon1 = Math.toRadians(airport1.longitude);
        double lon2 = Math.toRadians(airport2.longitude);

        // Calculate the distance using Harvesine formula
        double distance = 2 * 6371 * Math.asin( Math.pow( (Math.pow( Math.sin((lat2-lat1)/2.0) ,2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow( Math.sin((lon2-lon1)/2.0) ,2)) , 0.5) );
        
        return distance;
    }

    private double calculateCost(AirportInstance from, AirportInstance to, double distance){
        double fromW = getW(from);
        double toW = getW(to);
        return 300 * fromW * toW + distance;
    }

    private double getW(AirportInstance instance){
        if(instance.W >= 0) return instance.W;

        String stringVersion = Long.toBinaryString(instance.weatherCondition);

        while(stringVersion.length() < 5){
            stringVersion = "0" + stringVersion;
        }

        int Bb = stringVersion.charAt(stringVersion.length() - 1) - 48;
        int Bh = stringVersion.charAt(stringVersion.length() - 2) - 48;
        int Bs = stringVersion.charAt(stringVersion.length() - 3) - 48;
        int Br = stringVersion.charAt(stringVersion.length() - 4) - 48;
        int Bw = stringVersion.charAt(stringVersion.length() - 5) - 48;

        double W = (Bw * 1.05+(1- Bw)) * (Br * 1.05+(1 - Br)) * (Bs * 1.10+(1 - Bs)) * (Bh * 1.15+(1 - Bh)) * (Bb * 1.20+(1 - Bb));
        instance.W = W;
        return W;
    }

}



