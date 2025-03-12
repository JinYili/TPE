package org.example.helpers;

import org.example.models.Station;
import org.example.models.TimeTableRow;
import org.example.models.Train;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class utilities {

    public static int calculateDistance(Station xStation, Station yStation){
        final double EARTH_RADIUS = 6371;
        double lat1Rad = Math.toRadians(xStation.getLatitude());
        double lat2Rad = Math.toRadians(yStation.getLatitude());
        double lon1Rad = Math.toRadians(xStation.getLongitude());
        double lon2Rad = Math.toRadians(yStation.getLongitude());

        double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
        double y = (lat2Rad - lat1Rad);
        double distance = Math.sqrt(x * x + y * y) * EARTH_RADIUS;

        return (int) Math.round(distance);

    }

    public static List<String> getYAxisLabels(Map<String, Integer> map){

        Set<Map.Entry<String, Integer>> set = map.entrySet();
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
                set);

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        List<String> labels = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : list) {
            labels.add(entry.getKey() + " "+ entry.getValue());
        }

        List<String> shallowCopy = labels.subList(0, labels.size());
        return shallowCopy;
    }

    public static String getToolTip( int index,int item, List<Train> trains, List<Station> stations  ) throws ParseException {

        Train train = trains.get(index);
        int TrainNumber  = train.getTrainNumber();
        String operatorShortCode  = train.getOperatorShortCode();
        TimeTableRow row = trains.get(index).getTimeTableRows().get(item);
        String code = row.getStationShortCode();

        String timeStamp = !row.getActualTime().isEmpty() ? " Arrived at " +  formartDateTimeToTime(row.getActualTime()): "Scheduled at "+formartDateTimeToTime (row.getScheduledTime());
        return String.format("<html><body>%s  %s<br> %s <br> %s </body></html>", operatorShortCode, TrainNumber,getStationNameByShortCode(code, stations),timeStamp);
    }

    public static  Stroke soild = new BasicStroke(1.0f);
    public static Stroke dashed =  new BasicStroke(1.0f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f, new float[] {9.0f}, 0.0f);
    public static  Stroke highlightSoild = new BasicStroke(5.0f);
    public static Stroke highlightDashed =  new BasicStroke(5.0f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f, new float[] {9.0f}, 0.0f);
    public static Stroke thinest = new BasicStroke(0);

    public static Stroke getSolidOrDash(int row, int column, List<Train> trains, int highlightNumber, int displayOnly){


        Train train = trains.get(row) ;
        int trainNumber = train.getTrainNumber();

        if(displayOnly>-1){
            if(displayOnly!=trainNumber)
               return thinest;
        }

        String actualTime = train.getTimeTableRows().get(column).getActualTime();
        String scheduledTime = train.getTimeTableRows().get(column).getScheduledTime();
        String liveEstimateTime = train.getTimeTableRows().get(column).getLiveEstimateTime();

        if (actualTime.isEmpty() && (!scheduledTime.isEmpty() || !liveEstimateTime.isEmpty()) ){
            return  trainNumber==highlightNumber || displayOnly==trainNumber  ?  highlightDashed:dashed;
        } else {
            return  trainNumber==highlightNumber || displayOnly == trainNumber ?  highlightSoild:soild;
        }
    }

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static String formartDateTimeToTime(String datetimeStr) throws ParseException {

        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        Date mydate = df.parse(datetimeStr);
        mydate.setTime(mydate.getTime() + TimeUnit.HOURS.toMillis(2));
        return new SimpleDateFormat("HH:mm").format(mydate);
    }
    public static String getStationNameByShortCode(String code, List<Station> stations){
        for(Station station:stations){
            if(station.getStationShortCode().equals(code)) return station.getStationName();
        }
        return "";
    }

    public static Color colorGreen = new Color(0,139,139);
    public static final Color colorBlue = new Color(25,25,112);
    public static final Color colorTransparent =new Color(1,0,0,0 );
    public static final Color colorPink = new Color(255,20,147);
    public static final Color colorGray = new Color(220,220,220);
    public static final Color colorOrange = new Color(255,165,0);
    public static final Color colorWhite = new Color(255,255,255);
    public static final Color colorRed = new Color(220,20,60);
    public static final Color colorPurple= new Color(138,43,226);
    public static final Color colorSomke = new Color(245,245,245);
}
