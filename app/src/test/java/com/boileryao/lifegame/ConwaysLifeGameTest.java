package com.boileryao.lifegame;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by boileryao on 2018/6/9.
 * Class: ConwaysLifeGameTest
 */
public class ConwaysLifeGameTest {

    private ConwaysLifeGame lifeGame = new ConwaysLifeGame(10, 10, null);

    @Before
    public void init() {
        lifeGame.initialize((i, j) -> i == j);
    }

    @Test
    public void initialize() {
        boolean[][] livesMatrix = lifeGame.getLivesMatrix();
        Assert.assertTrue(livesMatrix[0][0]);
        Assert.assertTrue(livesMatrix[1][1]);
        Assert.assertFalse(livesMatrix[1][2]);
    }

    @Test
    public void iterate() {
        lifeGame.iterate();
        Assert.assertTrue(lifeGame.getLivesMatrix()[1][1]);
    }

}