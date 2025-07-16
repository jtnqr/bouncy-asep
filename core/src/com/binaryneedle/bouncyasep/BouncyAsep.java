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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public class BouncyAsep extends ApplicationAdapter {

    // Game constants
    private static final int WORLD_WIDTH = 1024;
    private static final int WORLD_HEIGHT = 768;
    private static final int TILE_SIZE = 64;
    private static final int INITIAL_OBSTACLE_COUNT = 5;
    private static final float OBSTACLE_SPACING = 5f;
    private static final float OBSTACLE_BASE_X = 16f;
    private static final float OBSTACLE_GAP = 250f;
    private static final float OBSTACLE_WIDTH = 7f;
    private static final float MAX_OBSTACLE_SPEED = 800f;

    // Core components
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    // Game objects
    private GameState currentState = GameState.MENU;
    private MainEntity entity;
    private Character sprite;
    private List<Obstacle> obstacles;
    // Background layers
    private Background layer1, layer2, layer3;
    // Audio
    private Sound jumpSound, crashSound, passSound;
    // UI
    private BitmapFont debugFont, infoFont, titleFont;
    private int score = 0;
    private boolean isDebugEnabled = false;
    // Game settings
    private boolean collisionEnabled = true;
    private boolean collisionHandled = false;

    /**
     * Initializes the game assets, fonts, camera, layers, sprite, entity, obstacles, and other game variables.
     */
    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        initializeGraphics();
        initializeAudio();
        initializeFonts();
        initializeGameObjects();
        initializeObstacles();

        resetGame();
    }

    /**
     * Initialize graphics components (camera, viewport, batch).
     */
    private void initializeGraphics() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        Obstacle.setCamera(camera);
    }

    /**
     * Initialize audio resources.
     */
    private void initializeAudio() {
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.wav"));
        crashSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hurt.wav"));
        passSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pass.wav"));
    }

    /**
     * Initialize fonts with proper scaling.
     */
    private void initializeFonts() {
        debugFont = new BitmapFont();
        debugFont.setColor(Color.WHITE);

        infoFont = new BitmapFont();
        infoFont.setColor(Color.WHITE);
        infoFont.getData().setScale(3.0f);

        titleFont = new BitmapFont();
        titleFont.setColor(Color.CYAN);
        titleFont.getData().setScale(4.0f);
    }

    /**
     * Initialize game objects (entity, sprite, background layers).
     */
    private void initializeGameObjects() {
        // Initialize background layers
        layer1 = new Background("bg/background_layer_1.png", 0.1f);
        layer2 = new Background("bg/background_layer_2.png", 125f);
        layer3 = new Background("bg/background_layer_3.png", 250f);

        // Initialize character sprite
        sprite = new Character("sprites/char_blue_1.png");

        // Initialize main entity
        entity = new MainEntity(
                WORLD_WIDTH / 2f - TILE_SIZE / 2f,
                WORLD_HEIGHT / 2f,
                sprite.getFrameWidth(),
                sprite.getFrameHeight()
        );
    }

    /**
     * Initialize obstacles with proper spacing.
     */
    private void initializeObstacles() {
        obstacles = new ArrayList<>();
        for (int i = 0; i < INITIAL_OBSTACLE_COUNT; i++) {
            float x = OBSTACLE_BASE_X + (OBSTACLE_SPACING * i);
            obstacles.add(new Obstacle(x, OBSTACLE_WIDTH, OBSTACLE_SPACING, OBSTACLE_GAP, TILE_SIZE));
        }
    }

    /**
     * Main render loop.
     */
    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Clear screen
        ScreenUtils.clear(0, 0, 0.2f, 1);

        // Update camera and viewport
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Handle input
        handleInput();

        // Update game logic
        update(deltaTime);

        // Render everything
        renderGame();
    }

    /**
     * Handle viewport resize.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
    }

    /**
     * Update game logic based on current state.
     */
    private void update(float deltaTime) {
        switch (currentState) {
            case MENU:
                // Menu state - only update sprite animation
                sprite.update(deltaTime, entity.getVelocity());
                break;

            case PLAYING:
                updatePlaying(deltaTime);
                break;

            case GAME_OVER:
                updateGameOver(deltaTime);
                break;
        }
    }

    /**
     * Update logic for playing state.
     */
    private void updatePlaying(float deltaTime) {
        // Update game objects
        entity.update(deltaTime);
        sprite.update(deltaTime, entity.getVelocity());

        // Update background layers
        layer1.update(deltaTime);
        layer2.update(deltaTime);
        layer3.update(deltaTime);

        // Update obstacles
        for (Obstacle obstacle : obstacles) {
            obstacle.update(deltaTime);
        }

        // Check collisions
        checkCollisions();

        // Check obstacle passes
        checkObstaclePass();
    }

    /**
     * Update logic for game over state.
     */
    private void updateGameOver(float deltaTime) {
        if (!collisionHandled) {
            sprite.setState(Character.State.DEAD);
            crashSound.play();
            collisionHandled = true;
        }
        sprite.update(deltaTime, entity.getVelocity());
    }

    /**
     * Check for collisions between entity and obstacles.
     */
    private void checkCollisions() {
        if (!collisionEnabled) return;

        for (Obstacle obstacle : obstacles) {
            if (obstacle.checkCollision(entity)) {
                currentState = GameState.GAME_OVER;
                break;
            }
        }
    }

    /**
     * Check if player has passed any obstacles and update score.
     */
    private void checkObstaclePass() {
        for (Obstacle obstacle : obstacles) {
            if (!obstacle.isPassed() &&
                    entity.getX() > obstacle.getTopRect().getX() + obstacle.getTopRect().getWidth()) {
                obstacle.setPassed(true);
                passSound.play();
                score++;

                // TODO: Implement dynamic difficulty scaling
                if (score % 5 == 0) {
                    increaseDifficulty();
                }
            }
        }
    }

    private void increaseDifficulty() {
        for (Obstacle obstacle : obstacles) {
            float currentSpeed = obstacle.getSpeed();
            if (currentSpeed < MAX_OBSTACLE_SPEED) {
                obstacle.setSpeed(currentSpeed + 25f);
            }
        }
    }

    /**
     * Render all game elements.
     */
    private void renderGame() {
        batch.begin();

        // Draw background layers
        layer1.draw(batch, WORLD_HEIGHT);
        layer2.draw(batch, WORLD_HEIGHT);
        layer3.draw(batch, WORLD_HEIGHT);

        // Draw obstacles
        drawObstacles();

        // Draw main character
        sprite.draw(batch, entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());

        // Draw UI
        drawUI();

        batch.end();
    }

    /**
     * Draw obstacles on screen.
     */
    private void drawObstacles() {
        for (Obstacle obstacle : obstacles) {
            Rectangle topRect = obstacle.getTopRect();
            Rectangle bottomRect = obstacle.getBottomRect();

            // Draw filler rectangles
            for (Rectangle fillerRect : obstacle.getFillerRects()) {
                batch.draw(
                        obstacle.getFillerTile(),
                        fillerRect.x, fillerRect.y,
                        fillerRect.width, fillerRect.height
                );
            }

            // Draw top and bottom tiles
            batch.draw(
                    obstacle.getTopTile(),
                    topRect.x, topRect.y,
                    topRect.width, topRect.height
            );

            batch.draw(
                    obstacle.getBottomTile(),
                    bottomRect.x, bottomRect.y,
                    bottomRect.width, bottomRect.height
            );
        }
    }

    /**
     * Draw all UI elements.
     */
    private void drawUI() {
        drawScore();
        drawStateSpecificUI();
        drawDebugInfo();
    }

    /**
     * Draw score display.
     */
    private void drawScore() {
        if (currentState == GameState.PLAYING || currentState == GameState.GAME_OVER) {
            String scoreText = String.valueOf(score);
            infoFont.draw(batch, scoreText, WORLD_WIDTH / 2f - 10, WORLD_HEIGHT - 50);
        }
    }

    /**
     * Draw UI elements specific to current game state.
     */
    private void drawStateSpecificUI() {
        switch (currentState) {
            case MENU:
                drawMenuUI();
                break;
            case GAME_OVER:
                drawGameOverUI();
                break;
            case PLAYING:
                // No additional UI for playing state
                break;
        }
    }

    /**
     * Draw menu UI.
     */
    private void drawMenuUI() {
        String titleText = "Bouncy Asep: The Game";
        titleFont.draw(batch, titleText, WORLD_WIDTH / 6f + 20, WORLD_HEIGHT - 130);

        String menuText = "Press SPACE or LMB to play";
        infoFont.draw(batch, menuText, WORLD_WIDTH / 4f - 30, WORLD_HEIGHT / 5f);
    }

    /**
     * Draw game over UI.
     */
    private void drawGameOverUI() {
        String gameOverText = "GAME OVER";
        titleFont.draw(batch, gameOverText, WORLD_WIDTH / 3f, WORLD_HEIGHT / 2f + 50);

        String restartText = "Press R to restart";
        infoFont.draw(batch, restartText, WORLD_WIDTH / 3f, WORLD_HEIGHT / 2f - 50);
    }

    /**
     * Draw debug information if enabled.
     */
    private void drawDebugInfo() {
        if (!isDebugEnabled) return;

        StringBuilder debugText = new StringBuilder()
                .append("FPS: ").append(Gdx.graphics.getFramesPerSecond())
                .append("\nState: ").append(currentState)
                .append("\nEntity Y: ").append(Math.round(entity.getY()))
                .append("\nGravity: ").append(entity.getGravity())
                .append("\nCollision: ").append(collisionEnabled)
                .append("\nVelocity: ").append(String.format("%.2f", entity.getVelocity()))
                .append("\nScore: ").append(score);

        if (!obstacles.isEmpty()) {
            debugText.append("\nObstacle Speed: ").append(obstacles.get(0).getSpeed());
        }

        debugFont.draw(batch, debugText.toString(), 10, WORLD_HEIGHT - 10);
    }

    /**
     * Handle user input based on current game state.
     */
    private void handleInput() {
        // Global inputs
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F12)) {
            isDebugEnabled = !isDebugEnabled;
        }

        // Debug collision toggle
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            collisionEnabled = !collisionEnabled;
            collisionHandled = false;
        }

        // State-specific input handling
        switch (currentState) {
            case MENU:
                handleMenuInput();
                break;
            case PLAYING:
                handlePlayingInput();
                break;
            case GAME_OVER:
                handleGameOverInput();
                break;
        }
    }

    /**
     * Handle input during menu state.
     */
    private void handleMenuInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) ||
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            startGame();
        }
    }

    /**
     * Handle input during playing state.
     */
    private void handlePlayingInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) ||
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            jump();
        }
    }

    /**
     * Handle input during game over state.
     */
    private void handleGameOverInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame();
        }
    }

    /**
     * Start the game from menu.
     */
    private void startGame() {
        currentState = GameState.PLAYING;
        jump(); // Initial jump to start movement
    }

    /**
     * Make the character jump.
     */
    private void jump() {
        sprite.startJump();
        jumpSound.play();
        entity.jump();
    }

    /**
     * Reset the game to initial state.
     */
    private void resetGame() {
        currentState = GameState.MENU;
        sprite.setState(Character.State.STANDING);
        collisionHandled = false;
        score = 0;

        // Reset entity
        entity.setVelocity(0);
        entity.setY(WORLD_HEIGHT / 2f);

        // Reset obstacles
        for (Obstacle obstacle : obstacles) {
            obstacle.reset();
        }

        // Reset background layers
        layer1.reset();
        layer2.reset();
        layer3.reset();
    }

    /**
     * Dispose of all resources.
     */
    @Override
    public void dispose() {
        // Dispose graphics
        if (batch != null) batch.dispose();

        // Dispose fonts
        if (debugFont != null) debugFont.dispose();
        if (infoFont != null) infoFont.dispose();
        if (titleFont != null) titleFont.dispose();

        // Dispose background layers
        if (layer1 != null) layer1.dispose();
        if (layer2 != null) layer2.dispose();
        if (layer3 != null) layer3.dispose();

        // Dispose audio
        if (jumpSound != null) jumpSound.dispose();
        if (crashSound != null) crashSound.dispose();
        if (passSound != null) passSound.dispose();

        // Dispose sprite
        if (sprite != null) sprite.dispose();
    }

    // Game state
    public enum GameState {
        MENU, PLAYING, GAME_OVER
    }
}