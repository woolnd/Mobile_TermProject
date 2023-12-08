package com.example.termproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TodayFragment extends Fragment {

    private ArrayList<String> images;
    private ArrayList<String> titles;
    private ArrayList<String> traffics;

    private ArrayList<String> links;
    private ArrayList<String> links_array;
    String todayDate;
    SimpleDateFormat dateFormat;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String country = "KR";

        // FetchTrendsTask를 실행하여 Google Trends 데이터 가져오기
        new FetchTrendsTask().execute(country);

        //저장할 배열들 초기화
        images = new ArrayList<>();
        titles = new ArrayList<>();
        traffics = new ArrayList<>();
        links = new ArrayList<>();
        links_array = new ArrayList<>();

        return inflater.inflate(R.layout.fragment_today, container, false);
    }

    // AsyncTask를 상속받은 내부 클래스
    private class FetchTrendsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String country = params[0];
            // Google Trends 데이터 가져오는 메서드
            return fetchXml(country);
        }

        @Override
        protected void onPostExecute(String xmlDocument) {
            if (xmlDocument != null) {
                // // XML 데이터 파싱 후 RecyclerView 설정
                retrieveTrends(xmlDocument);
                setupRecyclerView();
            }
        }
    }

    // URL에서 XML 데이터 가져오는 메서드
    private String fetchXml(String country) {
        String urlStr = "https://trends.google.com/trends/trendingsearches/daily/rss?geo=" + country; //가져올 XML 데이터의 URL을 구성
        HttpURLConnection connection = null;
        try {
            URL fetchUrl = new URL(urlStr); // 위에서 생성한 URL 문자열을 기반으로 URL 객체를 생성
            connection = (HttpURLConnection) fetchUrl.openConnection(); //URLConnection을 통해 HTTP 연결을 열고, 해당 URL과의 연결을 설정
            connection.setRequestMethod("GET"); //HTTP GET 요청을 설정

            int responseCode = connection.getResponseCode(); // 서버로부터의 HTTP 응답 코드를 받음
            if (responseCode == HttpURLConnection.HTTP_OK) { // 서버로부터 받은 응답 코드가 HTTP_OK(200)인 경우
                InputStream inputStream = connection.getInputStream(); //데이터를 읽어옴
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); // InputStream을 BufferedReader로 감싸 데이터를 읽기 위한 준비
                StringBuilder response = new StringBuilder(); // 서버로부터 데이터를 읽어오기 위한 InputStream을 생성
                String line;
                while ((line = reader.readLine()) != null) { // BufferedReader를 사용하여 서버로부터 읽은 데이터를 한 줄씩 읽어 StringBuilder에 추가
                    response.append(line);
                }
                return response.toString(); // 읽은 XML 데이터를 String 형태로 반환
            } else {
                return "Failed to fetch XML. Response code: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Exception: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect(); //작업이 끝난 후에는 HTTP 연결을 닫음
            }
        }
    }

    private void retrieveTrends(String xmlDocument) {
        //title 제목
        //ht:picture 이미지
        //ht:news_item_url 뉴스 링크
        //ht:approx_traffic 조횟수

        //정규 표현식 (Regular Expressions) 패턴 선언
        Pattern titlePattern = Pattern.compile("<title.*?>(.*?)</title>");
        Pattern trafficPattern = Pattern.compile("<ht:approx_traffic>(.*?)</ht:approx_traffic>");
        Pattern imagePattern = Pattern.compile("<ht:picture>(.*?)</ht:picture>");
        Pattern linkPattern = Pattern.compile("<ht:news_item_url>(.*?)</ht:news_item_url>");
        Pattern datePattern = Pattern.compile("\\d{2} [A-Z][a-z]{2} \\d{4}");

        //위에서 정의한 패턴을 사용하여 XML 문서에서 각각의 정보를 찾기 위한 Matcher를 생성
        Matcher titleMatcher = titlePattern.matcher(xmlDocument);
        Matcher trafficMatcher = trafficPattern.matcher(xmlDocument);
        Matcher imageMatcher = imagePattern.matcher(xmlDocument);
        Matcher linkMatcher = linkPattern.matcher(xmlDocument);
        Matcher datematcher = datePattern.matcher(xmlDocument);

        //객체를 생성하여 한국 시간대로 설정하고, 오늘의 날짜를 todayDate 변수에 저장
        dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH); //9일 Dec 2023
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // 한국 시간대로 설정
        todayDate = dateFormat.format(new Date());

        //XML 문서를 탐색하면서 <title> 태그를 찾음
        while(titleMatcher.find()){
            String title = titleMatcher.group(1);
            //"Daily Search Trends"라는 문자열을 포함하는 <title> 태그를 찾으면 반복문을 종료
            if(title.contains("Daily Search Trends")){
                break;
            }
        }

        //XML 문서에서 제목, 조횟수, 이미지, 날짜 태그를 차례로 찾음
        while(titleMatcher.find() && trafficMatcher.find() && datematcher.find() && imageMatcher.find()){
            String date = datematcher.group();
            String title = titleMatcher.group(1);
            String traffic = trafficMatcher.group(1).replaceAll("[+,]", "");
            String image = imageMatcher.group(1);

            //앱에서 가져온 날짜와 xml속 날짜가 동일 시 배열에 추가
            if(date.equals(todayDate)){
                titles.add(title);
                traffics.add(traffic);
                images.add(image);
            }
        }

        //링크는 하나의 타이틀에 2개임으로 타이틀 사이즈의 두 배만큼 뉴스 링크를 찾음
        for(int i = 0; i< titles.size()*2; i++){
            if(linkMatcher.find()){
                String link = linkMatcher.group(1);
                links.add(link.toString());
            }
        }

        //헤더기사는 두개의 링크중 첫번째에 위치이며 찾은 링크 중 홀수 번째 링크를 배열에 추가
        for(int i = 0; i< links.size(); i++){
            if(i == 0 || i%2==0){
                String link = links.get(i);
                links_array.add(link);
            }
        }
    }

    private void setupRecyclerView() {
        ArrayList<DataClass> data = new ArrayList<>();
        //titles의 크기만큼 반복하면서 각각의 항목에 대한 정보를 DataClass 객체로 만듬
        for (int i = 0; i < titles.size(); i++) {
            Integer rank = i+1;
            //순위를 문자열로 변환하고 "위"라는 문자열을 붙여 순위를 나타냄
            String ranking = rank.toString()+"위";

            //순위, 제목, 조횟수, 이미지 URL, 뉴스 링크를 가진 DataClass 객체를 생성하고 data 배열에 추가
            data.add(new DataClass(ranking, titles.get(i), traffics.get(i), images.get(i), links_array.get(i)));
        }

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerview);
        Adapter adapter = new Adapter(data); //데이터가 담긴 Adapter 객체를 생성
        recyclerView.setAdapter(adapter); //RecyclerView에 Adapter를 설정하여 데이터를 표시
        // RecyclerView의 레이아웃 매니저를 설정합, 여기서는 세로 방향으로 스크롤되는 LinearLayoutManager를 사용
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }
}
