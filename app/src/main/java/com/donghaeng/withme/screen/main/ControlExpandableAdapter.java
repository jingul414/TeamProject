package com.donghaeng.withme.screen.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.app.ControlAllowanceListChecker;
import com.donghaeng.withme.data.command.manager.AlarmManager;
import com.donghaeng.withme.data.command.manager.BrightnessControlManager;
import com.donghaeng.withme.data.command.manager.SoundControlManager;
import com.donghaeng.withme.data.message.firebasemessage.SendDataMessage;
import com.donghaeng.withme.screen.guide.ListItem;
import com.donghaeng.withme.service.BrightnessControlService;
import com.donghaeng.withme.service.RejectionManager;

import java.util.ArrayList;
import java.util.List;

public class ControlExpandableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> displayedItems;
    private Context context;
    private List<ControlListItem> originalItems;
    private SendDataMessage sendDataMessage;
    private final RejectionManager rejectionManager;


    private static class ControlPanel {
        private final ControlListItem parentItem;

        public ControlPanel(ControlListItem parentItem) {
            this.parentItem = parentItem;
        }

        public ControlListItem getParentItem() {
            return parentItem;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(List<ControlListItem> newItems) {
        this.originalItems = new ArrayList<>(newItems);
        this.displayedItems = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    public ControlExpandableAdapter(Context context, List<ControlListItem> items) {
        this.context = context;
        this.rejectionManager = RejectionManager.getInstance(context);
        this.originalItems = new ArrayList<>(items);
        this.displayedItems = new ArrayList<>(items);
        this.sendDataMessage = new SendDataMessage();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView nameText, rejectStatusText;
        ImageView arrowIcon;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            nameText = itemView.findViewById(R.id.nameText);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);
            rejectStatusText = itemView.findViewById(R.id.rejectStatusText);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Object item = displayedItems.get(position);
                    if (item instanceof ControlListItem) {
                        ControlListItem headerItem = (ControlListItem) item;
                        // 거절 상태일 때는 확장/축소 불가
                        if (rejectionManager.isRejected(headerItem.getId())) {
                            return;
                        }

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

    public Context getContext() {
        return context;
    }
    public SendDataMessage getSendDataMessage(){ return sendDataMessage; }

    public class ControlViewHolder extends RecyclerView.ViewHolder {
        private final ControlExpandableAdapter adapter;
        private final AlarmManager alarmManager;
        private final SoundControlManager soundControlManager;
        private final BrightnessControlManager brightnessControlManager;

        public ControlViewHolder(@NonNull View itemView, ControlExpandableAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            checkWriteSettingsPermission(context);

            /* 소리 제어 관리자 객체 */
            soundControlManager = new SoundControlManager(this);
            soundControlManager.initializeViews(itemView);
            soundControlManager.setupButtonListeners();
            soundControlManager.setupSoundControl();
            /* 밝기 제어 관리자 객체 */
            brightnessControlManager = new BrightnessControlManager(this);
            brightnessControlManager.initializeViews(itemView);
            brightnessControlManager.setupButtonListeners();
            brightnessControlManager.setupLightControl();
            /* 알람 관리자 객체 */
            alarmManager = new AlarmManager(this);
            alarmManager.initializeViews(itemView);
            alarmManager.setupButtonListeners();
            alarmManager.setupTimePicker();
        }

        public void updateControlsBasedOnPermissions() {
            // 각 제어 기능의 허용 여부 확인
            boolean volumeControlAllowed = ControlAllowanceListChecker.getValue(context, ControlAllowanceListChecker.KEY_VOLUME_CONTROL);
            boolean brightnessControlAllowed = ControlAllowanceListChecker.getValue(context, ControlAllowanceListChecker.KEY_BRIGHTNESS_CONTROL);
            boolean alarmControlAllowed = ControlAllowanceListChecker.getValue(context, ControlAllowanceListChecker.KEY_SETTING_ALARM);

            // 소리 제어 UI 업데이트
            soundControlManager.setEnabled(volumeControlAllowed);
            updateViewStyle(soundControlManager.getViews(), volumeControlAllowed);

            // 밝기 제어 UI 업데이트
            brightnessControlManager.setEnabled(brightnessControlAllowed);
            updateViewStyle(brightnessControlManager.getViews(), brightnessControlAllowed);

            // 알람 제어 UI 업데이트
            alarmManager.setEnabled(alarmControlAllowed);
            updateViewStyle(alarmManager.getViews(), alarmControlAllowed);
        }

        private void updateViewStyle(List<View> views, boolean enabled) {
            for (View view : views) {
                view.setAlpha(enabled ? 1.0f : 0.5f);

                if (view instanceof SeekBar) {
                    SeekBar seekBar = (SeekBar) view;
                    if (!enabled) {
                        seekBar.setProgressTintList(ColorStateList.valueOf(Color.GRAY));
                        seekBar.setThumbTintList(ColorStateList.valueOf(Color.GRAY));
                    } else {
                        seekBar.setProgressTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
                        seekBar.setThumbTintList(ColorStateList.valueOf(context.getColor(R.color.white)));
                    }
                }
            }
        }

        public ControlExpandableAdapter getAdapter() {
            return adapter;
        }

        public void setControlsEnabled(boolean enabled) {
            // 소리 제어 위젯들 활성화/비활성화
            soundControlManager.setEnabled(enabled);

            // 밝기 제어 위젯들 활성화/비활성화
            brightnessControlManager.setEnabled(enabled);

            // 알람 제어 위젯들 활성화/비활성화
            alarmManager.setEnabled(enabled);
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
            return new ControlViewHolder(view, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = displayedItems.get(position);

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            ControlListItem headerItem = (ControlListItem) item;

            // 이름 설정 추가
            headerHolder.nameText.setText(headerItem.getTitle());

            String targetId = headerItem.getId();
            boolean isRejected = rejectionManager.isRejected(targetId);

            if (isRejected) {
                // 거절 상태일 때
                int remainingMinutes = rejectionManager.getRemainingMinutes(targetId);
                headerHolder.rejectStatusText.setVisibility(View.VISIBLE);
                headerHolder.rejectStatusText.setText(String.format("제어 거절됨 (%d분 남음)", remainingMinutes));
                headerHolder.itemView.setAlpha(0.5f);
            } else {
                // 거절 상태가 아닐 때 제어 허용 상태 표시
                List<String> disabledControls = new ArrayList<>();

                if (!ControlAllowanceListChecker.getValue(context, ControlAllowanceListChecker.KEY_VOLUME_CONTROL)) {
                    disabledControls.add("소리 제어");
                }
                if (!ControlAllowanceListChecker.getValue(context, ControlAllowanceListChecker.KEY_BRIGHTNESS_CONTROL)) {
                    disabledControls.add("밝기 제어");
                }
                if (!ControlAllowanceListChecker.getValue(context, ControlAllowanceListChecker.KEY_SETTING_ALARM)) {
                    disabledControls.add("알람 설정");
                }

                if (!disabledControls.isEmpty()) {
                    headerHolder.rejectStatusText.setVisibility(View.VISIBLE);
                    String disabledText = String.join(", ", disabledControls) + " 거부됨";
                    headerHolder.rejectStatusText.setText(disabledText);
                    headerHolder.rejectStatusText.setTextColor(Color.RED);
                } else {
                    headerHolder.rejectStatusText.setVisibility(View.GONE);
                }
                headerHolder.itemView.setAlpha(1.0f);

            }


            // 아이템 확장 상태에 따른 화살표 방향 설정
            headerHolder.arrowIcon.setRotation(headerItem.isExpanded() ? 180 : 0);
        } else if (holder instanceof ControlViewHolder) {
            ControlViewHolder controlHolder = (ControlViewHolder) holder;
            String targetId = findItem(position);
            boolean isRejected = rejectionManager.isRejected(targetId);

            // 전체 View alpha 설정
            holder.itemView.setAlpha(isRejected ? 0.5f : 1.0f);

            // 거절 상태가 아닐 때만 권한 기반으로 컨트롤 업데이트
            if (!isRejected) {
                controlHolder.updateControlsBasedOnPermissions();
            } else {
                controlHolder.setControlsEnabled(false);
            }
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

    public String findItem(int currentPosition) {
        for (int i = currentPosition; i >= 0; i--) {
            Object item = displayedItems.get(i);
            if (item instanceof ControlListItem) {
                ControlListItem controlItem = (ControlListItem) item;
                if (controlItem.getType() == ListItem.TYPE_HEADER) {
                    return controlItem.getId();
                }
            }
        }
        return null;
    }

    private void expandItem(int position) {
        if (position < 0 || position >= displayedItems.size()) return;

        Object item = displayedItems.get(position);
        if (item instanceof ControlListItem) {
            ControlListItem headerItem = (ControlListItem) item;
            headerItem.setExpanded(true);
            displayedItems.add(position + 1, new ControlPanel(headerItem));
            notifyItemInserted(position + 1);
            notifyItemChanged(position);
        }
    }

    private void collapseItem(int position) {
        if (position < 0 || position >= displayedItems.size()) return;

        Object item = displayedItems.get(position);
        if (item instanceof ControlListItem) {
            ControlListItem headerItem = (ControlListItem) item;
            headerItem.setExpanded(false);
            if (position + 1 < displayedItems.size() &&
                    displayedItems.get(position + 1) instanceof ControlPanel) {
                displayedItems.remove(position + 1);
                notifyItemRemoved(position + 1);
                notifyItemChanged(position);
            }
        }
    }

    public int getScreenBrightness(Context context) {
        int brightness = 0;
        try {
            brightness = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("ControlViewHolder", "Failed to get screen brightness", e);
        }
        return brightness;
    }

    // TODO: 얘 사용하는 곳 없어짐
    private void setAutoBrightness(Context context, boolean isEnabled) {
        try {
            int mode = isEnabled ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                    : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
            Log.d("ControlViewHolder", "Auto Brightness Mode: " + (isEnabled ? "Enabled" : "Disabled"));
        } catch (Exception e) {
            Log.e("ControlViewHolder", "Failed to set auto brightness mode", e);
        }
    }

    public boolean isAutoBrightnessEnabled(Context context) {
        try {
            int mode = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            return (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("ControlViewHolder", "Failed to get auto brightness mode", e);
            return false;
        }
    }

    private void checkWriteSettingsPermission(Context context) {
        if (!Settings.System.canWrite(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }



    public void startBrightnessControlService(boolean autoLight, int brightness, int delay) {
        Intent serviceIntent = new Intent(context, BrightnessControlService.class);
        serviceIntent.putExtra("autoLight", autoLight);
        serviceIntent.putExtra("brightness", brightness);
        serviceIntent.putExtra("delay", delay);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    // UI 갱신을 위한 메서드 추가
    public void refreshUI() {
        notifyDataSetChanged();
    }
}