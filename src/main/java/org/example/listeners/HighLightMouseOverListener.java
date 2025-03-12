package org.example.listeners;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.entity.ChartEntity;


public class HighLightMouseOverListener implements ChartMouseListener {

    private int highlightNumber= -1;
    public int getHighlightNumber() {
        return highlightNumber;
    }

    public void setHighlightNumber(int highlightNumber) {

        this.highlightNumber = highlightNumber;
    }

    //doing nothing just ignore
    @Override
    public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {}


    @Override
    public void chartMouseMoved(ChartMouseEvent chartMouseEvent) {
        ChartEntity entity = chartMouseEvent.getEntity();

        if (entity.getToolTipText()==null){
            this.highlightNumber=-1;
            return;
        }
        String[] str = entity.getToolTipText().split("<br>")[0].split("  ");
        this.highlightNumber = str.length<2 ? -1: Integer.parseInt(str[1]) ;

    }
}
