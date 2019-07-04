# MKR-ANDROID-LOGIN-FIREBASE

#   Required GooglePlayService in project

#   AndroidManifest.xml
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

#	Project Level Gradle
		repositories {
			maven { url 'https://jitpack.io' }
		}

#	APP Level Gradle

	implementation 'com.github.THEMKR:android-lib-firebase:1.0.0'

	<!-- DEPENDENCY INCLUDE IN LIB -->
	implementation"org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
	implementation 'com.google.firebase:firebase-database:18.0.0'
	implementation 'com.google.firebase:firebase-core:17.0.0'
