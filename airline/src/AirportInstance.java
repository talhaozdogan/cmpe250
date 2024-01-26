public class AirportInstance {
    Airport airport;
    long time;
    long weatherCondition;
    double cost;
    int mission = -1;
    boolean settled = false;
    AirportInstance previous = null;
    double W = -1.0;
    int heapPosition = -1;

    AirportInstance(Airport airport, long time, long weatherCondition) {
        this.airport = airport;
        this.time = time;
        this.weatherCondition = weatherCondition;
    }

    public double getCost(int missionID){
        if(missionID == mission) return cost;
        return Double.MAX_VALUE;
    }

    public void setCost(int missionID, double cost){
        this.cost = cost;
        this.mission = missionID;
    }

    public boolean isSettled(int missionID){
        if(missionID == mission) return settled;
        return false;
    }

    public void setSettled(int missionID, boolean settled){
        this.settled = settled;
        this.mission = missionID;
    }

    public AirportInstance getPrevious(int missionID){
        if(missionID == mission) return previous;
        return null;
    }

    public void setPrevious(int missionID, AirportInstance previous){
        this.previous = previous;
        this.mission = missionID;
    }

    public int getHeapPosition(int missionID){
        if(missionID == mission) return heapPosition;
        return -1;
    }

    public void setHeapPosition(int missionID, int heapPosition){
        this.heapPosition = heapPosition;
        this.mission = missionID;
    }

    public boolean isInHeap(int missionID){
        if(missionID == mission) return heapPosition != -1;
        return false;
    }

}
