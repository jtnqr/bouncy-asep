package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * The Obstacle class represents an obstacle in the game.
 * It includes two main parts: top and bottom rectangles, and filler rectangles in between.
 * Obstacles move from right to left across the screen.
 */
public class Obstacle {
    private static final float RESET_MARGIN = 15f;
    private static final float TILE_WIDTH = 24f;
    private final Rectangle topRect;
    private float speed;
    private final Rectangle bottomRect;
    private final Array<Rectangle> fillerRects;
    private final TextureRegion topTile;
    private final TextureRegion bottomTile;
    private final TextureRegion fillerTile;
    private final float initialX;
    private final float gap;
    private boolean passed;
    private final float tileSquared;
    private final int maxY;

    /**
     * Creates an obstacle with specified parameters.
     *
     * @param initialX    Initial X position of the obstacle
     * @param maxY        Maximum Y position for random placement
     * @param gap         Gap between top and bottom parts
     * @param speed       Speed at which the obstacle moves
     * @param tileSquared Size of the tile in the tileset
     */
    public Obstacle(float initialX, int maxY, float gap, float speed, float tileSquared) {
        this.speed = speed;
        this.initialX = tileSquared * initialX;
        this.gap = tileSquared * gap;
        this.tileSquared = tileSquared;
        this.maxY = maxY;

        topRect = new Rectangle(this.initialX, this.tileSquared * random(0, maxY), this.tileSquared, this.tileSquared);
        bottomRect = new Rectangle(this.initialX, topRect.y + this.gap, this.tileSquared, this.tileSquared);
        fillerRects = new Array<>();

        for (int i = 0; i < 12; i++) {
            if (i < (topRect.getY() / tileSquared) || i > (bottomRect.getY() / tileSquared)) {
                fillerRects.add(new Rectangle(tileSquared * initialX, tileSquared * i, tileSquared, tileSquared));
            }
        }

        Texture tilesetTexture = new Texture("woods_tileset.png");
        TextureRegion[][] tiles = TextureRegion.split(tilesetTexture, 24, 24);

        topTile = tiles[10][15];
        bottomTile = tiles[11][15];
        fillerTile = tiles[5][15];
    }

    /**
     * Updates the position of the obstacle based on the delta time.
     *
     * @param deltaTime Time since the last frame
     */
    public void update(float deltaTime) {
        float screenWidth = Gdx.graphics.getWidth();
        float obstacleWidth = topRect.width;
        float totalObstaclesWidth = obstacleWidth * 5; // Total width of 5 obstacles
        float totalMargin = RESET_MARGIN * 5; // Total margin for 5 obstacles
        float additionalGap = TILE_WIDTH * 5; // Additional gap of one tile width for each obstacle
        float resetPositionX = screenWidth + totalObstaclesWidth + totalMargin + additionalGap; // Reset position for 5 obstacles

        if (topRect.x + obstacleWidth < 0) { // Check if the obstacle has moved completely off-screen
            setX(resetPositionX);
            setRandomY(0, maxY);
            passed = false;

            // Reset filler rectangles positions
            fillerRects.clear();
            for (int i = 0; i < 12; i++) {
                if (i < (topRect.getY() / tileSquared) || i > (bottomRect.getY() / tileSquared)) {
                    fillerRects.add(new Rectangle(topRect.x, tileSquared * i, tileSquared, tileSquared));
                }
            }
        }

        topRect.x -= speed * deltaTime;
        bottomRect.x -= speed * deltaTime;
        for (Rectangle rect : fillerRects) {
            rect.x -= speed * deltaTime;
        }
    }

    /**
     * Sets the X position of the obstacle and adjusts all parts accordingly.
     *
     * @param x New X position
     */
    public void setX(float x) {
        float offsetX = x - topRect.x;
        topRect.x = x;
        bottomRect.x = x;

        for (Rectangle rect : fillerRects) {
            rect.x += offsetX;
        }
    }

    /**
     * Sets the Y position of the obstacle and adjusts all parts accordingly.
     *
     * @param y New Y position
     */
    public void setY(float y) {
        topRect.y = y;
        bottomRect.y = y + gap;

        fillerRects.clear();
        for (int i = 0; i < 12; i++) {
            if (i < (topRect.getY() / tileSquared) || i > (bottomRect.getY() / tileSquared)) {
                fillerRects.add(new Rectangle(topRect.x, tileSquared * i, tileSquared, tileSquared));
            }
        }
    }

    /**
     * Sets a random Y position within a specified range.
     *
     * @param min Minimum Y position
     * @param max Maximum Y position
     */
    public void setRandomY(int min, int max) {
        setY(tileSquared * random(min, max));
    }

    /**
     * Gets the top rectangle of the obstacle.
     *
     * @return The top rectangle
     */
    public Rectangle getTopRect() {
        return topRect;
    }

    /**
     * Gets the bottom rectangle of the obstacle.
     *
     * @return The bottom rectangle
     */
    public Rectangle getBottomRect() {
        return bottomRect;
    }

    /**
     * Gets the filler rectangles between the top and bottom parts of the obstacle.
     *
     * @return An array of filler rectangles
     */
    public Array<Rectangle> getFillerRects() {
        return fillerRects;
    }

    /**
     * Gets the texture region for the top part of the obstacle.
     *
     * @return The texture region for the top part
     */
    public TextureRegion getTopTile() {
        return topTile;
    }

    /**
     * Gets the texture region for the bottom part of the obstacle.
     *
     * @return The texture region for the bottom part
     */
    public TextureRegion getBottomTile() {
        return bottomTile;
    }

    /**
     * Gets the texture region for the filler parts of the obstacle.
     *
     * @return The texture region for the filler parts
     */
    public TextureRegion getFillerTile() {
        return fillerTile;
    }

    /**
     * Gets the current speed of the obstacle.
     *
     * @return The speed of the obstacle
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Sets the speed of the obstacle.
     *
     * @param speed New speed of the obstacle
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Resets the obstacle to its initial position and randomizes the Y position.
     */
    public void reset() {
        setX(initialX);
        setRandomY(0, maxY);
        passed = false;
    }

    /**
     * Checks for collision with the specified entity.
     *
     * @param entity The entity to check collision against
     * @return True if there is a collision, false otherwise
     */
    public boolean checkCollision(Rectangle entity) {
        if (entity.overlaps(topRect) || entity.overlaps(bottomRect)) {
            return true;
        }

        for (Rectangle rect : fillerRects) {
            if (entity.overlaps(rect)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the obstacle has been passed by the player.
     *
     * @return True if the obstacle has been passed, false otherwise
     */
    public boolean isPassed() {
        return passed;
    }

    /**
     * Sets the passed status of the obstacle.
     *
     * @param passed True if the obstacle has been passed, false otherwise
     */
    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
