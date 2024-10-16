package com.donghaeng.withme.myscreen;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.donghaeng.withme.R;

public class ControllerConnectActivity extends AppCompatActivity {

    EditText edit_num1, edit_num2, edit_num3, edit_num4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controller_connect);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            initialize();

            return insets;
        });
    }



    void initialize(){
        edit_num1 = (EditText) findViewById(R.id.num1_edit);
        edit_num2 = (EditText) findViewById(R.id.num2_edit);
        edit_num3 = (EditText) findViewById(R.id.num3_edit);
        edit_num4 = (EditText) findViewById(R.id.num4_edit);

        edit_num1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edit_num1.getText().toString().length() == 1) {
                    edit_num2.requestFocus();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        edit_num2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edit_num2.getText().toString().length() == 1) {
                    edit_num3.requestFocus();
                } else if (edit_num2.getText().toString().isEmpty()) {
                    edit_num1.requestFocus();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        edit_num3.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edit_num3.getText().toString().length() == 1) {
                    edit_num4.requestFocus();
                } else if (edit_num3.getText().toString().isEmpty()) {
                    edit_num2.requestFocus();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        edit_num4.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edit_num4.getText().toString().isEmpty()) {
                    edit_num3.requestFocus();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}