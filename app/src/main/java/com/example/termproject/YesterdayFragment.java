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

public class YesterdayFragment extends Fragment {

    private ArrayList<String> images;
    private ArrayList<String> titles;
    private ArrayList<String> traffics;

    private ArrayList<String> links;
    private ArrayList<String> links_array;
    String todayDate;
    String yesterdayDate;
    SimpleDateFormat dateFormat;
    Calendar calendar;
    Date yesterday;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String country = "KR";
        new FetchTrendsTask().execute(country);

        images = new ArrayList<>();
        titles = new ArrayList<>();
        traffics = new ArrayList<>();
        links = new ArrayList<>();
        links_array = new ArrayList<>();

        return inflater.inflate(R.layout.fragment_yesterday, container, false);
    }

    private class FetchTrendsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String country = params[0];
            return fetchXml(country);
        }

        @Override
        protected void onPostExecute(String xmlDocument) {
            if (xmlDocument != null) {
                // Parsing XML data
                retrieveTrends(xmlDocument);
                Log.d("hello", xmlDocument);
                setupRecyclerView();
            }
        }
    }

    private String fetchXml(String country) {
        String urlStr = "https://trends.google.com/trends/trendingsearches/daily/rss?geo=" + country;
        HttpURLConnection connection = null;
        try {
            URL fetchUrl = new URL(urlStr);
            connection = (HttpURLConnection) fetchUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } else {
                return "Failed to fetch XML. Response code: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Exception: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void retrieveTrends(String xmlDocument) {
        //title 제목
        //ht:picture 이미지
        //ht:news_item_url 뉴스 링크
        //ht:news_item_snippet뉴스 부제목
        //ht:approx_traffic 조횟수

        Pattern titlePattern = Pattern.compile("<title.*?>(.*?)</title>");
        Pattern trafficPattern = Pattern.compile("<ht:approx_traffic>(.*?)</ht:approx_traffic>");
        Pattern imagePattern = Pattern.compile("<ht:picture>(.*?)</ht:picture>");
        Pattern linkPattern = Pattern.compile("<ht:news_item_url>(.*?)</ht:news_item_url>");
        Pattern datePattern = Pattern.compile("\\d{2} [A-Z][a-z]{2} \\d{4}");

        Matcher titleMatcher = titlePattern.matcher(xmlDocument);
        Matcher trafficMatcher = trafficPattern.matcher(xmlDocument);
        Matcher imageMatcher = imagePattern.matcher(xmlDocument);
        Matcher linkMatcher = linkPattern.matcher(xmlDocument);
        Matcher datematcher = datePattern.matcher(xmlDocument);

        dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // 한국 시간대로 설정

        // 현재 날짜 구하기
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // 하루를 뺀 날짜 계산
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        yesterday = calendar.getTime();
        yesterdayDate = dateFormat.format(yesterday);

        while(titleMatcher.find()){
            String title = titleMatcher.group(1);
            if(title.contains("Daily Search Trends")){
                break;
            }
        }

        while(titleMatcher.find() && trafficMatcher.find() && datematcher.find() && imageMatcher.find()){
            String date = datematcher.group();
            String title = titleMatcher.group(1);
            String traffic = trafficMatcher.group(1).replaceAll("[+,]", "");
            String image = imageMatcher.group(1);

            if(date.equals(yesterdayDate)){
                titles.add(title);
                traffics.add(traffic);
                images.add(image);
                Log.d("date123", title);
                Log.d("date123", traffic);
            }
        }

        for(int i = 0; i< titles.size()*2; i++){
            if(linkMatcher.find()){
                String link = linkMatcher.group(1);
                links.add(link.toString());
            }
        }


        for(int i = 0; i< links.size(); i++){
            if(i == 0 || i%2==0){
                String link = links.get(i);
                links_array.add(link);
                Log.d("date12", link);
            }
        }
    }

    private void setupRecyclerView() {
        ArrayList<DataClass> data = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            Integer rank = i+1;
            String ranking = rank.toString()+"위";
            data.add(new DataClass(ranking, titles.get(i), traffics.get(i), images.get(i), links_array.get(i)));
        }

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerview);
        Adapter adapter = new Adapter(data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }
}
