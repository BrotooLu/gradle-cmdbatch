package com.bro2.gradle.cmdbatch

import org.gradle.api.tasks.StopExecutionException

class Cmd {
    String name
    List<String> args
    List<String> subCmds

    String toString() {
        return "name: $name args: $args subCmds: $subCmds"
    }

    def checkParameters() {
        if (!Utils.checkString(name)) {
            throw new StopExecutionException('illegal cmd name: empty')
        }
    }
}