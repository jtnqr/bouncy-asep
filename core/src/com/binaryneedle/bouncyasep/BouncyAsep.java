package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class BouncyAsep extends ApplicationAdapter {
    Texture bucketImage;
    Texture rainImage;
    Background layer1, layer2, layer3;
    Sound jumpSound;
    SpriteBatch batch;
    OrthographicCamera camera;
    BitmapFont font;
    MainEntity entity;
    boolean isRunning, isColliding;
    private Texture tilesetTexture;
    private TextureRegion topTile, bottomTile, fillerTile;
    private final int tileSquared = 64;
    List<Obstacle> obstacles;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        batch = new SpriteBatch();

        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        rainImage = new Texture(Gdx.files.internal("drop.png"));
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump.mp3"));
        layer1 = new Background("bg/background_layer_1.png", 0.1f);
        layer2 = new Background("bg/background_layer_2.png", 125f);
        layer3 = new Background("bg/background_layer_3.png", 250f);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        entity = new MainEntity(
                1024 / 2f - 64 / 2f,
                768 / 2f,
                tileSquared,
                tileSquared
        );

        font = new BitmapFont();
        obstacles = new ArrayList<Obstacle>();

        for (int i = 0; i < 4; i++) {
            obstacles.add(new Obstacle(16f + (4f * i), 7, 5f, 250f, tileSquared));
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        layer1.draw(batch, Gdx.graphics.getHeight());
        layer2.draw(batch, Gdx.graphics.getHeight());
        layer3.draw(batch, Gdx.graphics.getHeight());

        for (Obstacle obs : obstacles) {
            float obstacleX = obs.getTopPipe().getX();
            float topY = obs.getTopPipe().getY() / tileSquared;
            float botY = obs.getBottomPipe().getY() / tileSquared;

            for (int i = 0; i < 12; i++) {
                if ((i >= topY && i - 1 < botY)) {
                    continue;
                }

                batch.draw(
                        obs.getFillerTile(),
                        obstacleX,
                        obs.getTileSquared() * i,
                        obs.getTopPipe().getWidth(),
                        obs.getTopPipe().getHeight()
                );
            }

            batch.draw(
                    obs.getTopTile(),
                    obstacleX,
                    obs.getTopPipe().getY(),
                    obs.getTopPipe().getWidth(),
                    obs.getTopPipe().getHeight()
            );
            batch.draw(
                    obs.getBottomTile(),
                    obstacleX,
                    obs.getBottomPipe().getY(),
                    obs.getBottomPipe().getWidth(),
                    obs.getBottomPipe().getHeight()
            );

            if (isRunning) obs.update(Gdx.graphics.getDeltaTime());
        }

        font.setColor(Color.WHITE);
        String debugText = "Y: " + Math.round(entity.getY()) +
                "\nVelocity: " + entity.getVelocity() +
                "\nGravity: " + entity.getGravity();

        font.draw(batch, debugText, 10, 768 - 10);

        batch.draw(bucketImage, entity.getX(), entity.getY());


        batch.end();
        engineRun();
    }

    public void engineRun() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            isRunning = false;
            entity.setVelocity(0);
            entity.setY(768 / 2f);

            for (Obstacle obs : obstacles) {
                obs.reset();
            }
            layer1.reset();
            layer2.reset();
            layer3.reset();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) Gdx.app.exit();
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) &&
                !isColliding) {
//            jumpSound.play();
            entity.jump();
        }

        if (entity.getVelocity() != 0) isRunning = true;

        for (Obstacle obs : obstacles) {
            if (Util.isColliding(entity.getX(), entity.getY(), obs.getTopPipe().getX(), obs.getTopPipe().getY())) {
                isRunning = false;
                isColliding = true;
            }
            if (Util.isColliding(entity.getX(), entity.getY(), obs.getBottomPipe().getX(), obs.getBottomPipe().getY())) {
                isRunning = false;
                isColliding = true;
            }
        }
        if (isRunning) {
            entity.update(Gdx.graphics.getDeltaTime());
            layer1.update(Gdx.graphics.getDeltaTime());
            layer2.update(Gdx.graphics.getDeltaTime());
            layer3.update(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        rainImage.dispose();
        bucketImage.dispose();
        jumpSound.dispose();
        layer1.dispose();
        layer2.dispose();
        layer3.dispose();
    }
}
