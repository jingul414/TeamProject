package com.donghaeng.withme.data.command.manager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.command.SoundMode;
import com.donghaeng.withme.data.message.firebasemessage.SendDataMessage;
import com.donghaeng.withme.screen.main.ControlExpandableAdapter;

import java.util.List;

public class SoundControlManager {
    private final ControlExpandableAdapter.ControlViewHolder holder;
    private final ControlExpandableAdapter adapter;
    private final Context context;
    private final SendDataMessage sendDataMessage;

    private ImageButton callButton, notificationButton, soundButton;
    private SeekBar soundSeekbar;
    private ImageButton muteButton;
    private TextView currentSoundPercent, targetSoundPercent;

    private int call_volume = 0, notification_volume = 0, media_volume = 0, lastSoundVolume = 0;

    public SoundControlManager(ControlExpandableAdapter.ControlViewHolder holder){
        this.holder = holder;
        this.adapter = holder.getAdapter();
        this.context = adapter.getContext();
        this.sendDataMessage = adapter.getSendDataMessage();
    }

    public void initializeViews(View itemView){
        callButton = itemView.findViewById(R.id.callButton);
        notificationButton = itemView.findViewById(R.id.notificationButton);
        soundButton = itemView.findViewById(R.id.soundButton);
        muteButton = itemView.findViewById(R.id.mute_button);
        soundSeekbar = itemView.findViewById(R.id.sound_seekbar);
        currentSoundPercent = itemView.findViewById(R.id.current_sound_percent);
        targetSoundPercent = itemView.findViewById(R.id.change_sound_percent);
    }

    public void setupSoundControl(){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (audioManager == null) {
            Log.e("ControlViewHolder", "AudioManager is null");
            return;
        }

        soundSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                targetSoundPercent.setText(String.valueOf(progress));

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
                String headerId = adapter.findItem(holder.getAdapterPosition());
                currentSoundPercent.setText(String.valueOf(seekBar.getProgress()));

                int selectedStreamType;
                switch (SoundMode.CURRENT) {
                    case SoundMode.CALL:
                        selectedStreamType = AudioManager.STREAM_RING;
                        break;
                    case SoundMode.NOTIFICATION:
                        selectedStreamType = AudioManager.STREAM_NOTIFICATION;
                        break;
                    case SoundMode.MEDIA:
                        selectedStreamType = AudioManager.STREAM_MUSIC;
                        break;
                    default:
                        selectedStreamType = AudioManager.STREAM_RING;
                        break;
                }

                if (headerId != null) {
                    sendDataMessage.sendDataMessage(headerId, "Volume",
                            String.valueOf(seekBar.getProgress()));
                }
            }
        });
        initializeVolume(audioManager);
    }


    private void initializeVolume(AudioManager audioManager) {
        int maxVolume;
        int currentVolume;

        switch (SoundMode.CURRENT) {
            case SoundMode.CALL:
                maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                break;
            case SoundMode.NOTIFICATION:
                maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
                break;
            case SoundMode.MEDIA:
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
        targetSoundPercent.setText(String.valueOf((currentVolume * 100) / maxVolume));

        if (currentVolume == 0) {
            muteButton.setImageResource(R.drawable.ic_volume_mute);
        } else {
            muteButton.setImageResource(R.drawable.ic_volume);
        }
    }

    public void setupButtonListeners(){
        muteButton.setOnClickListener(v -> {
            String headerId = adapter.findItem(holder.getAdapterPosition());
            int selectedStreamType = AudioManager.STREAM_RING;

            if (soundSeekbar.getProgress() > 0) {
                lastSoundVolume = soundSeekbar.getProgress();
                soundSeekbar.setProgress(0);
                currentSoundPercent.setText(String.valueOf(0));
                muteButton.setImageResource(R.drawable.ic_volume_mute);

                switch (SoundMode.CURRENT) {
                    case SoundMode.CALL:
                        call_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                        selectedStreamType = AudioManager.STREAM_RING;
                        break;
                    case SoundMode.NOTIFICATION:
                        notification_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                        selectedStreamType = AudioManager.STREAM_NOTIFICATION;
                        break;
                    case SoundMode.MEDIA:
                        media_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                        selectedStreamType = AudioManager.STREAM_MUSIC;
                        break;
                }
                if (headerId != null) {
                    sendDataMessage.sendDataMessage(headerId, "Volume", "0");
                }
            } else {
                soundSeekbar.setProgress(lastSoundVolume);
                currentSoundPercent.setText(String.valueOf(lastSoundVolume));
                muteButton.setImageResource(R.drawable.ic_volume);

                switch (SoundMode.CURRENT) {
                    case SoundMode.CALL:
                        call_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                        selectedStreamType = AudioManager.STREAM_RING;
                        break;
                    case SoundMode.NOTIFICATION:
                        notification_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                        selectedStreamType = AudioManager.STREAM_NOTIFICATION;
                        break;
                    case SoundMode.MEDIA:
                        media_volume = Integer.parseInt(currentSoundPercent.getText().toString());
                        selectedStreamType = AudioManager.STREAM_MUSIC;
                        break;
                }
                if (headerId != null) {
                    sendDataMessage.sendDataMessage(headerId, "Volume",
                            String.valueOf(lastSoundVolume));
                }
            }
        });

        callButton.setOnClickListener(v -> {
            String headerId = adapter.findItem(holder.getAdapterPosition());
            callButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE7363")));
            notificationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
            soundButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
            SoundMode.CURRENT = SoundMode.CALL;

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
            String headerId = adapter.findItem(holder.getAdapterPosition());
            callButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
            notificationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE7363")));
            soundButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
            SoundMode.CURRENT = SoundMode.NOTIFICATION;

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
            String headerId = adapter.findItem(holder.getAdapterPosition());
            callButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
            notificationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5EFD897F")));
            soundButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE7363")));
            SoundMode.CURRENT = SoundMode.MEDIA;

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

    public void setEnabled(boolean enabled) {
        callButton.setEnabled(enabled);
        notificationButton.setEnabled(enabled);
        soundButton.setEnabled(enabled);
        muteButton.setEnabled(enabled);
        soundSeekbar.setEnabled(enabled);
    }

    public List<View> getViews() {
        return List.of(callButton, notificationButton, soundButton, muteButton, soundSeekbar);
    }
}
