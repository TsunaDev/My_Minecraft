package game.minecraft;

import engine.GameItem;

public class Block extends GameItem {
    BlockType type;
    private final int[] topIndices = {8, 10, 11, 9, 8, 11};
    private final int[] botIndices = {20, 21, 23, 23, 22, 20};
    private final int[] leftIndices = {16, 18, 19, 19, 17, 16};
    private final int[] rightIndices = {12, 13, 15, 14, 12, 15};
    private final int[] frontIndices = {0, 1, 3, 3, 1, 2,};
    private final int[] backIndices = {4, 5, 7, 7, 6, 4};
    private int[] visibleIndices = null;

    private final int[] indices = new int[]{
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            8, 10, 11, 9, 8, 11,
            // Right face
            12, 13, 15, 14, 12, 15,
            // Left face
            16, 18, 19, 19, 17, 16,
            // Bottom face
            20, 21, 23, 23, 22, 20,
            // Back face
            4, 5, 7, 7, 6, 4
    };

    Block(BlockType type, int x, int y, int z) {
        super();
        this.setPos((float)x, (float)y, (float)z);
        this.type = type;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    public BlockType getType() {
        return type;
    }

    public int[] getTopIndices() {
        return topIndices;
    }

    public int[] getBackIndices() {
        return backIndices;
    }

    public int[] getBotIndices() {
        return botIndices;
    }

    public int[] getFrontIndices() {
        return frontIndices;
    }

    public int[] getLeftIndices() {
        return leftIndices;
    }

    public int[] getRightIndices() {
        return rightIndices;
    }

    public int[] getIndices() {
        return indices;
    }

    public void setVisibleIndices(int[] visibleIndices) {
        this.visibleIndices = visibleIndices;
    }

    public int[] getVisibleIndices() {
        return visibleIndices;
    }
}
