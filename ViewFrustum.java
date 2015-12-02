/***************************************************************
* file: ViewFrustum.java 
* author: Jorge Magana, Jonathan Wong, Michael Ng 
* class: CS 445 – Computer Graphics
* 
* assignment: Quarter Project - Final Checkpoint
* date last modified: 12/1/2015
* 
* purpose: This class is used to render blocks only shown on screen through a
* View Frustum Culling Algorithm.
****************************************************************/

import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.*;

public class ViewFrustum
{
    float[][] viewFrustum = new float[6][4];
    public static final int FRONT_SIDE = 5;
    public static final int BACK_SIDE = 4;
    public static final int TOP_SIDE = 3;
    public static final int BOTTOM_SIDE = 2;
    public static final int LEFT_SIDE = 1;
    public static final int RIGHT_SIDE = 0;
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;
    FloatBuffer model;
    FloatBuffer project;
    
    /**
     * Constructor for the ViewFrustum used to initialize Float Buffers.
     */
    public ViewFrustum() {
        model = BufferUtils.createFloatBuffer(16);
        project = BufferUtils.createFloatBuffer(16);
    }

     /**
     * Detects whether the specific cube is within the view frustum.
     * @param x x position of cube.
     * @param y y position of cube.
     * @param z z position of cube.
     * @param size length of cube.
     * @return true if the cube lies within the view frustum.
     */
    public boolean inFrustum(float x, float y, float z, float size) {
        for(int i = 0; i < 6; i++ ) {
            if(viewFrustum[i][FIRST] * (x - size) + viewFrustum[i][SECOND] * (y - size) + viewFrustum[i][THIRD] * (z - size) + viewFrustum[i][FOURTH] > 0)
                continue;
            if(viewFrustum[i][FIRST] * (x + size) + viewFrustum[i][SECOND] * (y - size) + viewFrustum[i][THIRD] * (z - size) + viewFrustum[i][FOURTH] > 0)
                continue;
            if(viewFrustum[i][FIRST] * (x - size) + viewFrustum[i][SECOND] * (y + size) + viewFrustum[i][THIRD] * (z - size) + viewFrustum[i][FOURTH] > 0)
                continue;
            if(viewFrustum[i][FIRST] * (x + size) + viewFrustum[i][SECOND] * (y + size) + viewFrustum[i][THIRD] * (z - size) + viewFrustum[i][FOURTH] > 0)
                continue;
            if(viewFrustum[i][FIRST] * (x - size) + viewFrustum[i][SECOND] * (y - size) + viewFrustum[i][THIRD] * (z + size) + viewFrustum[i][FOURTH] > 0)
                continue;
            if(viewFrustum[i][FIRST] * (x + size) + viewFrustum[i][SECOND] * (y - size) + viewFrustum[i][THIRD] * (z + size) + viewFrustum[i][FOURTH] > 0)
                continue;
            if(viewFrustum[i][FIRST] * (x - size) + viewFrustum[i][SECOND] * (y + size) + viewFrustum[i][THIRD] * (z + size) + viewFrustum[i][FOURTH] > 0)
                continue;
            if(viewFrustum[i][FIRST] * (x + size) + viewFrustum[i][SECOND] * (y + size) + viewFrustum[i][THIRD] * (z + size) + viewFrustum[i][FOURTH] > 0)
                continue;

            return false;
        }

        return true;
    }
    
