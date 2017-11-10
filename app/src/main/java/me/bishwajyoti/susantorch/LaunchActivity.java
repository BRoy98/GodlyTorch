package me.bishwajyoti.susantorch;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chrisplus.rootmanager.RootManager;

public class LaunchActivity extends AppCompatActivity {

    private TextView logText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        logText = findViewById(R.id.logText);
        progressBar = findViewById(R.id.progressBar);

        if (RootManager.getInstance().hasRooted()) {
            logText.setText("Obtaining root access...");
            progressBar.setVisibility(View.VISIBLE);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(RootManager.getInstance().obtainPermission()) {
                        logText.setText("Root access granted!");
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        logText.setText("Root access Denied. Please grant root access to continue.");
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }, 1200);

        } else {
            logText.setText("Device is not rooted!");
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
