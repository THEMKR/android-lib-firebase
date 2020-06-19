# MKR-ANDROID-LOGIN-FIREBASE

#   Required GooglePlayService in project

#   AndroidManifest.xml
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

#	Project Level Gradle
		repositories {
			maven { url "https://api.bitbucket.org/2.0/repositories/THEMKR/android-libs/src/releases" }
		}

#	APP Level Gradle

	implementation 'com.lory.library:firebase:1.0.1'

	<!-- DEPENDENCY INCLUDE IN LIB -->
	implementation"org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
	implementation 'com.google.firebase:firebase-database:19.3.0'
