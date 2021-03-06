package engine;

import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private String title;
    private int width;
    private int height;
    private long windowID;
    private boolean resized = false;
    private Vector4f clearColor;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        clearColor = new Vector4f(0, 0, 0, 0);
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW.");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        windowID = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowID == NULL)
            throw new RuntimeException("Failed to create a GLFW Window.");

        glfwSetFramebufferSizeCallback(windowID, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.resized = true;
        });

        glfwSetKeyCallback(windowID, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
        });

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(windowID, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);

        glfwMakeContextCurrent(windowID);
        glfwSwapInterval(1);
        glfwShowWindow(windowID);
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glClearColor(0, 0, 0, 0);
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowID);
    }

    public void setClearColor(float r, float g, float b, float a) {
        clearColor.x = r;
        clearColor.y = g;
        clearColor.z = b;
        clearColor.w = a;
        glClearColor(r, g, b, a);
    }

    public Vector4f getClearColor() {
        return clearColor;
    }

    public boolean isKeyPressed(int key) {
        return glfwGetKey(windowID, key) == GLFW_PRESS;
    }
    public boolean isKeyReleased(int key) {
        return glfwGetKey(windowID, key) == GLFW_RELEASE;
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void update() {
        glfwSwapBuffers(windowID);
        glfwPollEvents();
    }

    public long getWindowID() {
        return windowID;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
