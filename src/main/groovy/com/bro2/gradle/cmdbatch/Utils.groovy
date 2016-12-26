package com.bro2.gradle.cmdbatch

import java.util.regex.Pattern

class Utils {

    public static boolean checkString(String str) {
        return str != null && str.length() > 0
    }

    public static void appendMap(Map<?, ?> map, Map<?, ?> val, Closure append) {
        if (map == null || val == null)
            return

        val.each {
            Object original = map.get(it.key)
            if (original != null) {
                map.put(it.key, append(it.value, original))
            } else {
                map.put(it.key, it.value)
            }
        }
    }

    public static void closeClosable(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close()
            }
        } catch (Throwable e) {
            e.printStackTrace()
        }
    }

    public static boolean quickWriteWithNewLine(File file, String content) {
        BufferedWriter bw = null
        try {
            bw = new BufferedWriter(new FileWriter(file))
            bw.write(content)
            bw.newLine()
            return true
        } finally {
            closeClosable(bw)
        }
    }

    private static String formatPathAsUnixStyle(String path) {
        if (!OsDetector.isWindows()) {
            return path
        }

        String separator = File.separator
        if (path.indexOf(separator) < 0) {
            return path
        }

        return path.replaceAll(Pattern.quote(File.separator), "/")
    }

    public static File getDesireFile(String parent, String name, String defaultName,
                                     boolean replaceNameSeparator, String... rep) {
        StringBuilder filePath = new StringBuilder()
        boolean hasParent = checkString(parent)
        if (hasParent) {
            String parentPath = formatPathAsUnixStyle(parent)
            filePath.append(parentPath)
            if (!parentPath.endsWith("/")) {
                filePath.append("/")
            }
        }

        String child = name;
        if (!checkString(child)) {
            child = defaultName;
        }

        if (!checkString(child)) {
            throw new IllegalArgumentException("child name illegal: " + child)
        }

        child = formatPathAsUnixStyle(child)

        if (!replaceNameSeparator) {
            filePath.append(child)
        } else if (rep == null || rep.length < 1) {
            filePath.append(child.replaceAll(Pattern.quote(File.separator), "_"))
        } else {
            filePath.append(child.replaceAll(Pattern.quote(File.separator), rep[0]))
        }

        return new File(filePath.toString())
    }


}