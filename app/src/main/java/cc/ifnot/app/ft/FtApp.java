package cc.ifnot.app.ft;

import android.app.Application;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;

/**
 * author: dp
 * created on: 2020/6/19 3:50 PM
 * description:
 */
public class FtApp extends Application {

    public static final String FT_ENGINE_ID = "ft";

    @Override
    public void onCreate() {
        super.onCreate();

        FlutterEngine ftEngine = new FlutterEngine(this);
        ftEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );

        ftEngine.getNavigationChannel().setInitialRoute("/");

        FlutterEngineCache
                .getInstance()
                .put(FT_ENGINE_ID, ftEngine);
    }
}
