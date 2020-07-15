package cc.ifnot.app.ft;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cc.ifnot.libs.utils.Lg;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.android.FlutterActivityLaunchConfigs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static {
        Lg.tag("MainActivity");
        Lg.level(Lg.MORE);
    }

    private TextView ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ft = findViewById(R.id.ft);
        ft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ft:
                Lg.d("ft click");
                ft.setText("ft clicked");

                startActivity(FlutterActivity
                        .withCachedEngine(FtApp.FT_ENGINE_ID)
                        // todo: bug here https://github.com/flutter/flutter/issues/56125
//                        .backgroundMode(FlutterActivityLaunchConfigs.
//                                BackgroundMode.transparent)
                        .build(this));
//                startActivity(FlutterActivity.createDefaultIntent(this));
                break;
            default:
                break;
        }
    }
}
