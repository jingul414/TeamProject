package com.donghaeng.withme.screen.main;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donghaeng.withme.R;

import java.util.ArrayList;
import java.util.List;


public class TargetExpandableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> displayedItems; // 실제 표시될 아이템들
    private List<TargetListItem> originalItems; // 원본 아이템들
    private static final int TYPE_LOG = 4; // 제어 기록 타입


    public TargetExpandableAdapter(List<TargetListItem> items) {
        this.originalItems = new ArrayList<>(items);
        this.displayedItems = new ArrayList<>(items);
    }


    public class LogViewHolder extends RecyclerView.ViewHolder {
        TextView controlLog;
        TextView name;
        TextView time;
        ImageView arrowIcon;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            controlLog = itemView.findViewById(R.id.control_log_text);
            name = itemView.findViewById(R.id.name_text);
            time = itemView.findViewById(R.id.time_text);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);
            
            arrowIcon.setOnClickListener(v -> {
               // 여기에 제어 기록 되돌리기 기능 구현
               Log.d("LogViewHolder", "제어 기록 되돌리기 클릭");
            });
        }


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_LOG) {
            View view = inflater.inflate(R.layout.item_target_log, parent, false);
            return new LogViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = displayedItems.get(position);

        if (holder instanceof LogViewHolder) {
            LogViewHolder logHolder = (LogViewHolder) holder;
            TargetListItem logItem = (TargetListItem) item;
            logHolder.controlLog.setText(logItem.getControl_log());
            logHolder.name.setText(logItem.getName());
            logHolder.time.setText(logItem.getTime());
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = displayedItems.get(position);
        if (item instanceof TargetListItem) {
            return TYPE_LOG;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return displayedItems.size();
    }


}