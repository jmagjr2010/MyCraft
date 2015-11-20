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
            fp = new Camera(-20, -90, -30);
            fp.gameLoop(); // exectues render method
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
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
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
