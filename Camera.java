/***************************************************************
* file: Camera.java 
* author: Jorge Magana, Jonathan Wong, Michael Ng 
* class: CS 445 – Computer Graphics
* 
* assignment: Quarter Project - Final Checkpoint
* date last modified: 12/1/2015
* 
* purpose: This class represents the camera object which is used to control
* the camera movements and rotations in the 3d space we created.
****************************************************************/
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Camera {
    private Vector3f position = null;   // Camera's position
    private Vector3f lPosition = null;  // Camera's Lposition
    private float yaw = 0.0f;           // rotation around Y-axis
    private float pitch = 0.0f;         // rotation around X-axis
    private ViewFrustum vf = new ViewFrustum();
    private Chunk chunk = new Chunk(-30, 0, -30, vf);
    
    /**
     * Instantiates Camera at specified x,y,z coordinates.
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @param z Z-Coordinate
     */
    public Camera(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x, y, z);
        lPosition.x = 0.0f;
        lPosition.y = 15.0f;
        lPosition.z = 0.0f;
        vf.initFrustum();
        chunk.setViewFrustum(vf);
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
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    /**
     * Main method used to run the game until the ESC key is pressed.
     * Initializes Camera properties and other game configurations, and
     * also executes the render method.
     */
    public void gameLoop() {
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f;                // length of a frame
        float lastTime = 0.0f;          // time since last frame
        long time = 0;
        float mouseSensitivity = 0.09f; // Camera Look Sensitivity
        float movementSpeed = .35f;     // Camera movement speed
        Mouse.setGrabbed(true);         // hides the mouse cursor
        
        // run simulation until the escape key is pressed.
        while (!Display.isCloseRequested()
                && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            time = Sys.getTime();
            lastTime = time;
            
            //distance in mouse movement
            //from the last getDX() call.
            dx = Mouse.getDX();
            
            //distance in mouse movement
            //from the last getDY() call.
            dy = Mouse.getDY();

            //controls camera yaw from x movement from the mouse
            yaw(dx * mouseSensitivity);
            
            //controls camera pitch from y movement from the mouse
            pitch(dy * mouseSensitivity);
            
            //Set Movement Controls
            if (Keyboard.isKeyDown(Keyboard.KEY_W))//move forward
            {
                walkForward(movementSpeed);
                vf.initFrustum();
                chunk.setViewFrustum(vf);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S))//move backwards
            {
                walkBackwards(movementSpeed);
                vf.initFrustum();
                chunk.setViewFrustum(vf);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A))//strafe left 
            {
                strafeLeft(movementSpeed);
                vf.initFrustum();
                chunk.setViewFrustum(vf);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D))//strafe right 
            {
                strafeRight(movementSpeed);
                vf.initFrustum();
                chunk.setViewFrustum(vf);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))//move up 
            {
                moveUp(movementSpeed);
                vf.initFrustum();
                chunk.setViewFrustum(vf);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                moveDown(movementSpeed);
                vf.initFrustum();
                chunk.setViewFrustum(vf);
            }
            
            //reset modelview matrix
            glLoadIdentity();
            
            //look through the camera before you draw anything
            lookThrough();
            
            // Clear, render, and buffer scene
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            chunk.render();
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }
}
