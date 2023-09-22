package io.opentelemetry.javaagent.instrumentation.jedis.v1_4.piplinecluster;

public class ContextHolder {

    public static ThreadLocal<Boolean> mark = new ThreadLocal<>();

    public static void set(boolean isTrue){
        mark.set(isTrue);
    }

    public static Boolean get(){
        return mark.get();
    }

    public static void clear(){
        mark.remove();
    }
}
