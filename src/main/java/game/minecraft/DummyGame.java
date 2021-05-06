package game.minecraft;

import engine.*;
import engine.graph.Camera;
import engine.graph.Mesh;
import engine.graph.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static game.minecraft.BlockType.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class DummyGame implements IGameLogic {
    private Renderer renderer;
    private int directionY = 0;
    private int directionX = 0;
    private float colorR = 0.0f;
    private float colorG = 0.0f;
    private float colorB = 0.0f;
    private final Camera camera;
    private final Vector3f cameraMove;
    private ArrayList<GameItem> items;
    private final NoiseGenerator noiseGenerator;
    private ArrayList<Chunk> chunks;
    private Map<BlockType, Mesh> meshMap;

    public DummyGame() {
        this.renderer = new Renderer();
        this.camera = new Camera();
        camera.setPosition(0, 64f, 0);
        this.cameraMove = new Vector3f();
        this.items = new ArrayList<>();
        this.noiseGenerator = new NoiseGenerator();
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

        float[] dirtTexCoords = new float[]{
                0.5f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
                1.0f, 0.0f,

                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,

                // For text coords in top face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,

                // For text coords in right face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in left face
                1.0f, 0.0f,
                1.0f, 0.5f,

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
                14, 4, 6, 6, 15, 14,
                // Bottom face
                16, 17, 19, 19, 18, 16,
                // Back face
                4, 5, 7, 7, 6, 4
        };
        Mesh mesh = new Mesh(positions, texCoords, indices, new Texture("textures/grassblock.png"));
        Mesh meshDirt = new Mesh(positions, dirtTexCoords, indices, new Texture("textures/grassblock.png"));
        meshMap = new HashMap<BlockType, Mesh>();
        ArrayList<GameItem> gameItems = new ArrayList<>();
        meshMap.put(GRASS, mesh);
        meshMap.put(DIRT, meshDirt);
        chunks = new ArrayList<>();
        chunks.add(new Chunk(0, 0, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();
        chunks.add(new Chunk(16, 0, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();
        chunks.add(new Chunk(16, 16, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();
        chunks.add(new Chunk(0, 16, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();

        chunks.add(new Chunk(-16, -16, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();
        chunks.add(new Chunk(-32, -16, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();
        chunks.add(new Chunk(-32, -32, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();
        chunks.add(new Chunk(-16, -32, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();

        chunks.add(new Chunk(-32, 0, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();
        chunks.add(new Chunk(-16, 0, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();
        chunks.add(new Chunk(-32, 16, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();
        chunks.add(new Chunk(-16, 16, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();

        chunks.add(new Chunk(0, -32, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();
        chunks.add(new Chunk(16, -32, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();
        chunks.add(new Chunk(16, -16, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();
        chunks.add(new Chunk(0, -16, noiseGenerator));
        chunks.get(chunks.size() - 1).init();
        chunks.get(chunks.size() - 1).generate();
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
        if (window.isKeyPressed(GLFW_KEY_Q))
            cameraMove.y = 1;
        else if (window.isKeyPressed(GLFW_KEY_E))
            cameraMove.y = -1;

    }

    float scaleMod = 0.01f;

    @Override
    public void update(float interval, MouseInput mouseInput) {
       camera.translate(cameraMove.x * 0.1f, cameraMove.y * 0.1f, cameraMove.z * 0.1f);

       if (mouseInput.isRightButtonPressed()) {
           Vector2f rot = mouseInput.getDispVec();
           camera.rotate(rot.x * 0.2f, rot.y * 0.2f, 0);
       }
    }

    public void render(Window window) throws Exception {
        window.setClearColor(colorR, colorG, colorB, 1f);
        renderer.render(window, camera, chunks, meshMap);
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        for (Map.Entry<BlockType, Mesh> entry : meshMap.entrySet()) {
            entry.getValue().cleanUp();
        }
    }
}
