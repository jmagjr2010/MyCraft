/***************************************************************
* file: MyCraft.java 
* author: Jorge Magana, Jonathan Wong, Michael Ng 
* class: CS 445 – Computer Graphics
* 
* assignment: Quarter Project - Checkpoint 1
* date last modified: 11/5/2015
* 
* purpose: This program simulates a basic Minecraft-like game.
* NOTE: I moved the camera render method to the main render method in this
* file so the rendering takes place here. This is just for checkpoint 1.
* 
* CONTROLS: 
* ESC   - Closes the program.
* W     - Moves the camera forward.
* A     - Strafes the camera to the left.
* S     - Moves the camera backward.
* D     - Strafes the camera to the right.
* Space - Moves the camera up.
* Shift - Moves the camera down.
* 
****************************************************************/

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class MyCraft {
    private Camera fp;
    private DisplayMode displayMode;
    
    /**
     * Method used to begin creating display.
     */
    public void start() { 
        try {
            createWindow();
            initGL();
            gameLoop(); // exectues render method
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /**
     * Method used to create window size of 640 x 480 using LWJGL.
     * @throws Exception 
     */
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640
                    && d[i].getHeight() == 480
                    && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("MyCraft");
        Display.create();
    }
    
    /**
     * Method used to initialize window for 2D pixel art.
     */
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float) displayMode.getWidth() / (float) displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
    }
    
    /**
     * Main method used to run the game until the ESC key is pressed.
     * Initializes Camera properties and other game configurations, and
     * also executes the render method.
     */
    public void gameLoop() {
        // Begin right in front of the cube
        Camera camera = new Camera(0, 0, -3);
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
            camera.yaw(dx * mouseSensitivity);
            
            //controls camera pitch from y movement from the mouse
            camera.pitch(dy * mouseSensitivity);
            
            //Set Movement Controls
            if (Keyboard.isKeyDown(Keyboard.KEY_W))//move forward
            {
                camera.walkForward(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S))//move backwards
            {
                camera.walkBackwards(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A))//strafe left 
            {
                camera.strafeLeft(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D))//strafe right 
            {
                camera.strafeRight(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))//move up 
            {
                camera.moveUp(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                camera.moveDown(movementSpeed);
            }
            
            //reset modelview matrix
            glLoadIdentity();
            
            //look through the camera before you draw anything
            camera.lookThrough();
            
            // Clear, render, and buffer scene
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            render();
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }
    
    /**
     * Primary method used to draw pixel graphics onto screen.
     */
    private void render() {
        try {
            glBegin(GL_QUADS);
            //Top of block
            glColor3f(0.0f, 0.0f, 1.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            //Bottom of block
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);
            //Front of block
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            //Back of block
            glColor3f(0.0f, 1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);
            //Left of block
            glColor3f(1.0f, 0.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            //Right of block
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);
            glEnd();
            
            glBegin(GL_LINE_LOOP);
            //Top
            glColor3f(0.0f, 0.0f, 0.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            glEnd();
            glBegin(GL_LINE_LOOP);
            //Bottom
            glVertex3f(1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);
            glEnd();
            glBegin(GL_LINE_LOOP);
            //Front
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            glEnd();
            glBegin(GL_LINE_LOOP);
            //Back
            glVertex3f(1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);
            glEnd();
            glBegin(GL_LINE_LOOP);
            //Left
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glEnd();
            glBegin(GL_LINE_LOOP);
            //Right
            glVertex3f(1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);
            glEnd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Main Method used to initialize class and program.
     * @param args arguments passed in from command line(NOT USED).
     */
    public static void main(String[] args) {
        MyCraft mc = new MyCraft();
        mc.start();
    }
}
