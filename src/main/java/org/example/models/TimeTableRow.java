package org.example.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties
public class TimeTableRow {
    public TimeTableRow() {

    }

    public TimeTableRow(String stationShortCode, int stationUICCode, String countryCode, String type, boolean trainStopping, boolean commercialStop, String commercialTrack, boolean cancelled, String scheduledTime, String actualTime, int differenceInMinutes , String estimateSource, String liveEstimateTime) {
        this.stationShortCode = stationShortCode;
        this.stationUICCode = stationUICCode;
        this.countryCode = countryCode;
        this.type = type;
        this.trainStopping = trainStopping;
        this.commercialStop = commercialStop;
        this.commercialTrack = commercialTrack;
        this.cancelled = cancelled;
        this.scheduledTime = scheduledTime;
        this.actualTime = actualTime;
        this.differenceInMinutes = differenceInMinutes;
        this.estimateSource = estimateSource;
        this.liveEstimateTime = liveEstimateTime;
    }

    public TimeTableRow(JsonNode node) {

        this.stationShortCode =  node.get("stationShortCode").asText("");
        this.stationUICCode = node.get("stationUICCode").asInt(0);
        this.countryCode =  node.get("countryCode").asText("");
        this.type =   node.has("type") ?  node.get("type").asText(""):"";

        this.trainStopping = node.get("trainStopping").asBoolean(false);
        this.commercialStop = node.has("commercialStop") && node.get("commercialStop").asBoolean();
        this.cancelled = node.get("cancelled").asBoolean();

        this.commercialTrack =  node.get("commercialTrack").asText("");
        this.scheduledTime = node.get("scheduledTime").asText("");
        this.actualTime = node.has("actualTime") ?  node.get("actualTime").asText(""):"";

        this.differenceInMinutes = node.has("differenceInMinutes")? node.get("differenceInMinutes").asInt(0):0;
        this.trainReady = node.has("trainReady") ?  new TrainReady(node.get("trainReady")) :null;

        this.liveEstimateTime = node.has("liveEstimateTime")?  node.get("liveEstimateTime").asText(""):"";
        // this.causes = node.has("causes") ? createCuaseList( node.get("causes")): Collections.emptyList();
    }

    private String stationShortCode;
    private int stationUICCode;
    private String countryCode;
    private String type;
    private boolean trainStopping;
    private boolean commercialStop;
    private String commercialTrack;
    private boolean cancelled;
    private String scheduledTime;
    private String actualTime;
    private int differenceInMinutes;
    private String stationName;
    private int distance;
    private String estimateSource;
    private String liveEstimateTime;
  //  @JsonProperty("trainReady")
    private TrainReady trainReady;

    public String getStationShortCode() {
        return stationShortCode;
    }

    public int getStationUICCode() {
        return stationUICCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getType() {
        return type;
    }

    public Boolean getTrainStopping() {
        return trainStopping;
    }

    public Boolean getCommercialStop() {
        return commercialStop;
    }

    public String getCommercialTrack() {
        return commercialTrack;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public String getActualTime() {
        return actualTime;
    }

    public int getDifferenceInMinutes() {
        return differenceInMinutes;
    }

    public String getStationName() {
        return stationName;
    }


    public TrainReady getTrainReady() {
        return trainReady;
    }
    public void setStationShortCode(String stationShortCode) {
        this.stationShortCode = stationShortCode.toUpperCase();
    }

    public void setStationUICCode(int stationUICCode) {
        this.stationUICCode = stationUICCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTrainStopping(Boolean trainStopping) {
        this.trainStopping = trainStopping;
    }

    public void setCommercialStop(Boolean commercialStop) {
        this.commercialStop = commercialStop;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public void setCommercialTrack(String commercialTrack) {
        this.commercialTrack = commercialTrack;
    }

    public void setActualTime(String actualTime) {
        this.actualTime = actualTime;
    }

    public void setDifferenceInMinutes(int differenceInMinutes) {
        this.differenceInMinutes = differenceInMinutes;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public void setTrainReady(TrainReady trainReady) {
        this.trainReady = trainReady;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getEstimateSource() {
        return estimateSource == null ? "":estimateSource;
    }

    public void setEstimateSource(String estimateSource) {
        this.estimateSource = estimateSource;
    }

    public String getLiveEstimateTime() {
        return liveEstimateTime;
    }

    public void setLiveEstimateTime(String liveEstimateTime) {
        this.liveEstimateTime = liveEstimateTime;
    }
}

