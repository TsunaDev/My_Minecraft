package game.minecraft;

import engine.GameItem;
import engine.IGameLogic;
import engine.Window;
import engine.graph.Mesh;
import engine.graph.Texture;

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

        float[] positions = new float[] {
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,

                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,
        };
        float[] texCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7,};

        items = new GameItem[] { new GameItem(new Mesh(positions, texCoords, indices, new Texture("textures/grassblock.png"))) };
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

    float scaleMod = 0.01f;

    @Override
    public void update(float interval) {
        float scale = items[0].getScale() + scaleMod;
        if (scale > 1) {
            scale = 1;
            scaleMod = -scaleMod;
        } else if (scale < 0.5f) {
            scale = 0.5f;
            scaleMod = -scaleMod;
        }

        items[0].setScale(scale);

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
