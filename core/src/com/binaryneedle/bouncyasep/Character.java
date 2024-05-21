package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * The Character class represents an animated character in the game.
 * It extends the ApplicationAdapter class and handles the creation, drawing,
 * updating, and disposal of the character's animation.
 */
public class Character extends ApplicationAdapter {
    SpriteBatch batch;
    Texture texture;
    Animation<TextureRegion> animation;
    TextureRegion currentFrame;

    float elapsedTime = 0f;
    int frameWidth = 56;
    int frameHeight = 56;
    float frameDuration = 0.16f;
    int state = 0; // 0 = standing, 1 = jumping, 2 = falling, 3 = died
    private boolean isAnimationFinished = false;

    /**
     * Initializes the character by setting up the sprite batch and animation frames.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        texture = new Texture(Gdx.files.internal("sprites/char_blue_1.png"));
        stand(); // Set default standing animation
        currentFrame = animation.getKeyFrame(elapsedTime);
    }

    private void jump() {
        state = 1;
        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] animationFrames = new TextureRegion[2];

        // Set frames for jumping
        animationFrames[0] = tmp[3][6];
        animationFrames[1] = tmp[3][7];

        animation = new Animation<>(frameDuration, animationFrames);
    }

    private void fall() {
        state = 2;
        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] animationFrames = new TextureRegion[4];

        // Set frames for falling
        // Index 1 to 4
        System.arraycopy(tmp[4], 1, animationFrames, 0, 4);

        animation = new Animation<>(frameDuration, animationFrames);
    }

    private void stand() {
        state = 0;
        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] animationFrames = new TextureRegion[1];

        // Set default frame (standing)
        animationFrames[0] = tmp[0][0];

        animation = new Animation<>(frameDuration, animationFrames);
    }

    private void die() {
        state = 3;
        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] animationFrames = new TextureRegion[12];

        // Set frames for death
        // Index 0 to 7
        System.arraycopy(tmp[6], 0, animationFrames, 0, 8);
        // Index 0 to 3
        System.arraycopy(tmp[7], 0, animationFrames, 8, 4);

        animation = new Animation<>(frameDuration, animationFrames);
    }

    public void startJump() {
        isAnimationFinished = false;
        jump();
        // Additional logic to handle the jump animation
    }

    public void startStand() {
        stand();
    }

    public void startFall() {
        fall();
        // Additional logic to handle the fall animation
    }

    public void startDie() {
        die();
        // Additional logic to handle the death animation
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
        batch.draw(currentFrame, x, y, width, height);
    }

    /**
     * Updates the animation by advancing the elapsed time.
     *
     * @param deltaTime The time since the last frame
     */
    public void update(float deltaTime, float entityVelocity) {
        if (!isAnimationFinished) {
            elapsedTime += deltaTime;
            currentFrame = animation.getKeyFrame(elapsedTime, true);

            if (state == 3 && animation.getKeyFrameIndex(elapsedTime) == animation.getKeyFrames().length - 1) {
                isAnimationFinished = true;
            }
        }

        if (state == 1 && entityVelocity < 0) {
            startFall();
        }
    }

    /**
     * Gets the width of a single frame in the animation.
     *
     * @return The frame width
     */
    public int getWidth() {
        return this.frameWidth;
    }

    /**
     * Gets the height of a single frame in the animation.
     *
     * @return The frame height
     */
    public int getHeight() {
        return this.frameHeight;
    }

    /**
     * Disposes of the sprite batch and texture when they are no longer needed.
     */
    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
    }
}
