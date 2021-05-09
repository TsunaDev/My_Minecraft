package game.minecraft;

import engine.Utils;
import engine.Window;
import engine.graph.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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
    private ShaderProgram depthShaderProgram;
    private ShadowMap shadowMap;
    private float specularPower;

    public Renderer() {
        this.transformation = new Transformation();
        specularPower = 10f;
    }

    void init(Window window) throws Exception {
        shadowMap = new ShadowMap();

        setupSceneShader();
        setupDepthShader();

        projection = transformation.updateProjectionMatrix(Renderer.FOV, window.getWidth(), window.getHeight(), Renderer.CLIP_NEAR, Renderer.CLIP_FAR);


        window.setClearColor(0,0,0,0);
    }

    private void setupSceneShader() throws Exception {
        shaderProgram = new ShaderProgram();

        shaderProgram.createVertexShader(Utils.loadResource("/scene.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/scene.fs"));
        shaderProgram.link();

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createUniform("specularPower");
        shaderProgram.createMaterialUniform("material");
        shaderProgram.createPointLightUniform("pointLight");
        shaderProgram.createDirLightUniform("directionalLight");

        shaderProgram.createUniform("shadowMap");
        shaderProgram.createUniform("orthoProjectionMatrix");
        shaderProgram.createUniform("modelLightViewMatrix");
    }

    private void setupDepthShader() throws Exception {
        depthShaderProgram = new ShaderProgram();
        depthShaderProgram.createVertexShader(Utils.loadResource("/depth.vs"));
        depthShaderProgram.createFragmentShader(Utils.loadResource("/depth.fs"));
        depthShaderProgram.link();

        depthShaderProgram.createUniform("modelLightViewMatrix");
        depthShaderProgram.createUniform("orthoProjectionMatrix");
    }

    public void render(Window window, Camera camera, Scene scene) throws Exception {
        clear();

        renderDepthMap(window, camera, scene);

        glViewport(0, 0, window.getWidth(), window.getHeight());
        projection = transformation.updateProjectionMatrix(Renderer.FOV, window.getWidth(), window.getHeight(), Renderer.CLIP_NEAR, Renderer.CLIP_FAR);

        renderScene(window, camera, scene);

    }

    public void renderDepthMap(Window window, Camera camera, Scene scene) {
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShaderProgram.bind();

        DirectionalLight light = scene.getSunLight();
        Vector3f direction = light.getDirection();

        float angleX = (float)Math.toDegrees(Math.acos(direction.z));
        float angleY = (float)Math.toDegrees(Math.asin(direction.x));
        float angleZ = 0f;

        Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(direction).mul(light.getShadowPosMult()), new Vector3f(angleX, angleY, angleZ));
        DirectionalLight.OrthoCoords ortho = light.getOrthoCoords();
        Matrix4f orthoProjMatrix =  transformation.updateOrthoProjectionMatrix(ortho.left, ortho.right, ortho.bottom, ortho.top, ortho.near, ortho.far);

        depthShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);

        for (Chunk chunk : scene.getChunks()) {
            ArrayList<Block> drawables = chunk.getDrawables();
            for (Map.Entry<BlockType, Mesh> blockTypeMeshEntry : scene.getMeshMap().entrySet()) {
                blockTypeMeshEntry.getValue().start();
                for (Block block : drawables) {
                    if (blockTypeMeshEntry.getKey() == block.getType()) {
                        Matrix4f modelLightViewMatrix = transformation.buildModelViewMatrix(block, lightViewMatrix);
                        depthShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
                        int[] indices = block.getVisibleIndices();
                        if (indices != null && indices.length > 0)
                            blockTypeMeshEntry.getValue().renderFaces(indices);
                    }
                }
                blockTypeMeshEntry.getValue().end();
            }
        }
        depthShaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void renderScene(Window window, Camera camera, Scene scene) {
        shaderProgram.bind();


        shaderProgram.setUniform("projectionMatrix", projection);

        Matrix4f viewMatrix = transformation.updateViewMatrix(camera);

        shaderProgram.setUniform("ambientLight", scene.getAmbientLight());
        shaderProgram.setUniform("specularPower", specularPower);
        PointLight currPointLight = new PointLight(scene.getPointLight());
        Vector3f lightPos = currPointLight.getPos();
        Vector4f aux = new Vector4f(lightPos, 1f);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        shaderProgram.setUniform("pointLight", currPointLight);
        DirectionalLight currDirLight = new DirectionalLight(scene.getSunLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shaderProgram.setUniform("directionalLight", currDirLight);
        shaderProgram.setUniform("texture_sampler", 0);


        for (Chunk chunk : scene.getChunks()) {
            ArrayList<Block> drawables = chunk.getDrawables();
            for (Map.Entry<BlockType, Mesh> blockTypeMeshEntry : scene.getMeshMap().entrySet()) {
                shaderProgram.setUniform("material", blockTypeMeshEntry.getValue().getMaterial());
                blockTypeMeshEntry.getValue().start();
                for (Block block : drawables) {
                    if (blockTypeMeshEntry.getKey() == block.getType()) {
                        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(block, viewMatrix);
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
        if (depthShaderProgram != null)
            depthShaderProgram.cleanUp();
    }
}
