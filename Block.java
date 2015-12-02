/***************************************************************
* file: Block.java 
* author: Jorge Magana, Jonathan Wong, Michael Ng 
* class: CS 445 – Computer Graphics
* 
* assignment: Quarter Project - Final Checkpoint
* date last modified: 12/1/2015
* 
* purpose: This class is used to represent a block in the 3d space we created.
* It holds an id, coordinates, and type properties for a block.
* NOTE: This class is not used in the main program yet, but is in preparation
* for checkpoint 2.
****************************************************************/
public class Block {

    private boolean IsActive; // Whether the block is active in the 3D space.
    private BlockType Type;   // The type of block
    private float x, y, z;    // The coordinates of the block
    
    /**
     * Enum type used to store block type as well as Block ID.
     */
    public enum BlockType {

        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5),
        BlockType_Default(-1);
        private int BlockID;
        
        // Instantiate Block ID
        BlockType(int i) {
            BlockID = i;
        }
        // Retrieve block ID
        public int GetID() {
            return BlockID;
        }
        // Set a custom block ID
        public void SetID(int i) {
            BlockID = i;
        }
    }
    
    /**
     * Instantiate Block with specified block type.
     * @param type Type of block.
     */
    public Block(BlockType type) {
        Type = type;
        IsActive = true;
    }
    
    /**
     * Set block location in 3D space through Coordinate parameters.
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @param z Z-Coordinate
     */
    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Checks if the block is active in the 3D space.
     * @return True if block is active, else false.
     */
    public boolean IsActive() {
        return IsActive;
    }
    
    /**
     * Sets whether the block is active or not in the 3D space.
     * @param active Boolean used to set whether the block is active or not.
     */
    public void SetActive(boolean active) {
        IsActive = active;
    }
    
    /**
     * Retrieves block ID.
     * @return Block ID
     */
    public int GetID() {
        return Type.GetID();
    }
}



