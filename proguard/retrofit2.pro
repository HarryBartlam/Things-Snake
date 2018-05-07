# Retrofit 2.X
## https://square.github.io/retrofit/ ##

-dontwarn retrofit2.**
-keep,includedescriptorclasses class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okio.**
-dontwarn javax.annotation.**

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
