package com.bro2.gradle.cmdbatch

class Utils {

    static boolean checkString(String str) {
        return str != null && str.length() > 0
    }

    static void appendMap(Map<?, ?> map, Map<?, ?> val, Closure append) {
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

    static boolean quickWriteLine(File file, String content) {
        BufferedWriter bw = null
        try {
            bw = new BufferedWriter(new FileWriter(file))
            bw.write(content)
            bw.newLine()
        } finally {
            closeClosable(bw)
        }
    }

}