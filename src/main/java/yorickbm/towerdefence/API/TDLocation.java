package yorickbm.towerdefence.API;

/**
 * Last modified by: YorickBM on 27-06-2022
 */
public class TDLocation {

    private float x, y, z;

    public TDLocation(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public TDLocation() {}

    public TDLocation fromString(String location) {
        x = Float.parseFloat(location.split(";")[0]);
        y = Float.parseFloat(location.split(";")[1]);
        z = Float.parseFloat(location.split(";")[2]);
        return this;
    }
    public String toString() {
        return x + ";" + y + ";" + z;
    }


    public float getZ() {
        return z;
    }
    public void setZ(float z) {
        this.z = z;
    }

    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }
}
