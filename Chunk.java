
/***************************************************************
* file: Chunk.java 
* author: Jorge Magana, Jonathan Wong, Michael Ng 
* class: CS 445 – Computer Graphics
* 
* assignment: Quarter Project - Checkpoint 1
* date last modified: 11/19/2015
* 
* purpose: This class is used to generate chunks of blocks in the 3d world. 
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
	private Block[][][] Blocks;
	private int VBOVertexHandle;
	private int VBOColorHandle;
	private int StartX, StartY, StartZ;
	private Random r;
	private int VBOTextureHandle;
	private Texture texture;
	private SimplexNoise noise;
	private Random rand;
	FloatBuffer vertexPositionData;

	public Chunk(int startX, int startY, int startZ) {

		rand = new Random();
		noise = new SimplexNoise(CHUNK_SIZE, 0.04, rand.nextInt());

		try {

			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		} catch (Exception e) {
			System.out.print("ER-ROAR!");
		}

		Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];

		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int y = 0; y < CHUNK_SIZE; y++) {
				for (int z = 0; z < CHUNK_SIZE; z++) {
					int i = (int) (startX + x * CUBE_LENGTH);
					int k = (int) (startZ + z * CUBE_LENGTH);
					int maxHeight = (startY + (int) (100 * noise.getNoise(i, k)));

					Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);

					if (y > MIN_HEIGHT + maxHeight) {
						Blocks[x][y][z].SetActive(false);
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

	public void render() {
		glPushMatrix();
		glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
		glVertexPointer(3, GL_FLOAT, 0, 0L);
		glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
		glColorPointer(3, GL_FLOAT, 0, 0L);
		glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
		glBindTexture(GL_TEXTURE_2D, 1);
		glTexCoordPointer(2, GL_FLOAT, 0, 0L);
		glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
		glPopMatrix();
	}

	public void rebuildMesh(float startX, float startY, float startZ) {

		VBOVertexHandle = glGenBuffers();
		VBOColorHandle = glGenBuffers();
		VBOTextureHandle = glGenBuffers();

		vertexPositionData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 72);
		FloatBuffer vertexColorData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 72);
		FloatBuffer vertexTextureData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 72);

		for (float x = 0; x < CHUNK_SIZE; x++) {
			for (float z = 0; z < CHUNK_SIZE; z++) {
				int i = (int) (startX + x * CUBE_LENGTH);
				int k = (int) (startZ + z * CUBE_LENGTH);
				int maxHeight = (int) (startY + (int) (100 * noise.getNoise(i, k)));

				for (float y = 0; y < MIN_HEIGHT; y++) {
					vertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH),
							(float) (y * CUBE_LENGTH - CHUNK_SIZE), (float) (startZ + z * CUBE_LENGTH)));
					vertexColorData.put(createCubeVertexCol());
					vertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int) x][(int) y][(int) z]));

				}

				for (float y = MIN_HEIGHT; y <= maxHeight + MIN_HEIGHT; y++) {
					vertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH),
							(float) (y * CUBE_LENGTH - CHUNK_SIZE), (float) (startZ + z * CUBE_LENGTH)));
					vertexColorData.put(createCubeVertexCol());
					vertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int) x][(int) y][(int) z]));
				}
			}
		}
		vertexPositionData.flip();
		vertexColorData.flip();
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

	private float[] createCubeVertexCol() {
		float[] cubeColors = new float[72];
		for (int i = 0; i < cubeColors.length; i++) {
			cubeColors[i] = 1;
		}
		return cubeColors;
	}

	private float[] createCubeVertexCol(float[] CubeColorArray) {
		float[] cubeColors = new float[CubeColorArray.length * 4 * 6];

		for (int i = 0; i < cubeColors.length; i++) {
			cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
		}

		return cubeColors;
	}

	public static float[] createCube(float x, float y, float z) {
		int offset = CUBE_LENGTH / 2;
		return new float[] {
				// Top Quadrant
				x + offset, y + offset, z, x - offset, y + offset, z, x - offset, y + offset, z - CUBE_LENGTH,
				x + offset, y + offset, z - CUBE_LENGTH,
				// Bottom Quadrant
				x + offset, y - offset, z - CUBE_LENGTH, x - offset, y - offset, z - CUBE_LENGTH, x - offset,
				y - offset, z, x + offset, y - offset, z,
				// Front Quadrant
				x + offset, y + offset, z - CUBE_LENGTH, x - offset, y + offset, z - CUBE_LENGTH, x - offset,
				y - offset, z - CUBE_LENGTH, x + offset, y - offset, z - CUBE_LENGTH,
				// Back Quadrant
				x + offset, y - offset, z, x - offset, y - offset, z, x - offset, y + offset, z, x + offset, y + offset,
				z,
				// Left Quadrant
				x - offset, y + offset, z - CUBE_LENGTH, x - offset, y + offset, z, x - offset, y - offset, z,
				x - offset, y - offset, z - CUBE_LENGTH,
				// Right Quadrant
				x + offset, y + offset, z, x + offset, y + offset, z - CUBE_LENGTH, x + offset, y - offset,
				z - CUBE_LENGTH, x + offset, y - offset, z };
	}

	private float[] getCubeColor(Block block) {
		// switch (block.GetID()) {
		// case 1:
		// return new float[] {0, 1, 0};
		// case 2:
		// return new float[] {1, 0.5f, 0};
		// case 3:
		// return new float[] {0, 0f, 1f};
		// }

		return new float[] { 1, 1, 1 };
	}

	public static float[] createTexCube(float x, float y, Block block) {
		float offset = (1024f / 16) / 1024f;

		switch (block.GetID()) {
		default:
			return new float[] {
					// Bottom Quadrant
					x + offset * 3, y + offset * 10, x + offset * 2, y + offset * 10, x + offset * 2, y + offset * 9,
					x + offset * 3, y + offset * 9,
					// Top Quadrant
					x + offset * 3, y + offset * 1, x + offset * 2, y + offset * 1, x + offset * 2, y + offset * 0,
					x + offset * 3, y + offset * 0,
					// Front Quadrant
					x + offset * 3, y + offset * 0, x + offset * 4, y + offset * 0, x + offset * 4, y + offset * 1,
					x + offset * 3, y + offset * 1,
					// Back Quadrant
					x + offset * 4, y + offset * 1, x + offset * 3, y + offset * 1, x + offset * 3, y + offset * 0,
					x + offset * 4, y + offset * 0,
					// Left Quadrant
					x + offset * 3, y + offset * 0, x + offset * 4, y + offset * 1, x + offset * 4, y + offset * 1,
					x + offset * 3, y + offset * 0,
					// Right Quadrant
					x + offset * 3, y + offset * 0, x + offset * 4, y + offset * 0, x + offset * 4, y + offset * 1,
					x + offset * 3, y + offset * 1 };
		}
	}
}
