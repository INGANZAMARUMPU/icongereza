package bi.udev.incongereza;

class Kirundi {
    public String en, ki;

    public Kirundi(String en, String ki) {
        this.en = en;
        this.ki = ki;
    }

    @Override
    public String toString() {
        return "Kirundi{" +
                "en='" + en + '\'' +
                ", ki='" + ki + '\'' +
                '}';
    }
}
