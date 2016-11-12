package com.bro2.gradle.cmdbatch

import org.gradle.api.Plugin
import org.gradle.api.Project

class CmdBatchPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('cmdBatch', CmdBatchExtension)
        project.tasks.create('runCmdBatch', CmdBatchTask)
        if (project.hasProperty('debug')) {
            project.tasks.create('debug', {
                doLast {
                    project.logger.info("executing debug task...");
                }
            })
        }
        project.afterEvaluate {
            String after = project.cmdBatch.runCmdBatchAfter
            if (Utils.checkString(after)) {
                def before = project.tasks.findByName(after)
                if (before == null) {
                    project.logger.error("no task '${after}' found in project")
                } else {
                    before.finalizedBy(project.tasks.runCmdBatch)
                }
            } else {
                project.logger.info("no runCmdBatchAfter assigned")
            }
        }
    }
}