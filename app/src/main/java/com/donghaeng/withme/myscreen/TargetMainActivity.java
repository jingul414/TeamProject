package com.donghaeng.withme.myscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.donghaeng.withme.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TargetMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_target_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            //  네비게이션 설정
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent selectedIntent = null;
                    System.out.println(item.getItemId());

                    if (item.getItemId() == R.id.nav_controller) {
                        selectedIntent = new Intent(TargetMainActivity.this, ControllerMainActivity.class);
                    } else if (item.getItemId() == R.id.nav_home) {
                        selectedIntent = new Intent(TargetMainActivity.this, LoginActivity.class);
                    }

                    if(selectedIntent != null) startActivity(selectedIntent);

                    return true;
                }
            });

            return insets;
        });
    }
}