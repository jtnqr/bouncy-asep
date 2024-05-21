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
    int numFrames = 3;
    float frameDuration = 0.2f;

    /**
     * Initializes the character by setting up the sprite batch and animation frames.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        texture = new Texture(Gdx.files.internal("sprites/char_blue_1.png"));
        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] animationFrames = new TextureRegion[numFrames];
        for (int i = 0; i < numFrames; i++) {
            if (i != 3) {
                animationFrames[i] = tmp[3][i];
            } else {
                animationFrames[i] = tmp[4][0];
            }
        }
        animation = new Animation<>(frameDuration, animationFrames);
        currentFrame = animation.getKeyFrame(elapsedTime);
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
    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        currentFrame = animation.getKeyFrame(elapsedTime, true);
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
