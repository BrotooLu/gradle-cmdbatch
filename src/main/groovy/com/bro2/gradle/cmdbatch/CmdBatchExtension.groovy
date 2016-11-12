package com.bro2.gradle.cmdbatch

import org.gradle.api.Action
import org.gradle.api.tasks.StopExecutionException

class CmdBatchExtension {
    Map<String, Cmd> cmds = new HashMap<>()
    List<String> orderedCmds = []
    String dir
    Map<String, String> env
    String runCmdBatchAfter

    def checkParameters() {
        if (orderedCmds.size() < 1 || cmds.size() < 1) {
            throw new StopExecutionException('no cmd need to execute')
        }
    }

    def cmd(Action<? extends Cmd> action) {
        Cmd _cmd = new Cmd()
        action.execute(_cmd)

        String name = _cmd.name
        if (!Utils.checkString(name)) {
            throw new IllegalArgumentException("cmd name can't be empty")
        }

        if (cmds[name] != null) {
            int index = 1
            while (true) {
                name += "$index"
                if (cmds[name] == null) {
                    break
                }
            }
        }
        orderedCmds.add(name)
        cmds.put(name, _cmd)
    }

}