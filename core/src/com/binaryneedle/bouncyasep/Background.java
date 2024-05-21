package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * The Background class represents a scrolling background in the game.
 * It handles the creation, updating, drawing, resetting, and disposal of the background texture.
 */
public class Background {
    private final Texture texture;
    private final float y;
    private final float speed;
    private final float scale;
    private float x;

    /**
     * Constructs a new Background with the specified texture path and scrolling speed.
     *
     * @param texturePath The path to the background texture
     * @param speed       The speed at which the background scrolls
     */
    public Background(String texturePath, float speed) {
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.speed = speed;
        this.scale = (float) Gdx.graphics.getHeight() / texture.getHeight();

        this.x = 0;
        this.y = 0;
    }

    /**
     * Updates the background position based on the elapsed time and scrolling speed.
     *
     * @param deltaTime Time since the last frame
     */
    public void update(float deltaTime) {
        x -= speed * deltaTime;

        if (x <= -texture.getWidth() * scale) {
            x = 0;
        }
    }

    /**
     * Draws the background texture onto the screen.
     *
     * @param batch        The SpriteBatch used for drawing
     * @param screenHeight The height of the screen
     */
    public void draw(SpriteBatch batch, float screenHeight) {
        batch.draw(texture, x, y, texture.getWidth() * scale, screenHeight);
        batch.draw(texture, x + texture.getWidth() * scale, y, texture.getWidth() * scale, screenHeight);
    }

    /**
     * Resets the background position to its initial state.
     */
    public void reset() {
        x = 0;
    }

    /**
     * Disposes of the background texture when it's no longer needed.
     */
    public void dispose() {
        texture.dispose();
    }
}
