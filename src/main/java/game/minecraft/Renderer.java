package game.minecraft;

import engine.Utils;
import engine.Window;
import engine.graph.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {
    private static final float FOV = (float) Math.toRadians(70f);
    private static final float CLIP_NEAR = .03f;
    private static final float CLIP_FAR = 1000f;
    private Transformation transformation;
    private Matrix4f projection;
    private ShaderProgram shaderProgram;
    private float specularPower;

    public Renderer() {
        this.transformation = new Transformation();
        specularPower = 10f;
    }

    void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();

        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();

        float aspectRatio = (float) window.getWidth() / (float) window.getHeight();
        projection = transformation.getProjectionMatrix(Renderer.FOV, window.getWidth(), window.getHeight(), Renderer.CLIP_NEAR, Renderer.CLIP_FAR);
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createUniform("specularPower");
        shaderProgram.createMaterialUniform("material");
        shaderProgram.createPointLightUniform("pointLight");

        window.setClearColor(0,0,0,0);
    }

    public void render(Window window, Camera camera, ArrayList<Chunk> chunks, Map<BlockType, Mesh> meshMap, Vector3f ambientLight, PointLight pointLight) throws Exception {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        projection = transformation.getProjectionMatrix(Renderer.FOV, window.getWidth(), window.getHeight(), Renderer.CLIP_NEAR, Renderer.CLIP_FAR);
        shaderProgram.setUniform("projectionMatrix", projection);

        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shaderProgram.setUniform("ambientLight", ambientLight);
        shaderProgram.setUniform("specularPower", specularPower);
        PointLight currPointLight = new PointLight(pointLight);
        Vector3f lightPos = currPointLight.getPos();
        Vector4f aux = new Vector4f(lightPos, 1f);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        shaderProgram.setUniform("pointLight", currPointLight);

        shaderProgram.setUniform("texture_sampler", 0);


        for (Chunk chunk : chunks) {
            ArrayList<Block> drawables = chunk.getDrawables();
            for (Map.Entry<BlockType, Mesh> blockTypeMeshEntry : meshMap.entrySet()) {
                shaderProgram.setUniform("material", blockTypeMeshEntry.getValue().getMaterial());
                blockTypeMeshEntry.getValue().start();
                for (Block block : drawables) {
                    if (blockTypeMeshEntry.getKey() == block.getType()) {
                        Matrix4f modelViewMatrix = transformation.getModelViewMatrix(block, viewMatrix);
                        shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                        int[] indices = block.getVisibleIndices();
                        if (indices != null && indices.length > 0)
                            blockTypeMeshEntry.getValue().renderFaces(indices);
                    }
                }
                blockTypeMeshEntry.getValue().end();
            }
        }

        glBindVertexArray(0);
        shaderProgram.unbind();
    }

    void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanUp() {
        if (shaderProgram != null)
            shaderProgram.cleanUp();
    }
}
