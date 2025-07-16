package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * The Obstacle class represents an obstacle in the game.
 * It includes two main parts: top and bottom rectangles, and filler rectangles in between.
 * Obstacles move from right to left across the screen.
 * <p>
 * Improved for better performance, memory efficiency, and code organization.
 */
public class Obstacle {
    // Constants - now properly organized and documented
    private static final float RESET_MARGIN = 15f;
    private static final float TILE_WIDTH = 24f;
    private static final int GRID_HEIGHT = 12; // Number of tiles vertically
    private static final int OBSTACLE_COUNT = 5; // Number of obstacles in the game

    // Tileset coordinates for better maintainability
    private static final int TOP_TILE_X = 15, TOP_TILE_Y = 10;
    private static final int BOTTOM_TILE_X = 15, BOTTOM_TILE_Y = 11;
    private static final int FILLER_TILE_X = 15, FILLER_TILE_Y = 5;
    // Object pooling for rectangles to reduce garbage collection
    private static final Pool<Rectangle> RECTANGLE_POOL = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    // Static resources shared across all obstacles for memory efficiency
    private static TextureRegion topTile;
    private static TextureRegion bottomTile;
    private static TextureRegion fillerTile;
    private static boolean texturesLoaded = false;
    // Reference to camera for getting world width
    private static OrthographicCamera gameCamera;
    // Core obstacle properties
    private final Rectangle topRect;
    private final Rectangle bottomRect;
    private final Array<Rectangle> fillerRects;
    private final float initialX;
    private final float gap;
    private final float tileSize;
    private final int maxY;
    // Dynamic properties
    private float speed;
    private boolean passed;
    // Cached values for performance - using viewport world coordinates
    private float currentWorldWidth;
    private float resetPositionX;
    private boolean cacheValid = false;

    /**
     * Creates an obstacle with specified parameters.
     * Constructor signature matches the main class usage pattern.
     *
     * @param initialX      Initial X position of the obstacle (in tile units)
     * @param obstacleWidth Width of the obstacle (used for maxY calculation)
     * @param spacing       Spacing between obstacles
     * @param gap           Gap between top and bottom parts (in pixels)
     * @param tileSize      Size of each tile in pixels
     */
    public Obstacle(float initialX, float obstacleWidth, float spacing, float gap, float tileSize) {
        // Initialize static textures if not already loaded
        initializeTextures();

        // Store configuration
        this.speed = 250f; // Default speed, will be set by main class if needed
        this.initialX = tileSize * initialX;
        this.gap = gap; // Gap is already in pixels from main class
        this.tileSize = tileSize;
        this.maxY = (int) obstacleWidth; // Convert obstacleWidth to maxY as expected
        this.passed = false;

        // Initialize rectangles
        this.topRect = new Rectangle();
        this.bottomRect = new Rectangle();
        this.fillerRects = new Array<>(GRID_HEIGHT);

        // Set initial position
        reset();
    }

    /**
     * Sets the camera reference for all obstacles to get proper world coordinates.
     * This should be called once from your main class after camera initialization.
     *
     * @param camera The OrthographicCamera used by the game
     */
    public static void setCamera(OrthographicCamera camera) {
        gameCamera = camera;
    }

    /**
     * Initialize static texture resources (called once for all obstacles).
     */
    private static void initializeTextures() {
        if (texturesLoaded) return;

        try {
            Texture tilesetTexture = new Texture("woods_tileset.png");
            TextureRegion[][] tiles = TextureRegion.split(tilesetTexture, 24, 24);

            topTile = tiles[TOP_TILE_Y][TOP_TILE_X];
            bottomTile = tiles[BOTTOM_TILE_Y][BOTTOM_TILE_X];
            fillerTile = tiles[FILLER_TILE_Y][FILLER_TILE_X];

            texturesLoaded = true;
        } catch (Exception e) {
            Gdx.app.error("Obstacle", "Failed to load tileset texture", e);
            throw new RuntimeException("Could not initialize obstacle textures", e);
        }
    }

    /**
     * Static cleanup method for the entire obstacle system.
     * Call this when shutting down the game.
     */
    public static void disposeStatic() {
        RECTANGLE_POOL.clear();
        texturesLoaded = false;
        // Note: Texture disposal should be handled by the texture manager
    }

    /**
     * Updates the position of the obstacle based on the delta time.
     * Uses viewport world coordinates for responsive behavior.
     *
     * @param deltaTime Time since the last frame
     */
    public void update(float deltaTime) {
        // Update cached values if needed
        updateCache();

        // Move obstacle left
        float movement = speed * deltaTime;
        moveHorizontally(-movement);

        // Check if obstacle needs to be reset (using world coordinates)
        if (topRect.x + topRect.width < 0) {
            resetToRightSide();
        }
    }

    /**
     * Updates cached calculation values for performance optimization.
     * Now uses camera/viewport world coordinates for responsive behavior.
     */
    private void updateCache() {
        float newWorldWidth = getWorldWidth();

        if (!cacheValid || Math.abs(currentWorldWidth - newWorldWidth) > 0.1f) {
            currentWorldWidth = newWorldWidth;

            // Calculate reset position using current world width
            // This makes obstacles responsive to viewport changes
            float totalObstaclesWidth = tileSize * OBSTACLE_COUNT;
            float totalMargin = RESET_MARGIN * OBSTACLE_COUNT;
            float additionalGap = TILE_WIDTH * OBSTACLE_COUNT;
            resetPositionX = currentWorldWidth + totalObstaclesWidth + totalMargin + additionalGap;

            cacheValid = true;
        }
    }

