package model;

public class Room {

    private String roomId;
    private int    capacity;
    private int    occupied;
    private String type;
    private double price;

    public Room(String roomId, int capacity, int occupied, String type, double price) {
        this.roomId   = roomId;
        this.capacity = capacity;
        this.occupied = occupied;
        this.type     = type;
        this.price    = price;
    }

    public String getRoomId()   { return roomId; }
    public int    getCapacity() { return capacity; }
    public int    getOccupied() { return occupied; }
    public String getType()     { return type; }
    public double getPrice()    { return price; }

    public int     getAvailableBeds()  { return capacity - occupied; }
    public boolean isFull()            { return occupied >= capacity; }
    public boolean isEmpty()           { return occupied == 0; }

    public String getStatus() {
        if (isEmpty())  return "Empty";
        if (isFull())   return "Full";
        return "Partial";
    }

    /** Floor derived from room ID e.g. 101 → floor 1, 201 → floor 2 */
    public int getFloor() {
        try { return Integer.parseInt(roomId.substring(0, 1)); }
        catch (Exception e) { return 0; }
    }
}