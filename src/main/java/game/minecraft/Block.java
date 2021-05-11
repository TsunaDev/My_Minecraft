package game.minecraft;

import engine.GameItem;

import static engine.Utils.fromPixelsToPercentage;

public class Block extends GameItem {
    BlockType type;
    private final int[] topIndices = {8, 10, 11, 9, 8, 11};
    private final int[] botIndices = {20, 21, 23, 23, 22, 20};
    private final int[] leftIndices = {16, 18, 19, 19, 17, 16};
    private final int[] rightIndices = {12, 13, 15, 14, 12, 15};
    private final int[] frontIndices = {0, 1, 3, 3, 1, 2,};
    private final int[] backIndices = {4, 5, 7, 7, 6, 4};
    private int[] visibleIndices = null;
    private static final float[] positions = new float[] {
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

    private static final float[] grassTexCoords = new float[]{
            23f, 2f,
            23f, 3f,
            24f, 3f,
            24f, 2f,

            23f, 2f,
            24f, 2f,
            23f, 3f,
            24f, 3f,

            // For text coords in top face
            23f, 5f,
            24f, 5f,
            23f, 6f,
            24f, 6f,

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

    private static final float[] dirtTexCoords = new float[]{
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
    private static final float[] stoneTexCoords = new float[]{
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

    private static final float[] normals = new float[] {
            // Front face
            0f,0f,1f,
            0f,0f,1f,
            0f,0f,1f,
            0f,0f,1f,

            // Back face
            0f,0f,-1f,
            0f,0f,-1f,
            0f,0f,-1f,
            0f,0f,-1f,

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

    Block(BlockType type, int x, int y, int z) {
        super();
        this.setPos((float)x, (float)(y)-64f, (float)z);
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

    static public float[] getVertices() {
        return positions;
    }

    static public float[] getNormals() {
        return normals;
    }

    static public float[] getTextureCoords(BlockType type) {
        switch(type) {
            case GRASS:
                return fromPixelsToPercentage(grassTexCoords);
            case DIRT:
                return fromPixelsToPercentage(dirtTexCoords);
            case STONE:
                return fromPixelsToPercentage(stoneTexCoords);
            default:
                return null;
        }
    }

    static public float[] getTextureCoords(Block block) {
        return getTextureCoords(block.getType());
    }

    public void setVisibleIndices(int[] visibleIndices) {
        this.visibleIndices = visibleIndices;
    }

    public int[] getVisibleIndices() {
        return visibleIndices;
    }
}
