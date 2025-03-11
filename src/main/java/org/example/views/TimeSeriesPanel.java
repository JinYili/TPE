package org.example.views;

import org.example.listeners.HighLightMouseOverListener;
import org.example.models.Station;
import org.example.models.TimeTableRow;
import org.example.models.Train;
import org.example.services.TrainService;
import org.jfree.chart.*;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import org.jfree.data.xy.XYDataset;
import org.jfree.chart.ui.Layer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Calendar.HOUR;
import static org.example.helpers.utilities.*;

public class TimeSeriesPanel extends JPanel {

    private  Map<String, Integer> yAxisLabelsMap = new HashMap<>();
    private int max=0;
    private TrainService trainService;
    private  List<XYDataset> allDatasets = new ArrayList<>();
    private  XYDataset datasetHKITPE;
    private  XYDataset datasetTPEHKI;
    protected List<Station> stations;
    private List<XYLineAnnotation>  lines = new ArrayList<>();
    private List<Marker>  StationNameMarks= new ArrayList<>();
    private List<Marker>  trainNameMarks= new ArrayList<>();
    public JFreeChart chart;

    private List<Train> trainsHKITPE;
    private List<Train> trainsTPEHKI;

    protected HighLightMouseOverListener listener;

    public TimeSeriesPanel() throws ParseException {
        super();
        trainService = new TrainService();
        this.stations = this.trainService.getAllStatioins();

        // how much number of train will be fetched, n = limit*2, 0 will fetch all
        int limit = 0;
        this.trainsHKITPE = this.trainService.fetchData("HKI", "TPE", limit);
        this.trainsTPEHKI = this.trainService.fetchData("TPE", "HKI", limit );

        datasetHKITPE = createDataset(trainsHKITPE,true);
        datasetTPEHKI = createDataset(trainsTPEHKI,false);

        allDatasets.add(datasetHKITPE);
        allDatasets.add(datasetTPEHKI);
        this.chart = ChartFactory.createTimeSeriesChart(
                "HELSINKI - TAMPERE", "", "Distance(KM)", null, false, true, false);
        listener =new HighLightMouseOverListener();

    }

    public  XYDataset createDataset(List<Train> trains, boolean createStationName) throws ParseException {

        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);

        int index =0;
        //for draw the lines of each station
        long x1=0, x2=0;
        List<Integer> stationDistance = new ArrayList<>();
        List<RegularTimePeriod> trainArrivalTimeStamps = new ArrayList<>();
        List<String> trainNames = new ArrayList<>();

        for (Train train : trains){
            String trainOperatorCodesWithNumber =train.getOperatorShortCode() + " " +train.getTrainNumber();
            trainNames.add(trainOperatorCodesWithNumber);
            TimeSeries series = new TimeSeries(train.getTrainNumber());
            List<TimeTableRow> rows = train.getTimeTableRows();

            // data preparing
            int rowIndex =0;
            for (TimeTableRow row:  rows ){
                Date date = df.parse(!row.getActualTime().equals("")? row.getActualTime():row.getScheduledTime());
                date.setTime(date.getTime() + TimeUnit.HOURS.toMillis(2));

                RegularTimePeriod timestamp = new Minute(date);
                //need only incoming trains
                if(createStationName)
                    yAxisLabelsMap.put(row.getStationShortCode() , row.getDistance());

                // the most far way station in present
                this.max = Math.max(this.max, row.getDistance());
                series.addOrUpdate(timestamp, row.getDistance());
                stationDistance.add(row.getDistance());

                if(rowIndex==0)
                    trainArrivalTimeStamps.add(timestamp);

                rowIndex++;
            }
            // add data to time series collection
            timeSeriesCollection.addSeries(series);
            // this.originTimeSeriesCollection.addSeries(series);

            //starting point of horizontal line
            if(index==0)
                x1= series.getTimePeriod(0).getFirstMillisecond();

            // end point of horizontal line
            if(index == trains.size()-1)
                x2 = series.getTimePeriod(series.getItemCount()-1).getFirstMillisecond() ;
            index++;
        }

        // lines for the stations
        if(createStationName)
            for(int ds : stationDistance.stream().distinct().toList())
                lines.add(new XYLineAnnotation( x1, ds, x2, ds, new BasicStroke(0.5f), colorBlue));

        // preparing markers for the station short code
        for(String ds : getYAxisLabels(yAxisLabelsMap))
        {
            String[] str = ds.split(" ");
            Marker m = new ValueMarker( Integer.parseInt(str[1]));
            m.setPaint(colorWhite);
            m.setLabel(str[0]);
            m.setLabelBackgroundColor(colorTransparent);
            m.setLabelAnchor(RectangleAnchor.LEFT);
            m.setLabelTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
            StationNameMarks.add(m);
        }

