
/***************************************************************
* file: MyCraft.java
* author: Jorge Magana, Jonathan Wong, Michael Ng
* class: CS 445 – Computer Graphics
*
* assignment: Quarter Project
* date last modified: 10/9/2015
*
* purpose: This program simulates a basic Minecraft-like game.
* 
* CONTROLS:
* ESC        - Closes the program.
****************************************************************/

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

public class MyCraft {
	public void start() {
		try {
			createWindow();
			initGL();
			render();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createWindow() throws Exception {
		Display.setFullscreen(false);
		Display.setDisplayMode(new DisplayMode(640, 480));
		Display.setTitle("TEST");
		Display.create();
	}

	private void initGL() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 640, 0, 480, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);
		glEnable(GL_DEPTH_TEST);
	}

	private void render() {

		while (!Display.isCloseRequested()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				System.exit(0);
			}
			try {
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				glLoadIdentity();
				glBegin(GL_QUADS);
				// Top
				glColor3f(0.0f, 0.0f, 1.0f);
				glVertex3f(1.0f, 1.0f, -1.0f);
				glVertex3f(-1.0f, 1.0f, -1.0f);
				glVertex3f(-1.0f, 1.0f, 1.0f);
				glVertex3f(1.0f, 1.0f, 1.0f);
				// Bottom
				glVertex3f(1.0f, -1.0f, 1.0f);
				glVertex3f(-1.0f, -1.0f, 1.0f);
				glVertex3f(-1.0f, -1.0f, -1.0f);
				glVertex3f(1.0f, -1.0f, -1.0f);
				// Front
				glVertex3f(1.0f, 1.0f, 1.0f);
				glVertex3f(-1.0f, 1.0f, 1.0f);
				glVertex3f(-1.0f, -1.0f, 1.0f);
				glVertex3f(1.0f, -1.0f, 1.0f);
				// Back
				glVertex3f(1.0f, -1.0f, -1.0f);
				glVertex3f(-1.0f, -1.0f, -1.0f);
				glVertex3f(-1.0f, 1.0f, -1.0f);
				glVertex3f(1.0f, 1.0f, -1.0f);
				// Left
				glVertex3f(-1.0f, 1.0f, 1.0f);
				glVertex3f(-1.0f, 1.0f, -1.0f);
				glVertex3f(-1.0f, -1.0f, -1.0f);
				glVertex3f(-1.0f, -1.0f, 1.0f);
				// Right
				glVertex3f(1.0f, 1.0f, -1.0f);
				glVertex3f(1.0f, 1.0f, 1.0f);
				glVertex3f(1.0f, -1.0f, 1.0f);
				glVertex3f(1.0f, -1.0f, -1.0f);
				glEnd();

				glBegin(GL_LINE_LOOP);
				// Top
				glColor3f(0.0f, 0.0f, 0.0f);
				glVertex3f(1.0f, 1.0f, -1.0f);
				glVertex3f(-1.0f, 1.0f, -1.0f);
				glVertex3f(-1.0f, 1.0f, 1.0f);
				glVertex3f(1.0f, 1.0f, 1.0f);
				glEnd();

				glBegin(GL_LINE_LOOP);
				// Bottom
				glVertex3f(1.0f, -1.0f, 1.0f);
				glVertex3f(-1.0f, -1.0f, 1.0f);
				glVertex3f(-1.0f, -1.0f, -1.0f);
				glVertex3f(1.0f, -1.0f, -1.0f);
				glEnd();

				glBegin(GL_LINE_LOOP);
				// Front
				glVertex3f(1.0f, 1.0f, 1.0f);
				glVertex3f(-1.0f, 1.0f, 1.0f);
				glVertex3f(-1.0f, -1.0f, 1.0f);
				glVertex3f(1.0f, -1.0f, 1.0f);
				glEnd();

				glBegin(GL_LINE_LOOP);
				// Back
				glVertex3f(1.0f, -1.0f, -1.0f);
				glVertex3f(-1.0f, -1.0f, -1.0f);
				glVertex3f(-1.0f, 1.0f, -1.0f);
				glVertex3f(1.0f, 1.0f, -1.0f);
				glEnd();

				glBegin(GL_LINE_LOOP);
				// Left
				glVertex3f(-1.0f, 1.0f, 1.0f);
				glVertex3f(-1.0f, 1.0f, -1.0f);
				glVertex3f(-1.0f, -1.0f, -1.0f);
				glVertex3f(-1.0f, -1.0f, 1.0f);
				glEnd();

				glBegin(GL_LINE_LOOP);
				// Right
				glVertex3f(1.0f, 1.0f, -1.0f);
				glVertex3f(1.0f, 1.0f, 1.0f);
				glVertex3f(1.0f, -1.0f, 1.0f);
				glVertex3f(1.0f, -1.0f, -1.0f);
				glEnd();

			} catch (Exception e) {
			}
		}

	}

	public static void main(String[] args) {
		MyCraft mc = new MyCraft();
		mc.start();
	}
}