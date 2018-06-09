package com.boileryao.lifegame;

/**
 * Created by boileryao on 2018/6/9.
 * Licensed under WTFPL©2018.
 * May you have a good life, may you stand on the firm earth.
 * May you a better man and do no evil.
 */
@FunctionalInterface
public interface LivesMatrixInitializer {
    boolean getInitialState(int i, int j);
}
