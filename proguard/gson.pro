##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep,includedescriptorclasses class * implements com.google.gson.TypeAdapterFactory
-keep,includedescriptorclasses class * implements com.google.gson.JsonSerializer
-keep,includedescriptorclasses class * implements com.google.gson.JsonDeserializer

##---------------End: proguard configuration for Gson  ----------

-keep class com.simplyapp.bluetooth.data.model.** { *; }
-dontwarn com.simplyapp.bluetooth.data.model.**

-dontnote com.google.gson.**

-dontwarn com.google.gson.Gson$6.**
-dontwarn com.google.gson.Gson$6
