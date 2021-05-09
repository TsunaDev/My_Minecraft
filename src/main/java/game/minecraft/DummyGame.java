package game.minecraft;

import engine.*;
import engine.graph.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static engine.Utils.fromPixelsToPercentage;
import static game.minecraft.BlockType.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

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
    private Vector3f ambientLight;
    private PointLight pointLight;
    private Vector3f moveLight;
    private boolean wireframe = false;
    private boolean wPressed = false;

    public DummyGame() {
        this.renderer = new Renderer();
        this.camera = new Camera();
        camera.setPosition(0, 64f, 0);
        this.cameraMove = new Vector3f();
        this.items = new ArrayList<>();
        this.noiseGenerator = new NoiseGenerator();
        this.moveLight = new Vector3f();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);


        float[] positions = new float[] {
                // Front Face
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,

                // Back Face
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
                // V14
                0.5f, 0.5f, -0.5f,
                // V15
                0.5f, -0.5f, -0.5f,

                // For text coords in left face
                // V16: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V17: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V18
                -0.5f, 0.5f, -0.5f,
                // V19
                -0.5f, -0.5f, -0.5f,

                // For text coords in bottom face
                // V20: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V21: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V22: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V23: V2 repeated
                0.5f, -0.5f, 0.5f,
        };
        float[] texCoords = new float[]{
                23f, 2f,
                23f, 3f,
                24f, 3f,
                24f, 2f,

                23f, 2f,
                24f, 2f,
                23f, 3f,
                24f, 3f,

                // For text coords in top face
                29f, 2f,
                30f, 2f,
                29f, 3f,
                30f, 3f,

                // For text coords in right face
                23f, 2f,
                23f, 3f,
                24f, 2f,
                24f, 3f,

                // For text coords in left face
                24f, 2f,
                24f, 3f,
                23f, 2f,
                23f, 3f,

                // For text coords in bottom face
                20f, 2f,
                21f, 2f,
                20f, 3f,
                21f, 3f,
        };

        float[] dirtTexCoords = new float[]{
                20f, 2f,
                20f, 3f,
                21f, 3f,
                21f, 2f,

                20f, 2f,
                21f, 2f,
                20f, 3f,
                21f, 3f,

                // For text coords in top face
                20f, 2f,
                21f, 2f,
                20f, 3f,
                21f, 3f,

                // For text coords in right face
                20f, 2f,
                20f, 3f,
                21f, 2f,
                21f, 3f,

                // For text coords in left face
                21f, 2f,
                21f, 3f,
                20f, 2f,
                20f, 3f,

                // For text coords in bottom face
                20f, 2f,
                21f, 2f,
                20f, 3f,
                21f, 3f,
        };
        float[] stoneTexCoords = new float[]{
                3f, 21f,
                3f, 22f,
                4f, 22f,
                4f, 21f,

                3f, 21f,
                4f, 21f,
                3f, 22f,
                4f, 22f,

                // For text coords in top face
                3f, 21f,
                4f, 21f,
                3f, 22f,
                4f, 22f,

                // For text coords in right face
                3f, 21f,
                3f, 22f,
                4f, 21f,
                4f, 22f,

                // For text coords in left face
                4f, 21f,
                4f, 22f,
                3f, 21f,
                3f, 22f,

                // For text coords in bottom face
                3f, 21f,
                4f, 21f,
                3f, 22f,
                4f, 22f,
        };
        int[] indices = new int[]{
                // Front face
                0, 1, 3,
                3, 1, 2,
                // Back face
                4, 5, 7,
                7, 6, 4,
                // Top Face
                8, 10, 11,
                9, 8, 11,
                // Right face
                12, 13, 15,
                14, 12, 15,
                // Left face
                16, 18, 19,
                19, 17, 16,
                // Bottom face
                20, 21, 23,
                23, 22, 20
        };

        float[] normals = new float[] {
                // Front face
                0f,0f,-1f,
                0f,0f,-1f,
                0f,0f,-1f,
                0f,0f,-1f,

                // Back face
                0f,0f,1f,
                0f,0f,1f,
                0f,0f,1f,
                0f,0f,1f,

                // Top face
                0f,1f,0f,
                0f,1f,0f,
                0f,1f,0f,
                0f,1f,0f,

                // Right face
                1f, 0f, 0f,
                1f, 0f, 0f,
                1f, 0f, 0f,
                1f, 0f, 0f,

                // Left face
                -1f, 0f, 0f,
                -1f, 0f, 0f,
                -1f, 0f, 0f,
                -1f, 0f, 0f,

                // Bottom face
                0f, -1f, 0f,
                0f, -1f, 0f,
                0f, -1f, 0f,
                0f, -1f, 0f
        };

        Texture texture = new Texture("textures/atlas2.png");
        Material material = new Material(texture);
        Mesh mesh = new Mesh(positions, fromPixelsToPercentage(texCoords), normals);
        mesh.setMaterial(material);
        Mesh meshDirt = new Mesh(positions, fromPixelsToPercentage(dirtTexCoords), normals);
        meshDirt.setMaterial(material);
        Mesh meshStone = new Mesh(positions, fromPixelsToPercentage(stoneTexCoords), normals);
        meshStone.setMaterial(material);
        meshMap = new HashMap<BlockType, Mesh>();
        meshMap.put(GRASS, mesh);
        meshMap.put(DIRT, meshDirt);
        meshMap.put(STONE, meshStone);

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

        for (Chunk chunk : chunks) {
            chunk.updateNeighbors(chunks);
        }

        ambientLight = new Vector3f(.5f, .5f, .5f);
        pointLight = new PointLight(new Vector3f(1f, 1f, 1f), new Vector3f(10000f, 10000f, 0f), 1.0f);
        PointLight.Attenuation attenuation = new PointLight.Attenuation(1.5f, 0f, 0f);
        pointLight.setAttenuation(attenuation);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraMove.set(0, 0, 0);
        moveLight.set(0, 0, 0);
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
        if (window.isKeyPressed(GLFW_KEY_LEFT))
            moveLight.x = -1;
        else if (window.isKeyPressed(GLFW_KEY_RIGHT))
            moveLight.x = 1;

    }

    float scaleMod = 0.01f;

    @Override
    public void update(float interval, MouseInput mouseInput) {
       camera.translate(cameraMove.x * 0.1f, cameraMove.y * 0.1f, cameraMove.z * 0.1f);
       pointLight.setPos(new Vector3f(pointLight.getPos().x + (moveLight.x * 100f), pointLight.getPos().y, pointLight.getPos().z));
       if (mouseInput.isRightButtonPressed()) {
           Vector2f rot = mouseInput.getDispVec();
           camera.rotate(rot.x * 0.2f, rot.y * 0.2f, 0);
       }
    }

    public void render(Window window) throws Exception {
        window.setClearColor(colorR, colorG, colorB, 1f);
        renderer.render(window, camera, chunks, meshMap, ambientLight, pointLight);
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        for (Map.Entry<BlockType, Mesh> entry : meshMap.entrySet()) {
            entry.getValue().cleanUp();
        }
    }
}
