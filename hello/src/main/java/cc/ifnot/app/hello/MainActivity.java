package cc.ifnot.app.hello;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import cc.ifnot.libs.utils.Lg;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Lg.d("onCreate");

        findViewById(R.id.asyncTask).setOnClickListener(this);

        ImageView iv = findViewById(R.id.iv);
        iv.setImageBitmap(
//                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round)
                getBitmapFromDrawable(R.mipmap.ic_launcher_round)
        );


        try {
            @SuppressLint("PrivateApi")
            Class<?> clz = Class.forName("android.app.ActivityThread");
            Application app = (Application) clz.getDeclaredMethod("currentApplication").invoke(null);
            Lg.d("refect: %s", app.getClass().getTypeName());
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] bytes = digest.digest("12345".getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(Integer.toHexString(b & 0xff));
            }

            Lg.d("digest md5: %s", sb.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        testCode();
    }

    private Bitmap getBitmapFromDrawable(int ic_launcher_round) {
        Drawable drawable = getDrawable(ic_launcher_round);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable instanceof AdaptiveIconDrawable) {
            drawable = new LayerDrawable(new Drawable[]{((AdaptiveIconDrawable) drawable).getBackground(),
                    ((AdaptiveIconDrawable) drawable).getForeground()});
        }
        Bitmap b = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return b;
    }

    private void testCode() {
        ClassLoader classLoader = getClassLoader();
        Lg.e("findClassLoaders: %s - %s", classLoader.toString(), classLoader.getClass().getTypeName());
        findClassLoaders(classLoader.getParent());
    }

    private void findClassLoaders(ClassLoader classLoader) {
        if (classLoader != null) {

            try {
                Method findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
                findLoadedClass.setAccessible(true);
                Object o = findLoadedClass.invoke(classLoader, "java.lang.Object");
                Lg.e("findLoadedClass: %s", o != null ? o.toString() : null);

                Field classes = ClassLoader.class.getDeclaredField("classes");
                classes.setAccessible(true);
                Object v = classes.get(classLoader);
                Lg.e("classes: %s", v);

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                e.printStackTrace();
            }

            Lg.e("findClassLoaders: %s - %s", classLoader.toString(), classLoader.getClass().getTypeName());
            findClassLoaders(classLoader.getParent());
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
            publishProgress(1, 2, 3, 4);
            return null;
        }
    }

}