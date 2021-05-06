package edu.psu.sxa5362.sudoku;

public class Result {
    private String uid;
    private long time;

    public Result() {}

    public Result(String uid, long time){
        this.uid = uid;
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public long getTime() {
        return time;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
