package org.example.services;

import org.example.models.Station;
import org.example.models.TimeTableRow;
import org.example.models.Train;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.example.helpers.utilities.calculateDistance;


public class TrainService {

    public TrainService() {
        this.stationService = new StationService();
        this.stations = stationService.getAllStations();

        this.restTemplate = new RestTemplate();
        this.headers.add("Accept-Encoding","gzip;q=0,deflate,sdch");
        this.headers.add("Connection","keep-alive");
        this.httpEntity  = new HttpEntity<>(headers);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        this.dateStr =df.format(new Date()).trim();
    }
    private final StationService stationService;
    private final List<Station> stations ;
    private List<Train> trains ;
    private final RestTemplate restTemplate;
    private final HttpHeaders headers = new HttpHeaders();
    private final HttpEntity<String> httpEntity ;
    private final String dateStr ;


   // fetch the data from remote
    public List<Train> fetchData (String departure, String arrival, int limit){
        try{
            String limitStr = limit>=1?"&limit="+ limit:"";
            String url =  "https://rata.digitraffic.fi/api/v1/live-trains/station/"+departure.toUpperCase()+"/"+ arrival.toUpperCase()+"?departure_date="+ this.dateStr +"&include_nonstopping=false"+limitStr;
            //System.out.println(url);
            ResponseEntity<List<Train>> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {
            });
            this.trains = response.getBody();

            this.cleanTableRows(departure,arrival);
            for(Train train : this.trains){
                //hardcoded HKI for calculating the distance , could be update to a dynamic variable
                Station oringinDepartureStation = stationService.getStationByCode("HKI");
                for(int i =0; i<train.getTimeTableRows().size();i++){
                    //
                    Station arrivalStation = stationService.getStationByCode(train.getTimeTableRows().get(i).getStationShortCode());
                    int distance = calculateDistance(oringinDepartureStation, arrivalStation);
                    train.getTimeTableRows().get(i).setDistance(distance);

                }
            }
            // filter the train which arrival destination in next day
            filterLongDistanceNextDayTrain(departure, this.dateStr);

        }catch (Exception e) {

            e.printStackTrace();
        }

        return this.trains;
    }

    public  List<Station> getAllStatioins(){
        return this.stations;

    }

    private void filterLongDistanceNextDayTrain(String  departure, String dateStr){

        List<Integer> deleteTrainNumber = new ArrayList<>();

        for (Train train : this.trains){
            List<TimeTableRow> timeTableRow = train.getTimeTableRows();

            int indexDeparture = IntStream.range(0, timeTableRow.size())
                    .filter(k -> {
                        if(timeTableRow.get(k).getType().equals("DEPARTURE") && departure.equals(timeTableRow.get(k).getStationShortCode())){
                            String timestamp = timeTableRow.get(k).getActualTime() !="" ? timeTableRow.get(k).getActualTime(): timeTableRow.get(k).getScheduledTime();
                            String [] strDate = timestamp.split("T");
                            return !dateStr.equals(strDate[0]);
                        }
                        return false ;
                    })
                    .findFirst().orElse(-1);
            if(indexDeparture>-1){
                deleteTrainNumber.add(train.getTrainNumber());
            }
        }

        for(Integer i : deleteTrainNumber)
            this.trains.removeIf(t->{
                return deleteTrainNumber.stream().filter(n->n==t.getTrainNumber()).findFirst().isPresent();
            });
    }

    //clean the stop before the departure and after the destination
    private void cleanTableRows(String departure, String arrival){

        for (Train train : this.trains) {

            List<TimeTableRow> timeTableRow = train.getTimeTableRows();

            int indexDeparture = IntStream.range(0, timeTableRow.size())
                    .filter(k -> departure.equals(timeTableRow.get(k).getStationShortCode()))
                    .findFirst().orElse(0);

            int indexArrival = IntStream.range(0, timeTableRow.size())
                    .filter(k -> arrival.equals(timeTableRow.get(k).getStationShortCode()))
                    .findFirst().orElse(0);
            List<TimeTableRow> cutTableRows = timeTableRow.subList(indexDeparture, indexArrival + 1);
            List<TimeTableRow> filteredTimeTableRow  =  cutTableRows.stream().filter(r-> r.getCommercialStop().equals(true) && !Objects.equals(r.getCommercialTrack(), "")).collect(Collectors.toList());
            train.setCleanedTimeTableRows(filteredTimeTableRow);
        }



    }
}



