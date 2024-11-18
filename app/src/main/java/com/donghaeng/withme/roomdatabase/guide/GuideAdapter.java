//package com.donghaeng.withme.roomdatabase.guide;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.donghaeng.withme.R;
//
//import java.util.List;
//
//public class GuideAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    private List<GuideContent> contentList;
//
//    public GuideAdapter(List<GuideContent> contentList) {
//        this.contentList = contentList;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return contentList.get(position).getType().equals("text") ? 0 : 1;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == 0) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
//            return new TextViewHolder(view);
//        } else {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
//            return new ImageViewHolder(view);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        GuideContent content = contentList.get(position);
//        if (holder instanceof TextViewHolder) {
//            ((TextViewHolder) holder).textView.setText(content.getValue());
//        } else if (holder instanceof ImageViewHolder) {
//            Glide.with(holder.itemView.getContext())
//                    .load(content.getValue())
//                    .into(((ImageViewHolder) holder).imageView);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return contentList.size();
//    }
//}