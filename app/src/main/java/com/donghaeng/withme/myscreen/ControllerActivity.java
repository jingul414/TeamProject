package com.donghaeng.withme.myscreen;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.donghaeng.withme.R;

public class ControllerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controller);

        // Fragment 초기화 로직을 분리
        if (savedInstanceState == null && getIntent().getStringExtra("fragmentName") != null) {
            if(getIntent().getStringExtra("fragmentName").equals("controller_QR")){
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, new ControllerConnectFragment())
                        .commit();
            }else{
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, new ControllerMainFragment())
                        .commit();
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}