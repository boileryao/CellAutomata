package com.boileryao.lifegame;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by boileryao on 2018/6/9.
 * Class: DashboardAdapter
 */
public class DashboardAdapter extends BaseAdapter {
    private boolean[][] livesMatrix;

    public DashboardAdapter(boolean[][] livesMatrix) {
        this.livesMatrix = livesMatrix;
    }

    public void update(boolean[][] livesMatrix) {
        this.livesMatrix = livesMatrix;
        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        // it's RECT, so that's OK
        return livesMatrix.length > 0 ? livesMatrix.length * livesMatrix[0].length : 0;
    }

    @Override
    public Boolean getItem(int position) {
        int i = position / livesMatrix.length;
        int j = position % livesMatrix.length;
        return livesMatrix[i][j];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean alive = getItem(position);
        int layoutId = alive ? R.layout.item_cell_alive : R.layout.item_cell_dead;
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }
}
