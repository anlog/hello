import android.app.Application
import android.content.Context
import cc.ifnot.app.BuildConfig
import cc.ifnot.libs.utils.Lg
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor

/**
 * author: dp
 * created on: 2020/5/7 3:29 PM
 * description:
 */

open class App() : Application() {

    private val FT_ENGINE_ID = "ft"

    init {
        Lg.tag("App")
        Lg.level(if (BuildConfig.DEBUG) Lg.DEBUG else Lg.WARN)
        Lg.d()
    }

    // this will never here.
    constructor(o: Any) : this() {
        Lg.w("constructor")
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Lg.w("attachBaseContext %s", base)

        Lg.w("mapLibraryName: ${System.mapLibraryName("native-lib")}")
    }

    override fun onCreate() {
        super.onCreate()

        val ftEngine = FlutterEngine(this)
        ftEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )

        ftEngine.navigationChannel.setInitialRoute("/")

        FlutterEngineCache
            .getInstance()
            .put(FT_ENGINE_ID, ftEngine)
    }
}