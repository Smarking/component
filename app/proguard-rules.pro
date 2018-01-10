#############################################
#
# 一些基本指令
#
#############################################
-dontusemixedcaseclassnames
-keeppackagenames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-ignorewarnings
-dontpreverify
-verbose
-keepattributes SourceFile,LineNumberTable
-repackageclasses ''
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontoptimize

#############################################
#
# Android开发中一些需要保留的公共部分
#
#############################################
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-dontwarn android.support.v4.**,**CompatHoneycomb,**CompatCreatorHoneycombMR2,org.apache.**

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {*;}
-keepclassmembers class * implements android.os.Parcelable {*;}
-keep class * implements android.os.Parcelable {
   public static final android.os.Parcelable$Creator *;
}

#############################################
#
# NoProGuard注释
#
#############################################


# gson
-keep class sun.misc.Unsafe { *; }
#okhttp3
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okio.**
#retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions


# 组件
-keep class com.pitaya.comannotation.**{*;}
-keep class com.pitaya.commanager.**{*;}
-keep class com.pitaya.comprotocol.**{*;}

-keep public class * extends com.pitaya.commanager.ComLifecycle
-keep public class * extends com.pitaya.commanager.AbsProtocol


# recyclerview
-keepclasseswithmembers class * extends com.meituan.sankuai.cep.component.recyclerviewadapter.BaseViewHolder{
      <init>(...);
}

-keep class com.meituan.sankuai.cep.component.** {*;}
-keep public class * extends com.meituan.sankuai.cep.component.recyclerviewadapter.BaseQuickAdapter
