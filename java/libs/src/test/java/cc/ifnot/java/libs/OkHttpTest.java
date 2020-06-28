package cc.ifnot.java.libs;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

import cc.ifnot.libs.utils.Lg;
import kotlin.Pair;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.connection.RouteException;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

/**
 * author: dp
 * created on: 2020/6/27 9:50 AM
 * description:
 */
class OkHttpTest {

    @BeforeAll
    static void setUpAll() {
        Lg.level(Lg.MORE);
        Lg.d("============ all in ============");
    }

    @AfterAll
    static void tailDownAll() {
        Lg.d("============ all out ============");
    }

    @Test
    void testOkHttpBasic() throws IOException {
        final OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        // mock failed
                        final Request request = chain.request();
                        Lg.d("-> %s %s", request.method(), request.url());
                        final Headers headers = request.headers();

                        for (Pair<? extends String, ? extends String> header : headers) {
                            Lg.d("%s: %s", header.getFirst(), header.getSecond());
                        }
                        Lg.d();

                        final RequestBody body = request.body();
                        if (body != null) {
                            Buffer buf = new Buffer();
                            body.writeTo(buf);
                            Lg.d("%s - %s", body.contentType(), body.contentLength());
                            Lg.d(buf.readString(Charset.defaultCharset()));
                        }

                        final Response response = chain.proceed(request);

                        Lg.d("-> %s %s", response.code(), response.message());

                        final Headers resHeaders = response.headers();
                        for (Pair<? extends String, ? extends String> header : resHeaders) {
                            Lg.d("%s: %s", header.getFirst(), header.getSecond());
                        }
                        Lg.d();

                        if (HttpHeaders.promisesBody(response)) {

                            final BufferedSource source = Objects.requireNonNull(response.body()).source();
                            source.request(Integer.MAX_VALUE);

                            Buffer buffer = source.getBuffer();
                            Long gzippedLength = null;
                            if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
                                gzippedLength = buffer.size();
                                try (GzipSource gzippedResponseBody = new GzipSource(buffer.clone())) {
                                    buffer = new Buffer();
                                    buffer.writeAll(gzippedResponseBody);
                                }
                            }

                            Lg.d("%s", buffer.clone().readString(Charset.defaultCharset()));
                            Lg.d("-> %s: %s", gzippedLength != null ? "gzip length" : "length",
                                    gzippedLength != null ? gzippedLength : buffer.size());
                        }

                        // mock
                        if (true)
                            throw new RouteException(new IOException("mock"));
                        return new Response.Builder()
                                .code(500)
                                .request(request)
                                .protocol(Protocol.HTTP_1_1)
                                .addHeader("content-type", "application/json")
                                .message("mock message")
                                .body(ResponseBody.create(MediaType.parse("application/json"),
                                        Objects.requireNonNull(response.body()).source().getBuffer().clone().readByteString()))
                                .build();
                    }
                }).build();

        try (final Response res = client.newCall(new Request.Builder()
                .url("https://api.ipify.org?format=json")
                .build()).execute()) {
            Lg.d("== %s %s %s", res.code(), res.message(), Objects.requireNonNull(res.body()).string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @BeforeEach
    void setUp() {
        Lg.d("============ in ============");
    }

    @AfterEach
    void tailDown() {
        Lg.d("============ out ============");
    }
}
