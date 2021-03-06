package game.minecraft;

import engine.NoiseGenerator;
import engine.graph.*;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static game.minecraft.BlockType.AIR;

public class Scene {
    private final ArrayList<Chunk> chunks;
    private final Map<BlockType, Mesh> meshMap;
    private final NoiseGenerator noiseGenerator;
    private float lightAngle;
    private Vector3f ambientLight;
    private PointLight pointLight;
    private DirectionalLight sunLight;
    private Fog fog;
    public float lightInc = 0.11f;

    public Scene() {
        this.meshMap = new HashMap<>();
        this.chunks = new ArrayList<>();
        this.noiseGenerator = new NoiseGenerator();
        this.lightAngle = -10f;
        this.fog = Fog.NOFOG;
    }

    public void initMeshMap() throws Exception {
        Texture texture = new Texture("textures/atlas3.png");
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

    public void updateChunks(int xOrigin, int zOrigin, int nbChunkPerLine) {
        boolean updated = false;
        if (nbChunkPerLine % 2 != 0)
            nbChunkPerLine += 1;

        for (int distance = 0; distance < nbChunkPerLine / 2; distance++) {
            for (int x = xOrigin - (Chunk.WIDTH * (distance + 1)); x < xOrigin + (Chunk.WIDTH * (distance + 1)); x += Chunk.WIDTH) {
                for (int z = zOrigin - (Chunk.DEPTH * (distance + 1)); z < zOrigin + (Chunk.DEPTH * (distance + 1)); z += Chunk.DEPTH) {
                    int posX = (x % Chunk.WIDTH > Chunk.WIDTH / 2) ? x + (-x % Chunk.WIDTH) : x - x % Chunk.WIDTH;
                    int posZ = (z % Chunk.DEPTH > Chunk.DEPTH / 2) ? z + (-z % Chunk.DEPTH) : z - z % Chunk.DEPTH;

                    if (!Chunk.chunkExists(chunks, posX, posZ)) {
                        updated = true;
                        chunks.add(new Chunk(posX, posZ, noiseGenerator));
                        chunks.get(chunks.size() - 1).init();
                        chunks.get(chunks.size() - 1).generate();
                    }
                }
            }
        }

        for (int i = 0; i < chunks.size(); i++) {
            Chunk chunk =  chunks.get(i);
            int xDistance = Chunk.WIDTH * (nbChunkPerLine / 2);
            int zDistance = Chunk.DEPTH * (nbChunkPerLine / 2);

            if (chunk.getOffsetX() < xOrigin - xDistance || chunk.getOffsetX() > xOrigin + xDistance ||
            chunk.getOffsetZ() < zOrigin - zDistance || chunk.getOffsetZ() > zOrigin + zDistance) {
                chunks.remove(i);
                updated = true;
            }
        }

        if (updated)
            for (Chunk chunk : chunks)
                chunk.updateNeighbors(chunks);

    }

    public ArrayList<Chunk> getChunks() {
        return chunks;
    }

    public void initLighting() {
        ambientLight = new Vector3f(.5f, .5f, .5f);
        pointLight = new PointLight(new Vector3f(1f, 1f, 1f), new Vector3f(10000f, 10000f, 0f), 0.0f);
        PointLight.Attenuation attenuation = new PointLight.Attenuation(1.5f, 0f, 0f);
        pointLight.setAttenuation(attenuation);

        sunLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(-1, 0, 0), 1f);
        sunLight.setShadowPosMult(50);
        sunLight.setOrthoCoords(-50f, 50f, -50f, 50f, -1f, 50f);
    }

    public void update() {
        lightAngle += lightInc;
        if (lightAngle <= 0) {
            sunLight.setIntensity((lightAngle + 10f) / 10f);
            ambientLight.x = 0.04f * (10f + lightAngle) + 0.1f;
            ambientLight.y = 0.04f * (10f + lightAngle) + 0.1f;
            ambientLight.z = 0.04f * (10f + lightAngle) + 0.1f;
        }
        if (lightAngle > 180) {
            if (lightAngle <= 190) {
                sunLight.setIntensity((1f - (lightAngle - 180f) / 10f));
                ambientLight.x = 0.04f * (190f - lightAngle) + 0.1f;
                ambientLight.y = ambientLight.x;
                ambientLight.z = ambientLight.x;
            } else if (lightAngle > 250) {
                lightAngle = -10;
            }
        } else {
            float zValue = (float) Math.cos(Math.toRadians(lightAngle));
            float yValue = (float) Math.sin(Math.toRadians(lightAngle));
            Vector3f lightDirection = sunLight.getDirection();
            lightDirection.x = 0;
            lightDirection.y = yValue;
            lightDirection.z = zValue;
            lightDirection.normalize();
        }
    }

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
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

    public float getLightAngle() {
        return lightAngle;
    }

    public void cleanUp() {
        for (Map.Entry<BlockType, Mesh> entry : meshMap.entrySet()) {
            entry.getValue().cleanUp();
        }
    }
}
