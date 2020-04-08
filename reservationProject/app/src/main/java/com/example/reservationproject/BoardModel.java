package com.example.reservationproject;

import android.support.annotation.NonNull;

// 게시판 모델입니다. 이 정보를 바탕으로 db 이용
public class BoardModel implements Comparable<BoardModel>{

    private String id;
    private String title;
    private String contents;
    private String name;
    private String time;

    public BoardModel() {
    }

    public BoardModel(String id, String title, String contents, String name, String time) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.name = name;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "BoardModel{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull BoardModel o) {
        return time.compareTo(o.getTime()) * -1;
    }
}
