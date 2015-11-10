# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/matthewciampa/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Retrofit 1.X
-keepattributes Signature,*Annotation*

-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.* { *; }
-keep class org.apache.james.mime4j.* { *; }
-keep class javax.inject.** { *; }
-dontwarn rx.**
-keep class sun.misc.Unsafe { *; }

-keep class retrofit.** { *; }
-keep interface retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keepclasseswithmembers class * {
    @ua.* <methods>;
}

# Also you must note that if you are using GSON for conversion from JSON to POJO representation, you must ignore those POJO classes from being obfuscated.
# Here include the POJO's that have you have created for mapping JSON response to POJO for example.
-keep class mc_sg.translocapp.model.** { *; }
-keep class mc_sg.translocapp.network.** { *; }
-keep interface mc_sg.translocapp.network.** { *; }

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-dontwarn android.support.**

-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-dontwarn okio.**