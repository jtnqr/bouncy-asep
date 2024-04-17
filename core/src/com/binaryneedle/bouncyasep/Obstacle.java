package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Obstacle {
    private Rectangle topPipe;
    private Rectangle bottomPipe;
    private float speed;
    private TextureRegion topTile, bottomTile, fillerTile;

    public Obstacle(float x, float y, float tileSquared, float gap, float speed) {
        this.speed = speed;

        topPipe = new Rectangle(x, y, tileSquared, tileSquared);

        bottomPipe = new Rectangle(x, y + gap, tileSquared, tileSquared);

        Texture tilesetTexture = new Texture("woods_tileset.png");

        TextureRegion[][] tiles = TextureRegion.split(tilesetTexture, 24, 24);
        topTile = tiles[10][15];
        bottomTile = tiles[11][15];
        fillerTile = tiles[5][15];
    }

    public void update(float deltaTime) {
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

    public void setBottomPipeX(float x) {
        this.bottomPipe.x = x;
    }

    public void setTopPipeX(float x) {
        this.topPipe.x = x;
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
}

