# gradle-cmdbatch

### 用途
在gradle工程中自动化执行某些脚本，比如写一个用于 Android 的库或者可执行文件，可以用来编译完成后自动 push 到手机上。
### 更新说明
1、修复了不能重复执行同一个程序的问题；<br/>
2、统一使用一个工作目录和环境变量；<br/>
3、在某 task 后自动执行 runCmdBatch 的配置位置放在cmdBatch内；<br/>
4、不再提供直接读取输入文件的功能；<br/>
5、不再提供设置输出路径的功能，而是直接输出在工作目录下，按“程序下标+名字+同名下标“命名;<br/>
6、[v0.1.0 文档 README](https://github.com/liu-bro2/gradle-cmdbatch/tree/v0.1.0)
7、特殊对待环境变量PATH，使用独立的配置
### 使用方式
#### 一、自动档

编辑你的项目 build.gradle 示例：

```
buildscript {
    dependencies {
        classpath 'com.bro2.gradle:cmdbatch:0.2.2'
    }
    repositories {
        maven {
            url  "https://dl.bintray.com/bro2/repo-maven-gradle" 
        }
    }
}

apply plugin: 'com.bro2.gradle.cmd-batch'

cmdBatch {
    dir = '.'                          // [optional] 工作目录，默认为工程目录/build/cmdbatch
    env = ['optional'：'optional']     // [optional] 环境变量
    runCmdBatchAfter = 'build'         // [optional] 在 build 后自动执行
    path = '...'                       // [optional] 追加PATH环境变量

    cmd {
        name = 'adb'                   // [mandatory] 运行程序adb
        args = ['shell']               // [optional] 参数
        subCmds = ['ls', 'exit']       // [optional] 程序adb解析的命令
    }

    cmd {
        name = 'ls'
        args = ['-a', '-l']
    }
    
    cmd {
        name = 'ls'
    }

    // ...
}
```

#### 二、手动档

1、clone 源码；<br/>
2、生成jar包；(可以运行gradle/gradlew jar，AndroidStudio用户可以直接点击task面板的jar)；<br/>
3、将生成的jar包导入工程目录某个文件夹，如build-lib；<br/>

[不编译获取jar包请点击](https://dl.bintray.com/bro2/repo-maven-gradle/com/bro2/gradle/cmdbatch/0.2.2/)
build.gradle编写示例同一，但需要修改dependencies：

```
buildscript {
    dependencies {
        classpath fileTree(dir: 'build-lib', include:'*.jar')
    }
}
```

如果不想要手动拷贝jar包，可以利用 mavenLocal 帮忙，步骤：<br/>
1、clone 源码; <br/>
2、运行publishToMavenLocal; <br/>
3、修改工程依赖；<br/>

```
buildscript {
    dependencies {
        classpath 'com.bro2.gradle:cmdbatch:0.2.2'
    }
    repositories {
        mavenLocal()
    }
}
```

### 注意事项
1、对于在解析器(a)中执行解析器(b)的情况，有的b会将所有剩下的输入都读完，导致任务一直处于运行状态无法结束，此类情况只能用其它方式解决，比如多次执行该执行器或者看看解释器有没有解决方案eg:<br/>
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
    cmd {
        args = ['shell']
        subCmds = ['ls', "su -c id", 'ps', 'exit']
    }
}
```