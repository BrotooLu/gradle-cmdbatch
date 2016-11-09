package com.bro2.gradle.cmdbatch

import org.gradle.api.Plugin
import org.gradle.api.Project

class CmdBatchPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('cmdBatch', CmdBatchExtension, project)
        project.tasks.create('runCmdBatch', CmdBatchTask)
    }
}