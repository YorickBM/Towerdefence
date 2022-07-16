package yorickbm.towerdefence.API;

public class Pair<K, U> {

    private K _key;
    private U _value;

    public Pair(K key, U value) {
        _key = key;
        _value = value;
    }

    public K getKey() { return _key; }
    public U getValue() { return _value; }

    public void setKey(K key) {
        _key = key;
    }
    public void setValue(U value) {
        _value = value;
    }
}
