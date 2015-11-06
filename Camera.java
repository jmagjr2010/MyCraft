/***************************************************************
* file: Camera.java 
* author: Jorge Magana, Jonathan Wong, Michael Ng 
* class: CS 445 – Computer Graphics
* 
* assignment: Quarter Project - Checkpoint 1
* date last modified: 11/5/2015
* 
* purpose: This class represents the camera object which is used to control
* the camera movements and rotations in the 3d space we created.
****************************************************************/
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class Camera {
    private Vector3f position = null;   // Camera's position
    private Vector3f lPosition = null;  // Camera's Lposition
    private float yaw = 0.0f;           // rotation around Y-axis
    private float pitch = 0.0f;         // rotation around X-axis
    private CameraVector me;            // Camera's coordinates in 3D space.
    
    /**
     * Instantiates Camera at specified x,y,z coordinates.
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @param z Z-Coordinate
     */
    public Camera(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x, y, z);
        lPosition.x = 0f;
        lPosition.y = 15f;
        lPosition.z = 0f;
    }

    /**
     * Increments the camera's Y-rotation
     * @param amount size of increment.
     */
    public void yaw(float amount) {
        yaw += amount;
    }

    /**
     * Decrement the camera's Y-rotation
     * @param amount size of decrement.
     */
    public void pitch(float amount) {
        pitch -= amount;
    }
    
    /**
     * Move the camera forward by the specified distance, relative to its
     * current Y-rotation.
     * @param distance Distance to move.
     */
    public void walkForward(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
    }

    /**
     * Move the camera backward by the specified distance, relative to its
     * current Y-rotation.
     * @param distance Distance to move.
     */
    public void walkBackwards(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
    }

    /**
     * Strafe the camera to the left by the specified distance, relative to its
     * current Y-rotation.
     * @param distance Distance to move.
     */
    public void strafeLeft(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        position.x -= xOffset;
        position.z += zOffset;
    }

    /**
     * Strafe the camera to the right by the specified distance, relative to its
     * current Y-rotation.
     * @param distance Distance to move.
     */
    public void strafeRight(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        position.x -= xOffset;
        position.z += zOffset;
    }

    /**
     * Moves the camera up by the specified distance, relative to its
     * current Y-rotation.
     * @param distance Distance to move.
     */
    public void moveUp(float distance) {
        position.y -= distance;
    }
    
    /**
     * Moves the camera down by the specified distance, relative to its
     * current Y-rotation.
     * @param distance Distance to move.
     */
    public void moveDown(float distance) {
        position.y += distance;
    }

    /**
     * Translates and rotates matrix so that the window view is through
     * the camera's perspective.
     */
    public void lookThrough() {
        //rotate the pitch around the X axis
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //rotate the yaw around the Y axis
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location
        glTranslatef(position.x, position.y, position.z);
    }
}
