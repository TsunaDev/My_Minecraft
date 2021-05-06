package engine;

import org.joml.Vector3f;

public class GameItem {
    private final Vector3f pos;
    private final Vector3f rot;
    private float scale;

    public GameItem() {
        this.pos = new Vector3f(0, 0, 0);
        this.rot = new Vector3f(0, 0, 0);
        this.scale = 1;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(float x, float y, float z) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
    }

    public Vector3f getRot() {
        return rot;
    }

    public void setRot(float x, float y, float z) {
        this.rot.x = x;
        this.rot.y = y;
        this.rot.z = z;

    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
