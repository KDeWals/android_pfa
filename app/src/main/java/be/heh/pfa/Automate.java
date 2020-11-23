package be.heh.pfa;

public class Automate {

    private int id;
    private String name;
    private String ip;
    private int rack;
    private int slot;
    private String type;

    public Automate(){

    }

    public Automate(int id, String name, String ip, int rack, int slot, String type) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.rack = rack;
        this.slot = slot;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getRack() {
        return rack;
    }

    public void setRack(int rack) {
        this.rack = rack;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString(){
        String str = "Je m'appelle " + getName() + ", j'ai l'adresse ip : " + getIp()
                + ", j'ai comme rack et slot : " + getRack() + " | " + getSlot() +
                " et je suis de type " + getType();
        return str;
    }
}
