package engine.graph;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Mesh {
    private final int VAO;
    private final int posVBO;
    private final int texVBO;
    private final int normVBO;
    private Material material;

    public Mesh(float[] positions, float[] texCoords, float[] normals) {

        FloatBuffer verticesBuffer = null;
        FloatBuffer texCoordsBuffer = null;
        FloatBuffer normCoordsBuffer = null;

        try {

            this.VAO = glGenVertexArrays();
            glBindVertexArray(this.VAO);

            this.posVBO = glGenBuffers();
            verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
            verticesBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, this.posVBO);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            this.texVBO = glGenBuffers();
            texCoordsBuffer = MemoryUtil.memAllocFloat(texCoords.length);
            texCoordsBuffer.put(texCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, this.texVBO);
            glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            this.normVBO = glGenBuffers();
            normCoordsBuffer = MemoryUtil.memAllocFloat(normals.length);
            normCoordsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, this.normVBO);
            glBufferData(GL_ARRAY_BUFFER, normCoordsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (verticesBuffer != null)
                memFree(verticesBuffer);
            if (texCoordsBuffer != null)
                memFree(texCoordsBuffer);
            if (normCoordsBuffer != null)
                memFree(normCoordsBuffer);
        }
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void start() {
        if (material.isTextured()) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, material.getTexture().getId());
        }
        glBindVertexArray(VAO);
    }

    public void end() {
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }


    public void renderFaces(int[] indices) {
        IntBuffer indexBuffer = null;
        try {
            indexBuffer = MemoryUtil.memAllocInt(indices.length);
            indexBuffer.put(indices).flip();
            glDrawElements(GL_TRIANGLES, indexBuffer);
        } finally {
            memFree(indexBuffer);
        }

    }


    public void cleanUp() {
        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(posVBO);
        glDeleteBuffers(texVBO);
        glDeleteBuffers(normVBO);

        if (material.isTextured())
            material.getTexture().cleanUp();

        glBindVertexArray(0);
        glDeleteVertexArrays(VAO);
    }
}
