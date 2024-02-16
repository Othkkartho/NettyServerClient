### Netty + OSHI + Message Pack

### Development Enviroment
* OS: Windows 11
* Java: OpenJDK 1.8
* IntelliJ: 2023.3.4
* Build Automation: Gradle(8.4) + Groovy
* Encoding: UTF-8

### Library
* [OSHI](https://mvnrepository.com/artifact/com.github.oshi/oshi-core): 5.8.7
* ~~[Netty - All In One](https://mvnrepository.com/artifact/io.netty/netty-all): 4.1.106.Final~~
* [Netty - Buffer](https://mvnrepository.com/artifact/io.netty/netty-buffer/4.1.106.Final): 4.1.106.Final
* [Netty - Handler](https://mvnrepository.com/artifact/io.netty/netty-handler/4.1.106.Final): 4.1.106.Final
* [MessagePack-core](https://mvnrepository.com/artifact/org.msgpack/msgpack-core): 0.8.24
* 지워도 되는 의존성: slf4j, jna, jna-platform, resolver

### 설명

* 폴더 설명

| 폴더 이름 | 설명 |
| :---: | :---: |
| Client | Netty Client Code |
| Server | Netty Server Code |
| Util | Except Main Server and Client Code. There is utility code in the file. |
