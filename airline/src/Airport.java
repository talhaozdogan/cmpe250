import java.util.ArrayList;
import java.util.HashMap;

public class Airport {
    String airportName;
    String airfieldCode;
    double latitude;
    double longitude;
    double parkingCost;
    ArrayList<long[]> weatherConditions;
    HashMap<Long, AirportInstance> instances = new HashMap<>();

    Airport(String airportName, String airfieldCode,double latitude, double longitude, double parkingCost, ArrayList<long[]> weatherConditions) {
        this.airportName = airportName;
        this.airfieldCode = airfieldCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parkingCost = parkingCost;
        this.weatherConditions = weatherConditions;
        this.createInstances();
    }

    private void createInstances(){
        for(int i = 0; i < weatherConditions.size(); i++){
            AirportInstance tempInstance = new AirportInstance(this, weatherConditions.get(i)[0], weatherConditions.get(i)[1]);
            instances.put(weatherConditions.get(i)[0], tempInstance);
        }
    }
}
