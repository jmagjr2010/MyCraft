/***************************************************************
* file: MyCraft.java 
* author: Jorge Magana, Jonathan Wong, Michael Ng 
* class: CS 445 – Computer Graphics
* 
* assignment: Quarter Project - Final Checkpoint
* date last modified: 12/1/2015
* 
* purpose: This program simulates a basic Minecraft-like game. This version
* should have created a 30x30 chunk area to render randomly placed blocks. The
* minimum height has been set to 20 blocks in order to keep a foundation. The noise
* generation can be modified in the Chunk.java file to increase or decrease terrain
* levels. By default terrain levels are set to low to show smooth increase in hills
* and valleys, along with rivers/lakes.
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
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class MyCraft {
    private Camera fp;
    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    private Sound sound;
    
    /**
     * Method used to begin creating display.
     */
    public void start() { 
        try {
            createWindow();
            initGL();
            sound = new Sound();
            sound.playSound();
            fp = new Camera(0f, -50f, -30f);
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
        
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
    }
    
    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
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
