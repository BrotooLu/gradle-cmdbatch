# gradle-cmdbatch
#### 用途
在gradle工程中自动化执行某些脚本，比如写一个用于 Android 的库或者可执行文件，可以用来编译完成后自动 push 到手机上。

#### 配置格式

```
cmdBatch {
    cmd-name {
        args = ['optional']            // 参数
        dir 'optional'                 // 工作目录，默认当前目录
        input 'optional'               // 重定向输入方式需要
        output 'optional'              // 重定向输出，默认当前目录/output
        cmds = ['optional']            // 数组-直接配置批命令方式
        env = [optional：'optional']   // 环境变量
    }

    ...
}
```

```
cmdBatch {
    bash {
        input = 'build/test/input'
        output = 'build/test/output'
        env = [PATH: '/Users/bro2/Library/Android/sdk/platform-tools']
    }
    ps {
        args = ['-l']
        output = 'build/test/outputps'
        env = [PATH: '/Users/bro2/Library/Android/sdk/platform-tools']
    }
}
cmdBatch {
    interpreter 'mandatory'        // 命令解析器 eg: adb
    args = ['optional']            // 参数 eg: shell
    pwd 'optional'                 // 工作目录，默认当前目录
    input 'optional'               // 重定向输入方式需要
    output 'optional'              // 重定向输出，默认当前目录/output
    cmds = ['optional']            // 数组-直接配置批命令方式
    env = [optional：'optional']   // 环境变量
}
```

### 注意事项
对于在解析器(a)中执行解析器(b)的情况，有的b会将所有剩下的命令都读完，导致任务一直处于运行状态无法结束，此类情况需要使用reRunCmds,eg:
adb shell
ls
su
id
exit
ps
exit
某些su会把id exit ps exit都读进来，导致adb无法执行 ps 和 exit，则可以使用如下方式运行：
```
cmdBatch {
    adb {
        args = ['shell']
        cmds = ['ls', "su -c id", 'ps', 'exit']
    }
}
```