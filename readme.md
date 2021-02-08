# Incubator

Incubator服务于[Eggs](https://github.com/hexffff0/eggs)项目，目的是方便你编写Eggs的执行单元。

## TODO

- [ ] 完善使用文档
- [ ] Intellij IDEA API文档

# Usage

1. 克隆该项目`git clone https://github.com/hexffff0/incubator.git`

2. 国内用Gradle下载依赖会比较慢，可以考虑使用代理

    ```properties
    # 在~/.gradle/gradle.properties文件添加以下内容
    systemProp.http.proxyHost=127.0.0.1
    systemProp.http.proxyPort=1080
    systemProp.https.proxyHost=127.0.0.1
    systemProp.https.proxyPort=1080
    ```

3. 该项目会下载并使用一个社区版的IDEA，如果你想使用系统已安装的IDEA

    ```groovy
    // 修改项目根目录的build.grale文件
    intellij {
        plugins = ['java']
        // 使用系统安装的IDEA的路径
        intellij.localPath '/Applications/IntelliJ IDEA.app'
        // 下载2020.2.2版的IDEA
        // intellij.version '2020.2.2'
        // intellij.localPath与intellij.version不能共存
    }
    ```

4. 在该项目下编写代码，然后复制到EggsSettings，你可以使用任何你在该项目下能引用到的类

5. 如果你需要引用第三方Jar包，在EggsSettings设置libPath

    ```
    libPath可以是一到多个目录和文件，它们之间通过:分割，例如
    /lib1/lib1.jar:/lib2
    ```

    

# License

``` 
Copyright 2021 com.github.hexffff0

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```

