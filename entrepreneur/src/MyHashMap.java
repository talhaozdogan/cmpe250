import java.util.ArrayList;
import java.util.LinkedList;

public class MyHashMap<KeyType, ValueType> {
    private ArrayList< LinkedList< TableItem<KeyType, ValueType> > > array;
    private int size;
    private int capacity;
    private static final double MAX_LOAD_FACTOR = 2;

    //constructor
    MyHashMap(int capacity){
        this.capacity = capacity;
        //initializing the array of linked lists
        array = new ArrayList<>();
        for(int i = 0; i < capacity; i++){
            array.add(null);
        }
    }

    public void insert(KeyType key, ValueType value){
        //check if it already exist? TODO: may result in performance problems

        //inserting the element
        int index = hash(key);
        //if the index of the array is empty, then add a new empty linked list
        if(array.get(index) == null) array.set( index, new LinkedList< TableItem<KeyType, ValueType> >() );
        array.get(index).add( new TableItem<>(key, value) );
        size++;

        //if load factor is exceeded, rehash
        if(getLoadFactor() > MAX_LOAD_FACTOR) rehash();
    }

    public ValueType remove(KeyType key){
        int index = hash(key);
        LinkedList<TableItem<KeyType, ValueType>> tempList = array.get(index);
        if (tempList == null) return null;

        //removing the object with the specific key
        for(TableItem<KeyType, ValueType> item : tempList){
            if(item.key.equals(key)) {
                tempList.remove(item);
                size--;
                return item.value;
            }
        }
        //if item is not found
        return null;
    }

    public boolean contains(KeyType key){
        int index = hash(key);
        if (array.get(index) == null) return false;

        for( TableItem<KeyType, ValueType> item : array.get(index)){
            if(item.key.equals(key)) return true;
        }
        return false;
    }

    public ValueType get(KeyType key){
        int index = hash(key);
        if (array.get(index) == null) return null;

        for( TableItem<KeyType, ValueType> item : array.get(index)){
            if(item.key.equals(key)) return item.value;
        }
        return null;
    }

    private int hash(KeyType key){
        //calculating the hashcode for the key
        //TODO: hashcode function can be optimized
        int hash = key.hashCode();

        //modifying it
        hash %= capacity;
        if(hash < 0) return hash + capacity;
        return hash;
    }

    private void rehash(){
        ArrayList< LinkedList< TableItem<KeyType, ValueType> > > oldArray = array;

        //creating the new array
        this.capacity = nextPrime(capacity * 2 + 1);
        this.array = new ArrayList<>();
        for(int i = 0; i < capacity; i++){
            this.array.add(null);
        }

        //copying old elements and inserting them to the new array
        for( LinkedList< TableItem<KeyType, ValueType> > list: oldArray )
            if (list != null)
                for( TableItem<KeyType, ValueType> item : list) {
                    insert(item.key, item.value);
                }
    }

    private int nextPrime(int number){
        for(int i = number; ;i++)
            if(isPrime(i)) return i;
    }

    private boolean isPrime(int number){
        if(number <= 1) return false;
        for(int i = 2; i < Math.sqrt(number); i++)
            if( (number % i) == 0) return false;
        return true;
    }

    private double getLoadFactor(){
        return size / (double)capacity;
    }

}