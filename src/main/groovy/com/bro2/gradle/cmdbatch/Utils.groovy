package com.bro2.gradle.cmdbatch

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
        if (closeable != null) {
            closeable.close()
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

    public static File getDesireFile(String parent, String name, String defaultName) {
        StringBuilder filePath = new StringBuilder()
        if (checkString(parent)) {
            filePath.append(parent)
            if (!parent.endsWith(File.separator)) {
                filePath.append(File.separator)
            }
        }
        if (checkString(name)) {
            filePath.append(name)
        } else {
            filePath.append(defaultName)
        }

        return new File(filePath.toString())
    }


}