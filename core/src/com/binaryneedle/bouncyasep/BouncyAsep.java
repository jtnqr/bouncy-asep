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

        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        rainImage = new Texture(Gdx.files.internal("drop.png"));
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump.mp3"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        entity = new MainEntity(
                1024 / 2f - 64 / 2f,
                768 / 2f,
                tileSquared,
                tileSquared
        );

        font = new BitmapFont();
//        obs = new Obstacle(64 * 15, 64 * 3, tileSquared, 64 * 4, 250);
        obstacles = new ArrayList<Obstacle>();

        for (int i = 0; i < 4; i++) {
            obstacles.add(new Obstacle(15f + (4f * i), 7, 5f, 250f, tileSquared));
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

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
        if (isRunning) entity.update(Gdx.graphics.getDeltaTime());
    }

    private List<Obstacle> generateObstacles(int numObstacles, float minX, float maxX, float minY, float maxY) {
        // Implement logic to randomly generate obstacle positions and parameters
        // ...
        List<Obstacle> generatedObstacles = new ArrayList<Obstacle>();
        for (int i = 0; i < numObstacles; i++) {
            float randomX = minX + (float) Math.random() * (maxX - minX);
            float randomY = minY + (float) Math.random() * (maxY - minY);
            // ... generate other random parameters ...
//            generatedObstacles.add(new Obstacle(randomX, randomY, ...));
        }
        return generatedObstacles;
    }

//    private Obstacle generateObstacle() {
//
//    }

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
        rainImage.dispose();
        bucketImage.dispose();
        jumpSound.dispose();
    }
}
