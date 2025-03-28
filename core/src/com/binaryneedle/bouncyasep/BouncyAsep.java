package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class BouncyAsep extends ApplicationAdapter {
    private final int tileSquared = 64;
    private Background layer1, layer2, layer3;
    private Sound jumpSound, crashSound, passSound;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont infoFont, debugFont, titleFont;
    private MainEntity entity;
    private boolean isRunning, isColliding, isDebugEnabled, collision = true, collisionHandled;
    private List<Obstacle> obstacles;
    private Character sprite;
    private int score;

    /**
     * Initializes the game assets, fonts, camera, layers, sprite, entity, obstacles, and other game variables.
     */
    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        batch = new SpriteBatch();

        layer1 = new Background("bg/background_layer_1.png", 0.1f);
        layer2 = new Background("bg/background_layer_2.png", 125f);
        layer3 = new Background("bg/background_layer_3.png", 250f);

        jumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.wav"));
        crashSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hurt.wav"));
        passSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pass.wav"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        sprite = new Character();
        sprite.create();

        entity = new MainEntity(
                1024 / 2f - 64 / 2f,
                768 / 2f,
                sprite.getWidth(),
                sprite.getHeight()
        );

        debugFont = new BitmapFont();
        infoFont = new BitmapFont();
        infoFont.setColor(Color.WHITE);
        infoFont.getData().setScale(3.0f);
        titleFont = new BitmapFont();
        titleFont.getData().setScale(4.0f);
        obstacles = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            obstacles.add(new Obstacle(16f + (5f * i), 7, 5f, 250f, tileSquared));
        }

        score = 0;
    }

    /**
     * Clears the screen and renders the game elements such as backgrounds, obstacles, the main character, score, and text.
     * Also updates the game logic.
     */
    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        layer1.draw(batch, Gdx.graphics.getHeight());
        layer2.draw(batch, Gdx.graphics.getHeight());
        layer3.draw(batch, Gdx.graphics.getHeight());

        drawObstacles();
        sprite.draw(batch, entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());

        drawScore();
        drawInfoText();
        drawDebugText();
        drawTitleText();

        batch.end();

        engineRun();
    }

    /**
     * Draws the obstacles on the screen and updates their position if the game is running.
     */
    private void drawObstacles() {
        for (Obstacle obs : obstacles) {
            float obstacleX = obs.getTopRect().getX();

            for (Rectangle fillerRect : obs.getFillerRects()) {
                batch.draw(
                        obs.getFillerTile(),
                        fillerRect.x,
                        fillerRect.y,
                        fillerRect.width,
                        fillerRect.height
                );
            }

            batch.draw(
                    obs.getTopTile(),
                    obstacleX,
                    obs.getTopRect().getY(),
                    obs.getTopRect().getWidth(),
                    obs.getTopRect().getHeight()
            );
            batch.draw(
                    obs.getBottomTile(),
                    obstacleX,
                    obs.getBottomRect().getY(),
                    obs.getBottomRect().getWidth(),
                    obs.getBottomRect().getHeight()
            );

            if (isRunning) obs.update(Gdx.graphics.getDeltaTime());
        }
    }

    /**
     * Draws the player's score on the screen.
     */
    private void drawScore() {
        infoFont.draw(batch, String.valueOf(score), 1024 / 2f - 10, 768 - 50);
    }

    /**
     * Draws the title text when the game is not running and there are no collisions.
     */
    private void drawTitleText() {
        if (!isRunning && !isColliding) {
            String titleText = "Bouncy Asep: The Game";
            titleFont.draw(batch, titleText, 1024 / 6f + 20, 768 - 130);
        }
    }

    /**
     * Draws instructional text when the game is not running.
     */
    private void drawInfoText() {
        if (!isRunning) {
            String menuText = isColliding
                    ? "GAME OVER, press R to reset"
                    : "Press SPACE or LMB to play";
            infoFont.draw(batch, menuText, 1024 / 4f - 30, 768 / 5f);
        }
    }

    /**
     * Draws debug information if debug mode is enabled.
     */
    private void drawDebugText() {
        if (isDebugEnabled) {
            String debugText = "FPS: " + Gdx.graphics.getFramesPerSecond() +
                    "\nsprite_y: " + Math.round(entity.getY()) +
                    "\ngravity: " + entity.getGravity() +
                    "\nsprite_collision: " + collision +
                    "\nsprite_velocity: " + entity.getVelocity() +
                    "\nobstacle_speed: " + obstacles.getFirst().getSpeed();
            debugFont.draw(batch, debugText, 10, 768 - 10);
        }
    }

    /**
     * Handles the main game logic, updating the game state, checking for collisions, and handling user input.
     */
    public void engineRun() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        handleInput();

        if (isRunning) {
            entity.update(deltaTime);
            layer1.update(deltaTime);
            layer2.update(deltaTime);
            layer3.update(deltaTime);
        }
        if (isColliding && !collisionHandled) {
            sprite.startDie();
            crashSound.play();
            collisionHandled = true;
        }
        sprite.update(deltaTime, entity.getVelocity());

        checkObstaclePass();
    }

    /**
     * Handles user input for jumping, restarting the game, toggling debug mode, and exiting the game.
     */
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) resetGame();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
        if (Gdx.input.isKeyJustPressed(Input.Keys.F12)) isDebugEnabled = !isDebugEnabled;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            sprite.startJump();
            collision = !collision;
            collisionHandled = false;
        }

        if ((Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) ||
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) && !isColliding) {
            sprite.startJump();
            jumpSound.play();
            entity.jump();
        }

        if (entity.getVelocity() != 0) isRunning = true;
        if (collision) {
            for (Obstacle obs : obstacles) {
                if (obs.checkCollision(entity)) {
                    isRunning = false;
                    isColliding = true;
                }
            }
        } else {
            isColliding = false;
        }
    }

    /**
     * Checks if the player has passed an obstacle and updates the score.
     */
    private void checkObstaclePass() {
        for (Obstacle obs : obstacles) {
            if (!obs.isPassed() && entity.getX() > obs.getTopRect().getX() + obs.getTopRect().getWidth()) {
                obs.setPassed(true);
                passSound.play();
                score++;
            }

//            TODO: dynamic difficulty scaling
//            if (score % 2 == 0) {
//                if ()
//            }
        }
    }

    /**
     * Resets the game to its initial state, including the position of the main character and obstacles.
     */
    private void resetGame() {
        sprite.startStand();
        isColliding = false;
        isRunning = false;
        collisionHandled = false;
        entity.setVelocity(0);
        entity.setY(768 / 2f);

        for (Obstacle obs : obstacles) obs.reset();
        layer1.reset();
        layer2.reset();
        layer3.reset();

        score = 0;
    }

    /**
     * Disposes of assets when the game is closed to free up resources.
     */
    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        infoFont.dispose();
        debugFont.dispose();
        layer1.dispose();
        layer2.dispose();
        layer3.dispose();
        jumpSound.dispose();
        crashSound.dispose();
        passSound.dispose();
        sprite.dispose();
    }
}
