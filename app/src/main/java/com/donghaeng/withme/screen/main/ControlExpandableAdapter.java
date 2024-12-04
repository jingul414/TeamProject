package com.donghaeng.withme.screen.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.database.firestore.SendDataMessage;
import com.donghaeng.withme.screen.guide.ListItem;
import com.donghaeng.withme.service.AlarmService;
import com.donghaeng.withme.service.BrightnessControlService;
import com.donghaeng.withme.service.VolumeControlService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ControlExpandableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> displayedItems;
    private Context context;
    private List<ControlListItem> originalItems;
    private SendDataMessage sendDataMessage;

    public void updateItems(List<ControlListItem> newItems) {
        this.originalItems = new ArrayList<>(newItems);
        this.displayedItems = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    public ControlExpandableAdapter(Context context, List<ControlListItem> items) {
        this.context = context;
        this.originalItems = new ArrayList<>(items);
        this.displayedItems = new ArrayList<>(items);
        this.sendDataMessage = new SendDataMessage();
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
        private final ControlExpandableAdapter adapter;
        ImageButton callButton, notificationButton, soundButton;
        ImageButton muteButton, autoLight;
        SeekBar soundSeekbar, lightSeekbar;
        TextView currentSoundPercent, changeSoundPercent;
        TextView currentLightPercent, changeLightPercent;
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

        public ControlViewHolder(@NonNull View itemView, ControlExpandableAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            checkWriteSettingsPermission(context);
            initializeViews(itemView);
            setupTimePicker();
            setupSoundControl();
            setupLightControl();
            setupButtonListeners();
        }

        private void initializeViews(View itemView) {
            callButton = itemView.findViewById(R.id.callButton);
            notificationButton = itemView.findViewById(R.id.notificationButton);
            soundButton = itemView.findViewById(R.id.soundButton);
            muteButton = itemView.findViewById(R.id.mute_button);
            autoLight = itemView.findViewById(R.id.auto_light);
            setAlarmButton = itemView.findViewById(R.id.setAlarmButton);

            soundSeekbar = itemView.findViewById(R.id.sound_seekbar);
            lightSeekbar = itemView.findViewById(R.id.light_seekbar);

            currentSoundPercent = itemView.findViewById(R.id.current_sound_percent);
            changeSoundPercent = itemView.findViewById(R.id.change_sound_percent);
            currentLightPercent = itemView.findViewById(R.id.current_light_percent);
            changeLightPercent = itemView.findViewById(R.id.change_light_percent);

            hourPicker = itemView.findViewById(R.id.hourPicker);
            minutePicker = itemView.findViewById(R.id.minutePicker);
        }

        private void setupTimePicker() {
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(23);
            hourPicker.setFormatter(value -> String.format("%02d", value));

            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(59);
            minutePicker.setFormatter(value -> String.format("%02d", value));

            Calendar calendar = Calendar.getInstance();
            hourPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
            minutePicker.setValue(calendar.get(Calendar.MINUTE));
        }

        private void setupSoundControl() {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            if (audioManager == null) {
                Log.e("ControlViewHolder", "AudioManager is null");
                return;
            }

            soundSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    changeSoundPercent.setText(String.valueOf(progress));

                    if (progress == 0) {
                        muteButton.setImageResource(R.drawable.ic_volume_mute);
                    } else {
                        muteButton.setImageResource(R.drawable.ic_volume);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    String headerId = adapter.findItem(getAdapterPosition());
                    currentSoundPercent.setText(String.valueOf(seekBar.getProgress()));

                    int selectedStreamType = AudioManager.STREAM_RING;
                    switch (SOUND_MODE) {
                        case SOUND_CALL:
                            selectedStreamType = AudioManager.STREAM_RING;
                            break;
                        case SOUND_NOTIFICATION:
                            selectedStreamType = AudioManager.STREAM_NOTIFICATION;
                            break;
                        case SOUND_MEDIA:
                            selectedStreamType = AudioManager.STREAM_MUSIC;
                            break;
                    }

                    if (headerId != null) {
                        sendDataMessage.sendDataMessage(headerId, "Volume",
                                String.valueOf(seekBar.getProgress()));
                    }
                    startVolumeControlService(seekBar.getProgress(), selectedStreamType, 10);
                }
            });
            initializeVolume(audioManager);
        }

        private void setupLightControl() {
            int initialBrightness = getScreenBrightness(context);
            lightSeekbar.setMax(255);
            lightSeekbar.setProgress(initialBrightness);
            currentLightPercent.setText(String.valueOf((initialBrightness * 100) / 255));

            lightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    changeLightPercent.setText(String.valueOf((progress * 100) / 255));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int brightness = seekBar.getProgress();
                    String headerId = adapter.findItem(getAdapterPosition());
                    if (headerId != null) {
                        sendDataMessage.sendDataMessage(headerId, "Brightness",
                                String.valueOf((brightness * 100) / 255));
                    }
                    //startBrightnessControlService(false, brightness, 10);
                    currentLightPercent.setText(String.valueOf((brightness * 100) / 255) + "%");
                }
            });
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
                    maxVolume = 100;
                    currentVolume = 0;
                    break;
            }

            soundSeekbar.setMax(100);
            soundSeekbar.setProgress((currentVolume * 100) / maxVolume);
            currentSoundPercent.setText(String.valueOf((currentVolume * 100) / maxVolume));
            changeSoundPercent.setText(String.valueOf((currentVolume * 100) / maxVolume));

            if (currentVolume == 0) {
                muteButton.setImageResource(R.drawable.ic_volume_mute);
            } else {
                muteButton.setImageResource(R.drawable.ic_volume);
            }
        }

        private void setupButtonListeners() {
            muteButton.setOnClickListener(v -> {
                String headerId = adapter.findItem(getAdapterPosition());
                int selectedStreamType = AudioManager.STREAM_RING;

                if (soundSeekbar.getProgress() > 0) {
                    lastSoundVolume = soundSeekbar.getProgress();
                    soundSeekbar.setProgress(0);
                    currentSoundPercent.setText(String.valueOf(0));
                    muteButton.setImageResource(R.drawable.ic_volume_mute);

                    switch (SOUND_MODE) {
                        case SOUND_CALL:
                            call_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                            selectedStreamType = AudioManager.STREAM_RING;
                            break;
                        case SOUND_NOTIFICATION:
                            notification_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                            selectedStreamType = AudioManager.STREAM_NOTIFICATION;
                            break;
                        case SOUND_MEDIA:
                            media_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                            selectedStreamType = AudioManager.STREAM_MUSIC;
                            break;
                    }
                    if (headerId != null) {
                        sendDataMessage.sendDataMessage(headerId, "Volume", "0");
                    }
                    startVolumeControlService(0, selectedStreamType, 10);
                } else {
                    soundSeekbar.setProgress(lastSoundVolume);
                    currentSoundPercent.setText(String.valueOf(lastSoundVolume));
                    muteButton.setImageResource(R.drawable.ic_volume);

                    switch (SOUND_MODE) {
                        case SOUND_CALL:
                            call_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                            selectedStreamType = AudioManager.STREAM_RING;
                            break;
                        case SOUND_NOTIFICATION:
                            notification_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                            selectedStreamType = AudioManager.STREAM_NOTIFICATION;
                            break;
                        case SOUND_MEDIA:
                            media_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                            selectedStreamType = AudioManager.STREAM_MUSIC;
                            break;
                    }
                    if (headerId != null) {
                        sendDataMessage.sendDataMessage(headerId, "Volume",
                                String.valueOf(lastSoundVolume));
                    }
                    startVolumeControlService(lastSoundVolume, selectedStreamType, 10);
                }
            });

            autoLight.setOnClickListener(v -> {
                String headerId = adapter.findItem(getAdapterPosition());
                boolean isCurrentlyAuto = isAutoBrightnessEnabled(context);

                if (isCurrentlyAuto) {
                    startBrightnessControlService(false, lightSeekbar.getProgress(), 10);
                    LIGHT_MODE = LIGHT_SET;
                    autoLight.setImageResource(R.drawable.ic_light_mode);
                    currentLightPercent.setText(String.valueOf((lightSeekbar.getProgress() * 100) / 255));
                    changeLightPercent.setText(String.valueOf((lightSeekbar.getProgress() * 100) / 255));
                    lightSeekbar.setEnabled(true);
                    if (headerId != null) {
                        sendDataMessage.sendDataMessage(headerId, "AutoBrightness", "false");
                    }
                } else {
                    startBrightnessControlService(true, -1, 10);
                    LIGHT_MODE = LIGHT_AUTO;
                    autoLight.setImageResource(R.drawable.ic_light_mode);
                    currentLightPercent.setText("Auto");
                    changeLightPercent.setText("Auto");
                    lightSeekbar.setEnabled(false);
                    if (headerId != null) {
                        sendDataMessage.sendDataMessage(headerId, "AutoBrightness", "true");
                    }
                }
            });

            setAlarmButton.setOnClickListener(v -> {
                String headerId = adapter.findItem(getAdapterPosition());
                int hour = hourPicker.getValue();
                int minute = minutePicker.getValue();
                if (headerId != null) {
                    sendDataMessage.sendDataMessage(headerId, "Alarm",
                            String.format("%02d:%02d", hour, minute));
                }
                checkPermissionsAndScheduleAlarm();
            });

            callButton.setOnClickListener(v -> {
                String headerId = adapter.findItem(getAdapterPosition());
                callButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE7363")));
                notificationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
                soundButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
                SOUND_MODE = SOUND_CALL;

                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

                int volumePercent = (currentVolume * 100) / maxVolume;

                soundSeekbar.setProgress(volumePercent);
                currentSoundPercent.setText(String.valueOf(volumePercent));
                if (headerId != null) {
                    sendDataMessage.sendDataMessage(headerId, "SoundMode", "CALL");
                }

                muteButton.setImageResource(currentVolume == 0 ? R.drawable.ic_volume_mute : R.drawable.ic_volume);
            });

            notificationButton.setOnClickListener(v -> {
                String headerId = adapter.findItem(getAdapterPosition());
                callButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
                notificationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE7363")));
                soundButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
                SOUND_MODE = SOUND_NOTIFICATION;

                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

                int volumePercent = (currentVolume * 100) / maxVolume;

                soundSeekbar.setProgress(volumePercent);
                currentSoundPercent.setText(String.valueOf(volumePercent));
                if (headerId != null) {
                    sendDataMessage.sendDataMessage(headerId, "SoundMode", "NOTIFICATION");
                }

                muteButton.setImageResource(currentVolume == 0 ? R.drawable.ic_volume_mute : R.drawable.ic_volume);
            });

            soundButton.setOnClickListener(v -> {
                String headerId = adapter.findItem(getAdapterPosition());
                callButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
                notificationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
                soundButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE7363")));
                SOUND_MODE = SOUND_MEDIA;

                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                int volumePercent = (currentVolume * 100) / maxVolume;

                soundSeekbar.setProgress(volumePercent);
                currentSoundPercent.setText(String.valueOf(volumePercent));
                if (headerId != null) {
                    sendDataMessage.sendDataMessage(headerId, "SoundMode", "MEDIA");
                }

                muteButton.setImageResource(currentVolume == 0 ? R.drawable.ic_volume_mute : R.drawable.ic_volume);
            });
        }

        private void checkPermissionsAndScheduleAlarm() {
            if (Build.VERSION.SDK_INT >= 33) {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context,
                            "알림 권한이 필요합니다. 설정에서 권한을 허용해주세요.",
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }
            }
            startAlarmService();
        }

        private void startAlarmService() {
            Intent serviceIntent = new Intent(context, AlarmService.class);
            serviceIntent.putExtra("hour", hourPicker.getValue());
            serviceIntent.putExtra("minute", minutePicker.getValue());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }

            Toast.makeText(context, "5초 후에 알람 설정 알림이 표시됩니다.", Toast.LENGTH_SHORT).show();
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
            headerHolder.nameText.setText(headerItem.getTitle());
            headerHolder.arrowIcon.setRotation(headerItem.isExpanded() ? 180 : 0);
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

    private int getScreenBrightness(Context context) {
        int brightness = 0;
        try {
            brightness = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("ControlViewHolder", "Failed to get screen brightness", e);
        }
        return brightness;
    }

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

    private boolean isAutoBrightnessEnabled(Context context) {
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

    private void startVolumeControlService(int volume, int streamType, int delay) {
        Intent serviceIntent = new Intent(context, VolumeControlService.class);
        serviceIntent.putExtra("volume", volume);
        serviceIntent.putExtra("streamType", streamType);
        serviceIntent.putExtra("delay", delay);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    private void startBrightnessControlService(boolean autoLight, int brightness, int delay) {
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

    private static class ControlPanel {
        private ControlListItem parentItem;

        public ControlPanel(ControlListItem parentItem) {
            this.parentItem = parentItem;
        }
    }
}