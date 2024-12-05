package com.donghaeng.withme.data.command.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.database.firestore.SendDataMessage;
import com.donghaeng.withme.screen.main.ControlExpandableAdapter;
import com.donghaeng.withme.service.AlarmService;

import java.util.Calendar;

public class AlarmManager {
    private final ControlExpandableAdapter.ControlViewHolder holder;
    private final ControlExpandableAdapter adapter;
    private final Context context;
    private final SendDataMessage sendDataMessage;

    NumberPicker hourPicker, minutePicker;
    Button setAlarmButton;

    public AlarmManager(ControlExpandableAdapter.ControlViewHolder holder) {
        this.holder = holder;
        this.adapter = holder.getAdapter();
        this.context = adapter.getContext();
        this.sendDataMessage = adapter.getSendDataMessage();
    }

    public void initializeViews(View itemView) {
        setAlarmButton = itemView.findViewById(R.id.setAlarmButton);
        hourPicker = itemView.findViewById(R.id.hourPicker);
        minutePicker = itemView.findViewById(R.id.minutePicker);
    }

    public void setupTimePicker() {
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

    public void setupButtonListeners() {
        setAlarmButton.setOnClickListener(v -> {
            String headerId = adapter.findItem(holder.getAdapterPosition());
            int hour = hourPicker.getValue();
            int minute = minutePicker.getValue();
            if (headerId != null) {
                sendDataMessage.sendDataMessage(headerId, "Alarm",
                        String.format("%02d:%02d", hour, minute));
            }
            checkPermissionsAndScheduleAlarm();
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
