public class GraphEdge {
    Airport from;
    Airport to;
    double distance;
    int timeDelay;

    GraphEdge(Airport from, Airport to, double distance, int timeDelay) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.timeDelay = timeDelay;
    }
}
