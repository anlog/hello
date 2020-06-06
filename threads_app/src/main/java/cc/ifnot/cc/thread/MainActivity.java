package cc.ifnot.cc.thread;

import androidx.appcompat.app.AppCompatActivity;
import cc.ifnot.libs.utils.Lg;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static {
        Lg.tag("MainActivity");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.asyncTask:
                // asyncTask
                Lg.d("onClick asyncTask");
                MyAsyncTask myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute("a", "b", "c");

                break;

        }
    }

    static class MyAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Lg.d("onPreExecute: %s", Thread.currentThread().getName());
            Lg.d("onPreExecute");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Lg.d("onPostExecute: %s", Thread.currentThread().getName());
            Lg.d("onPostExecute: %s", s);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Lg.d("onProgressUpdate: %s", Thread.currentThread().getName());
            Lg.d("onProgressUpdate: %s", Arrays.asList(values).toString());
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            Lg.d("onCancelled: %s", Thread.currentThread().getName());
            Lg.d("onCancelled: %s", s);
        }

        @Override
        protected void onCancelled() {
            Lg.d("onCancelled: %s", Thread.currentThread().getName());
            super.onCancelled();
            Lg.d("onCancelled");
        }

        @Override
        protected String doInBackground(String... strings) {
            Lg.d("doInBackground: %s", Thread.currentThread().getName());
            Lg.d("doInBackground");
            onProgressUpdate(0, 1, 2, 3);
            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Lg.d("onCreate");

        findViewById(R.id.asyncTask).setOnClickListener(this);

    }

}
