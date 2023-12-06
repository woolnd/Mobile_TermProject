package com.example.termproject;


public class DataClass {

    String ranking;
    String title;
    String traffic;
    String url;
    String link;


    public DataClass(String ranking, String title, String traffic, String url, String link) {
        this.ranking = ranking;
        this.title = title;
        this.traffic = traffic;
        this.url= url;
        this.link = link;
    }

    public String getRanking() {
        return ranking;
    }
    public String getTitle() {
        return title;
    }
    public String getTraffic() {
        return traffic;
    }

    public String getUrl() {
        return url;
    }

    public String getLink() {
        return link;
    }
    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLink(String link) {
        this.link = link;
    }
}