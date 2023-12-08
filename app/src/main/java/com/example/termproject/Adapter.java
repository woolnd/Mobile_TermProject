package com.example.termproject;

import static com.example.termproject.MainActivity.getCurrentTime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewholder> {

    private ArrayList<DataClass> Data_arrayList; // 데이터 클래스의 ArrayList

    // Adapter 생성자: 데이터 리스트를 받아옴
    public Adapter(ArrayList<DataClass> arrayList) {
        this.Data_arrayList = arrayList;
    }

    @NonNull
    @Override
    public AdapterViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰 홀더를 생성하고 연결할 레이아웃 설정
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_headline, parent, false);
        AdapterViewholder holder = new AdapterViewholder(view);

        // 링크 이미지를 클릭했을 때의 동작 정의
        ImageView link = view.findViewById(R.id.link);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = "";
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // 데이터 배열에서 해당 포지션의 링크를 가져와 Intent에 추가하여 NewsActivity 시작
                    data = Data_arrayList.get(position).getLink();
                    Intent intent = new Intent(view.getContext(), NewsActivity.class);
                    intent.putExtra("link", data);
                    view.getContext().startActivity(intent);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewholder holder, int position) {
        // 현재 시간 가져오기
        String currentTime = getCurrentTime();
        int time = Integer.parseInt(currentTime);

        // 시간에 따라 이미지 및 글자색 변경
        if (time < 18 && time >= 9) {
            holder.link.setImageResource(R.drawable.link_afternoon);
            holder.card.setImageResource(R.drawable.card_afternoon);
        } else if (time >= 21 || time < 6) {
            holder.link.setImageResource(R.drawable.link_night);
            holder.card.setImageResource(R.drawable.card_night);
        } else {
            holder.link.setImageResource(R.drawable.link_dawn);
            holder.card.setImageResource(R.drawable.card_dawn);
        }

        loadImage(Data_arrayList.get(position).getUrl(), holder.imageView); // 이미지 로딩
        holder.title.setText(Data_arrayList.get(position).getTitle()); // 제목 설정
        holder.traffic.setText(Data_arrayList.get(position).getTraffic()); // 트래픽 설정
        holder.rank.setText(Data_arrayList.get(position).getRanking()); // 순위 설정
    }

    @Override
    public int getItemCount() {
        return (null != Data_arrayList ? Data_arrayList.size() : 0);
    }

    public class AdapterViewholder extends RecyclerView.ViewHolder {
        TextView rank;
        ImageView imageView;
        TextView title;
        TextView traffic;
        ImageView link;
        ImageView card;

        public AdapterViewholder(@NonNull View itemView) {
            super(itemView);
            // 뷰 홀더에서 각 뷰 설정
            rank = itemView.findViewById(R.id.ranking);
            imageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            traffic = itemView.findViewById(R.id.traffic);
            link = itemView.findViewById(R.id.link);
            card = itemView.findViewById(R.id.card);
        }
    }

    private void loadImage(String imageUrl, ImageView imageView) {
        // 새로운 스레드를 생성하여 이미지 로딩 및 UI 업데이트 작업을 처리합니다.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // URL을 생성하고 연결합니다.
                    URL url = new URL(imageUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    // 연결된 InputStream을 통해 이미지를 읽어옵니다.
                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    // UI 업데이트를 위해 UI 스레드에서 실행되어야 하는 부분을 imageView.post 내에 작성합니다.
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            // 비트맵을 이미지뷰에 설정하여 화면에 표시합니다.
                            imageView.setImageBitmap(bitmap);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start(); // 스레드 실행
    }

}
