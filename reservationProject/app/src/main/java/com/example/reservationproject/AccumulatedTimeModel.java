package com.example.reservationproject;

public class AccumulatedTimeModel {
    private String userId;
    private String documentId;
    private String accumulatedTime;

    public AccumulatedTimeModel() {
    }

    public AccumulatedTimeModel(String userId, String documentId, String accumulatedTime) {
        this.userId = userId;
        this.documentId = documentId;
        this.accumulatedTime = accumulatedTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getAccumulatedTime() {
        return accumulatedTime;
    }

    public void setAccumulatedTime(String accumulatedTime) {
        this.accumulatedTime = accumulatedTime;
    }

    @Override
    public String toString() {
        return "AccumulatedTimeModel{" +
                "userId='" + userId + '\'' +
                ", documentId='" + documentId + '\'' +
                ", accumulatedTime='" + accumulatedTime + '\'' +
                '}';
    }
}
