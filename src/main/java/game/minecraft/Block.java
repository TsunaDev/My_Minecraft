package game.minecraft;

import engine.GameItem;

public class Block extends GameItem {
    BlockType type;

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
}
