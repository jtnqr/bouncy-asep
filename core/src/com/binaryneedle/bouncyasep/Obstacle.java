package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

import static com.badlogic.gdx.math.MathUtils.random;

public class Obstacle {
    private Rectangle topPipe;
    private Rectangle bottomPipe;
    private float speed;
    private TextureRegion topTile, bottomTile, fillerTile;
    private float initalX;
    private float gap;
    private float tileSquared;
    private int maxY;

    public Obstacle(float initialX, int maxY, float gap, float speed, float tileSquared) {
        this.speed = speed;
        this.initalX = tileSquared * initialX;
        this.gap = tileSquared * gap;
        this.tileSquared = tileSquared;
        this.maxY = maxY;

        int randY = random(0, maxY);
        topPipe = new Rectangle(tileSquared * initialX, (tileSquared * randY), tileSquared, tileSquared);
        bottomPipe = new Rectangle(tileSquared * initialX, (tileSquared * randY) + (tileSquared * gap), tileSquared, tileSquared);

        Texture tilesetTexture = new Texture("woods_tileset.png");

        TextureRegion[][] tiles = TextureRegion.split(tilesetTexture, 24, 24);
        topTile = tiles[10][15];
        bottomTile = tiles[11][15];
        fillerTile = tiles[5][15];
    }

    public void update(float deltaTime) {
        if (this.topPipe.x < 0) {
            setX(1024f + 15f + 4f);
            setRandomY(1, this.maxY);
        }

        topPipe.x -= speed * deltaTime;
        bottomPipe.x -= speed * deltaTime;
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

    public void setX(float x) {
        this.topPipe.x = x;
        this.bottomPipe.x = x;
    }

    public void setY(float y) {
        this.topPipe.y = y;
        this.bottomPipe.y = y + this.gap;
    }

    public void setRandomY(int min, int max) {
        setY(this.tileSquared * random(min, max));
    }

    public Rectangle getTopPipe() {
        return topPipe;
    }

    public Rectangle getBottomPipe() {
        return bottomPipe;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void reset() {
        setX(initalX);
        setRandomY(0, this.maxY);
    }

    public float getTileSquared() {
        return this.tileSquared;
    }
}

