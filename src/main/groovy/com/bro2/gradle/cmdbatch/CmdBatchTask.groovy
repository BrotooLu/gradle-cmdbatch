package com.bro2.gradle.cmdbatch

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

class CmdBatchTask extends DefaultTask {

    @TaskAction
    void runCmdBatch() throws IOException {
        try {
            CmdBatchExtension batch = project.extensions.cmdBatch
            batch.orderedCmds.eachWithIndex { it, idx ->
                project.logger.info("executing $it: [ ${batch.cmds[it]} ]")
                executeCmd(batch.cmds[it], batch.dir, "${idx}-${it}", batch.env, batch.path)
            }
        } catch (Throwable e) {
            if (e instanceof StopExecutionException) {
                throw e
            }

            Throwable cause = e.getCause()
            if (cause != null) {
                throw new StopExecutionException("runCmdBatch error, cause: ${cause.getMessage()} err: ${e.toString()}")
            } else {
                throw new StopExecutionException("runCmdBatch error, msg: ${e.getMessage()} err: ${cause.toString()}")
            }
        }
    }

    void executeCmd(Cmd cmd, String dir, String output, Map<String, String> env, String path) {
        cmd.checkParameters()

        def cmdArgs = []
        cmdArgs.add(cmd.name)
        if (cmd.args != null) {
            cmdArgs.addAll(cmd.args as List<String>)
        }

        File dirFile = Utils.getDesireFile(null, dir, project.buildDir.getCanonicalPath()
                + File.separator + "cmdbatch", false)
        String dirPath = dirFile.getCanonicalPath()
        if (!dirFile.exists()) {
            project.logger.info("mkdirs $dirPath")
            dirFile.mkdirs()
        }

        File outputFile = Utils.getDesireFile(dirPath, output, output, true)
        StringBuilder sb = new StringBuilder();
        sb.append(SimpleDateFormat.getDateTimeInstance().format(new Date()))
        sb.append(System.lineSeparator());
        sb.append(cmdArgs);
        Utils.quickWriteWithNewLine(outputFile, sb.toString())
        startProcess(dirFile, cmd.subCmds, outputFile, env, cmdArgs, path)
    }

    void startProcess(File dir, List<String> input, File output, Map<String, String> env,
                             List<String> cmd, String path) {
        ProcessBuilder pb = new ProcessBuilder(cmd)
        Map<String, String> oriEnv = pb.environment()
        if (oriEnv == null) {
            oriEnv = System.getenv()
        }

        if (oriEnv == null) {
            project.logger.warn('process original environment is null')
        }

        if (env != null && env.size() > 0) {
            env.each {
                oriEnv.put(it.key, it.value)
            }
        }
        if (Utils.checkString(path)) {
            String pathKey = "PATH"
            if (OsDetector.isWindows()) {
                oriEnv.find {
                    if (pathKey.equalsIgnoreCase(it.key)) {
                        pathKey = it.key
                        return true
                    }
                    return false
                }
            }
            String oriPath = oriEnv.get(pathKey)
            if (Utils.checkString(oriPath)) {
                oriEnv.put(pathKey, path + File.pathSeparator + oriPath)
            } else {
                oriEnv.put(pathKey, path)
            }
        }
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

}