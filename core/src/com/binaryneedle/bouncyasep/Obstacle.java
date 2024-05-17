package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import static com.badlogic.gdx.math.MathUtils.random;

public class Obstacle {
    private Rectangle topPipe;
    private Rectangle bottomPipe;
    private float speed;
    private TextureRegion topTile, bottomTile, fillerTile;
    private float initialX;
    private float gap;
    private float tileSquared;
    private int maxY;
    private boolean passed;

    public Obstacle(float initialX, int maxY, float gap, float speed, float tileSquared) {
        this.speed = speed;
        this.initialX = tileSquared * initialX;
        this.gap = tileSquared * gap;
        this.tileSquared = tileSquared;
        this.maxY = maxY;

        topPipe = new Rectangle(this.initialX, this.tileSquared * random(0, maxY), this.tileSquared, this.tileSquared);
        bottomPipe = new Rectangle(this.initialX, topPipe.y + this.gap, this.tileSquared, this.tileSquared);

        Texture tilesetTexture = new Texture("woods_tileset.png");
        TextureRegion[][] tiles = TextureRegion.split(tilesetTexture, 24, 24);

        topTile = tiles[10][15];
        bottomTile = tiles[11][15];
        fillerTile = tiles[5][15];
    }

    public void update(float deltaTime) {
        float resetPositionX = 1024f + 15f + 4f;
        if (topPipe.x < -gap * 2 / 3f - 1f) {
            setX(resetPositionX);
            setRandomY(0, maxY);
            passed = false;
        }

        topPipe.x -= speed * deltaTime;
        bottomPipe.x -= speed * deltaTime;
    }

    public void setX(float x) {
        topPipe.x = x;
        bottomPipe.x = x;
    }

    public void setY(float y) {
        topPipe.y = y;
        bottomPipe.y = y + gap;
    }

    public void setRandomY(int min, int max) {
        setY(tileSquared * random(min, max));
    }

    public Rectangle getTopPipe() {
        return topPipe;
    }

    public Rectangle getBottomPipe() {
        return bottomPipe;
    }

    public TextureRegion getTopTile() {
        return topTile;
    }

    public TextureRegion getBottomTile() {
        return bottomTile;
    }

    public TextureRegion getFillerTile() {
        return fillerTile;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void reset() {
        setX(initialX);
        setRandomY(0, maxY);
        passed = false;
    }

    public float getTileSquared() {
        return tileSquared;
    }

    public boolean checkCollision(Rectangle entity) {
        if (entity.overlaps(topPipe) || entity.overlaps(bottomPipe)) {
            return true;
        }

        float obstacleX = topPipe.getX();
        float topY = topPipe.getY() / tileSquared;
        float botY = bottomPipe.getY() / tileSquared;

        for (int i = 0; i < 12; i++) {
            if (i >= topY && i - 1 < botY) continue;

            Rectangle fillerRect = new Rectangle(obstacleX, tileSquared * i, topPipe.getWidth(), topPipe.getHeight());
            if (entity.overlaps(fillerRect)) {
                return true;
            }
        }

        return false;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
