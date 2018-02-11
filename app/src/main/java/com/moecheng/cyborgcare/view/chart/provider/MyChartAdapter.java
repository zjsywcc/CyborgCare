package com.moecheng.cyborgcare.view.chart.provider;

import java.util.ArrayList;

/**
 * Created by wangchengcheng on 2018/1/28.
 */

public class MyChartAdapter extends SimpleChartAdapter {

    private int lineCount;
    private ArrayList<Float> dataList;
    private int lineColor;
    private int shadowColor;
    private int xLabelsCount;

    public MyChartAdapter(int lineCount, ArrayList<Float> dataList, int lineColor, int shadowColor, int xLabelsCount) {
        this.lineColor = lineColor;
        this.lineCount = lineCount;
        this.dataList = dataList;
        this.shadowColor = shadowColor;
        this.xLabelsCount = xLabelsCount;
    }

    @Override
    public int getXLabelsCount() {
        return this.xLabelsCount;
    }

    @Override
    public String getXLabel(int position) {
        return position + "";
    }

    @Override
    public int getShadowColor(int position) {
        return this.shadowColor;
    }

    @Override
    public int getLineCount() {
        return 1;
    }

    @Override
    public ArrayList<Float> getLineData(int index) {
        return this.dataList;
    }

    @Override
    public int getLineColorId(int index) {
        return this.lineColor;
    }
}
