# MKR-ANDROID-LOGIN-FIREBASE

#   Required GooglePlayService in project

#   AndroidManifest.xml
		<uses-permission android:name="android.permission.INTERNET" />
	    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

#	Project Level Gradle
		repositories {
			maven {
				url "https://api.bitbucket.org/1.0/repositories/THEMKR/android-libs/raw/releases"
			}
		}
		
		classpath 'com.google.gms:google-services:4.1.0'

#	APP Level Gradle
        <!-- DEPENDENCY INCLUDE IN LIB -->
        implementation 'com.google.firebase:firebase-database:16.0.4'
        implementation 'com.google.firebase:firebase-core:16.0.4'
        apply plugin: 'com.google.gms.google-services'
         
        <!-- SUPPORT MUST BE INCLUDE -->
		implementation 'com.lory.library:firebase:1.0.1'
		