    /**
     * Gets the current world width from the camera/viewport.
     * Falls back to screen width conversion if camera is not set.
     *
     * @return Current world width in world coordinates
     */
    private float getWorldWidth() {
        if (gameCamera != null) {
            // Get world width from camera's viewport
            return gameCamera.viewportWidth;
        } else {
            // Fallback: assume world coordinates match screen coordinates
            // This should not happen if setCamera() is called properly
            Gdx.app.log("Obstacle", "Warning: Camera not set, using screen width as fallback");
            return Gdx.graphics.getWidth();
        }
    }

    /**
     * Moves the obstacle horizontally by the specified amount.
     *
     * @param deltaX Amount to move horizontally
     */
    private void moveHorizontally(float deltaX) {
        topRect.x += deltaX;
        bottomRect.x += deltaX;

        for (Rectangle rect : fillerRects) {
            rect.x += deltaX;
        }
    }

    /**
     * Resets obstacle to the right side of the screen with random Y position.
     */
    private void resetToRightSide() {
        setX(resetPositionX);
        setRandomY(0, maxY);
        passed = false;
    }

    /**
     * Rebuilds filler rectangles efficiently using object pooling.
     */
    private void rebuildFillerRects() {
        // Return existing rectangles to pool
        for (Rectangle rect : fillerRects) {
            RECTANGLE_POOL.free(rect);
        }
        fillerRects.clear();

        // Calculate Y bounds in tile coordinates
        int topTileY = (int) (topRect.y / tileSize);
        int bottomTileY = (int) (bottomRect.y / tileSize);

        // Create filler rectangles above and below the gap
        for (int i = 0; i < GRID_HEIGHT; i++) {
            if (i < topTileY || i > bottomTileY) {
                Rectangle rect = RECTANGLE_POOL.obtain();
                rect.set(topRect.x, tileSize * i, tileSize, tileSize);
                fillerRects.add(rect);
            }
        }
    }

    /**
     * Sets a random Y position within a specified range.
     * Uses proper locale for consistent behavior across different systems.
     *
     * @param min Minimum Y position (in tile units)
     * @param max Maximum Y position (in tile units)
     */
    public void setRandomY(int min, int max) {
        setY(tileSize * random(min, max));
    }

    /**
     * Resets the obstacle to its initial position and randomizes the Y position.
     * More efficient implementation using world coordinates.
     */
    public void reset() {
        setX(initialX);
        setRandomY(0, maxY);
        passed = false;
        cacheValid = false; // Force cache update
    }

    /**
     * Optimized collision detection with early exit conditions.
     *
     * @param entity The entity to check collision against
     * @return True if there is a collision, false otherwise
     */
    public boolean checkCollision(Rectangle entity) {
        // Early exit if entity is clearly not near obstacle
        if (entity.x + entity.width < topRect.x || entity.x > topRect.x + topRect.width) {
            return false;
        }

        // Check main obstacle parts first (most likely collision points)
        if (entity.overlaps(topRect) || entity.overlaps(bottomRect)) {
            return true;
        }

        // Check filler rectangles
        for (Rectangle rect : fillerRects) {
            if (entity.overlaps(rect)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Alternative collision check method that accepts MainEntity directly.
     * Provides better type safety and potential for future optimizations.
     *
     * @param entity The MainEntity to check collision against
     * @return True if there is a collision, false otherwise
     */
    public boolean checkCollision(MainEntity entity) {
        return checkCollision(new Rectangle(entity.getX(), entity.getY(),
                entity.getWidth(), entity.getHeight()));
    }

    /**
     * Gets the top rectangle of the obstacle.
     *
     * @return The top rectangle (do not modify directly)
     */
    public Rectangle getTopRect() {
        return topRect;
    }

    // Getters - optimized and documented

    /**
     * Gets the bottom rectangle of the obstacle.
     *
     * @return The bottom rectangle (do not modify directly)
     */
    public Rectangle getBottomRect() {
        return bottomRect;
    }

    /**
     * Gets the filler rectangles between the top and bottom parts.
     *
     * @return An array of filler rectangles (do not modify directly)
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
     * @return The speed of the obstacle in pixels per second
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Sets the speed of the obstacle.
     *
     * @param speed New speed of the obstacle in pixels per second
     */
    public void setSpeed(float speed) {
        this.speed = speed;
        cacheValid = false; // Invalidate cache when speed changes
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

    /**
     * Gets the current X position of the obstacle.
     *
     * @return The X position of the obstacle
     */
    public float getX() {
        return topRect.x;
    }

    /**
     * Sets the X position of the obstacle and adjusts all parts accordingly.
     * More efficient implementation that calculates offset once.
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
     * Gets the current Y position of the top part of the obstacle.
     *
     * @return The Y position of the top part
     */
    public float getY() {
        return topRect.y;
    }

    /**
     * Sets the Y position of the obstacle and adjusts all parts accordingly.
     * Optimized to rebuild filler rectangles more efficiently.
     *
     * @param y New Y position
     */
    public void setY(float y) {
        topRect.set(topRect.x, y, tileSize, tileSize);
        bottomRect.set(bottomRect.x, y + gap, tileSize, tileSize);

        rebuildFillerRects();
    }

    /**
     * Gets the width of the obstacle.
     *
     * @return The width of the obstacle
     */
    public float getWidth() {
        return tileSize;
    }

    /**
     * Gets the height of the gap between top and bottom parts.
     *
     * @return The gap height
     */
    public float getGap() {
        return gap;
    }

    /**
     * Cleanup method to return pooled objects when obstacle is no longer needed.
     * Call this when removing obstacles to prevent memory leaks.
     */
    public void dispose() {
        for (Rectangle rect : fillerRects) {
            RECTANGLE_POOL.free(rect);
        }
        fillerRects.clear();
    }
}