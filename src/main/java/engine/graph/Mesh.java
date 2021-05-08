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
    //private final int idxVBO;
    private final int vertexCount;
    private final Texture texture;

    public Mesh(float[] positions, float[] texCoords, int[] indices, Texture texture) {

        FloatBuffer verticesBuffer = null;
        FloatBuffer texCoordsBuffer = null;
        IntBuffer indicesBuffer = null;

        try {
            this.texture = texture;
            this.vertexCount = indices.length;

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

            /*this.idxVBO = glGenBuffers();
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.idxVBO);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
*/
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (verticesBuffer != null)
                memFree(verticesBuffer);
            if (indicesBuffer != null)
                memFree(indicesBuffer);
            if (texCoordsBuffer != null)
                memFree(texCoordsBuffer);
        }
    }

    public void start() {
        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, texture.getId());
        glBindVertexArray(VAO);
    }

    public void end() {
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindVertexArray(0);
    }

    public void render() {


            glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

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

    public int getVAO() {
        return VAO;
    }


    public int getVertexCount() {
        return vertexCount;
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(posVBO);
        //glDeleteBuffers(idxVBO);
        glDeleteBuffers(texVBO);

        texture.cleanUp();

        glBindVertexArray(0);
        glDeleteVertexArrays(VAO);
    }
}
