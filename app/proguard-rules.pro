-dontwarn sun.security.**
-dontwarn sys.util.**
-dontwarn **

-obfuscationdictionary proguard-dictionary.txt
-packageobfuscationdictionary proguard-dictionary.txt
-classobfuscationdictionary proguard-dictionary.txt
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute flex
-repackageclasses "org"

-keepclassmembers class ** extends sun1.security.x509.Extension{
<init>(...);
}
-keep class ** extends java.util.ListResourceBundle{
<init>(...);
}

-keep class jadx.core.clsp.ClsSet{
<init>(...);
}
-keep class org.xmlpull.v1.** { *;} -dontwarn org.xmlpull.v1.**
