package com.donghaeng.withme.screen.main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.message.firebasemessage.SendDataMessage;
import com.donghaeng.withme.service.RejectionManager;

public class TimeRejectDialog extends Dialog {
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private Button confirmButton;
    private Button cancelButton;
    private final String controllerToken;
    private final SendDataMessage sendDataMessage;

    private DialogInterface.OnDismissListener dismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.dismissListener = listener;
    }

    public TimeRejectDialog(Context context, String controllerToken) {
        super(context);
        this.controllerToken = controllerToken;
        this.sendDataMessage = new SendDataMessage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_time_reject);

        initializeViews();
        setupTimePickers();
        setupButtons();
    }

    private void initializeViews() {
        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);
        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void setupTimePickers() {
        // 시간: 0-24시간
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(0);

        // 분: 0-59분
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(30);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (dismissListener != null) {
            dismissListener.onDismiss(this);
        }
    }

    private void setupButtons() {
        confirmButton.setOnClickListener(v -> {
            int hours = hourPicker.getValue();
            int minutes = minutePicker.getValue();

            if (hours == 0 && minutes == 0) {
                Toast.makeText(getContext(), "시간을 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // 총 시간을 분으로 변환
            int totalMinutes = (hours * 60) + minutes;

            // 로컬에 거절 상태 저장
            RejectionManager.getInstance(getContext()).setRejection(controllerToken, totalMinutes);

            // FCM 메시지로 거절 시간 전송
            sendDataMessage.sendDataMessage(controllerToken, "reject", String.valueOf(totalMinutes));

            Toast.makeText(getContext(),
                    String.format("%d시간 %d분 동안 제어가 거절됩니다", hours, minutes),
                    Toast.LENGTH_SHORT).show();
            dismiss();  // 여기서 OnDismissListener가 호출됨
        });

        cancelButton.setOnClickListener(v -> dismiss());
    }
}