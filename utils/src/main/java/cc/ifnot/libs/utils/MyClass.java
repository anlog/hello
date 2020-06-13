package cc.ifnot.libs.utils;

public class MyClass {

    public static void main(String[] args) {

//        https://stackoverflow.com/questions/214741/what-is-a-stackoverflowerror
//        new Object() {
//            {
//                try {
//                    getClass().newInstance();
//                } catch (InstantiationException | IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        };

        Lg.tag("LogUtils");
        Lg.showMore(true);

        Lg.i("%s - %s", "b", "hekko");

        Lg.v("%s - %s", "b", "hekko");
        Lg.d("%s - %s", "b", "hekko");
        Lg.w("%s - %s", "b", "hekko");
        Lg.e("%s - %s", "b", "hekko");
        Lg.i("%s", "b", new Exception("test"));

        Lg.e("test: %s", new Exception() instanceof Throwable);
    }
}
