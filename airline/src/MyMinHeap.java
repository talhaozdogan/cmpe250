import java.util.ArrayList;
import java.util.Comparator;

public class MyMinHeap {
    public ArrayList<AirportInstance> heap;
    private Comparator<AirportInstance> comparator;
    private int missionID;

    public MyMinHeap(int missionID) {
        this.heap = new ArrayList<>();
        this.comparator = Comparator.comparing(instance -> instance.getCost(missionID));
        this.missionID = missionID;
    }

    public void add(AirportInstance instance) {
        heap.add(instance);
        instance.setHeapPosition(missionID, heap.size() - 1);
        siftUp(heap.size() - 1);
    }

    private void siftUp(int i) {
        if (i > 0) {
            int parent = (i - 1) / 2;
            if (comparator.compare(heap.get(i), heap.get(parent)) < 0) {
                swap(i, parent);
                siftUp(parent);
            }
        }
    }

    public AirportInstance poll() {
        if (heap.isEmpty()) {
            return null;
        }
    
        AirportInstance min = heap.get(0);
        int lastIndex = heap.size() - 1;
    
        if (lastIndex > 0) {
            AirportInstance lastInstance = heap.get(lastIndex);
    
            // Move the last instance to the removed instance's position
            heap.set(0, lastInstance);
    
            // Update the position of the last instance
            lastInstance.setHeapPosition(missionID, 0);
        }
    
        // Remove the last instance from the end
        heap.remove(lastIndex);
    
        // Set the heap position to -1 for the removed instance
        min.setHeapPosition(missionID, -1);
    
        if (heap.size() > 1) {
            siftDown(0);
        }
    
        return min;
    }

    private void siftDown(int i) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        int smallest = i;

        if (left < heap.size() && comparator.compare(heap.get(left), heap.get(smallest)) < 0) {
            smallest = left;
        }
        if (right < heap.size() && comparator.compare(heap.get(right), heap.get(smallest)) < 0) {
            smallest = right;
        }
        if (smallest != i) {
            swap(i, smallest);
            siftDown(smallest);
        }
    }

    private void swap(int i, int j) {
        //updating the position of the instances
        heap.get(i).setHeapPosition(missionID, j);
        heap.get(j).setHeapPosition(missionID, i);

        AirportInstance temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int getSize(){
        return heap.size();
    }

    public void updateHeap(AirportInstance instance){
        if(instance.getHeapPosition(missionID) == -1) return;
        siftUp(instance.getHeapPosition(missionID));
    }
}