package com.canonale.timerseries.listview;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.canonale.timerseries.helper.Cronometro;

import java.util.ArrayList;

/**
 * Created by adrian on 4/08/13.
 */
public class SeriesAdapter extends BaseAdapter {

    private ArrayList<Cronometro> series;

    public SeriesAdapter(ArrayList<Cronometro> series){
        this.series = series;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return series.size();
    }

    @Override
    public Object getItem(int i) {
        return series.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        SeriesView seriesView;
        if (view == null)
            seriesView = new SeriesView(viewGroup.getContext());
        else
            seriesView = (SeriesView) view;
        seriesView.setNombreSerie(series.get(i));
        //seriesView.setColorText(0xCC0000);

        return seriesView;
    }
}
