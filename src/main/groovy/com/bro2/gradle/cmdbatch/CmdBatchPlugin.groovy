package com.bro2.gradle.cmdbatch

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project

class CmdBatchPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        NamedDomainObjectContainer<Cmd> cmdBatch = project.container(Cmd)
        project.extensions.add('cmdBatch', cmdBatch)
        project.tasks.create('runCmdBatch', CmdBatchTask)
        project.afterEvaluate {
            if (!project.ext.has('runCmdBatchAfter')) {
                project.logger.info("no runCmdBatchAfter property")
                return
            }
            String after = project.ext.get('runCmdBatchAfter')
            if (Utils.checkString(after)) {
                def before = project.tasks.findByName(after)
                if (before == null) {
                    project.logger.error("no task '${after}' found in project")
                } else {
                    before.finalizedBy(project.tasks.runCmdBatch)
                }
            }
        }
    }
}