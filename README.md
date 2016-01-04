# rx
SqlBrite, RxJava and Retrofit

https://www.youtube.com/watch?v=KIAoQbAu3eA&feature=youtu.be

	dependencies {
		compile fileTree(dir: 'libs', include: ['*.jar'])
		testCompile 'junit:junit:4.12'
		compile 'com.android.support:appcompat-v7:23.1.1'
		compile 'com.android.support:design:23.1.1'
		// other stuff
		compile 'com.squareup.sqlbrite:sqlbrite:0.5.0' // https://github.com/square/sqlbrite
		compile 'com.squareup.okhttp:okhttp:2.7.0'     // https://github.com/square/okhttp
		compile 'com.squareup.picasso:picasso:2.5.2'   // https://github.com/square/picasso
		// rx
		compile 'io.reactivex:rxandroid:1.1.0'
		// Because RxAndroid releases are few and far between, it is recommended you also
		// explicitly depend on RxJava's latest version for bug fixes and new features.
		compile 'io.reactivex:rxjava:1.1.0'

		// retrofit
		compile 'com.squareup.retrofit:retrofit:2.0.0-beta2'
		compile 'com.squareup.retrofit:converter-gson:2.0.0-beta2'
		compile 'com.squareup.retrofit:adapter-rxjava:2.0.0-beta2'
	}
