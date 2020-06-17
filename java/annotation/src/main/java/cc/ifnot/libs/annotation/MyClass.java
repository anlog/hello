package cc.ifnot.libs.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@MyAnnotation(type = "class")
public class MyClass {

    @MyAnnotation(value = "method", type = "object_method")
    void method() {

    }

    @MyAnnotation(value = "main", type = "class_method")
    public static void main(@MyAnnotation String[] args) {
        MyAnnotation annotation = MyClass.class.getAnnotation(MyAnnotation.class);
        System.out.println(annotation.value());
        System.out.println(annotation.type());
        try {

            Method[] declaredMethods = MyClass.class.getDeclaredMethods();
            for (Method m : declaredMethods) {
                System.out.println("-- " + m.getName());
            }

            MyAnnotation main = MyClass.class.getDeclaredMethod("main", String[].class).getAnnotation(MyAnnotation.class);
            System.out.println(main.value());
            System.out.println(main.type());

            MyAnnotation method = MyClass.class.getDeclaredMethod("method", (Class<?>[]) null).getAnnotation(MyAnnotation.class);
            System.out.println(method.value());
            System.out.println(method.type());

            Annotation[][] parameterAnnotations = MyClass.class.getDeclaredMethod("main", String[].class).getParameterAnnotations();
            for (Annotation[] as : parameterAnnotations) {
                for (Annotation a : as) {
                    System.out.println("---- " + a.toString());
                }
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}