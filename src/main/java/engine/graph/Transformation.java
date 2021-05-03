package engine.graph;

import engine.GameItem;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

public class Transformation {
    private final Matrix4f projection;
    private final Matrix4f modelViewMatrix;
    private final Matrix4f viewMatrix;

    public Transformation() {
        projection = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float clipNear, float clipFar) {
        float aspect = width/height;

        projection.identity();
        projection.perspective(fov, aspect, clipNear, clipFar);

        return projection;
    }

    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f cameraRot = camera.getRotation();

        viewMatrix.identity();
        viewMatrix.rotate((float)Math.toRadians(cameraRot.x), new Vector3f(1, 0, 0)).
                rotate((float)Math.toRadians(cameraRot.y), new Vector3f(0, 1, 0));
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    public Matrix4f getModelViewMatrix(GameItem gameItem, Matrix4f viewMatrix) {
        Vector3f rotation = gameItem.getRot();
        modelViewMatrix.identity().translate(gameItem.getPos()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameItem.getScale());
        Matrix4f currView = new Matrix4f(viewMatrix);
        return currView.mul(modelViewMatrix);
    }
}
