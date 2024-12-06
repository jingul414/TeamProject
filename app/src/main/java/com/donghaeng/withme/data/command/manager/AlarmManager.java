package com.donghaeng.withme.data.command.manager;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.message.firebasemessage.SendDataMessage;
import com.donghaeng.withme.screen.main.ControlExpandableAdapter;

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
        });
    }

    public void setEnabled(boolean enabled) {
        setAlarmButton.setEnabled(enabled);
        hourPicker.setEnabled(enabled);
        minutePicker.setEnabled(enabled);
    }
}
