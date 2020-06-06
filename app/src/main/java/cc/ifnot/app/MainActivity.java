package cc.ifnot.app;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import cc.ifnot.libs.utils.Lg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        Lg.tag(TAG);
        Lg.w("loadLibrary");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final String url = "https://api.ipify.org?format=json";

        findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lg.w("onClick");
                http_test(url);
            }
        });

        if (false) {
            return;
        }

        try {
            // Try installing new SSL provider from Google Play Service
            Context gms = createPackageContext("com.google.android.gms",
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            gms.getClassLoader()
                    .loadClass("com.google.android.gms.common.security.ProviderInstallerImpl")
                    .getMethod("insertProvider", Context.class)
                    .invoke(null, gms);
        } catch (Exception e) {
//            if (Build.VERSION.SDK_INT < 21) {
            // Failed to update SSL provider, use NoSSLv3SocketFactory on SDK < 21
            // and return false to notify potential issues
//                HttpsURLConnection.setDefaultSSLSocketFactory(new NoSSLv3SocketFactory());
//                return false;
//            }
            e.printStackTrace();
        }

        Lg.e("onCreate");
        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());


    }

    private void http_test(final String url) {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                BufferedReader bufferedReader = null;
                StringBuffer sb = new StringBuffer();

                Lg.tag("http_test");
                Lg.w("begin");
                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                    inputStream = urlConnection.getInputStream();
                    Lg.w("begin1");
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        Lg.w(line);
                        sb.append(line);
                        sb.append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                    if (sb.length() > 0) {
                        try {
                            JSONObject jsonObject = new JSONObject(sb.toString());
                            final String ip = jsonObject.getString("ip");
                            findViewById(android.R.id.content).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((TextView) findViewById(R.id.sample_text)).setText(ip);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    Lg.w("end");
                    try {
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }

                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Lg.w("end1");
                }
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
