# ExceptionRecordManager
PermissionManager为单例类，一般在继承Application的自定义类重写onCreate()，调用如下代码：
```java
ExceptionRecordManager.init(this).start();
```
Usage
===
Step 1. Add it in your root build.gradle at the end of repositories:
```java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```java
	dependencies {
	        implementation 'com.github.iwhoyoung:ExceptionRecordManager:1.0.0'
	}
```