    /**
     * calculates frustum based on clip and projection matrix
     */
    public void initFrustum() {
        float[] projection = new float[16];
        float[] modelMat = new float[16];
        float[] clip = new float[16];

        project.rewind();
        glGetFloat(GL_PROJECTION_MATRIX, project);
        project.rewind();
        project.get(projection);

        model.rewind();
        glGetFloat(GL_MODELVIEW_MATRIX, model);
        model.rewind();
        model.get(modelMat);

        // Create clipping matrix through multiplying projection and model matrices.
        clip[0] = modelMat[0] * projection[0] + modelMat[1] * projection[4] + modelMat[2] * projection[8] + modelMat[3] * projection[12];
        clip[1] = modelMat[0] * projection[1] + modelMat[1] * projection[5] + modelMat[2] * projection[9] + modelMat[3] * projection[13];
        clip[2] = modelMat[0] * projection[2] + modelMat[1] * projection[6] + modelMat[2] * projection[10] + modelMat[3] * projection[14];
        clip[3] = modelMat[0] * projection[3] + modelMat[1] * projection[7] + modelMat[2] * projection[11] + modelMat[3] * projection[15];
        clip[4] = modelMat[4] * projection[0] + modelMat[5] * projection[4] + modelMat[6] * projection[8] + modelMat[7] * projection[12];
        clip[5] = modelMat[4] * projection[1] + modelMat[5] * projection[5] + modelMat[6] * projection[9] + modelMat[7] * projection[13];
        clip[6] = modelMat[4] * projection[2] + modelMat[5] * projection[6] + modelMat[6] * projection[10] + modelMat[7] * projection[14];
        clip[7] = modelMat[4] * projection[3] + modelMat[5] * projection[7] + modelMat[6] * projection[11] + modelMat[7] * projection[15];
        clip[8] = modelMat[8] * projection[0] + modelMat[9] * projection[4] + modelMat[10] * projection[8] + modelMat[11] * projection[12];
        clip[9] = modelMat[8] * projection[1] + modelMat[9] * projection[5] + modelMat[10] * projection[9] + modelMat[11] * projection[13];
        clip[10] = modelMat[8] * projection[2] + modelMat[9] * projection[6] + modelMat[10] * projection[10] + modelMat[11] * projection[14];
        clip[11] = modelMat[8] * projection[3] + modelMat[9] * projection[7] + modelMat[10] * projection[11] + modelMat[11] * projection[15];
        clip[12] = modelMat[12] * projection[0] + modelMat[13] * projection[4] + modelMat[14] * projection[8] + modelMat[15] * projection[12];
        clip[13] = modelMat[12] * projection[1] + modelMat[13] * projection[5] + modelMat[14] * projection[9] + modelMat[15] * projection[13];
        clip[14] = modelMat[12] * projection[2] + modelMat[13] * projection[6] + modelMat[14] * projection[10] + modelMat[15] * projection[14];
        clip[15] = modelMat[12] * projection[3] + modelMat[13] * projection[7] + modelMat[14] * projection[11] + modelMat[15] * projection[15];

        // Get each side of the frustrum and normalize it based on magnitude, starting with the left.
        viewFrustum[LEFT_SIDE][FIRST] = clip[ 3] + clip[ 0];
        viewFrustum[LEFT_SIDE][SECOND] = clip[ 7] + clip[ 4];
        viewFrustum[LEFT_SIDE][THIRD] = clip[11] + clip[ 8];
        viewFrustum[LEFT_SIDE][FOURTH] = clip[15] + clip[12];
        adjustPlane(viewFrustum, LEFT_SIDE);
        viewFrustum[RIGHT_SIDE][FIRST] = clip[ 3] - clip[ 0];
        viewFrustum[RIGHT_SIDE][SECOND] = clip[ 7] - clip[ 4];
        viewFrustum[RIGHT_SIDE][THIRD] = clip[11] - clip[ 8];
        viewFrustum[RIGHT_SIDE][FOURTH] = clip[15] - clip[12];
        adjustPlane(viewFrustum, RIGHT_SIDE);
        viewFrustum[BOTTOM_SIDE][FIRST] = clip[ 3] + clip[ 1];
        viewFrustum[BOTTOM_SIDE][SECOND] = clip[ 7] + clip[ 5];
        viewFrustum[BOTTOM_SIDE][THIRD] = clip[11] + clip[ 9];
        viewFrustum[BOTTOM_SIDE][FOURTH] = clip[15] + clip[13];
        adjustPlane(viewFrustum, BOTTOM_SIDE);
        viewFrustum[TOP_SIDE][FIRST] = clip[ 3] - clip[ 1];
        viewFrustum[TOP_SIDE][SECOND] = clip[ 7] - clip[ 5];
        viewFrustum[TOP_SIDE][THIRD] = clip[11] - clip[ 9];
        viewFrustum[TOP_SIDE][FOURTH] = clip[15] - clip[13];
        adjustPlane(viewFrustum, TOP_SIDE);
        viewFrustum[FRONT_SIDE][FIRST] = clip[ 3] + clip[ 2];
        viewFrustum[FRONT_SIDE][SECOND] = clip[ 7] + clip[ 6];
        viewFrustum[FRONT_SIDE][THIRD] = clip[11] + clip[10];
        viewFrustum[FRONT_SIDE][FOURTH] = clip[15] + clip[14];
        adjustPlane(viewFrustum, FRONT_SIDE);
        viewFrustum[BACK_SIDE][FIRST] = clip[ 3] - clip[ 2];
        viewFrustum[BACK_SIDE][SECOND] = clip[ 7] - clip[ 6];
        viewFrustum[BACK_SIDE][THIRD] = clip[11] - clip[10];
        viewFrustum[BACK_SIDE][FOURTH] = clip[15] - clip[14];
        adjustPlane(viewFrustum, BACK_SIDE);
    }

    /**
     * Normalizes the plane for Z-buffering based on plane magnitude.
     * @param frustum frustrum matrix
     * @param side the side of the frustum to normalize
     */
    public void adjustPlane(float[][] frustum, int side) {
        float mag = (float) Math.sqrt(frustum[side][FIRST] * frustum[side][FIRST] + frustum[side][SECOND] * frustum[side][SECOND] + frustum[side][THIRD] * frustum[side][THIRD]);

        // divide each frustum side by magnitude
        frustum[side][FIRST] /= mag;
        frustum[side][SECOND] /= mag;
        frustum[side][THIRD] /= mag;
        frustum[side][FOURTH] /= mag;
    }
}