package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.math.Rectangle;

public class Util {
//    public static boolean isColliding(Rectangle object, Obstacle obstacle) {
//        float obstacleTopX = obstacle.getTopPipe().getX();
//        float obstacleTopY = obstacle.getTopPipe().getY() + obstacle.getTopPipe().getHeight(); // Adjust for the top pipe height
//
//        float obstacleBotX = obstacle.getBottomPipe().getX();
//        float obstacleBotY = obstacle.getBottomPipe().getY();
//
//        float objectX = object.getX();
//        float objectY = object.getY();
//        float objectWidth = object.getWidth();
//        float objectHeight = object.getHeight();
//
//        float objectRight = objectX + objectWidth;
//        float objectBottom = objectY + objectHeight;
//
//        // Check collision with top pipe
//        boolean collidesWithTopPipe = (objectRight >= obstacleTopX && objectX <= obstacleTopX + objectWidth) &&
//                (objectBottom >= obstacleTopY && objectY <= Float.MAX_VALUE);
//
//        // Check collision with bottom pipe
//        boolean collidesWithBottomPipe = (objectRight >= obstacleBotX && objectX <= obstacleBotX + objectWidth) &&
//                (objectBottom >= Float.MIN_VALUE && objectY <= obstacleBotY + objectHeight);
//
//        // Return true if collides with either top or bottom pipe
//        return collidesWithTopPipe || collidesWithBottomPipe;
//    }

//    public boolean isColliding(Rectangle entity) {
//        // Check collision with top pipe
//        if (entity.overlaps(topPipe)) {
//            return true;
//        }
//
//        // Check collision with bottom pipe
//        if (entity.overlaps(bottomPipe)) {
//            return true;
//        }
//
//        // Check if entity is within the gap between pipes
//        float pipeCenterY = topPipe.y + topPipe.height / 2f;
//        return !(entity.y + entity.height < pipeCenterY || entity.y > pipeCenterY + gap);
//    }

}
