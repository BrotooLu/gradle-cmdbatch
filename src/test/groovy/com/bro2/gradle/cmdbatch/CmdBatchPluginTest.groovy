package com.bro2.gradle.cmdbatch

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CmdBatchPluginTest {
    @Rule
    public final TemporaryFolder testProjectFolder = new TemporaryFolder()
    File buildFile
    List<File> pluginClasspath

    @Before
    void initProject() {
        buildFile = testProjectFolder.newFile("build.gradle")
        def classpathResource = getClass().classLoader.findResource("plugin-classpath.txt")
        if (classpathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource")
        }

        pluginClasspath = classpathResource.readLines().collect {
            new File(it)
        }
    }

    @Test
    void test() {
        /*def content =
                """|plugins {
                   | id 'com.bro2.gradle.cmd-batch'
                   |}
                   |cmdBatch {
                   |    interpreter 'adb'
                   |    args = ['shell']
                   |    //pwd "${testProjectFolder.root.getCanonicalPath()}"
                   |    input 'build/test/input'
                   |    output 'build/test/output'
                   |    cmds = ['ls', 'id', 'exit']
                   |    reRunCmds = [['cd /data/local/tmp', 'pwd', 'exit'], ['cd /data/local/tmp', 'ls', 'exit']]
                   |    env = [PATH: '/Users/bro2/Library/Android/sdk/platform-tools']
                   |}
                   |""".stripMargin()*/
        def content =
                """|plugins {
                   | id 'com.bro2.gradle.cmd-batch'
                   |}
                   |cmdBatch {
                   |    cmd {
                   |        interpreter 'adb1'
                   |        args = ['shell']
                   |        output 'build/test/output'
                   |        cmds = ['ls', 'id', 'exit']
                   |        env = [PATH: '/Users/bro2/Library/Android/sdk/platform-tools']
                   |    }
                   |    cmd {
                   |        interpreter 'adb2'
                   |        args = ['shell']
                   |        output 'build/test/output'
                   |        cmds = ['ls', 'id', 'exit']
                   |        env = [PATH: '/Users/bro2/Library/Android/sdk/platform-tools']
                   |    }
                   |}
                   |""".stripMargin()

        writeFile(buildFile, content)

        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectFolder.root)
                .withArguments("-i", "runCmdBatch")
                .withPluginClasspath(pluginClasspath)
                .build()

        println result.output
    }

    private static void writeFile(File destination, String content) throws IOException {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(destination));
            output.write(content);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

}