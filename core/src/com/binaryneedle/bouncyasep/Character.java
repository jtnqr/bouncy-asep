package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * The Character class represents an animated character in the game.
 * It handles the creation, drawing, updating, and disposal of the character's animation.
 */
public class Character implements Disposable {

    // Animation frame configurations for each state
    private static final AnimationConfig[] ANIMATION_CONFIGS = {
            new AnimationConfig(0, 0, 1, true),   // STANDING
            new AnimationConfig(3, 6, 2, true),   // JUMPING
            new AnimationConfig(4, 1, 4, true),   // FALLING
            new AnimationConfig(6, 0, 12, false)  // DEAD (spans multiple rows)
    };
    // Core components
    private final Texture texture;
    private final TextureRegion[][] spriteSheet;
    private final Animation<TextureRegion>[] animations;
    // Frame properties
    private final int frameWidth;
    private final int frameHeight;
    private final float frameDuration;
    private TextureRegion currentFrame;
    // Animation properties
    private float elapsedTime = 0f;
    private State currentState = State.STANDING;
    private boolean isAnimationFinished = false;
    // Display properties
    private float scale = 1.0f;
    /**
     * Creates a new Character with the specified texture and animation properties.
     *
     * @param texturePath   Path to the character sprite sheet
     * @param frameWidth    Width of each frame in pixels
     * @param frameHeight   Height of each frame in pixels
     * @param frameDuration Duration of each frame in seconds
     */
    public Character(String texturePath, int frameWidth, int frameHeight, float frameDuration) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frameDuration = frameDuration;

        // Load texture and create sprite sheet
        this.texture = new Texture(texturePath);
        this.spriteSheet = TextureRegion.split(texture, frameWidth, frameHeight);

        // Initialize animations
        this.animations = new Animation[State.values().length];
        initializeAnimations();

        // Set initial frame
        setState(State.STANDING);
    }

    /**
     * Convenience constructor with default values.
     */
    public Character(String texturePath) {
        this(texturePath, 56, 56, 0.16f);
    }

    /**
     * Initialize all animations based on configuration.
     */
    @SuppressWarnings("unchecked")
    private void initializeAnimations() {
        for (State state : State.values()) {
            animations[state.getValue()] = createAnimation(state);
        }
    }

    /**
     * Creates animation for the specified state.
     */
    private Animation<TextureRegion> createAnimation(State state) {
        AnimationConfig config = ANIMATION_CONFIGS[state.getValue()];
        TextureRegion[] frames = new TextureRegion[config.frameCount];

        if (state == State.DEAD) {
            // Special handling for death animation (spans multiple rows)
            System.arraycopy(spriteSheet[6], 0, frames, 0, 8);
            System.arraycopy(spriteSheet[7], 0, frames, 8, 4);
        } else {
            // Standard single-row animation
            System.arraycopy(spriteSheet[config.startRow], config.startCol, frames, 0, config.frameCount);
        }

        return new Animation<>(frameDuration, frames);
    }

    /**
     * Sets the character's state and updates animation accordingly.
     */
    public void setState(State newState) {
        if (currentState != newState) {
            currentState = newState;
            elapsedTime = 0f;
            isAnimationFinished = false;
            updateCurrentFrame();
        }
    }

    /**
     * Updates the current frame based on elapsed time and state.
     */
    private void updateCurrentFrame() {
        Animation<TextureRegion> currentAnimation = animations[currentState.getValue()];
        AnimationConfig config = ANIMATION_CONFIGS[currentState.getValue()];

        if (config.loop) {
            currentFrame = currentAnimation.getKeyFrame(elapsedTime, true);
        } else {
            currentFrame = currentAnimation.getKeyFrame(elapsedTime, false);
            if (currentAnimation.isAnimationFinished(elapsedTime)) {
                isAnimationFinished = true;
            }
        }
    }

    /**
     * Updates the animation by advancing the elapsed time.
     *
     * @param deltaTime      The time since the last frame
     * @param entityVelocity The vertical velocity of the entity
     */
    public void update(float deltaTime, float entityVelocity) {
        if (!isAnimationFinished) {
            elapsedTime += deltaTime;
            updateCurrentFrame();
        }

        // Auto-transition from jumping to falling when velocity becomes negative
        if (currentState == State.JUMPING && entityVelocity < 0) {
            setState(State.FALLING);
        }
    }

    /**
     * Draws the current frame of the animation at the specified position.
     *
     * @param batch The SpriteBatch used for drawing
     * @param x     The X coordinate
     * @param y     The Y coordinate
     */
    public void draw(SpriteBatch batch, float x, float y) {
        draw(batch, x, y, getScaledWidth(), getScaledHeight());
    }

    /**
     * Draws the current frame of the animation at the specified position and size.
     *
     * @param batch  The SpriteBatch used for drawing
     * @param x      The X coordinate
     * @param y      The Y coordinate
     * @param width  The width of the frame
     * @param height The height of the frame
     */
    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        if (currentFrame != null) {
            batch.draw(currentFrame, x, y, width, height);
        }
    }

    // Convenience methods for state transitions
    public void startJump() {
        setState(State.JUMPING);
    }

    public void startStand() {
        setState(State.STANDING);
    }

    public void startFall() {
        setState(State.FALLING);
    }

    public void startDie() {
        setState(State.DEAD);
    }

    // Getters
    public State getCurrentState() {
        return currentState;
    }

    public boolean isAnimationFinished() {
        return isAnimationFinished;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public float getScale() {
        return scale;
    }

    /**
     * Sets the scale factor for the sprite rendering.
     * A scale of 2.0f will make the sprite twice as large.
     *
     * @param scale The scale factor (1.0f = normal size)
     */
    public void setScale(float scale) {
        this.scale = Math.max(0.1f, scale); // Prevent negative or zero scale
    }

    public float getScaledWidth() {
        return frameWidth * scale;
    }

    public float getScaledHeight() {
        return frameHeight * scale;
    }

    // Deprecated methods for backward compatibility
    @Deprecated
    public int getWidth() {
        return getFrameWidth();
    }

    @Deprecated
    public int getHeight() {
        return getFrameHeight();
    }

    /**
     * Disposes of the texture when it's no longer needed.
     */
    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    // Animation states
    public enum State {
        STANDING(0), JUMPING(1), FALLING(2), DEAD(3);

        private final int value;

        State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Animation configurations
    private static final class AnimationConfig {
        final int startRow, startCol, frameCount;
        final boolean loop;

        AnimationConfig(int startRow, int startCol, int frameCount, boolean loop) {
            this.startRow = startRow;
            this.startCol = startCol;
            this.frameCount = frameCount;
            this.loop = loop;
        }
    }
}