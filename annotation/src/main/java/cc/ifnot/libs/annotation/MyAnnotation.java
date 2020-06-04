package cc.ifnot.libs.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({
        ElementType.TYPE,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.LOCAL_VARIABLE,
        ElementType.CONSTRUCTOR,
        ElementType.ANNOTATION_TYPE,
        ElementType.PACKAGE,
        ElementType.TYPE_PARAMETER,
        ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MyAnnotation {

    String value() default "default";

    String type() default "undefined";

    interface MyInterface {

        @Deprecated
        void method();
    }

    @SuppressWarnings(value = {"test", "test2"})
    class MyInterfaceImpl implements MyInterface {
        @Override
        public void method() {

        }
    }
}
