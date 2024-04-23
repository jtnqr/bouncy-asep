package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        batch.draw(currentFrame, x, y, width, height);
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        currentFrame = animation.getKeyFrame(elapsedTime, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
    }
}
