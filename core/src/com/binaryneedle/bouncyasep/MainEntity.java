package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.math.Rectangle;

public class MainEntity extends Rectangle {
    private float gravity = 1000.0f;
    private float velocity = 0;
    private final float power = 500;

    public MainEntity(float initialX, float initialY, float width, float height) {
        super(initialX, initialY, width, height);
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void jump() {
        velocity = power;
    }

    public void update(float deltaTime) {
        velocity -= gravity * deltaTime;

        float newY = y + velocity * deltaTime;

        if (newY < 768 - 20 - 64 && newY > 20) {
            y = newY;
        } else {
            velocity = 0;
        }
    }
}
