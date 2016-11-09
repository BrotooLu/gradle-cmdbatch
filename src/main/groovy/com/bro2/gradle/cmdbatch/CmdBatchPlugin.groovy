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
    }
}