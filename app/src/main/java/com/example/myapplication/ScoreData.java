package com.example.myapplication;

public class ScoreData {
    public String category;
    public  int result;
    public int total;
    ScoreData(){

    }
    public ScoreData(String category, int result, int total) {
        this.category = category;
        this.result = result;
        this.total = total;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
