# gradle-cmdbatch

### 用途
在gradle工程中自动化执行某些脚本，比如写一个用于 Android 的库或者可执行文件，可以用来编译完成后自动 push 到手机上。
### 使用方式
#### 一、自动档

优点：不需要源码；缺点：可能需要wall网。<br/>
build.gradle编写示例：

```
buildscript {
    dependencies {
        classpath 'com.bro2.gradle:cmdbatch:0.1.0'
    }
    repositories {
        maven {
            url  "https://dl.bintray.com/bro2/repo-maven-gradle" 
        }
    }
}

apply plugin: 'com.bro2.gradle.cmd-batch'

ext {
    runCmdBatchAfter = 'build'         // 在 build 执行后自动执行
}

cmdBatch {
    adb {
        args = ['shell']               // 参数
        dir = '.'                      // 工作目录，默认为工程目录/build/cmdbatch
        cmds = ['ls', 'exit']          // adb 解析命令方式：数组-配置
        input = 'optional'             // adb 解析命令方式：直接从文件里面读取
        output = 'optional'            // 重定向输出，默认为工程目录/build/cmdbatch/*_output
        env = ['optional'：'optional'] // 环境变量
    }

    ls {
        args = ['-al']
    }
    
    ps {
    }

    // ...
}
```

#### 二、手动档

1、clone 源码；<br/>
2、生成jar包；(可以运行gradle/gradlew jar，AndroidStudio用户可以直接点击task面板的jar)；<br/>
3、将生成的jar包导入工程目录某个文件夹，如build-lib；<br/>
build.gradle编写示例同一，但需要修改dependencies：

```
buildscript {
    dependencies {
        classpath files('build-lib/cmdbatch-0.1.0.jar')
    }
}
```

### 注意事项
对于在解析器(a)中执行解析器(b)的情况，有的b会将所有剩下的命令都读完，导致任务一直处于运行状态无法结束，此类情况只能用其它方式解决，比如多次执行任务或者看看解释器有没有解决方案eg:<br/>
adb shell<br/>
ls<br/>
su<br/>
id<br/>
exit<br/>
ps<br/>
exit<br/>
某些su会把id exit ps exit都读进来，导致adb无法执行 ps 和 exit，则可以使用如下方式运行：

```
cmdBatch {
    adb {
        args = ['shell']
        cmds = ['ls', "su -c id", 'ps', 'exit']
    }
}
```