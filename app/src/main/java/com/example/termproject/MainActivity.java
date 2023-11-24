package com.example.termproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    String api = "097176059f7a6fe3934a296004c7abcf";
    String city = "Ulsan";
    TextView city_view, temp_view, max_view, wind_view, humidity_view, cloud_view, min_view, description_view;
    private static final int REQUEST_CODE = 1; // 요청 코드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        city_view = findViewById(R.id.title);
        temp_view = findViewById(R.id.temp);
        max_view = findViewById(R.id.max);
        wind_view = findViewById(R.id.wind);
        humidity_view = findViewById(R.id.humidity);
        cloud_view = findViewById(R.id.cloud);
        min_view = findViewById(R.id.min);
        description_view = findViewById(R.id.description);

        TextView date_view = findViewById(R.id.date);
        TextView week_view = findViewById(R.id.week);
        ConstraintLayout top = findViewById(R.id.top);

        ImageView setting = findViewById(R.id.setting);

        String title = city + "의 날씨";
        city_view.setText(title);
        date_view.setText(getCurrentDate());
        switch (getCurrentWeek()){
            case 1:
                week_view.setText("일요일");
                break;
            case 2:
                week_view.setText("월요일");
                break;
            case 3:
                week_view.setText("화요일");
                break;
            case 4:
                week_view.setText("수요일");
                break;
            case 5:
                week_view.setText("목요일");
                break;
            case 6:
                week_view.setText("금요일");
                break;
            case 7:
                week_view.setText("토요일");
                break;
        }

        String currentTime = getCurrentTime();
        int time = Integer.parseInt(currentTime);
        if(time<18 && time >=9){
            top.setBackgroundResource(R.drawable.afternoon);
            description_view.setTextColor(Color.parseColor("#99ccff"));
            date_view.setTextColor(Color.parseColor("#99ccff"));
            week_view.setTextColor(Color.parseColor("#99ccff"));
            temp_view.setTextColor(Color.parseColor("#99ccff"));
            max_view.setTextColor(Color.parseColor("#99ccff"));
            min_view.setTextColor(Color.parseColor("#99ccff"));
            wind_view.setTextColor(Color.parseColor("#99ccff"));
            humidity_view.setTextColor(Color.parseColor("#99ccff"));
            cloud_view.setTextColor(Color.parseColor("#99ccff"));

        }
        else if(time>=21 || time < 6){
            top.setBackgroundResource(R.drawable.night);
            description_view.setTextColor(Color.parseColor("#000080"));
            date_view.setTextColor(Color.parseColor("#000080"));
            week_view.setTextColor(Color.parseColor("#000080"));
            temp_view.setTextColor(Color.parseColor("#000080"));
            max_view.setTextColor(Color.parseColor("#000080"));
            min_view.setTextColor(Color.parseColor("#000080"));
            wind_view.setTextColor(Color.parseColor("#000080"));
            humidity_view.setTextColor(Color.parseColor("#000080"));
            cloud_view.setTextColor(Color.parseColor("#000080"));
        }
        else{
            top.setBackgroundResource(R.drawable.dawn);
            description_view.setTextColor(Color.parseColor("#FF9500"));
            date_view.setTextColor(Color.parseColor("#FF9500"));
            week_view.setTextColor(Color.parseColor("#FF9500"));
            temp_view.setTextColor(Color.parseColor("#FF9500"));
            max_view.setTextColor(Color.parseColor("#FF9500"));
            min_view.setTextColor(Color.parseColor("#FF9500"));
            wind_view.setTextColor(Color.parseColor("#FF9500"));
            humidity_view.setTextColor(Color.parseColor("#FF9500"));
            cloud_view.setTextColor(Color.parseColor("#FF9500"));
        }

        loadWeatherInfo(city);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private void loadImage(String imageUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imageUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    // UI 업데이트는 runOnUiThread를 사용하여 메인 스레드에서 처리
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 이미지뷰에 비트맵 설정
                            ImageView image = findViewById(R.id.weather);
                            image.setImageBitmap(bitmap);
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String getCurrentDate() {
        Date dateNow = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("M월 dd일", Locale.getDefault());  // 2월 15일
        // "M월 dd일" 에는 원하는 형식을 넣어주면 됩니다.

        return format.format(dateNow);
    }
    public static int getCurrentWeek() {
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        int dayOfWeekNumber = calendar.get(Calendar.DAY_OF_WEEK);

        return dayOfWeekNumber;
    }

    public static String getCurrentTime() {
        long now = System.currentTimeMillis();
        Date currentTime = new Date(now);
        DateFormat dateFormat = new SimpleDateFormat("HH");	// 09
        //"HH:mm" 에는 원하는 형식을 넣어주면 됩니다.

        return dateFormat.format(currentTime);
    }

    // SettingActivity에서 반환된 데이터 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String updatedCity = data.getStringExtra("key"); // 데이터 가져오기
                city = updatedCity; // MainActivity에서 받은 데이터로 city 변수 업데이트
                // 업데이트된 도시에 대한 정보를 가져오는 API 호출 등의 로직 수행
                loadWeatherInfo(city);
                updateUI(city);
            }
        }
    }

    private void loadWeatherInfo(String city) {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")    // baseUrl 등록
                .addConverterFactory(GsonConverterFactory.create())  // Gson 변환기 등록
                .build();

        Service service = retrofit.create(Service.class);

        Call<JsonObject> call = service.currentWeather(city, api);
        call.enqueue(new Callback<JsonObject>() {
            AlertDialog.Builder menu = new AlertDialog.Builder(MainActivity.this);

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject mainObject = response.body().getAsJsonObject("main");
                    double temp = mainObject.get("temp").getAsDouble() - 275.15; // "temp" 값을 받아서 변환한 후 계산
                    String temp_format = "현재 "+String.format("%.2f", temp)+"°C"; // 두 번째 소수 자리까지 포맷팅
                    temp_view.setText(temp_format); // TextView에 출력

                    double temp_max = mainObject.get("temp_max").getAsDouble() - 275.15; // "temp" 값을 받아서 변환한 후 계산
                    String temp_max_format = "최고 "+String.format("%.2f", temp_max)+"°C"; // 두 번째 소수 자리까지 포맷팅
                    max_view.setText(temp_max_format); // TextView에 출력

                    double temp_min = mainObject.get("temp_min").getAsDouble() - 275.15; // "temp" 값을 받아서 변환한 후 계산
                    String temp_min_format = " / 최저 "+String.format("%.2f", temp_min)+"°C"; // 두 번째 소수 자리까지 포맷팅
                    min_view.setText(temp_min_format); // TextView에 출력

                    JsonObject windObject = response.body().getAsJsonObject("wind");
                    double wind = windObject.get("speed").getAsDouble();
                    String wind_format = "바람 "+String.format("%.1f", wind)+"m/s";
                    wind_view.setText(wind_format);

                    double humidity = mainObject.get("humidity").getAsDouble();
                    String humidity_format = " / 습도 "+String.format("%.0f", humidity)+"%";
                    humidity_view.setText(humidity_format);

                    JsonObject cloudObject = response.body().getAsJsonObject("clouds");
                    double cloud = cloudObject.get("all").getAsDouble();
                    String cloud_format = " / 구름 "+String.format("%.0f", cloud)+"%";
                    cloud_view.setText(cloud_format);


                    JsonArray weatherArray = response.body().getAsJsonArray("weather"); // "weather" 키에 해당하는 JsonArray 추출
                    JsonObject weatherObject = weatherArray.get(0).getAsJsonObject(); // 첫 번째 요소 추출
                    String description = weatherObject.get("description").getAsString();
                    description_view.setText(description);
                    String icon = weatherObject.get("icon").getAsString(); // "icon" 키의 값을 가져옴

                    // 이미지 URL을 기반으로 이미지를 가져와서 ImageView에 표시
                    String imageUrl = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
                    loadImage(imageUrl);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                menu.setTitle("API 호출 실패");
                AlertDialog alertDialog = menu.create();
                alertDialog.show();
            }
        });
    }

    private void updateUI(String city) {
        // 여기에서 title 등의 UI 업데이트를 수행하세요.
        city_view = findViewById(R.id.title);
        String title = city + "의 날씨";
        city_view.setText(title);
        // 나머지 UI 업데이트 코드를 추가하세요.
    }
}