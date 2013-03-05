ant-lock
========

File based mutex lock for ant.

Use cases
* Running several builds in parallel
* Using ant-contrib's parallel tasks

Example
---
Create a build.xml file with the following code:
```xml
<project default="build">
  <taskdef name="lock" classname="com.simedw.ant.Lock"/>

  <target name="build">
    <lock name="sleep">
      <echo message="I got the lock"/>
      <sleep second="10"/>
    </lock>
  </target>
</project>
```

From two terminals run:
```sh
ant build
```
One instance will recieve the lock, print out "I got the lock" and then sleep for 10 seconds before releasing the lock. 
The other instance will wait for about 10 seconds before it succeeds in claiming the lock.
A file named "lock-sleep" while be created in the current directory. If any of the nestled task failes, the lock will be released. 
