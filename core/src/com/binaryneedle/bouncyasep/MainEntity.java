package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.math.Rectangle;

/**
 * The MainEntity class represents the main character in the game.
 * It extends the Rectangle class and includes properties for gravity and velocity,
 * allowing the entity to jump and be affected by gravity.
 */
public class MainEntity extends Rectangle {
    private float gravity = 1000.0f;
    private float velocity = 0;
    private final float power = 500;
    private final float upperBound = 768 - 20 - 64;
    private final float lowerBound = 20;

    /**
     * Constructs a new MainEntity with the specified initial position and size.
     *
     * @param initialX Initial X position
     * @param initialY Initial Y position
     * @param width    Width of the entity
     * @param height   Height of the entity
     */
    public MainEntity(float initialX, float initialY, float width, float height) {
        super(initialX, initialY, width, height);
    }

    /**
     * Gets the gravity affecting the entity.
     *
     * @return The gravity
     */
    public float getGravity() {
        return gravity;
    }

    /**
     * Sets the gravity affecting the entity.
     *
     * @param gravity The new gravity value
     */
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    /**
     * Gets the current velocity of the entity.
     *
     * @return The velocity
     */
    public float getVelocity() {
        return velocity;
    }

    /**
     * Sets the current velocity of the entity.
     *
     * @param velocity The new velocity value
     */
    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    /**
     * Causes the entity to jump by setting its velocity to a predefined power value.
     */
    public void jump() {
        velocity = power;
    }

    /**
     * Updates the entity's position based on the delta time and its current velocity.
     * The entity's position is adjusted by gravity and bounded within the upper and lower limits.
     *
     * @param deltaTime Time since the last frame
     */
    public void update(float deltaTime) {
        velocity -= gravity * deltaTime;
        float newY = y + velocity * deltaTime;

        if (newY < upperBound && newY > lowerBound) {
            y = newY;
        } else {
            velocity = 0;
        }
    }
}
