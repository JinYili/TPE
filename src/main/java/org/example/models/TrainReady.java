package org.example.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties
public class TrainReady {
    public TrainReady() {
    }

    private String source;
    private boolean accepted;
    private String timestamp;

    public TrainReady( JsonNode node) {
        this.source = node.get("source").asText();
        this.accepted = node.get("accepted").asBoolean();
        this.timestamp = node.get("timestamp").asText();

    }


    public String getSource() {
        return source;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public TrainReady(String source, boolean accepted, String timestamp) {
        this.source = source;
        this.accepted = accepted;
        this.timestamp = timestamp;
    }
}
