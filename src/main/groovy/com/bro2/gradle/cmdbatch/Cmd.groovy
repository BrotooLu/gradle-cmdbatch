package com.bro2.gradle.cmdbatch

import org.gradle.api.tasks.StopExecutionException

class Cmd {
    String interpreter
    List<String> args
    List<String> cmds
    String pwd
    String output
    String input
    Map<String, String> env

    def checkParameters() {
        if (!Utils.checkString(interpreter)) {
            throw new StopExecutionException('no cmd interpreter')
        }

        if (cmds == null || cmds.size() < 1) {
            throw new StopExecutionException('no cmd need to execute')
        }
    }

}