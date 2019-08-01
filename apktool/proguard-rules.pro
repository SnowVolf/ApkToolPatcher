# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\tools\adt-bundle-windows-x86_64-20131030\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn sun.security.**
-dontwarn sys.util.**
#-dontobfuscate

-obfuscationdictionary proguard-dictionary.txt
-packageobfuscationdictionary proguard-dictionary.txt
-classobfuscationdictionary proguard-dictionary.txt
-repackageclasses 'huesos'
-keepattributes SourceFile,LineNumberTable
-keepclassmembers class ** extends sun1.security.x509.Extension {
<init>(...);
}
-keep class ** extends java.util.ListResourceBundle{
<init>(...);
}

-keep class jadx.core.clsp.ClsSet{
<init>(...);
}
-keep class org.xmlpull.v1.** { *;}
-dontwarn org.xmlpull.v1.**
-keep class ru.svolf.** { *;}