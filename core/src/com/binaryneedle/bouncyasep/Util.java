package com.binaryneedle.bouncyasep;

public class Util {
    public static boolean isColliding(float objectX, float objectY, float obstacleX, float obstacleY) {
        float objectRight = objectX + 64.0f;
        float objectBottom = objectY + 64.0f;

        return (objectRight >= obstacleX && objectX <= obstacleX + 64.0f) &&
                (objectBottom >= obstacleY && objectY <= obstacleY + 64.0f);
    }
}
