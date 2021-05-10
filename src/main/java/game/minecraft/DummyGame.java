package game.minecraft;

import engine.*;
import engine.graph.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class DummyGame implements IGameLogic {
    private final Renderer renderer;
    private final Camera camera;
    private final Vector3f cameraMove;
    private final Scene scene;
    private boolean wireframe = false;
    private boolean wPressed = false;

    public DummyGame() {
        this.renderer = new Renderer();
        this.camera = new Camera();
        camera.setPosition(0, 5f, 0);
        this.cameraMove = new Vector3f();
        this.scene = new Scene();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        scene.initMeshMap();
        scene.initChunks(8);
        scene.initLighting();
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraMove.set(0, 0, 0);

        if (window.isKeyPressed(GLFW_KEY_W))
            cameraMove.z = -1;
        else if (window.isKeyPressed(GLFW_KEY_S))
            cameraMove.z = 1;
        if (window.isKeyPressed(GLFW_KEY_A))
            cameraMove.x = -1;
        else if (window.isKeyPressed(GLFW_KEY_D))
            cameraMove.x = 1;
        if (window.isKeyPressed(GLFW_KEY_SPACE))
            cameraMove.y = 1;
        else if (window.isKeyPressed(GLFW_KEY_LEFT_CONTROL))
            cameraMove.y = -1;
        if (window.isKeyPressed(GLFW_KEY_Z))
            wPressed = true;
        if (wPressed && window.isKeyReleased(GLFW_KEY_Z)) {
            if (!wireframe)
                glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
            else
                glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
            wireframe = !wireframe;
            wPressed = false;
        }
        DirectionalLight.OrthoCoords coords = scene.getSunLight().getOrthoCoords();

        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            coords.far -= 1f;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT))
            coords.far += 1f;
        if (window.isKeyPressed(GLFW_KEY_1)) {
            coords.left -= 1f;
        } else if (window.isKeyPressed(GLFW_KEY_2))
            coords.left += 1f;
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            coords.far -= 1f;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT))
            coords.far += 1f;
        System.out.println(coords.far);
        scene.getSunLight().setOrthoCoords(coords.left, coords.right, coords.bottom, coords.top, coords.near, coords.far);
        if (window.isKeyPressed(GLFW_KEY_UP))
            scene.lightInc = 0.2f;
        if (window.isKeyPressed(GLFW_KEY_DOWN))
            scene.lightInc = -0.2f;

    }


    @Override
    public void update(float interval, MouseInput mouseInput) {
       camera.translate(cameraMove.x * 0.1f, cameraMove.y * 0.1f, cameraMove.z * 0.1f);
       if (mouseInput.isRightButtonPressed()) {
           Vector2f rot = mouseInput.getDispVec();
           camera.rotate(rot.x * 0.2f, rot.y * 0.2f, 0);
       }
       scene.update();
    }

    public void render(Window window) throws Exception {
        renderer.render(window, camera, scene);
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        scene.cleanUp();
    }
}
