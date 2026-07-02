// model/Person.java
package model;

public abstract class Person {
    protected String id;
    protected String name;
    protected int age;
    protected String email;
    protected String phone;
    protected String address;
    
    public Person(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    @Override
    public String toString() {
        return name + " (ID: " + id + ")";
    }
}