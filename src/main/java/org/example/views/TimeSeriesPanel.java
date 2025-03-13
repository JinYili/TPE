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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;
import static org.example.helpers.utilities.*;

public class TimeSeriesPanel extends JPanel {

    private  Map<String, Integer> yAxisLabelsMap = new HashMap<>();
    private int max=0;
    private TrainService trainService;
    private  XYDataset datasetHKITPE;
    private  XYDataset datasetTPEHKI;
    private List<Station> stations;
    private List<XYLineAnnotation>  lines = new ArrayList<>();
    private List<Marker>  StationNameMarks= new ArrayList<>();
    private List<Marker>  trainNameMarks= new ArrayList<>();
    private JFreeChart chart;
    private ValueMarker currentTimeMarker = createCurrentTimeline();
    private List<Train> trainsHKITPE;
    private List<Train> trainsTPEHKI;
    private ChartPanel chartPanel;
    private HighLightMouseOverListener listener;

    private int displayOnly =-1;

    public TimeSeriesPanel() throws ParseException {
        super();
        trainService = new TrainService();
        this.stations = this.trainService.getAllStatioins();

        // how much number of train will be fetched, n = limit*2, 0 will fetch all
        int limit = 0;
        this.trainsHKITPE = this.trainService.fetchData("HKI", "TPE", limit);
        this.trainsTPEHKI = this.trainService.fetchData("TPE", "HKI", limit );

        //this.allTrains.addAll (this.trainsHKITPE);
       // this.allTrains.addAll (this.trainsTPEHKI);

        datasetHKITPE = createDataset(trainsHKITPE,true);
        datasetTPEHKI = createDataset(trainsTPEHKI,false);

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
            for (TimeTableRow row:  rows){

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
        this.chart.setBackgroundPaint(colorWhite);
        plot.setBackgroundPaint(colorSomke);
        plot.setDrawingSupplier(new DefaultDrawingSupplier( new Paint[] { colorGreen
        },
                DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));


        //give departure train data to draw
        plot.setDataset(0,datasetHKITPE);
        for(int i=0; i<datasetHKITPE.getSeriesCount();i++) {
              plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i, colorGreen );
        }
        XYLineAndShapeRenderer renderer = getXyLineAndShapeRenderer(this.trainsHKITPE);
        plot.setRenderer(0, renderer);

        //give incoming train data to draw
        plot.setDataset(1,datasetTPEHKI);
        for(int j=0; j<datasetTPEHKI.getSeriesCount();j++) {
                 plot.getRendererForDataset(plot.getDataset(1)).setSeriesPaint(j,colorPink);
        }
        XYLineAndShapeRenderer renderer2 = getXyLineAndShapeRenderer(this.trainsTPEHKI);
        plot.setRenderer(1, renderer2);

        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setDomainGridlinesVisible(false);

        // add horizontal line for each city
        for(XYLineAnnotation line :lines)
            plot.addAnnotation(line);

        // add Markers for station codes
        for(Marker marker :StationNameMarks)
            plot.addRangeMarker(marker);

        // label of trains
        for(Marker marker :trainNameMarks)
            plot.addDomainMarker(marker);


        // each min we redraw the line of current
        Timer timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask() {public void run() {
            plot.removeDomainMarker(currentTimeMarker);
            currentTimeMarker = createCurrentTimeline();
            plot.addDomainMarker(currentTimeMarker);
        }},0, 1000*60);


        // draws the marks for each hour
        ZonedDateTime startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        long todayMillis1 = startOfToday.toEpochSecond() * 1000;
        for(int n =1; n<24;n++)
            plot.addDomainMarker(new ValueMarker(todayMillis1+3600000*n , colorRed, new BasicStroke(1f)), Layer.FOREGROUND);

        //settings on the y-axis
        NumberAxis  yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setAutoRange(false);
        yAxis.setRange(-8, this.max+8);
        yAxis.setLabelLocation( AxisLabelLocation.MIDDLE);
        yAxis.setVisible(true);
        yAxis.setMinorTickCount(0);
        yAxis.setTickUnit(new NumberTickUnit(10));

        // settings on the x-axis
        DateAxis xAxis = (DateAxis) plot.getDomainAxis();
        xAxis.setVerticalTickLabels(true);
        xAxis.setDateFormatOverride(simpleDateFormat);
        xAxis.setStandardTickUnits(DateAxis.createStandardDateTickUnits());
        DateTickUnit unit = new DateTickUnit(DateTickUnitType.MINUTE,15);
        xAxis.setTickUnit(unit);

