package com.xingge.carble.dialog;

import android.content.Context;
import android.graphics.Color;

import com.daivd.chart.component.axis.BaseAxis;
import com.daivd.chart.component.axis.VerticalAxis;
import com.daivd.chart.component.base.IAxis;
import com.daivd.chart.component.base.IComponent;
import com.daivd.chart.core.LineChart;
import com.daivd.chart.data.ChartData;
import com.daivd.chart.data.LineData;
import com.daivd.chart.data.style.FontStyle;
import com.daivd.chart.data.style.PointStyle;
import com.daivd.chart.listener.OnClickColumnListener;
import com.daivd.chart.provider.component.point.LegendPoint;
import com.daivd.chart.provider.component.point.Point;
import com.xingge.carble.R;
import com.xingge.carble.bean.GpsInfo;
import com.xingge.carble.util.Tool;

import java.util.ArrayList;
import java.util.List;

public class LineChartDialog extends BaseDialog {

    private LineChart lineChart;

    public LineChartDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        lineChart = getDialog().findViewById(R.id.lin_chart);
        setCanceledOnTouchOutside(true);

        initView();
    }

    private void initView() {
        FontStyle.setDefaultTextSpSize(getContext(), 12);

        lineChart.setLineModel(LineChart.LINE_MODEL);

        VerticalAxis leftAxis = lineChart.getLeftVerticalAxis();
        leftAxis.setStartZero(false);
        leftAxis.setMaxValue(8000);
        leftAxis.setMinValue(-500);

        BaseAxis horizontalAxis = lineChart.getHorizontalAxis();

        VerticalAxis rightAxis = lineChart.getRightVerticalAxis();
        rightAxis.setStartZero(true);
        rightAxis.setMaxValue(500);
        //设置竖轴方向
        leftAxis.setAxisDirection(IAxis.AxisDirection.LEFT);

        //设置网格
        leftAxis.setDrawGrid(true);
        //设置横轴方向
        horizontalAxis.setAxisDirection(IAxis.AxisDirection.BOTTOM);
//        horizontalAxis.setDrawGrid(true);
        //设置线条样式
        leftAxis.getAxisStyle().setWidth(getContext(), 1);

//        DashPathEffect effects = new DashPathEffect(new float[]{1, 2, 4, 8}, 1);
//        verticalAxis.getGridStyle().setWidth(getContext(), 1).setColor(Color.RED).setEffect(effects);
//        horizontalAxis.getGridStyle().setWidth(getContext(), 1).setColor(Color.RED).setEffect(effects);
//        VerticalCross cross = new VerticalCross();
//        LineStyle crossStyle = cross.getCrossStyle();
//        crossStyle.setWidth(getContext(), 1);
//        crossStyle.setColor(Color.YELLOW);
//        lineChart.getProvider().setCross(cross);
        lineChart.setZoom(true);
        //开启十字架
//        lineChart.getProvider().setOpenCross(true);
        //开启MarkView
        lineChart.getProvider().setOpenMark(true);
        //设置MarkView
        lineChart.getProvider().setMarkView(new CustomMarkView(getContext()));
        //设置显示点
        Point point = new Point();
        point.getPointStyle().setShape(PointStyle.CIRCLE);
        //设置显示点的样式
        lineChart.getProvider().setPoint(point);
        //设置显示点显示值
        lineChart.getProvider().setShowText(true);
        //提示
        /*Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(DensityUtils.sp2px(getContext(), 13));
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        MultiLineBubbleTip tip = new MultiLineBubbleTip<LineData>(getContext(),
                R.mipmap.round_rect, R.mipmap.triangle, paint) {
            @Override
            public boolean isShowTip(LineData lineData, int position) {
                return position == 2;
            }

            @Override
            public String[] format(LineData lineData, int position) {
                String title = lineData.getName();
                String value = lineData.getChartYDataList().get(position) + lineData.getUnit();
                return new String[]{title, value};
            }
        };
        tip.setColorFilter(Color.parseColor("#FA8072"));
        tip.setAlpha(0.8f);
        lineChart.getProvider().setTip(tip);*/
        //设置显示标题
        lineChart.setShowChartName(true);

        //设置标题方向
        lineChart.getChartTitle().setDirection(IComponent.TOP);
        //设置标题比例
        lineChart.getChartTitle().setPercent(0.2f);
        //设置标题样式
        FontStyle fontStyle = lineChart.getChartTitle().getFontStyle();
        fontStyle.setTextColor(Color.BLACK);
        fontStyle.setTextSpSize(getContext(), 15);

        //底部的线
//        LevelLine levelLine = new LevelLine(5);
//        DashPathEffect effects2 = new DashPathEffect(new float[]{1, 2, 2, 4}, 1);
//        levelLine.getLineStyle().setWidth(getContext(), 1).setColor(Color.BLACK).setEffect(effects);
//        levelLine.getLineStyle().setEffect(effects2);
//        lineChart.getProvider().addLevelLine(levelLine);
        lineChart.getLegend().setDirection(IComponent.BOTTOM);
        LegendPoint legendPoint = (LegendPoint) lineChart.getLegend().getPoint();
        PointStyle style = legendPoint.getPointStyle();
        style.setShape(PointStyle.RECT);

        lineChart.getHorizontalAxis().setRotateAngle(90);
        lineChart.getHorizontalAxis().setDisplay(false);
        lineChart.setFirstAnim(false);

        lineChart.startChartAnim(1000);
        lineChart.setOnClickColumnListener(new OnClickColumnListener<LineData>() {
            @Override
            public void onClickColumn(LineData lineData, int position) {
                //  Toast.makeText(LineChartActivity.this,lineData.getChartYDataList().get(position)+lineData.getUnit(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showData(List<GpsInfo> list) {
        List<String> chartYDataList = new ArrayList<>();

        ArrayList<Double> altitudeList = new ArrayList<>();
        ArrayList<Double> speedList = new ArrayList<>();

        double aMin = 0, aMax = 10, sMin = 0, sMax = 10;
        for (int i = 0; i < list.size(); i++) {
            GpsInfo info = list.get(i);

            double altitude = Double.parseDouble(info.altitude);
            double speed = Double.parseDouble(info.speed);
            aMin = Math.min(aMin, altitude);
            aMax = Math.max(aMax, altitude);

            sMin = Math.min(sMin, speed);
            sMax = Math.max(sMax, speed);

//            if (i % 5 == 0) {
            chartYDataList.add(Tool.dateToHour(info.date));
            altitudeList.add(altitude);
            speedList.add(speed);
//            }
        }

//        if (list.size() % 20 != 0) {
//            GpsInfo info = list.get(list.size() - 1);
//            chartYDataList.add(Tool.dateToHour(info.date));
//            altitudeList.add(Double.parseDouble(info.altitude));
//            speedList.add(Double.parseDouble(info.speed));
//        }

//        DisplayMetrics mDisplayMetrics = getContext().getResources().getDisplayMetrics();
//        int w = mDisplayMetrics.widthPixels - Tool.dip2px(getContext(), 80);

        //控制显示的数量
//        lineChart.getMatrixHelper().setWidthMultiple(chartYDataList.size() * 1.0f / w);
        lineChart.getMatrixHelper().setWidthMultiple(2f);

        lineChart.getLegend().setPercent(0.3f);


        VerticalAxis leftAxis = lineChart.getLeftVerticalAxis();
        leftAxis.setStartZero(true);
        leftAxis.setMaxValue(sMax + 20);
        leftAxis.getAxisStyle().setColor(Color.GREEN);

        VerticalAxis rightAxis = lineChart.getRightVerticalAxis();
        rightAxis.setStartZero(false);
        rightAxis.setMinValue(aMin - 20);
        rightAxis.setMaxValue(aMax + 20);
        rightAxis.getAxisStyle().setColor(Color.BLUE);


        LineData columnData1 = new LineData("海拔", "M", IAxis.AxisDirection.RIGHT, Color.BLUE, altitudeList);

        LineData columnData2 = new LineData("速度", "KM/H", Color.GREEN, speedList);
        List<LineData> columnDatas = new ArrayList<>();
        columnDatas.add(columnData2);
        columnDatas.add(columnData1);
        ChartData<LineData> chartData = new ChartData<>("速度与海拔", chartYDataList, columnDatas);
//        chartData.getScaleData().totalScale = 10;
        lineChart.setChartData(chartData);
        super.show();
    }

    @Override
    public int initLayoutId() {
        return R.layout.dialog_line_chart;
    }


}