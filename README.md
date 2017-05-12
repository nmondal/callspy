callspy
=======

A simple tracing agent

Build:
gradlew jar

Run:
```
java -DXbootclasspath/p:build/libs/javassist-3.22.0-CR1.jar:build/libs/callspy-0.1.jar -javaagent:build/libs/callspy-0.1.jar Test
```