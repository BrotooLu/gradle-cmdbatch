package com.bro2.gradle.cmdbatch

public class OsDetector {
    
    public static String getOsName() {
        return System.getProperty("os.name").toLowerCase()
    }

    public static boolean isWindows() {
        return getOsName().indexOf("windows") >= 0
    }
}