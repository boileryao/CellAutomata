package com.boileryao.lifegame;

import android.support.annotation.Nullable;

import java.util.Arrays;

/**
 * Created by boileryao on 2018/6/9.
 * Licensed under WTFPL©2018.
 * May you have a good life, may you stand on the firm earth.
 * May you a better man and do no evil.
 */
public class ConwaysLifeGame {
    private static int[][] neighborVectors = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
    };

    private boolean[][] livesMatrix;
    private LivesMatrixDisplay display;

    private int width;
    private int height;

    ConwaysLifeGame(int width, int height, @Nullable LivesMatrixDisplay display) {
        this.width = width;
        this.height = height;
        this.livesMatrix = new boolean[height][width];
        this.display = display;
    }

    public void initialize(LivesMatrixInitializer initializer) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                livesMatrix[i][j] = initializer.getInitialState(i, j);
            }
        }
    }

    public void iterate() {
        boolean[][] livesMatrixCopy = copyOfLivesMatrix();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                switch (getAliveNeighbors(i, j)) {
                    case 2:
                        continue;  // 保持该位置状态不变
                    case 3:
                        livesMatrixCopy[i][j] = true;
                        continue;
                    default:
                        livesMatrixCopy[i][j] = false;
                }
            }
        }
        livesMatrix = livesMatrixCopy;
        if (display != null) {
            display.display(livesMatrix);
        }
    }

    public boolean[][] getLivesMatrix() {
        return livesMatrix;
    }

    private int getAliveNeighbors(int i, int j) {
        int aliveNeighborCount = 0;
        for (int[] diff : neighborVectors) {
            int h = i + diff[0];
            int w = j + diff[1];
            if (w < 0 || w >= width || h < 0 || h >= height) continue;

            if (/*this neighbor is alive */livesMatrix[h][w ]) {
                ++aliveNeighborCount;
            }
        }
        return aliveNeighborCount;
    }

    private boolean[][] copyOfLivesMatrix() {
        boolean[][] copyMatrix = new boolean[height][];
        for (int i = 0; i < height; i++) {
            copyMatrix[i] = Arrays.copyOf(livesMatrix[i], width);
        }
        return copyMatrix;
    }

    public int getWidth() {
        return width;
    }
}
