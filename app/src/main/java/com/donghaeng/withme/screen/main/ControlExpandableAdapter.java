package com.donghaeng.withme.screen.main;

import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.screen.guide.ListItem;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ControlExpandableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> displayedItems; // 실제 표시될 아이템들
    private List<ControlListItem> originalItems; // 원본 아이템들

    public ControlExpandableAdapter(List<ControlListItem> items) {
        this.originalItems = new ArrayList<>(items);
        this.displayedItems = new ArrayList<>(items);
    }

    // ViewHolder 클래스들은 그대로 유지
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView nameText;
        ImageView arrowIcon;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            nameText = itemView.findViewById(R.id.nameText);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Object item = displayedItems.get(position);
                    if (item instanceof ControlListItem) {
                        ControlListItem headerItem = (ControlListItem) item;
                        if (headerItem.isExpanded()) {
                            collapseItem(position);
                            arrowIcon.setRotation(0);
                        } else {
                            expandItem(position);
                            arrowIcon.setRotation(180);
                        }
                    }
                }
            });
        }
    }

    public class ControlViewHolder extends RecyclerView.ViewHolder {
        ImageButton callButton, notificationButton, soundButton;
        View setAlarmButton;
        TextInputEditText timePickerEditText;

        public ControlViewHolder(@NonNull View itemView) {
            super(itemView);
            callButton = itemView.findViewById(R.id.callButton);
            notificationButton = itemView.findViewById(R.id.notificationButton);
            soundButton = itemView.findViewById(R.id.soundButton);
            setAlarmButton = itemView.findViewById(R.id.setAlarmButton);
            timePickerEditText = itemView.findViewById(R.id.timePickerEditText);

            setUpClickListeners();
        }

        private void setUpClickListeners() {
            callButton.setOnClickListener(v -> {
                // 통화 기능 구현
            });

            notificationButton.setOnClickListener(v -> {
                // 알림 기능 구현
            });

            soundButton.setOnClickListener(v -> {
                // 소리 기능 구현
            });

            setAlarmButton.setOnClickListener(v -> {
                // 알람 기능 구현
            });

            timePickerEditText.setOnClickListener(v -> showTimePickerDialog(timePickerEditText));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ListItem.TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_control_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_control_panel, parent, false);
            return new ControlViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = displayedItems.get(position);

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            ControlListItem headerItem = (ControlListItem) item;
            headerHolder.nameText.setText(headerItem.getTitle());
            headerHolder.arrowIcon.setRotation(headerItem.isExpanded() ? 180 : 0);
            // 프로필 이미지 설정
            // headerHolder.profileImage.setImageResource(...);
        } else if (holder instanceof ControlViewHolder) {
            ControlViewHolder controlHolder = (ControlViewHolder) holder;
            ControlPanel controlPanel = (ControlPanel) item;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = displayedItems.get(position);
        if (item instanceof ControlListItem) {
            return ListItem.TYPE_HEADER;
        } else {
            return ListItem.TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return displayedItems.size();
    }

    // 컨트롤 패널을 위한 데이터 클래스
    private static class ControlPanel {
        private ControlListItem parentItem;

        public ControlPanel(ControlListItem parentItem) {
            this.parentItem = parentItem;
        }

    }

    private void expandItem(int position) {
        if (position < 0 || position >= displayedItems.size()) return;

        Object item = displayedItems.get(position);
        if (item instanceof ControlListItem) {
            ControlListItem headerItem = (ControlListItem) item;
            headerItem.setExpanded(true);

            // 컨트롤 패널 추가
            displayedItems.add(position + 1, new ControlPanel(headerItem));
            notifyItemInserted(position + 1);
            notifyItemChanged(position); // 헤더 상태 업데이트
        }
    }

    private void collapseItem(int position) {
        if (position < 0 || position >= displayedItems.size()) return;

        Object item = displayedItems.get(position);
        if (item instanceof ControlListItem) {
            ControlListItem headerItem = (ControlListItem) item;
            headerItem.setExpanded(false);

            // 컨트롤 패널 제거
            if (position + 1 < displayedItems.size() &&
                    displayedItems.get(position + 1) instanceof ControlPanel) {
                displayedItems.remove(position + 1);
                notifyItemRemoved(position + 1);
                notifyItemChanged(position); // 헤더 상태 업데이트
            }
        }
    }

    private void showTimePickerDialog(TextInputEditText timePickerEditText) {
        // 현재 시간을 기본값으로 설정
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                timePickerEditText.getContext(),
                (view, hourOfDay, selectedMinute) -> {
                    // 24시간 형식을 12시간 형식으로 변환
                    String period = "AM";
                    int hour12 = hourOfDay;
                    if (hourOfDay > 12) {
                        hour12 = hourOfDay - 12;
                        period = "PM";
                    } else if (hourOfDay == 12) {
                        period = "PM";
                    } else if (hourOfDay == 0) {
                        hour12 = 12;
                    }

                    // 선택된 시간을 텍스트로 표시
                    String time = String.format(Locale.getDefault(),
                            "%s %d:%02d", period, hour12, selectedMinute);
                    timePickerEditText.setText(time);
                },
                hour,
                minute,
                false  // 24시간 형식 여부
        );

        timePickerDialog.show();
    }
}