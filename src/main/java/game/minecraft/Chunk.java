package game.minecraft;

import engine.GameItem;
import engine.NoiseGenerator;

import java.util.ArrayList;

import static game.minecraft.BlockType.*;

public class Chunk {
    static public int WIDTH = 16;
    static public int DEPTH = 16;
    static public int HEIGHT = 128;
    private final int offsetX;
    private final int offsetZ;
    private final Block[][][] blocks;
    private final NoiseGenerator noiseGenerator;

    Chunk(int offsetX, int offsetZ, NoiseGenerator noiseGenerator) {
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
        blocks = new Block[HEIGHT][WIDTH][DEPTH];
        this.noiseGenerator = noiseGenerator;
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
                int y = (int) (noiseGenerator.noise(x + offsetX, 0, z + offsetZ) * 10f);
                blocks[y + (HEIGHT / 2)][x][z].setType(GRASS);
                blocks[y + (HEIGHT / 2) - 1][x][z].setType(DIRT);
                blocks[y + (HEIGHT / 2) - 2][x][z].setType(DIRT);
                blocks[y + (HEIGHT / 2) - 3][x][z].setType(DIRT);
                blocks[y + (HEIGHT / 2) - 4][x][z].setType(DIRT);
            }
        }
    }

    private boolean isVisible(int x, int y, int z) {
        if (y == 0 || y == HEIGHT - 1 || z == 0 || z == DEPTH - 1 || x == 0 || x == WIDTH - 1)
            return true;
        return blocks[y - 1][x][z].getType() == AIR ||
                blocks[y + 1][x][z].getType() == AIR ||
                blocks[y][x - 1][z].getType() == AIR ||
                blocks[y][x + 1][z].getType() == AIR ||
                blocks[y][x][z - 1].getType() == AIR ||
                blocks[y][x][z + 1].getType() == AIR;
    }

    public ArrayList<Block> getDrawables() {
        ArrayList<Block> drawables = new ArrayList<>();

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                for (int z = 0; z < DEPTH; z++) {
                    if (blocks[y][x][z].getType() != AIR && isVisible(x, y, z))
                        drawables.add(blocks[y][x][z]);
                }
            }
        }
        return drawables;
    }
}
