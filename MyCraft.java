/***************************************************************
* file: MyCraft.java 
* author: Jorge Magana, Jonathan Wong, Michael Ng 
* class: CS 445 – Computer Graphics
* 
* assignment: Quarter Project - Checkpoint 2
* date last modified: 11/19/2015
* 
* purpose: This program simulates a basic Minecraft-like game. This version
* should have created a 30x30 chunk area to render randomly placed blocks. The
* minimum height has been set to 5 blocks in order to keep a foundation. The noise
* generation can be modified in the Chunk.java file to increase or decrease terrain
* levels. By default terrain levels are set to low to show smooth increase in hills
* and valleys.
* 
* NOTE: Currently all blocks are also randomly textured. If you would like only
* 1 type of texture to be displayed, please add 1.0 to r.nextInt() in the constructor
* of the chunk class, that way the randomly generated block will always be a grass
* block.
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
            fp = new Camera(-20, -50, -70);
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
        glClearColor(0.1f, 0.6f, 0.9f, 0.0f);
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
