package com.xiaomi.youpin.gwdash.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * 操作类型
     * @return
     */
    LogType type();

    enum LogType{
        ADD(1,"添加"),
        DEL(2,"删除"),
        UPDATE(3,"更新"),
        ;
        LogType(int code,String desc){
            this.code = code;
            this.desc = desc;
        }
        private int code;
        private String desc;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }


    /**
     * 排除的内容
     * @return
     */
    Column[] exclusion() default {Column.NONE};
    enum Column{
        NONE,
        ARGS,
        RESULT,
        DATA;
    }
}
