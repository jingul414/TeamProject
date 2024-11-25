package com.donghaeng.withme.screen.guide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donghaeng.withme.R;
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

    public GuideContentAdapter(Context context, String jsonContent) {
        this.context = context;
        this.items = parseJson(jsonContent);
    }

    private List<GuideItem> parseJson(String jsonContent) {
        List<GuideItem> guideItems = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonContent);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                guideItems.add(new GuideItem(
                        item.getString("type"),
                        item.getString("value")
                ));
            }
        } catch (JSONException e) {
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
            // TODO 이미지 어떻게 저장 되있는거? 안불러와짐
            Picasso.get()
                    .load(new File(item.getValue().replace("file://", "")))
                    .into(imageHolder.imageView);
        }
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