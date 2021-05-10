package game.minecraft;

import engine.GameItem;
import engine.NoiseGenerator;

import java.util.ArrayList;
import java.util.Arrays;


import static engine.Utils.merge;
import static game.minecraft.BlockType.*;

public class Chunk {
    static public int WIDTH = 16;
    static public int DEPTH = 16;
    static public int HEIGHT = 128;
    private final int offsetX;
    private final int offsetZ;
    private final Block[][][] blocks;
    private final NoiseGenerator noiseGenerator;
    private Chunk leftNeighbor = null;
    private Chunk rightNeighbor = null;
    private Chunk frontNeighbor = null;
    private Chunk backNeighbor = null;
    private ArrayList<Block> drawables = new ArrayList<>();
    private boolean update = false;


    Chunk(int offsetX, int offsetZ, NoiseGenerator noiseGenerator) {
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
        blocks = new Block[HEIGHT][WIDTH][DEPTH];
        this.noiseGenerator = noiseGenerator;
    }

    public static boolean chunkExists(ArrayList<Chunk> chunks, int offsetX, int offsetZ) {
        for (Chunk chunk : chunks) {
            if (chunk.getOffsetX() == offsetX && chunk.getOffsetZ() == offsetZ)
                return true;
        }
        return false;
    }


    public void updateNeighbors(ArrayList<Chunk> chunks) {
        for (Chunk chunk : chunks) {
            int x = chunk.getOffsetX();
            int z = chunk.getOffsetZ();
            if (x == offsetX - WIDTH && z == offsetZ)
                leftNeighbor = chunk;
            else if (x == offsetX + WIDTH && z == offsetZ)
                rightNeighbor = chunk;
            else if (x == offsetX && z == offsetZ - DEPTH)
                backNeighbor = chunk;
            else if (x == offsetX && z == offsetZ + DEPTH)
                frontNeighbor = chunk;
        }
    }

    void init() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                for (int z = 0; z < DEPTH; z++) {
                    blocks[y][x][z] = new Block(AIR, x + offsetX, y, z + offsetZ);
                }
            }
        }
    }

    void generate() {
        for (int x = 0; x < WIDTH; x++) {
            for (int z = 0; z < DEPTH; z++) {
                int y = (int) (noiseGenerator.noise(x + offsetX, 0, z + offsetZ) * 10f) + (HEIGHT / 2);

                if (x == 0 && z == 0)
                    blocks[y + 3][x][z].setType(GRASS);
                blocks[y][x][z].setType(GRASS);
                for (int i = 0; i < 5; i++) {
                    y--;
                    blocks[y][x][z].setType(DIRT);
                }
                y--;
                for (int i = 1; y > 2; y--, i++) {
                    double noise = noiseGenerator.noise(x + offsetX, i, z + offsetZ);

                    if (noise <= 0.7)
                        blocks[y][x][z].setType(STONE);
                }
            }
        }
    }

    public int[] getVisibleFaces(int x, int y, int z) {
        int[] indices = {};

        if (y == 0 || blocks[y - 1][x][z].getType() == AIR)
            indices = merge(indices, blocks[y][x][z].getBotIndices());
        if (y != HEIGHT && blocks[y + 1][x][z].getType() == AIR)
            indices = merge(indices, blocks[y][x][z].getTopIndices());
        if ((rightNeighbor != null && x == WIDTH - 1 && rightNeighbor.getBlocks()[y][0][z].getType() == AIR) || (rightNeighbor == null && x == WIDTH - 1) || (x != WIDTH - 1 && blocks[y][x + 1][z].getType() == AIR))
            indices = merge(indices, blocks[y][x][z].getRightIndices());
        if ((leftNeighbor != null && x == 0 && leftNeighbor.getBlocks()[y][WIDTH - 1][z].getType() == AIR) || (leftNeighbor == null && x == 0) || (x != 0 && blocks[y][x - 1][z].getType() == AIR))
            indices = merge(indices, blocks[y][x][z].getLeftIndices());
        if ((frontNeighbor != null && z == DEPTH - 1 && frontNeighbor.getBlocks()[y][x][0].getType() == AIR) || (frontNeighbor == null && z == DEPTH - 1) || (z != DEPTH - 1 && blocks[y][x][z + 1].getType() == AIR))
            indices = merge(indices, blocks[y][x][z].getFrontIndices());
        if ((backNeighbor != null && z == 0 && backNeighbor.getBlocks()[y][x][DEPTH - 1].getType() == AIR) || (backNeighbor == null && z == 0) || (z != 0 && blocks[y][x][z - 1].getType() == AIR))
            indices = merge(indices, blocks[y][x][z].getBackIndices());
        return indices;
    }

    public Block[][][] getBlocks() {
        return blocks;
    }

    public ArrayList<Block> getDrawables() {
        if (drawables.size() > 0 && !update)
            return drawables;

        try {
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    for (int z = 0; z < DEPTH; z++) {
                        if (blocks[y][x][z].getType() != AIR) {
                            int[] indices = getVisibleFaces(x, y, z);
                            if (indices.length > 0) {
                                blocks[y][x][z].setVisibleIndices(indices);
                                drawables.add(blocks[y][x][z]);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return drawables;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetZ() {
        return offsetZ;
    }
}
