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
        boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0
        if (!isWindows) {
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

        String child = name
        if (!checkString(child)) {
            if (!checkString(defaultName)) {
                throw new IllegalArgumentException("no name or default name assigned")
            }
            child = defaultName
        }

        child = formatPathAsUnixStyle(child)

        if (!replaceNameSeparator) {
            filePath.append(child)
        } else {
            String replace
            if (rep == null || rep.length < 1) {
                replace = "_"
            } else {
                replace = rep[0]
            }

            if (hasParent) {
                child = child.replaceAll(":", "")
            }
            filePath.append(child.replaceAll("/", replace))
        }

        return new File(filePath.toString())
    }


}