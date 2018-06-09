package com.boileryao.lifegame;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LifeGameActivity extends AppCompatActivity {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private ConwaysLifeGame lifeGame;
    private GridView mDashboardGridView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mDashboardGridView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_life_game);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mDashboardGridView = findViewById(R.id.gv_dashboard);
        initDashBoard();
    }

    private void initDashBoard() {
        lifeGame = new ConwaysLifeGame(10, 10, null);
        lifeGame.initialize((i, j) -> Math.random() > 0.5);
        mDashboardGridView.setNumColumns(lifeGame.getRowCount());

        DashboardAdapter dashboardAdapter = new DashboardAdapter(lifeGame.getLivesMatrix());
        mDashboardGridView.setAdapter(dashboardAdapter);

        Timer refreshTimer = new Timer(true);
        TimerTask refreshTask = new TimerTask() {
            @Override
            public void run() {
                lifeGame.iterate();
                boolean[][] livesMatrix = lifeGame.getLivesMatrix();
                boolean allDead = true;
                outerLoop:
                for (boolean[] livesRow : livesMatrix) {
                    for (boolean alive : livesRow) {
                        if (alive) {
                            allDead = false;
                            break outerLoop;
                        }
                    }
                }
                boolean finalAllDead = allDead;
                runOnUiThread(() -> {
                    if (finalAllDead) {
                        Toast.makeText(getApplicationContext(), "全部死亡, 重新开始游戏", Toast.LENGTH_LONG).show();
                        lifeGame.initialize((i, j) -> Math.random() > 0.5);
                    }
                    dashboardAdapter.update(livesMatrix);
                });
            }
        };
        refreshTimer.scheduleAtFixedRate(refreshTask, 1200, 1200);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mDashboardGridView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
