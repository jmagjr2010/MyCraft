/***************************************************************
* file: Chunk.java 
* author: Jorge Magana, Jonathan Wong, Michael Ng 
* class: CS 445 – Computer Graphics
* 
* assignment: Quarter Project - Checkpoint 2
* date last modified: 11/19/2015
* 
* purpose: This class is used to generate the 3d world filled with randomly
* placed blocks through noise generation. It also contains the main render method
* of this application.
****************************************************************/
import java.nio.FloatBuffer;
import java.util.Random;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    static final int MIN_HEIGHT = 5;
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    private int VBOTextureHandle;
    private Texture texture;
    private SimplexNoise noise;
    
    /**
     * Constructor for Chunk class. Initializes image file for texture mapping and
     * initializes starting point of chunk along with each block that is generated
     * within the chunk.
     * @param startX x-coordinate of starting point for chunk.
     * @param startY y-coordinate of starting point for chunk.
     * @param startZ z-coordinate of starting point for chunk.
     */
    public Chunk(int startX, int startY, int startZ) {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        }
        catch (Exception e) {
            System.out.print("ER-ROAR!");
        }
        r = new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {                 
                    if (r.nextFloat() > 0.0f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    } else if(r.nextFloat() > 0.7f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    } else if(r.nextFloat() > 0.5f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                    } else if(r.nextFloat() > 0.3f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                    } else if(r.nextFloat() > 0.1f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    } else {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    }
                }
            }
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
    
    /**
     * Renders GL Matrix and buffers for Chunk.
     */
    public void render() {
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
            glBindTexture(GL_TEXTURE_2D, 1);
            glTexCoordPointer(2, GL_FLOAT, 0, 0L);
            glDrawArrays(GL_QUADS, 0, CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE*24);
        glPopMatrix();
    }
    
    /**
     * Builds the layout of all the blocks within the 3d world.
     * @param startX starting x-point for chunk.
     * @param startY starting y-point for chunk.
     * @param startZ starting z-point for chunk.
     */
    public void rebuildMesh(float startX, float startY, float startZ) {
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer vertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        FloatBuffer vertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        FloatBuffer vertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        
        noise = new SimplexNoise(CHUNK_SIZE, 0.04, r.nextInt());
        float maxHeight;
        
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                for (float y = 0; y < CHUNK_SIZE; y++) {
                    int i = (int)(startX + x * ((CHUNK_SIZE - startX) / CHUNK_SIZE));
                    int k = (int)(startZ + z * ((CHUNK_SIZE - startY) / CHUNK_SIZE));
//                    int j = (int)(startY + y * ((30 - startY) / 5));
                    maxHeight = (startY + (int)(100 * noise.getNoise(i,k) * CUBE_LENGTH));
                    
                    if (y <= MIN_HEIGHT || y < MIN_HEIGHT + maxHeight) {
                        vertexPositionData.put(createCube((float)(startX + x * CUBE_LENGTH), (float)(y * CUBE_LENGTH + (int)(CHUNK_SIZE * .8)), (float)(startZ + z * CUBE_LENGTH)));
                        vertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int) x][(int) y][(int) z]));
                        vertexColorData.put(createCubeVertexCol());
                    }
                    else
                        break;
                }
            }
        }
        
        vertexColorData.flip();
        vertexPositionData.flip();
        vertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    /**
     * Creates a cube vertex for a column of blocks.
     * @return float array representing cube vertex.
     */
    private float[] createCubeVertexCol() {
        float[] cubeColors = new float[72];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = 1;
        }
        return cubeColors;
    }
    
    /**
     * UNUSED. Was used for testing when the blocks were colored.
     * @param CubeColorArray array of floats representing color cubes.
     * @return array of floats representing cube vertex.
     */
    private float[] createCubeVertexCol(float [] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        
        return cubeColors;
    }
    
    /**
     * creates cube mesh at specified coordinate in the 3D world.
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return cube vertex representing created cubes.
     */
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
            return new float[] {
                // Top Quadrant
                x + offset, y + offset, z,
                x - offset, y + offset, z,
                x - offset, y + offset, z - CUBE_LENGTH,
                x + offset, y + offset, z - CUBE_LENGTH,
                // Bottom Quadrant
                x + offset, y - offset, z - CUBE_LENGTH,
                x - offset, y - offset, z - CUBE_LENGTH,
                x - offset, y - offset, z,
                x + offset, y - offset, z,
                // Front Quadrant
                x + offset, y + offset, z - CUBE_LENGTH,
                x - offset, y + offset, z - CUBE_LENGTH,
                x - offset, y - offset, z - CUBE_LENGTH,
                x + offset, y - offset, z - CUBE_LENGTH,
                // Back Quadrant
                x + offset, y - offset, z,
                x - offset, y - offset, z,
                x - offset, y + offset, z,
                x + offset, y + offset, z,
                // Left Quadrant
                x - offset, y + offset, z - CUBE_LENGTH,
                x - offset, y + offset, z,
                x - offset, y - offset, z,
                x - offset, y - offset, z - CUBE_LENGTH,
                // Right Quadrant
                x + offset, y + offset, z,
                x + offset, y + offset, z - CUBE_LENGTH,
                x + offset, y - offset, z - CUBE_LENGTH,
                x + offset, y - offset, z };
    }
    
    /**
     * UNUSED. was used for testing when the blocks were colored.
     * @param block
     * @return vertex
     */
    private float[] getCubeColor(Block block) {        
        return new float[] {1, 1, 1};
    }
    
    /**
     * Textures each cube depending on its ID.
     * @param x x-coordinate
     * @param y y-coordinate
     * @param block block to be textured.
     * @return vertex for newly textured block.
     */
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f/16)/1024f;
        
        switch (block.GetID()) {
            case 0:
                // grass texture
                return new float[] {
                    // Top Quadrant
                    x + offset*3, y + offset*10,
                    x + offset*2, y + offset*10,
                    x + offset*2, y + offset*9,
                    x + offset*3, y + offset*9,
                    // Bottom Quadrant
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // Front Quadrant
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // Back Quadrant
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    // Left Quadrant
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // Right Quadrant
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1 };
            case 1:
                // sand texture
                return new float[] {
                    // Top Quadrant
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    // Bottom Quadrant
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    // Front Quadrant
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // Back Quadrant
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    // Left Quadrant
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // Right Quadrant
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2 };
            case 2:
                // water texture
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 15, y + offset * 13,
                    x + offset * 14, y + offset * 13,
                    x + offset * 14, y + offset * 12,
                    x + offset * 15, y + offset * 12,
                    // TOP!
                    x + offset * 15, y + offset * 13,
                    x + offset * 14, y + offset * 13,
                    x + offset * 14, y + offset * 12,
                    x + offset * 15, y + offset * 12,
                    // FRONT QUAD
                    x + offset * 14, y + offset * 12,
                    x + offset * 15, y + offset * 12,
                    x + offset * 15, y + offset * 13,
                    x + offset * 14, y + offset * 13,
                    // BACK QUAD
                    x + offset * 15, y + offset * 13,
                    x + offset * 14, y + offset * 13,
                    x + offset * 14, y + offset * 12,
                    x + offset * 15, y + offset * 12,
                    // LEFT QUAD
                    x + offset * 14, y + offset * 12,
                    x + offset * 15, y + offset * 12,
                    x + offset * 15, y + offset * 13,
                    x + offset * 14, y + offset * 13,
                    // RIGHT QUAD
                    x + offset * 14, y + offset * 12,
                    x + offset * 15, y + offset * 12,
                    x + offset * 15, y + offset * 13,
                    x + offset * 14, y + offset * 13};
            case 3:
                // dirt texture
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // TOP!
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // FRONT QUAD
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // BACK QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // LEFT QUAD
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1 };

            case 4:
                // stone texture
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // TOP!
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // FRONT QUAD
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // BACK QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // LEFT QUAD
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1};

            case 5:
                // bedrock texture
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // TOP!
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // FRONT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // BACK QUAD
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // LEFT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // RIGHT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2};
            default:
                return new float[] {
                    // Bottom Quadrant
                    x + offset * 4, y + offset * 13,
                    x + offset * 3, y + offset * 13,
                    x + offset * 3, y + offset * 14,
                    x + offset * 4, y + offset * 14,
                    // Top Quadrant
                    x + offset * 4, y + offset * 13,
                    x + offset * 3, y + offset * 13,
                    x + offset * 3, y + offset * 14,
                    x + offset * 4, y + offset * 14,
                    // Front Quadrant
                    x + offset * 3, y + offset * 14,
                    x + offset * 4, y + offset * 14,
                    x + offset * 4, y + offset * 13,
                    x + offset * 3, y + offset * 13,
                    // Back Quadrant
                    x + offset * 4, y + offset * 13,
                    x + offset * 3, y + offset * 13,
                    x + offset * 3, y + offset * 14,
                    x + offset * 4, y + offset * 14,
                    // Left Quadrant
                    x + offset * 3, y + offset * 14,
                    x + offset * 4, y + offset * 14,
                    x + offset * 4, y + offset * 13,
                    x + offset * 3, y + offset * 13,
                    // Right Quadrant
                    x + offset * 3, y + offset * 14,
                    x + offset * 4, y + offset * 14,
                    x + offset * 4, y + offset * 13,
                    x + offset * 3, y + offset * 13 };
        }
    }
}
