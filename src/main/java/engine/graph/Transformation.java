package engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {
    public final Matrix4f projection;
    public final Matrix4f world;

    public Transformation() {
        projection = new Matrix4f();
        world = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float clipNear, float clipFar) {
        float aspect = width/height;

        projection.identity();
        projection.perspective(fov, aspect, clipNear, clipFar);

        return projection;
    }

    public Matrix4f getWorldMatrix(Vector3f offset, Vector3f rotation, float scale) {
        world.identity().translate(offset).
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                scale(scale);
        return world;
    }
}
