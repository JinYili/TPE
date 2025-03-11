package org.example.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectReader;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties
public class Train {

    private int trainNumber;
    private String departureDate;
    private int operatorUICCode;
    private String operatorShortCode;
    private String trainType;
    private String trainCategory;
    private String commuterLineID;
    private boolean runningCurrently;
    private boolean cancelled;
    private long version;
    private String timetableType;
    private String timetableAcceptanceDate;
    @JsonProperty("timeTableRows")
    private List<TimeTableRow> timeTableRow;
    private String direction="Forward";
    private Train() {
    }

    public int getTrainNumber() {
        return trainNumber;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public int getOperatorUICCode() {
        return operatorUICCode;
    }

    public String getOperatorShortCode() {
        return operatorShortCode;
    }

    public String getTrainType() {
        return trainType;
    }

    public String getTrainCategory() {
        return trainCategory;
    }

    public String getCommuterLineID() {
        return commuterLineID;
    }

    public Boolean getRunningCurrently() {
        return runningCurrently;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public long getVersion() {
        return version;
    }

    public String getTimetableType() {
        return timetableType;
    }

    public String getTimetableAcceptanceDate() {
        return timetableAcceptanceDate;
    }

    public List<TimeTableRow> getTimeTableRows() {

        return timeTableRow;
    }

    public void setTrainNumber(int trainNumber) {

        this.trainNumber = trainNumber;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public void setOperatorUICCode(int operatorUICCode) {
        this.operatorUICCode = operatorUICCode;
    }

    public void setOperatorShortCode(String operatorShortCode) {
        this.operatorShortCode = operatorShortCode;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public void setTrainCategory(String trainCategory) {
        this.trainCategory = trainCategory;
    }

    public void setCommuterLineID(String commuterLineID) {
        this.commuterLineID = commuterLineID;
    }

    public void setRunningCurrently(Boolean runningCurrently) {
        this.runningCurrently = runningCurrently;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void setTimetableType(String timetableType) {
        this.timetableType = timetableType;
    }

    public void setTimetableAcceptanceDate(String timetableAcceptanceDate) {
        this.timetableAcceptanceDate = timetableAcceptanceDate;
    }

    public void setCleanedTimeTableRows(List<TimeTableRow> timeTableRows) {

        this.timeTableRow =  timeTableRows;

    }

    public void setTimeTableRows(JsonNode timeTableRows) {
        this.timeTableRow =  deserializeTimeTableRow(timeTableRows);
    }

    private List<TimeTableRow> deserializeTimeTableRow(JsonNode timeTableRows){
        List<TimeTableRow> rows = new ArrayList<>();
        for(int i=0; i<timeTableRows.size();i++)
        {
            TimeTableRow row = new TimeTableRow(timeTableRows.get(i));
            rows.add(row);
        }
        return rows;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