        // prepare markers for the train names  from Hel -> Tampere
        for(int i=0; i<trainNames.size();i++){
            Marker marker = new ValueMarker(createStationName ?trainArrivalTimeStamps.get(i).getFirstMillisecond():trainArrivalTimeStamps.get(i).getLastMillisecond());
            marker.setLabelAnchor(createStationName ? RectangleAnchor.BOTTOM_LEFT:RectangleAnchor.TOP_LEFT);
            marker.setLabelTextAnchor(createStationName?  TextAnchor.BASELINE_LEFT:TextAnchor.HALF_ASCENT_LEFT);
            marker.setLabelBackgroundColor(colorTransparent);
            marker.setPaint(colorWhite);
            marker.setLabel(trainNames.get(i));
            trainNameMarks.add(marker);
        }


        return timeSeriesCollection;
    }

    public  JFreeChart createChart() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());

        XYPlot plot = (XYPlot)  this.chart.getPlot();

        //background;gridline;outline
        this.chart.setBackgroundPaint(colorWhite);
        GradientPaint gradient =
                new GradientPaint(new Point(), colorWhite, new Point(), colorGray);
        plot.setBackgroundPaint(gradient);
        plot.setDrawingSupplier(new DefaultDrawingSupplier( new Paint[] { colorGreen
        },
                DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));


        //give departure train data to draw
        plot.setDataset(0,datasetHKITPE);
        XYLineAndShapeRenderer renderer = getXyLineAndShapeRenderer(this.trainsHKITPE);
        for(int k=0; k<plot.getDataset(0).getSeriesCount();k++) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(k,colorGreen);
        }
        plot.setRenderer(0, renderer);

        //give incoming train data to draw
        plot.setDataset(1,datasetTPEHKI);
        XYLineAndShapeRenderer renderer2 = getXyLineAndShapeRenderer(this.trainsTPEHKI);
        for(int k=0; k<plot.getDataset(1).getSeriesCount();k++) {
            plot.getRendererForDataset(plot.getDataset(1)).setSeriesPaint(k,colorPink);
        }
        plot.setRenderer(1, renderer2);


        plot.setDomainGridlinePaint(colorWhite);
        plot.setRangeGridlinePaint(colorWhite);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setDomainGridlinesVisible(false);

        // add horizontal line for each city
        for(XYLineAnnotation line :lines)
            plot.addAnnotation(line);

        // add Markers for station codes
        for(Marker marker :StationNameMarks)
            plot.addRangeMarker(marker);

        for(Marker marker :trainNameMarks)
            plot.addDomainMarker(marker);


        // this mark show current time
        plot.addDomainMarker(new ValueMarker(System.currentTimeMillis(), colorOrange, new BasicStroke(3f)), Layer.FOREGROUND);

        // draws the marks for each hour
        ZonedDateTime startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        long todayMillis1 = startOfToday.toEpochSecond() * 1000;
        for(int n =1; n<24;n++)
            plot.addDomainMarker(new ValueMarker(todayMillis1+3600000*n , colorPink, new BasicStroke(1f)), Layer.FOREGROUND);

        DateAxis domain = (DateAxis) plot.getDomainAxis();
        domain.setDateFormatOverride(simpleDateFormat);
        domain.setVerticalTickLabels(true);

        //range in the max KM
        NumberAxis  yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setAutoRange(false);
        yAxis.setRange(-8, this.max+8);
        yAxis.setLabelLocation( AxisLabelLocation.MIDDLE);
        yAxis.setVisible(true);
        yAxis.setMinorTickCount(0);
        yAxis.setTickUnit(new NumberTickUnit(10));

        DateTickUnit unit = new DateTickUnit(DateTickUnitType.MINUTE,15);
        DateAxis xAxis = (DateAxis) plot.getDomainAxis();
        xAxis.setDateFormatOverride(simpleDateFormat);
        xAxis.setStandardTickUnits(xAxis.createStandardDateTickUnits());
        xAxis.setTickUnit(unit);

        this.chart.setPadding(new RectangleInsets(4, 8, 2, 2));
        return  this.chart;
    }



    private XYLineAndShapeRenderer getXyLineAndShapeRenderer(List<Train> trains) {
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer() {
            @Override
            public Stroke getItemStroke(int row, int column) {
                return getSolidOrDash(row, column, trains,  listener.getHighlightNumber());
            }
        };

        renderer.setLegendItemURLGenerator(new StandardXYSeriesLabelGenerator(""));
        renderer.setDefaultToolTipGenerator((datasetT, index, item) -> {
            try {
                return getToolTip(index, item, trains, this.stations);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        renderer.setDefaultShapesVisible(true);
        return renderer;
    }

    public   ChartPanel createPanel(JFreeChart chart){
        ChartPanel chartPanel = new ChartPanel(chart , false) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(2560  , 960   );
            }
        };
        chartPanel.setMouseWheelEnabled(true);
//        chartPanel.setDisplayToolTips(true);
        chartPanel.setFillZoomRectangle(true);
        chartPanel.setHorizontalAxisTrace(false);
        chartPanel.setVerticalAxisTrace(false);


        chartPanel.addChartMouseListener(listener);
        chartPanel.setInitialDelay(0);

        return chartPanel;
    }


    public JScrollPane wrapTrainJScrollPanel(Component view) {
        JScrollPane  jsp = new JScrollPane(view);
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.setWheelScrollingEnabled(false);
        return jsp;
    }


}

