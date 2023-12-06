package com.example.termproject;

import static com.example.termproject.MainActivity.getCurrentTime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
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

    private ArrayList<DataClass> Data_arrayList;   ///아까 만들어두었던 Data 클래스를 어레이리스트로 만든다.


    public Adapter(ArrayList<DataClass> arrayList) { //Main_class와 연동하기 위한 adapter 파라미터
        this.Data_arrayList = arrayList;
    }

    @NonNull
    @Override    ///onCreateViewHolder에서는 어떤 레이아웃과 연결해야되는지 설정하고 view를 만들어준다.
    public AdapterViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_headline,parent,false);
        AdapterViewholder holder = new AdapterViewholder(view);

        ImageView link = view.findViewById(R.id.link);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = "";
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    data = Data_arrayList.get(position).getLink();
                    Intent intent = new Intent(view.getContext(), NewsActivity.class);
                    intent.putExtra("link", data);
                    view.getContext().startActivity(intent);
                }

            }
        });

        return holder;
    }

    @Override //받아온 데이터를 item_layout에  set해주는 곳
    public void onBindViewHolder(@NonNull AdapterViewholder holder, int position) {
        //getCurrentTime()함수로 현재 시간 반환
        String currentTime = getCurrentTime();
        //반환 값을 int로 변경
        int time = Integer.parseInt(currentTime);
        //9시부터 18시전에는 낮에 대한 이미지와 파란 글자색으로 변경
        if(time<18 && time >=9){
            holder.link.setImageResource(R.drawable.link_afternoon);
            holder.card.setImageResource(R.drawable.card_afternoon);
        }
        else if(time>=21 || time < 6){
            holder.link.setImageResource(R.drawable.link_night);
            holder.card.setImageResource(R.drawable.card_night);
        }
        else{
            holder.link.setImageResource(R.drawable.link_dawn);
            holder.card.setImageResource(R.drawable.card_dawn);
        }
        loadImage(Data_arrayList.get(position).getUrl(), holder.imageView);
        holder.title.setText(Data_arrayList.get(position).getTitle());
        holder.traffic.setText(Data_arrayList.get(position).getTraffic());
        holder.rank.setText(Data_arrayList.get(position).getRanking());
    }

    @Override
    //아이템 카운트
    public int getItemCount() {
        return (null != Data_arrayList ? Data_arrayList.size() :0);
    }


    /// item_layout의 데이터 초기화해주는 클래스
    public class AdapterViewholder extends RecyclerView.ViewHolder {
        TextView rank;
        ImageView imageView;
        TextView title;
        TextView traffic;
        ImageView link;
        ImageView card;
        public AdapterViewholder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.ranking);
            imageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            traffic = itemView.findViewById(R.id.traffic);
            link = itemView.findViewById(R.id.link);
            card = itemView.findViewById(R.id.card);
        }
    }

    private void loadImage(String imageUrl, ImageView imageView) {
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

                    // 이미지뷰에 비트맵 설정
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}