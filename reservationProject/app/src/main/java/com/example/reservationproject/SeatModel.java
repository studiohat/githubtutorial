package com.example.reservationproject;

import android.support.annotation.NonNull;

// db에 저장될 예약 좌석의 모델입니다.
public class SeatModel implements Comparable<SeatModel> {
    private String documentId;
    private String reserve;
    private String seat;
    private String user;
    private String reserve_time;
    private String now_time;
    private String start_time;
    private String rest;

    public SeatModel() {
    }

    public SeatModel(String documentId, String reserve, String seat, String user, String reserve_time, String now_time, String start_time, String rest) {
        this.documentId = documentId;
        this.reserve = reserve;
        this.seat = seat;
        this.user = user;
        this.reserve_time = reserve_time;
        this.now_time = now_time;
        this.start_time = start_time;
        this.rest = rest;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReserve_time() {
        return reserve_time;
    }

    public void setReserve_time(String reserve_time) {
        this.reserve_time = reserve_time;
    }

    public String getNow_time() {
        return now_time;
    }

    public void setNow_time(String now_time) {
        this.now_time = now_time;
    }

    public void setStart_time(String start_time) { this.start_time = start_time;}

    public String getStart_time() { return  start_time;}

    public void setRest(String rest) {this.rest = rest;}
    public String getRest() { return rest;}

    @Override
    public String toString() {
        return "SeatModel{" +
                "documentId='" + documentId + '\'' +
                ", reserve='" + reserve + '\'' +
                ", seat='" + seat + '\'' +
                ", user='" + user + '\'' +
                ", reserve_time='" + reserve_time + '\'' +
                ", now_time='" + now_time + '\'' +
                ", start_time='" + start_time + '\'' +
                ", rest='" + rest + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull SeatModel o) {
        return seat.compareTo(o.getSeat());
    }
}
