package com.bro2.gradle.cmdbatch

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CmdBatchTask extends DefaultTask {

    @TaskAction
    def startExecute() throws IOException {
        CmdExtension cmds = project.extensions.cmds
        cmds.checkParameters()

        String dir
        if (cmds.pwd != null && cmds.pwd.length() > 0) {
            dir = cmds.pwd
        } else {
            dir = new File(".").getCanonicalPath()
        }

        if (dir.endsWith(File.separator)) {
            dir = dir.substring(0, dir.lastIndexOf(File.separator))
        }
        File input = getDesireFile(dir, cmds.input, "input")
        File output = getDesireFile(dir, cmds.output, "output")

        logger.info("""pwd: ${dir}
                      |input: ${input.getCanonicalPath()}
                      |output: ${output.getCanonicalPath()}
                      |""".stripMargin())

        BufferedWriter bw
        try {
            String inputPath = input.getCanonicalPath();
            String inputParent = inputPath.substring(0, inputPath.lastIndexOf(File.separator))
            File inputParentFile = new File(inputParent)
            inputParentFile.mkdirs()
            bw = new BufferedWriter(new FileWriter(input))
            cmds.cmds.each {
                bw.write(it)
                bw.newLine()
            }
        } finally {
            if (bw != null) {
                bw.close()
            }
        }

        ProcessBuilder pb = new ProcessBuilder(cmds.interpreter.split(" "))
        Map<String, String> env = cmds.env
        if (env != null) {
            Map<String, String> pbEnv = pb.environment()
            env.each {
                String pbVal = pbEnv.get(it.key)
                if (pbVal != null) {
                    pbEnv.put(it.key, it.value + File.pathSeparator + pbVal)
                } else {
                    pbEnv.put(it.key, it.value)
                }
            }
        }
        pb.redirectInput(ProcessBuilder.Redirect.from(input))
        pb.redirectOutput(ProcessBuilder.Redirect.to(output))
        pb.start()
    }

    private static File getDesireFile(String parent, String name, String defaultName) {
        if (Utils.checkString(name)) {
            return new File(parent + File.separator + name)
        } else {
            return new File(parent + File.separator + defaultName)
        }
    }

}