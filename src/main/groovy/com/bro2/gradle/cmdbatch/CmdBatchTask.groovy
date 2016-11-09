package com.bro2.gradle.cmdbatch

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

class CmdBatchTask extends DefaultTask {

    @TaskAction
    def startExecute() throws IOException {

        project.extensions.cmdBatch.each {
            println "${it}"
        }

        /*try {
            CmdBatchExtension cmdBatch = project.extensions.cmdBatch
            execute(cmdBatch)
        } catch (Throwable e) {
            Throwable cause = e.getCause()
            if (cause != null) {
                throw new StopExecutionException("runCmdBatch error, cause: ${cause.getMessage()} causeString: ${e.toString()}")
            } else {
                throw new StopExecutionException("runCmdBatch error, msg: ${e.getMessage()} string: ${cause.toString()}")
            }
        }*/
    }

    private void execute(CmdBatchExtension cmdBatch) {
        cmdBatch.checkParameters()

        def cmd = []
        cmd.add(cmdBatch.interpreter)
        cmd.addAll(cmdBatch.args)
        File dirFile = getDesireFile(null, cmdBatch.pwd, ".")
        String dir = dirFile.getCanonicalPath()
        File output = getDesireFile(dir, cmdBatch.output, "output")
        if (!output.exists()) {
            String outputPath = output.getCanonicalPath()
            String outputParentPath = outputPath.substring(0, outputPath.lastIndexOf(File.separator))
            new File(outputParentPath).mkdirs()
            logger.info("make output dirs '${outputParentPath}'")
        }

        Utils.quickWriteLine(output, "output of '${SimpleDateFormat.getDateTimeInstance().format(new Date())}'")
        logger.info("pwd: ${dir} output: ${output.getCanonicalPath()}")
        if (Utils.checkString(cmdBatch.input)) {
            File input = new File(cmdBatch.input)
            if (!input.exists()) {
                input.createNewFile()
                logger.warn("input file: '${input.getCanonicalPath()}' not exist")
            }
            startProcess(dirFile, input, output, cmdBatch.env, cmd)
        } else {
            startProcess(dirFile, cmdBatch.cmds, output, cmdBatch.env, cmd)
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