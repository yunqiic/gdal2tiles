### 1. 代码说明
> 本使用的是jdk11,至于jdk11 的 javafx包,需要再 openfx官网下载.

### 2. 调试打包

##### 2.1 DEBUG 模式的时候 
> 需要添加vm参数 -Djava.library.path=$/FileRelativeDir$\libs204

##### 2.2 打包的时候 

> 打包时候首先配置pom.xml中 properties结点中的 outputDirectory 位置,然后执行下面命令:
```
  mvn clean package
```
> 会先打包成jar,然后拷贝libs204中的dll,以及生成的jar到指定位置. 接着执行命令:
`java -jar gdal2tiles.jar`运行


       