        this.chart.setPadding(new RectangleInsets(4, 8, 2, 2));
        return  this.chart;
    }

    private XYLineAndShapeRenderer getXyLineAndShapeRenderer(List<Train> trains) {
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer() {
            @Override
            public Stroke getItemStroke(int row, int column) {
                return getSolidOrDash(row, column, trains,  listener.getHighlightNumber() ,displayOnly);
            }

            final int min = Math.min(trainsHKITPE.size(), trainsTPEHKI.size());
            final Color fixColor = trainsHKITPE.size() < trainsTPEHKI.size() ? colorGreen:colorPink;
            // need it to fix the color bug
            public Paint getItemPaint(int row, int col) {
                return row<min ? super.getItemPaint(row, col):fixColor;
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

    public ChartPanel createPanel(JFreeChart chart){
        this.chartPanel = new ChartPanel(chart , false) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(1600   , 720);
            }
        };
        this.chartPanel.setMouseWheelEnabled(true);
//        chartPanel.setDisplayToolTips(true);
        this.chartPanel.setFillZoomRectangle(true);
        this.chartPanel.setHorizontalAxisTrace(false);
        this.chartPanel.setVerticalAxisTrace(false);

        this.chartPanel.addChartMouseListener(listener);
        this.chartPanel.setInitialDelay(0);

        return chartPanel;
    }

    private ValueMarker createCurrentTimeline(){
        return new ValueMarker(System.currentTimeMillis(), colorOrange, new BasicStroke(3f));
    }

    public JScrollPane wrapTrainJScrollPanel(Component view) {
        JScrollPane  jsp = new JScrollPane(view);
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.setWheelScrollingEnabled(false);
        return jsp;
    }

    public JPanel createTImeTable() throws ParseException {


        JTable jTableHKITPE = createJTimeTable(trainsHKITPE);
        JLabel jLabelHKITPE = createTableLabel("HELSINKI -> TAMPERE", colorPink);
        JScrollPane jsp1 = createTableScrollPanel(jTableHKITPE);

        JTable jTableTPEHKI =  createJTimeTable(trainsTPEHKI);
        JLabel jLabelTPEHKI = createTableLabel ("TAMPERE -> HELSINKI",colorGreen);
        JScrollPane jsp2 = createTableScrollPanel(jTableTPEHKI);

        JPanel timetablePanel = new JPanel();
        timetablePanel.setLayout(new FlowLayout());
        timetablePanel.setPreferredSize(new Dimension(200,720 ));
        timetablePanel.add(createBox());

        timetablePanel.add(jLabelHKITPE);
        timetablePanel.add(jsp1);
        timetablePanel.add(jLabelTPEHKI);
        timetablePanel.add(jsp2);

        return  timetablePanel;
    }

    private JTable createJTimeTable (List<Train> trains ) throws ParseException {
        String[] columnNames = { "Name" , "Time"};
        String[][] data =  new String[trains.size()][2];
        int index =0;
        for(Train t: trains){
            data[index] = new String[] { t.getOperatorShortCode().toUpperCase() + " " + t.getTrainNumber(), !t.getTimeTableRows().get(0).getActualTime().isEmpty() ? formartDateTimeToTime(t.getTimeTableRows().get(0).getActualTime()) : formartDateTimeToTime(t.getTimeTableRows().get(0).getScheduledTime() )};
            index++;
        }
        JTable jtbale = new JTable(data, columnNames);
        jtbale.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                tableRowClickListener( jtbale, e);
            }
        });
        jtbale.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {}

            @Override
            public void mouseMoved(MouseEvent e) {
                tableRowHoverListener(jtbale,e);
            }
        });
        jtbale.getTableHeader().setUI(null);
        jtbale.getTableHeader().setVisible(false);
        return jtbale;
    }
    private JLabel createTableLabel( String text, Color c){

        JLabel label = new JLabel(text);
        label.setForeground(c);
        return label;
    }
    private JScrollPane createTableScrollPanel(JTable t){

        JScrollPane jsp = new JScrollPane(t);
        jsp.setPreferredSize(new Dimension(200,350 ));
        jsp.setBackground(colorWhite);
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return jsp;
    }

    private void tableRowClickListener(JTable table, ListSelectionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String text = table.getValueAt(selectedRow,0).toString();
            int trainNumber = Integer.parseInt(text.split(" ")[1]);
            listener.setHighlightNumber(trainNumber);
            this.chartPanel.repaint();
        }
    }

    private void tableRowHoverListener(JTable table, MouseEvent  e) {
        Point p = e.getPoint();
        int hoveredRow = table.rowAtPoint(p);
        String text = table.getValueAt(hoveredRow,0).toString();
        if(!text.isEmpty()){
            displayOnly=-1;
            int trainNumber = Integer.parseInt(text.split(" ")[1]);
            listener.setHighlightNumber(trainNumber);
            this.chartPanel.repaint();
        }

    }

    private JComboBox createBox() {
        final JComboBox jComboBox = new JComboBox();

        final String[] allNames = new String[trainsHKITPE.size() + trainsTPEHKI.size() + 1];
        allNames[0] = "- All -";
        int index=1;
        for (Train train : trainsHKITPE) {
            allNames[index] = train.getOperatorShortCode() + " " + train.getTrainNumber();
            index++;
        }
        for (Train train : trainsTPEHKI) {
            allNames[index] = train.getOperatorShortCode() + " " + train.getTrainNumber();
            index++;
        }

        jComboBox.setModel(new DefaultComboBoxModel(allNames));
        jComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = Objects.requireNonNull(jComboBox.getSelectedItem()).toString();
                if(!str.equals(allNames[0])){

                    String[] strs = str.split(" ");
                    displayOnly = Integer.parseInt(strs[1]);
                }else{
                    displayOnly=-1;

                }

                System.out.println(max);
                chartPanel.repaint();
                chart.setNotify(true);
            }
        });
        return jComboBox;
    }
}

