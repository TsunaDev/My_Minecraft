package engine.graph;

import org.joml.Vector3f;

public class DirectionalLight {
    private Vector3f color;
    private Vector3f direction;
    private float intensity;
    private OrthoCoords orthoCoords;
    private float shadowPosMult;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
        this.orthoCoords = new OrthoCoords();
        this.shadowPosMult = 1;
    }

    public DirectionalLight(DirectionalLight light) {
        this(new Vector3f(light.getColor()), new Vector3f(light.getDirection()), light.getIntensity());
    }

    public float getShadowPosMult() {
        return shadowPosMult;
    }

    public void setShadowPosMult(float shadowPosMult) {
        this.shadowPosMult = shadowPosMult;
    }

    public OrthoCoords getOrthoCoords() {
        return orthoCoords;
    }

    public void setOrthoCoords(float left, float right, float bottom, float top, float near, float far) {
        orthoCoords.left = left;
        orthoCoords.right = right;
        orthoCoords.bottom = bottom;
        orthoCoords.top = top;
        orthoCoords.near = near;
        orthoCoords.far = far;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public static class OrthoCoords {
        public float left;
        public float right;
        public float bottom;
        public float top;
        public float near;
        public float far;
    }
}
