package org.example.views;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;


public class MainWindow {

    private final JFrame frame;
    //private final TimetablePanel timetable;
    private final HeaderPanel headerPanel;
    private final ChartPanel chartPanel;
    public MainWindow() throws ParseException {


        this.frame = new JFrame();
        this.frame.setTitle("HKI -> TPE");
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setSize(2048,1080 );
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(true);

        this.headerPanel = new HeaderPanel();
        this.frame.add(headerPanel, BorderLayout.NORTH);

        TimeSeriesPanel jp =  new TimeSeriesPanel();
        JFreeChart chart = jp.createChart();
        this.chartPanel =jp.createPanel(chart);
        this.frame.add(jp.wrapTrainJScrollPanel(this.chartPanel), BorderLayout.CENTER);
        this.frame.pack();
    }

    public void show() {
        this.frame.setVisible(true);
    }

}
