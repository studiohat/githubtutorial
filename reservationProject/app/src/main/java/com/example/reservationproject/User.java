package com.example.reservationproject;

// 유저의 모델입니다. 이 정보를 바탕으로 db를 이용
public class User {
    private String id;
    private String pw;
    private String email;
    private String name;
    private String documentId;

    private String[] bookMarks;

    public User() {
    }

    public User(String id, String pw, String email, String name, String documentId, String[] bookMarks) {
        this.id = id;
        this.pw = pw;
        this.email = email;
        this.name = name;
        this.documentId = documentId;
        this.bookMarks = bookMarks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String[] getBookMarks() {
        return bookMarks;
    }

    public void setBookMarks(String[] bookMarks) {
        this.bookMarks = bookMarks;
    }

    public void setBookMark(String bookMark, int i) {
        this.bookMarks[i] = bookMark;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", documentId='" + documentId + '\'' +
                '}';
    }
}
