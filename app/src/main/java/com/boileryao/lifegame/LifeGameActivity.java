package com.boileryao.lifegame;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LifeGameActivity extends AppCompatActivity {
    private Timer refreshTimer;
    private TimerTask refreshTask;
    private int timerPeriodMs = 800;
    private int width = 11;
    private int height = 23;
    private ConwaysLifeGame lifeGame;
    private GridView mDashboardGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_life_game);

        mDashboardGridView = findViewById(R.id.gv_dashboard);
        initDashBoard(width, height);
    }

    private void initDashBoard(int width, int height) {
        if (refreshTimer != null) refreshTimer.cancel();  // cancel as soon as possible

        // record current w/h for future usage
        this.width = width;
        this.height = height;

        lifeGame = new ConwaysLifeGame(width, height, null);
        lifeGame.initialize((i, j) -> Math.random() > 0.5);
        mDashboardGridView.setNumColumns(lifeGame.getWidth());

        DashboardAdapter dashboardAdapter = new DashboardAdapter(lifeGame.getLivesMatrix());
        mDashboardGridView.setAdapter(dashboardAdapter);

        refreshTimer = new Timer(true);
        refreshTask = new TimerTask() {
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
        refreshTimer.scheduleAtFixedRate(refreshTask, timerPeriodMs, timerPeriodMs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_life_game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_resize_map:
                showConfigWhDialog();
                return true;
            case R.id.menu_restart:
                initDashBoard(width, height);
                Toast.makeText(getApplicationContext(), "已重置", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfigWhDialog() {
        @SuppressLint("InflateParams")
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_config_wh, null);
        new AlertDialog.Builder(this)
                .setTitle("设置地图宽高")
                .setView(dialogView)
                .setPositiveButton("Yap", (dialog, which) -> {
                    EditText etHeight = dialogView.findViewById(R.id.et_height);
                    EditText etWidth = dialogView.findViewById(R.id.et_width);
                    String heightString = etHeight.getText().toString();
                    String widthString = etWidth.getText().toString();
                    if (heightString.isEmpty() || widthString.isEmpty()) {
                        return;
                    }
                    int width = Integer.parseInt(widthString);
                    int height = Integer.parseInt(heightString);
                    if (width == 0 || height == 0) {
                        Toast.makeText(getApplicationContext(), "参数错误", Toast.LENGTH_LONG).show();
                        return;
                    }
                    initDashBoard(width, height);
                })
                .show();
    }
}
