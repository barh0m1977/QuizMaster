############### 🔐 General Android & Kotlin Keep Rules ###############

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Kotlin data classes (default constructors, properties)
-keepclassmembers class * {
    public <init>(...);
}
-keepclassmembers class * {
    *** get*();
    void set*(***);
}

# Keep all ViewModel classes
-keep class androidx.lifecycle.ViewModel { *; }
-keep class com.ibrahim.quizmaster.viewmodel.** { *; }

# Keep LiveData observers
-keep class androidx.lifecycle.LiveData { *; }

############### 🔥 Firebase ###############

# Keep Firebase classes and annotations
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Needed for Firebase Firestore deserialization
-keep class com.ibrahim.quizmaster.data.model.** { *; }
-keepclassmembers class com.ibrahim.quizmaster.data.model.** {
    public <init>();
    <fields>;
}
# Keep @PropertyName-annotated methods
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <methods>;
}

# Firebase Crashlytics
-keep class com.google.firebase.crashlytics.** { *; }

# Firebase Auth
-keep class com.google.firebase.auth.** { *; }

# Firebase Analytics
-keep class com.google.firebase.analytics.** { *; }

# Firebase Storage
-keep class com.google.firebase.storage.** { *; }

############### 📦 Jetpack Compose ###############

# Needed to keep Compose compiler metadata
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Material3 Compose
-keep class androidx.compose.material3.** { *; }

# Navigation Compose
-keep class androidx.navigation.compose.** { *; }

############### 🖼️ Coil (image loader) ###############

# Coil image loading
-keep class coil.** { *; }
-dontwarn coil.**

############### 📊 Apache POI (Excel) ###############

# Apache POI Excel Reader
-keep class org.apache.poi.** { *; }
-dontwarn org.apache.poi.**

# Suppress POI-related missing class warnings
-dontwarn com.microsoft.schemas.office.visio.x2012.main.CpType
-dontwarn com.microsoft.schemas.office.visio.x2012.main.FldType
-dontwarn com.microsoft.schemas.office.visio.x2012.main.PpType
-dontwarn com.microsoft.schemas.office.visio.x2012.main.TpType

# Suppress AWT/Graphics-related warnings from Apache POI
-dontwarn java.awt.Rectangle
-dontwarn java.awt.Shape
-dontwarn java.awt.geom.Rectangle2D$Double
-dontwarn java.awt.geom.Rectangle2D
