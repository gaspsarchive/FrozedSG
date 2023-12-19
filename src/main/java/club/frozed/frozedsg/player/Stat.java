package club.frozed.frozedsg.player;

public class Stat {
    private int amount;
    private int name;

    public Stat() {
    }

    public void increaseAmount(int amount) {
        this.amount += amount;
    }

    public void decreaseAmount(int amount) {
        this.amount -= amount;
    }

    public int getAmount() {
        return this.amount;
    }

    public int getName() {
        return this.name;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setName(int name) {
        this.name = name;
    }
}
