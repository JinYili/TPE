package org.example.services;

import org.example.models.Station;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.List;

public class StationService {

    private final List<Station> stations;
    public StationService() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept-Encoding","gzip;q=0,deflate,sdch");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        String url =  "https://rata.digitraffic.fi/api/v1/metadata/stations";

        ResponseEntity<List<Station>> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {
        });
        this.stations = response.getBody();
    }

    public List<Station> getAllStations (){
            return this.stations;
    }


     public Station getStationByCode (String code){
        for( Station s : this.stations){
            if(s.getStationShortCode().equals(code))
               return s;
        }
        return null;
    }

}
