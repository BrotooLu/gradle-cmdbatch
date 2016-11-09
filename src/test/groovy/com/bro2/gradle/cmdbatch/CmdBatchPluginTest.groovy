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
        def content =
                """|plugins {
                   | id 'com.bro2.gradle.cmd-batch'
                   |}
                   |cmdBatch {
                   |    bash {
                   |        input = 'build/test/input'
                   |        output = 'build/test/output'
                   |        env = [PATH: '/Users/bro2/Library/Android/sdk/platform-tools']
                   |    }
                   |    ps {
                   |        args = ['-l']
                   |        output = 'build/test/outputps'
                   |        env = [PATH: '/Users/bro2/Library/Android/sdk/platform-tools']
                   |    }
                   |}
                   |""".stripMargin()

        writeFile(buildFile, content)

        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectFolder.root)
                .withArguments("-i", "-s", "runCmdBatch")
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