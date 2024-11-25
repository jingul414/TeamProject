package com.donghaeng.withme.screen.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.screen.guide.ListItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ControlExpandableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> displayedItems;
    private Context context;

    private List<ControlListItem> originalItems;

    public ControlExpandableAdapter(Context context, List<ControlListItem> items) {
        this.context = context;
        this.originalItems = new ArrayList<>(items);
        this.displayedItems = new ArrayList<>(items);
    }

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
        ImageButton muteButton, autoLight;
        SeekBar soundSeekbar, lightSeekbar;
        TextView currentSoundPercent, changeSoundPercent;
        TextView currentLightPercent, changeLightPercent;

        // 타임피커 관련 요소들
        NumberPicker hourPicker, minutePicker;
        Button setAlarmButton;

        private int call_volume = 0, notification_volume = 0, media_volume = 0;

        private int SOUND_MODE = 0;
        final private int SOUND_CALL = 0;
        final private int SOUND_NOTIFICATION = 1;
        final private int SOUND_MEDIA = 2;

        private int LIGHT_MODE = 0;
        final private int LIGHT_AUTO = 1;
        final private int LIGHT_SET = 0;

        private int lastSoundVolume = 0;
        private int lastLightValue = 0;


        public ControlViewHolder(@NonNull View itemView) {
            super(itemView);
            checkWriteSettingsPermission(context);
            initializeViews(itemView);
            setupTimePicker();
            setupSoundControl();
            setupLightControl();
            setupButtonListeners();
        }

        private void initializeViews(View itemView) {
            // 버튼들
            callButton = itemView.findViewById(R.id.callButton);
            notificationButton = itemView.findViewById(R.id.notificationButton);
            soundButton = itemView.findViewById(R.id.soundButton);
            muteButton = itemView.findViewById(R.id.mute_button);
            autoLight = itemView.findViewById(R.id.auto_light);
            setAlarmButton = itemView.findViewById(R.id.setAlarmButton);

            // 시크바들
            soundSeekbar = itemView.findViewById(R.id.sound_seekbar);
            lightSeekbar = itemView.findViewById(R.id.light_seekbar);

            // 텍스트뷰들
            currentSoundPercent = itemView.findViewById(R.id.current_sound_percent);
            changeSoundPercent = itemView.findViewById(R.id.change_sound_percent);
            currentLightPercent = itemView.findViewById(R.id.current_light_percent);
            changeLightPercent = itemView.findViewById(R.id.change_light_percent);

            // 넘버피커
            hourPicker = itemView.findViewById(R.id.hourPicker);
            minutePicker = itemView.findViewById(R.id.minutePicker);
        }

        private void setupTimePicker() {
            // 시간 설정 (0-23)
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(23);
            hourPicker.setFormatter(value -> String.format("%02d", value));

            // 분 설정 (0-59)
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(59);
            minutePicker.setFormatter(value -> String.format("%02d", value));

            // 현재 시간으로 초기값 설정
            Calendar calendar = Calendar.getInstance();
            hourPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
            minutePicker.setValue(calendar.get(Calendar.MINUTE));
        }

        private void setupSoundControl() {
            // AudioManager 초기화
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            if (audioManager == null) {
                Log.e("ControlViewHolder", "AudioManager is null");
                return;
            }

            soundSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    changeSoundPercent.setText(String.valueOf(progress));

                    // 상태에 따라 muteButton 이미지 변경
                    if (progress == 0) {
                        muteButton.setImageResource(R.drawable.ic_volume_mute); // 음소거 이미지
                    } else {
                        muteButton.setImageResource(R.drawable.ic_volume); // 일반 볼륨 이미지
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    currentSoundPercent.setText(String.valueOf(seekBar.getProgress()));
                    // TODO 소리 설정 제어 관련 기능 구현
                    switch (SOUND_MODE){
                        case (SOUND_CALL):
                            // 전화 소리 설정
                            updateVolume(AudioManager.STREAM_RING, seekBar.getProgress(), audioManager);
                            break;
                        case (SOUND_NOTIFICATION):
                            // 알림 소리 설정
                            updateVolume(AudioManager.STREAM_NOTIFICATION, seekBar.getProgress(), audioManager);
                            break;
                        case (SOUND_MEDIA):
                            // 미디어 소리 설정
                            updateVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), audioManager);
                            break;
                    }
                }
            });

            // 현재 볼륨 설정
            initializeVolume(audioManager);
        }

        private void updateVolume(int streamType, int progress, AudioManager audioManager) {
            // 오디오 스트림의 실제 최대 볼륨 값
            int maxVolume = audioManager.getStreamMaxVolume(streamType);

            // SeekBar 퍼센트 값을 실제 볼륨 값으로 변환
            int newVolume = (progress * maxVolume) / 100;

            // 볼륨 설정
            audioManager.setStreamVolume(streamType, newVolume, 0);

            Log.d("ControlViewHolder", "StreamType: " + streamType + ", NewVolume: " + newVolume + ", MaxVolume: " + maxVolume);
        }

        private void initializeVolume(AudioManager audioManager) {
            int maxVolume;
            int currentVolume;

            switch (SOUND_MODE) {
                case SOUND_CALL:
                    maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                    break;

                case SOUND_NOTIFICATION:
                    maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
                    break;

                case SOUND_MEDIA:
                    maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    break;

                default:
                    maxVolume = 100; // 기본값
                    currentVolume = 0;
                    break;
            }

            // SeekBar 최대값과 현재값 동기화
            soundSeekbar.setMax(100);
            soundSeekbar.setProgress((currentVolume * 100) / maxVolume);

            // 텍스트뷰 초기화
            currentSoundPercent.setText(String.valueOf((currentVolume * 100) / maxVolume));
            changeSoundPercent.setText(String.valueOf((currentVolume * 100) / maxVolume));

            // 초기 상태에 따라 muteButton 이미지 설정
            if (currentVolume == 0) {
                muteButton.setImageResource(R.drawable.ic_volume_mute); // 음소거 이미지
            } else {
                muteButton.setImageResource(R.drawable.ic_volume); // 일반 볼륨 이미지
            }
        }


        private void setupLightControl() {
            // 초기 밝기 값 설정
            int initialBrightness = getScreenBrightness(context);
            lightSeekbar.setMax(255); // 밝기 최대값
            lightSeekbar.setProgress(initialBrightness);
            currentLightPercent.setText(String.valueOf((initialBrightness * 100) / 255));

            lightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    changeLightPercent.setText(String.valueOf((progress * 100) / 255));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO seekBar 에서 손을 놨을 때 정보 전달

                    int brightness = seekBar.getProgress();
                    setScreenBrightness(context, brightness);

                    // 현재 밝기 텍스트뷰 업데이트
                    currentLightPercent.setText(String.valueOf((brightness * 100) / 255));
                }
            });
        }

        private int getScreenBrightness(Context context) {
            int brightness = 0;
            try {
                brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                Log.e("ControlViewHolder", "Failed to get screen brightness", e);
            }
            return brightness;
        }

        private void setScreenBrightness(Context context, int brightness) {
            try {
                // 밝기 값 제한 (0 ~ 255)
                int newBrightness = Math.max(0, Math.min(brightness, 255));

                // 밝기 설정
                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, newBrightness);

                Log.d("ControlViewHolder", "Screen Brightness set to: " + newBrightness);
            } catch (Exception e) {
                Log.e("ControlViewHolder", "Failed to set screen brightness", e);
            }
        }

        private void setupButtonListeners() {
            muteButton.setOnClickListener(v -> {
                if (soundSeekbar.getProgress() > 0) {
                    lastSoundVolume = soundSeekbar.getProgress();
                    soundSeekbar.setProgress(0);
                    currentSoundPercent.setText(String.valueOf(0));
                    muteButton.setImageResource(R.drawable.ic_volume_mute);
                    // TODO 소리 설정 제어 관련 기능 구현
                    switch (SOUND_MODE){
                        case (SOUND_CALL):
                            call_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                            break;
                        case (SOUND_NOTIFICATION):
                            notification_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                            break;
                        case (SOUND_MEDIA):
                            media_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                            break;
                    }
                } else {
                    soundSeekbar.setProgress(lastSoundVolume);
                    currentSoundPercent.setText(String.valueOf(lastSoundVolume));
                    muteButton.setImageResource(R.drawable.ic_volume);
                    // TODO 소리 설정 제어 관련 기능 구현
                    switch (SOUND_MODE){
                        case (SOUND_CALL):
                            call_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                            break;
                        case (SOUND_NOTIFICATION):
                            notification_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                            break;
                        case (SOUND_MEDIA):
                            media_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                            break;
                    }
                }
            });

            autoLight.setOnClickListener(v -> {
                boolean isCurrentlyAuto = isAutoBrightnessEnabled(context);

                if (isCurrentlyAuto) {
                    // 자동 밝기 모드를 끄고 수동 모드로 설정
                    setAutoBrightness(context, false);
                    LIGHT_MODE = LIGHT_SET;

                    // UI 업데이트
                    autoLight.setImageResource(R.drawable.ic_light_mode);
                    currentLightPercent.setText(String.valueOf((lightSeekbar.getProgress() * 100) / 255));
                    changeLightPercent.setText(String.valueOf((lightSeekbar.getProgress() * 100) / 255));
                    lightSeekbar.setEnabled(true);
                } else {
                    // 자동 밝기 모드를 켜기
                    setAutoBrightness(context, true);
                    LIGHT_MODE = LIGHT_AUTO;

                    // UI 업데이트
                    autoLight.setImageResource(R.drawable.ic_light_mode);
                    currentLightPercent.setText("Auto");
                    changeLightPercent.setText("Auto");
                    lightSeekbar.setEnabled(false);
                }
            });

            setAlarmButton.setOnClickListener(v -> {
                int hour = hourPicker.getValue();
                int minute = minutePicker.getValue();
                String time = String.format("%02d:%02d", hour, minute);
                // TODO: 알람 설정 처리
            });

            // 소리 제어 모드 변경
            // TODO 소리 모드 변경 시 현재 음량 받아와서 SeekBar 설정 및 textView 수정 해야 함
            callButton.setOnClickListener(v -> {
                callButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE7363")));
                notificationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
                soundButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
                SOUND_MODE = SOUND_CALL;
            });

            notificationButton.setOnClickListener(v -> {
                callButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
                notificationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE7363")));
                soundButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
                SOUND_MODE = SOUND_NOTIFICATION;
            });

            soundButton.setOnClickListener(v -> {
                callButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
                notificationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
                soundButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE7363")));
                SOUND_MODE = SOUND_MEDIA;
            });
        }
    }

    private void setAutoBrightness(Context context, boolean isEnabled) {
        try {
            int mode = isEnabled ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                    : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;

            // 자동 밝기 모드 설정
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mode);

            Log.d("ControlViewHolder", "Auto Brightness Mode: " + (isEnabled ? "Enabled" : "Disabled"));
        } catch (Exception e) {
            Log.e("ControlViewHolder", "Failed to set auto brightness mode", e);
        }
    }

    private boolean isAutoBrightnessEnabled(Context context) {
        boolean isEnabled = false;
        try {
            int mode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            isEnabled = (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("ControlViewHolder", "Failed to get auto brightness mode", e);
        }
        return isEnabled;
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
        } else if (holder instanceof ControlViewHolder) {
            // ControlPanel 바인딩 시 필요한 초기화 작업
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

    private void checkWriteSettingsPermission(Context context) {
        if (!Settings.System.canWrite(context)) {
            // 사용자에게 설정 화면으로 이동하도록 요청
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}