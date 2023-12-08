package com.example.termproject;

// 데이터를 담는 클래스 정의
public class DataClass {

    String ranking; // 순위를 저장하는 변수
    String title; // 제목을 저장하는 변수
    String traffic; // 트래픽을 저장하는 변수
    String url; // URL을 저장하는 변수
    String link; // 링크를 저장하는 변수

    // 생성자: 데이터를 초기화하는 메서드
    public DataClass(String ranking, String title, String traffic, String url, String link) {
        this.ranking = ranking;
        this.title = title;
        this.traffic = traffic;
        this.url = url;
        this.link = link;
    }

    // 순위 반환 메서드
    public String getRanking() {
        return ranking;
    }

    // 제목 반환 메서드
    public String getTitle() {
        return title;
    }

    // 트래픽 반환 메서드
    public String getTraffic() {
        return traffic;
    }

    // URL 반환 메서드
    public String getUrl() {
        return url;
    }

    // 링크 반환 메서드
    public String getLink() {
        return link;
    }

    // 순위 설정 메서드
    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    // 제목 설정 메서드
    public void setTitle(String title) {
        this.title = title;
    }

    // 트래픽 설정 메서드
    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    // URL 설정 메서드
    public void setUrl(String url) {
        this.url = url;
    }

    // 링크 설정 메서드
    public void setLink(String link) {
        this.link = link;
    }
}
