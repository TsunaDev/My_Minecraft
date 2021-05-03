package engine;

import org.joml.Vector2d;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {
    private final Vector2d prevPos;
    private final Vector2d curPos;
    private final Vector2f dispVec;
    private boolean inWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;

    public MouseInput() {
        this.prevPos = new Vector2d(-1, -1);
        this.curPos = new Vector2d(0, 0);
        this.dispVec = new Vector2f();
    }

    public void init(Window window) {
        glfwSetCursorPosCallback(window.getWindowID(), (windowID, x, y) -> {
            curPos.x = x;
            curPos.y = y;
        });

        glfwSetCursorEnterCallback(window.getWindowID(), (windowID, entered) -> {
            inWindow = entered;
        });

        glfwSetMouseButtonCallback(window.getWindowID(), (windowID, button, action, mode) -> {
           leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
           rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }

    public void input(Window window) {
        dispVec.x = 0;
        dispVec.y = 0;

        if (prevPos.x > 0 && prevPos.y > 0 && inWindow) {
            double deltaX = curPos.x - prevPos.x;
            double deltaY = curPos.y - prevPos.y;

            if (deltaX != 0)
                dispVec.y = (float)deltaX;
            if (deltaY != 0)
                dispVec.x = (float)deltaY;
        }
        prevPos.x = curPos.x;
        prevPos.y = curPos.y;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    public Vector2f getDispVec() {
        return dispVec;
    }
}
