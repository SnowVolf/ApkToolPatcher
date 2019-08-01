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

#-keepattributes SourceFile,LineNumberTable
-keepclassmembers class org.mozilla.universalchardet.* {
   *;
}

-classobfuscationdictionary proguard-dictionary.txt
-packageobfuscationdictionary proguard-dictionary.txt
-obfuscationdictionary proguard-dictionary.txt
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-repackageclasses 'retrofit2'

-dontwarn sun.security.**
-dontwarn sys.util.**
-keep class org.xmlpull.v1.** { *; }
