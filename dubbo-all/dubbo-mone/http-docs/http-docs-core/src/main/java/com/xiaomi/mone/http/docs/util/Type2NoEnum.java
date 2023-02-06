package com.xiaomi.mone.http.docs.util;

/**
 * '0': '[string]',
 * '1': '[file]',
 * '2': '[json]',
 * '3': '[int]',
 * '4': '[float]',
 * '5': '[double]',
 * '6': '[date]',
 * '7': '[datetime]',
 * '8': '[boolean]',
 * '9': '[byte]',
 * '10': '[short]',
 * '11': '[long]',
 * '12': '[array]',
 * '13': '[object]',
 * '14': '[number]'
 */
public enum Type2NoEnum {
    STRING_NO("0"),
    File_NO("1"),
    JSON_NO("2"),
    INT_NO("3"),
    FLOAT_NO("4"),
    DOUBLE_NO("5"),
    DATE_NO("6"),
    DATETIME_NO("7"),
    BOOLEAN_NO("8"),
    BYTE_NO("9"),
    SHORT_NO("10"),
    LONG_NO("11"),
    ARRAY_NO("12"),
    OBJ_NO("13"),
    NUM_NO("14");

    private String value;

    Type2NoEnum(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
