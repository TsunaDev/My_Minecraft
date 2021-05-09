package game.minecraft;

import engine.NoiseGenerator;
import engine.graph.*;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static game.minecraft.BlockType.AIR;
import static game.minecraft.BlockType.GRASS;

public class Scene {
    private final ArrayList<Chunk> chunks;
    private final Map<BlockType, Mesh> meshMap;
    private final NoiseGenerator noiseGenerator;
    private float lightAngle;
    private Vector3f ambientLight;
    private PointLight pointLight;
    private DirectionalLight sunLight;

    public Scene() {
        this.meshMap = new HashMap<>();
        this.chunks = new ArrayList<>();
        this.noiseGenerator = new NoiseGenerator();
        this.lightAngle = -90f;
    }

    public void initMeshMap() throws Exception {
        Texture texture = new Texture("textures/atlas2.png");
        Material material = new Material(texture);

        for (BlockType type : BlockType.values()) {
            if (type == AIR)
                continue;
            float[] texCoords = Block.getTextureCoords(type);

            if (texCoords == null)
                System.err.println("Type '" + type + "' is not set.");
            else {
                Mesh mesh = new Mesh(Block.getVertices(), texCoords, Block.getNormals());
                mesh.setMaterial(material);
                meshMap.put(type, mesh);
            }
        }
    }

    public Map<BlockType, Mesh> getMeshMap() {
        return meshMap;
    }

    public void initChunks(int nbChunkPerLine) throws Exception {
        int xOrigin = 0;
        int zOrigin = 0;

        if (nbChunkPerLine % 2 != 0)
            nbChunkPerLine += 1;

        for (int distance = 0; distance < nbChunkPerLine / 2; distance++) {
            for (int x = xOrigin - (Chunk.WIDTH * (distance + 1)); x < xOrigin + (Chunk.WIDTH * (distance + 1)); x += Chunk.WIDTH) {
                for (int z = zOrigin - (Chunk.DEPTH * (distance + 1)); z < zOrigin + (Chunk.DEPTH * (distance + 1)); z += Chunk.DEPTH) {
                    if (!Chunk.chunkExists(chunks, x, z)) {
                        chunks.add(new Chunk(x, z, noiseGenerator));
                        chunks.get(chunks.size() - 1).init();
                        chunks.get(chunks.size() - 1).generate();
                    }
                }
            }
        }

        for (Chunk chunk : chunks) {
            chunk.updateNeighbors(chunks);
        }

    }

    public ArrayList<Chunk> getChunks() {
        return chunks;
    }

    public void initLighting() {
        ambientLight = new Vector3f(.3f, .3f, .3f);
        pointLight = new PointLight(new Vector3f(1f, 1f, 1f), new Vector3f(10000f, 10000f, 0f), 0.0f);
        PointLight.Attenuation attenuation = new PointLight.Attenuation(1.5f, 0f, 0f);
        pointLight.setAttenuation(attenuation);

        sunLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(-1, 0, 0), 0.9f);
    }

    public void update() {
        lightAngle += 0.11f;
        if (lightAngle > 90) {
            sunLight.setIntensity(0);
            if (lightAngle >= 120) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            sunLight.setIntensity(factor);
            sunLight.getColor().y = Math.max(factor, 0.9f);
            sunLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            sunLight.setIntensity(0.9f);
            sunLight.getColor().x = 1;
            sunLight.getColor().y = 1;
            sunLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        sunLight.getDirection().x = (float) Math.sin(angRad);
        sunLight.getDirection().y = (float) Math.cos(angRad);
    }

    public DirectionalLight getSunLight() {
        return sunLight;
    }

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void cleanUp() {
        for (Map.Entry<BlockType, Mesh> entry : meshMap.entrySet()) {
            entry.getValue().cleanUp();
        }
    }
}
