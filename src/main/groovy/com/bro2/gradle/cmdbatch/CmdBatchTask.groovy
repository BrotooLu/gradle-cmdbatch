package com.bro2.gradle.cmdbatch

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

class CmdBatchTask extends DefaultTask {

    @TaskAction
    void startExecute() throws IOException {
        try {
            project.extensions.cmdBatch.all {
                logger.info("executing '${it.name}'")
                this.executeCmd(it as Cmd)
            }
        } catch (Throwable e) {
            Throwable cause = e.getCause()
            if (cause != null) {
                throw new StopExecutionException("runCmdBatch error, cause: ${cause.getMessage()} causeString: ${e.toString()}")
            } else {
                throw new StopExecutionException("runCmdBatch error, msg: ${e.getMessage()} string: ${cause.toString()}")
            }
        }
    }

    void executeCmd(Cmd cmd) {
        cmd.checkParameters()
        def cmdArgs = []
        cmdArgs.add(cmd.name)
        if (cmd.args != null) {
            cmdArgs.addAll(cmd.args as List<String>)
        }
        File dirFile = getDesireFile(null, cmd.dir, "build/cmdbatch")
        String dir = dirFile.getCanonicalPath()
        File output = getDesireFile(dir, cmd.output, "${cmd.name}_output")
        if (!output.exists()) {
            String outputPath = output.getCanonicalPath()
            String outputParentPath = outputPath.substring(0, outputPath.lastIndexOf(File.separator))
            new File(outputParentPath).mkdirs()
            logger.info("make output dirs '${outputParentPath}'")
        }

        Utils.quickWriteLine(output, "output of '${SimpleDateFormat.getDateTimeInstance().format(new Date())}'")
        logger.info("dir: ${dir} output: ${output.getCanonicalPath()}")
        if (Utils.checkString(cmd.input)) {
            File input = new File(cmd.input)
            if (!input.exists()) {
                input.createNewFile()
                logger.warn("input file: '${input.getCanonicalPath()}' not exist")
            }
            startProcess(dirFile, input, output, cmd.env, cmdArgs)
        } else {
            startProcess(dirFile, cmd.cmds, output, cmd.env, cmdArgs)
        }
    }

    private void startProcess(File dir, File input, File output, Map<String, String> env,
                              List<String> cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd)
        Utils.appendMap(pb.environment(), env, { original, append ->
            original + File.pathSeparator + append
        })
        pb.directory(dir)
        pb.redirectInput(ProcessBuilder.Redirect.from(input))
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(output))
        pb.start().waitFor()
    }

    private void startProcess(File dir, List<String> input, File output, Map<String, String> env,
                              List<String> cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd)
        Utils.appendMap(pb.environment(), env, { original, append ->
            original + File.pathSeparator + append
        })
        pb.directory(dir)
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(output))
        Process process = pb.start()
        OutputStream os = null
        try {
            if (input != null) {
                os = process.getOutputStream()
                input.each {
                    os.write(it.getBytes())
                    os.write(System.lineSeparator().getBytes())
                }
                os.flush()
            }
            process.waitFor()
        } finally {
            Utils.closeClosable(os)
        }
    }

    private static File getDesireFile(String parent, String name, String defaultName) {
        StringBuilder filePath = new StringBuilder()
        if (Utils.checkString(parent)) {
            filePath.append(parent)
            if (!parent.endsWith(File.separator)) {
                filePath.append(File.separator)
            }
        }
        if (Utils.checkString(name)) {
            filePath.append(name)
        } else {
            filePath.append(defaultName)
        }

        return new File(filePath.toString())
    }

}