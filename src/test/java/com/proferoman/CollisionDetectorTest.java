package com.proferoman;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CollisionDetectorTest {

    @Test
    public void testCollide1() {
        Obstacle o1 = new Obstacle(255, 272);
        Obstacle o2 = new Obstacle(255, 272);

        assertTrue(CollisionDetector.collide(o1, o2));
    }

    @Test
    public void testCollide2() {
        Obstacle o1 = new Obstacle(275, 275);
        Obstacle o2 = new Obstacle(295, 300);

        assertTrue(CollisionDetector.collide(o1, o2));
    }

    @Test
    public void testCollide3() {
        Obstacle o1 = new Obstacle(275, 275);
        Obstacle o2 = new Obstacle(295, 300);

        assertTrue(CollisionDetector.collide(o2, o1));
    }

    @Test
    public void testCollide4() {
        Obstacle o1 = new Obstacle(300, 300);
        Obstacle o2 = new Obstacle(280, 320);

        assertTrue(CollisionDetector.collide(o1, o2));
    }

    @Test
    public void testCollide5() {
        Obstacle o1 = new Obstacle(300, 300);
        Obstacle o2 = new Obstacle(320, 280);

        assertTrue(CollisionDetector.collide(o1, o2));
    }

    @Test
    public void testCollide6() {
        Obstacle o1 = new Obstacle(300, 300);
        Obstacle o2 = new Obstacle(280, 280);

        assertTrue(CollisionDetector.collide(o1, o2));
    }
}