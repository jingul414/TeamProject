package com.donghaeng.withme.screen.guide;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donghaeng.withme.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuideContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_IMAGE = 1;

    private List<GuideItem> items;
    private Context context;
    private FirebaseStorage storage;


    public GuideContentAdapter(Context context, String jsonContent) {
        this.context = context;
        this.items = parseJson(jsonContent);
        this.storage = FirebaseStorage.getInstance();
    }

    private List<GuideItem> parseJson(String jsonContent) {
        List<GuideItem> guideItems = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);
            Log.d("GuideContentAdapter", "Parsing JSON: " + jsonContent);  // 로그 추가
            JSONArray jsonArray = jsonObject.getJSONArray("content");  // "content" 키의 배열을 가져옴

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                GuideItem guideItem = new GuideItem(
                        item.getString("type"),
                        item.getString("value")
                );
                guideItems.add(guideItem);
                Log.d("GuideContentAdapter", "Added item: " + guideItem);  // 로그 추가
            }
        } catch (JSONException e) {
            Log.e("GuideContentAdapter", "Error parsing JSON", e);  // 에러 로그 추가
            e.printStackTrace();
        }
        return guideItems;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType().equals("text") ? TYPE_TEXT : TYPE_IMAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TEXT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_guide_text, parent, false);
            return new TextViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_guide_image, parent, false);
            return new ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GuideItem item = items.get(position);

        if (holder instanceof TextViewHolder) {
            ((TextViewHolder) holder).textView.setText(item.getValue());
        } else if (holder instanceof ImageViewHolder) {
            ImageViewHolder imageHolder = (ImageViewHolder) holder;
            // 로컬 파일에서 이미지 로드
            loadFirebaseImage(item.getValue(), imageHolder.imageView);
        }
    }

    private void loadFirebaseImage(String gsUrl, ImageView imageView) {
        // gs:// URL을 StorageReference로 변환
        StorageReference gsReference = storage.getReferenceFromUrl(gsUrl);

        // 다운로드 URL 가져오기
        gsReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Picasso로 이미지 로드
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.placeholder) // 로딩 중 표시할 이미지
                    .error(R.drawable.error_image) // 에러 시 표시할 이미지
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            // 에러 처리
            exception.printStackTrace();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        TextViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.guideText);
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.guideImage);
        }
    }
}