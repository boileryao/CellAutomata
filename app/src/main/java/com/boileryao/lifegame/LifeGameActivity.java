package com.boileryao.lifegame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LifeGameActivity extends AppCompatActivity {
    private int width = 10;
    private int height = 10;

    private ConwaysLifeGame lifeGame;
    private GridView mDashboardGridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_life_game);

        mDashboardGridView = findViewById(R.id.gv_dashboard);
        initDashBoard();
    }

    private void initDashBoard() {
        lifeGame = new ConwaysLifeGame(10, 20, null);
        lifeGame.initialize((i, j) -> Math.random() > 0.5);
        mDashboardGridView.setNumColumns(lifeGame.getWidth());

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

}
