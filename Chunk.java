/***************************************************************
* file: Chunk.java 
* author: Jorge Magana, Jonathan Wong, Michael Ng 
* class: CS 445 – Computer Graphics
* 
* assignment: Quarter Project - Final Checkpoint
* date last modified: 12/1/2015
* 
* purpose: This class is used to generate the 3d world filled with randomly
* placed blocks through noise generation. It also contains the main render method
* of this application.
* NOTE: If you wish to use View Frustum Culling, please set ViewFrustumCulling to true.
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
    static final int MIN_HEIGHT = 20;
    private boolean ViewFrustumCulling = false;
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    private Texture texture;
    private SimplexNoise noise;
    private ViewFrustum viewFrustum;
    
    /**
     * Constructor for Chunk class. Initializes image file for texture mapping and
     * initializes starting point of chunk along with each block that is generated
     * within the chunk. Also creates chunk terrain based on simplex noise generation.
     * @param startX x-coordinate of starting point for chunk.
     * @param startY y-coordinate of starting point for chunk.
     * @param startZ z-coordinate of starting point for chunk.
     * @param vf     Frustum used for chunk generation.
     */
    public Chunk(int startX, int startY, int startZ, ViewFrustum vf) {
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        viewFrustum = vf;
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        }
        catch (Exception e) {
            System.out.print("ER-ROAR!");
        }
        r = new Random();
        noise = new SimplexNoise(CHUNK_SIZE, 0.03, r.nextInt());
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                // generate random max height for each x,z position using simplex noise
                int i = (int)(StartX + x * ((CHUNK_SIZE - StartX) / CHUNK_SIZE));
                int k = (int)(StartZ + z * ((CHUNK_SIZE - StartY) / CHUNK_SIZE));
                int maxHeight = (StartY + (int)(100 * noise.getNoise(i,5,k) * CUBE_LENGTH));
                
                for (int y = 0; y < CHUNK_SIZE; y++) {
                    Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Default);
                    
                    // BedRock at the lowest level.
                    // Block and Stone randomly placed under top-most terrain.
                    // NOTE: More stone in lower half and more dirt on top half
                    // Sand placed right under top-most terrain.
                    // Rest is grass.
                    if (y == 0) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    } else if (y < MIN_HEIGHT/2) {
                        if (r.nextFloat() > 0.2)
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                        else
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                        
                        if (maxHeight < 0 && y + 1 == MIN_HEIGHT) {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                        }
                    } else if (y < MIN_HEIGHT) {
                        if (r.nextFloat() > 0.8)
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                        else
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                        
                        if (maxHeight < 0 && y + 1 == MIN_HEIGHT) {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                        }
                    } else {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    }
                    
                    // set any blocks above the max height to inactive.
                    // should leave craters in terrain.
                    if (y > MIN_HEIGHT + maxHeight) {
                        Blocks[x][y][z].SetActive(false);
                    }
                }
            }
        }
        
        
        // Re-iterates through chunk and changes;
        // Fills craters with water and puts 2 sand blocks under lowest water block
        // Changes blocks below grass to dirt or can leave as grass by uncommenting "finished = true"
        // Changes blocks below and above sand to sand
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < CHUNK_SIZE; y++) {
                    boolean finished = false;
                    
                    if (!Blocks[x][y][z].IsActive()) {
                        if (y < MIN_HEIGHT) {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                            Blocks[x][y-1][z] = new Block(Block.BlockType.BlockType_Sand);
                            
                            for (int i = y+1; i < MIN_HEIGHT; i++) {
                                Blocks[x][i][z] = new Block(Block.BlockType.BlockType_Water);
                            }
                        }
                        break;
                    }
                    
                    switch (Blocks[x][y][z].GetID()) {
                        case 0:
                            Blocks[x][y-1][z] = new Block(Block.BlockType.BlockType_Dirt);
                            Blocks[x][y-2][z] = new Block(Block.BlockType.BlockType_Dirt);
//                            finished = true;
                            break;
                        case 1:
                            Blocks[x][y-1][z] = new Block(Block.BlockType.BlockType_Sand);
                            Blocks[x][y-2][z] = new Block(Block.BlockType.BlockType_Sand);
                            Blocks[x][y+1][z] = new Block(Block.BlockType.BlockType_Sand);
                            finished = true;
                            break;
                        default:
                            break;
                    }
                    
                    if (finished) {
                        break;
                    }
                }
            }
        }
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        rebuildMesh();
    }
    
    /**
     * Renders GL Matrix and buffers for Chunk.
     */
    public void render() {
        if (ViewFrustumCulling == true)
            rebuildMesh();
        
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
     * Sets the Frustum for this chunk.
     * @param vf View Frustum
     */
    public void setViewFrustum(ViewFrustum vf) {
        viewFrustum = vf;
    }
    
    /**
     * Builds the layout of all the blocks within the 3d world.
     */
    private void rebuildMesh() {
        FloatBuffer vertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        FloatBuffer vertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        FloatBuffer vertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                
                //generate all blocks that are less than the minimum height.
                for (float y = 0; y < MIN_HEIGHT; y++) {
                    Blocks[(int)x][(int)y][(int)z].SetActive(true);
                    // If the block is in the view of the frustum, generate it, otherwise disable it
                    // NOTE: Only functions is ViewFrustumCulling is true.
                    if (ViewFrustumCulling == true && !viewFrustum.inFrustum(x, y, z, 2)) {
                        Blocks[(int)x][(int)y][(int)z].SetActive(false);
                    }
                    if (!Blocks[(int)x][(int)y][(int)z].IsActive()) {
                        continue;
                    }
                    vertexPositionData.put(createCube((float)(StartX + x * CUBE_LENGTH), (float)(y * CUBE_LENGTH + (int)(CHUNK_SIZE * .8)), (float)(StartZ + z * CUBE_LENGTH)));
                    vertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int) x][(int) y][(int) z]));
                    vertexColorData.put(createCubeVertexCol());
                }
                
                //generate terrain on topmost part of chunk.
                for (float y = MIN_HEIGHT; y < CHUNK_SIZE; y++) {
                    if (!Blocks[(int)x][(int)y][(int)z].IsActive()) {
                        continue;
                    }
                    vertexPositionData.put(createCube((float)(StartX + x * CUBE_LENGTH), (float)(y * CUBE_LENGTH + (int)(CHUNK_SIZE * .8)), (float)(StartZ + z * CUBE_LENGTH)));
                    vertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int) x][(int) y][(int) z]));
                    vertexColorData.put(createCubeVertexCol());
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
