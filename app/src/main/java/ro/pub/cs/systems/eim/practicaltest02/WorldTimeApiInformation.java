package ro.pub.cs.systems.eim.practicaltest02;

public class WorldTimeApiInformation {
    private String key;
    private String value;

    public WorldTimeApiInformation(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public WorldTimeApiInformation() {
        this.key = null;
        this.value = null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
