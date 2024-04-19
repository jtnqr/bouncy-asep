package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Background {
    private Texture texture;
    private float x, y, speed, scale;

    public Background(String texturePath, float speed) {
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.speed = speed;
        this.scale = (float) Gdx.graphics.getHeight() / texture.getHeight();

        this.x = 0;
        this.y = 0;
    }

    public void update(float deltaTime) {
        x -= speed * deltaTime;

        if (x <= -texture.getWidth() * scale) {
            x = 0;
        }
    }

    public void draw(SpriteBatch batch, float screenHeight) {
        batch.draw(texture, x, y, texture.getWidth() * scale, screenHeight);
        batch.draw(texture, x + texture.getWidth() * scale, y, texture.getWidth() * scale, screenHeight);
    }

    public void reset() {
        x = 0;
    }

    public void dispose() {
        texture.dispose();
    }
}
