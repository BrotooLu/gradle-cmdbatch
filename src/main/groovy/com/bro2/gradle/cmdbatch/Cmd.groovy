package com.bro2.gradle.cmdbatch

import org.gradle.api.tasks.StopExecutionException

class Cmd {
    String name
    List<String> args
    List<String> cmds
    String dir
    String output
    String input
    Map<String, String> env

    Cmd(String name) {
        this.name = name
    }

    def checkParameters() {
        if (!Utils.checkString(name)) {
            throw new StopExecutionException('no cmd assigned')
        }
    }

}