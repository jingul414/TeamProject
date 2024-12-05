package com.donghaeng.withme.data.command.manager;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.command.LightMode;
import com.donghaeng.withme.data.database.firestore.SendDataMessage;
import com.donghaeng.withme.screen.main.ControlExpandableAdapter;

public class BrightnessControlManager {
    private final ControlExpandableAdapter.ControlViewHolder holder;
    private final ControlExpandableAdapter adapter;
    private final Context context;
    private final SendDataMessage sendDataMessage;

    private ImageButton autoLight;
    private SeekBar lightSeekbar;
    private TextView currentLightPercent, targetLightPercent;

    private int lastLightValue = 0;

    public BrightnessControlManager(ControlExpandableAdapter.ControlViewHolder holder) {
        this.holder = holder;
        this.adapter = holder.getAdapter();
        this.context = adapter.getContext();
        this.sendDataMessage = adapter.getSendDataMessage();
    }

    public void initializeViews(View itemView) {
        autoLight = itemView.findViewById(R.id.auto_light);
        lightSeekbar = itemView.findViewById(R.id.light_seekbar);
        currentLightPercent = itemView.findViewById(R.id.current_light_percent);
        targetLightPercent = itemView.findViewById(R.id.change_light_percent);
    }

    public void setupLightControl() {
        int initialBrightness = adapter.getScreenBrightness(context);
        lightSeekbar.setMax(255);
        lightSeekbar.setProgress(initialBrightness);
        currentLightPercent.setText(String.valueOf((initialBrightness * 100) / 255));

        lightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                targetLightPercent.setText(String.valueOf((progress * 100) / 255));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int brightness = seekBar.getProgress();
                String headerId = adapter.findItem(holder.getAdapterPosition());
                if (headerId != null) {
                    sendDataMessage.sendDataMessage(headerId, "Brightness",
                            String.valueOf(brightness));
                }
                //startBrightnessControlService(false, brightness, 10);
                currentLightPercent.setText(String.valueOf((brightness * 100) / 255) + "%");
            }
        });
    }

    public void setupButtonListeners() {
        autoLight.setOnClickListener(v -> {
            String headerId = adapter.findItem(holder.getAdapterPosition());
            boolean isCurrentlyAuto = adapter.isAutoBrightnessEnabled(context);

            if (isCurrentlyAuto) {
                adapter.startBrightnessControlService(false, lightSeekbar.getProgress(), 10);
                LightMode.CURRENT = LightMode.SET;
                autoLight.setImageResource(R.drawable.ic_light_mode);
                currentLightPercent.setText(String.valueOf((lightSeekbar.getProgress() * 100) / 255));
                targetLightPercent.setText(String.valueOf((lightSeekbar.getProgress() * 100) / 255));
                lightSeekbar.setEnabled(true);
                if (headerId != null) {
                    sendDataMessage.sendDataMessage(headerId, "AutoBrightness", "false");
                }
            } else {
                adapter.startBrightnessControlService(true, -1, 10);
                LightMode.CURRENT = LightMode.AUTO;
                autoLight.setImageResource(R.drawable.ic_light_mode);
                currentLightPercent.setText("Auto");
                targetLightPercent.setText("Auto");
                lightSeekbar.setEnabled(false);
                if (headerId != null) {
                    sendDataMessage.sendDataMessage(headerId, "AutoBrightness", "true");
                }
            }
        });
    }
}
