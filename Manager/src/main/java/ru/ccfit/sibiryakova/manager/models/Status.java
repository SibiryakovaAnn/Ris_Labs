package ru.ccfit.sibiryakova.manager.models;

import java.util.List;

public class Status {
    private StatusEnum status;
    private List<String> data;
    private int responsesCount;
    private int totalWorkers;

    public Status(StatusEnum status, List<String> data, int responsesCount, int totalWorkers) {
        this.status = status;
        this.data = data;
        this.responsesCount = responsesCount;
        this.totalWorkers = totalWorkers;
    }

    // getters and setters
    public StatusEnum getStatus() { return status; }
    public void setStatus(StatusEnum status) { this.status = status; }

    public List<String> getData() { return data; }
    public void setData(List<String> data) { this.data = data; }

    public int getResponsesCount() { return responsesCount; }
    public void setResponsesCount(int responsesCount) { this.responsesCount = responsesCount; }

    public int getTotalWorkers() { return totalWorkers; }
    public void setTotalWorkers(int totalWorkers) { this.totalWorkers = totalWorkers; }
}