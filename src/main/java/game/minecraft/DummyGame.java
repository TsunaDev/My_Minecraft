package game.minecraft;

import engine.GameItem;
import engine.IGameLogic;
import engine.Window;
import engine.graph.Mesh;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class DummyGame implements IGameLogic {
    private Renderer renderer;
    private int directionY = 0;
    private int directionX = 0;
    private float colorR = 0.0f;
    private float colorG = 0.0f;
    private float colorB = 0.0f;
    private GameItem[] items;

    public DummyGame() {
        this.renderer = new Renderer();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        float[] positions = new float[]{
                // VO
                -0.5f,  0.5f,  0.5f,
                // V1
                -0.5f, -0.5f,  0.5f,
                // V2
                0.5f, -0.5f,  0.5f,
                // V3
                0.5f,  0.5f,  0.5f,
                // V4
                -0.5f,  0.5f, -0.5f,
                // V5
                0.5f,  0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        };

        int[] indices = new int[] {
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,
        };

        float[] colors = new float[] {
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f
        };

        items = new GameItem[] { new GameItem(new Mesh(positions, colors, indices)) };
        items[0].setPos(0, 0, -2);

    }

    @Override
    public void input(Window window) {
        if (window.isKeyPressed(GLFW_KEY_UP))
            directionY = 1;
        else if (window.isKeyPressed(GLFW_KEY_DOWN))
            directionY = -1;
        else
            directionY = 0;

        if (window.isKeyPressed(GLFW_KEY_RIGHT))
            directionX = 1;
        else if (window.isKeyPressed(GLFW_KEY_LEFT))
            directionX = -1;
        else
            directionX = 0;

    }

    private float checkColor(float color) {
        if (color > 1f)
            color = 1f;
        else if (color < 0f)
            color = 0f;
        return color;
    }

    @Override
    public void update(float interval) {
        float rotation = items[0].getRot().x + 1.5f;
        if (rotation > 360)
            rotation = 0;
        items[0].setRot(rotation, rotation, rotation);
    }

    public void render(Window window) throws Exception {
        window.setClearColor(colorR, colorG, colorB, 1f);
        renderer.render(window, items);
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        for (GameItem item : items)
            item.getMesh().cleanUp();
    }
}
