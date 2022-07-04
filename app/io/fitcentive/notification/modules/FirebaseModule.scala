package io.fitcentive.notification.modules

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import com.google.inject.{AbstractModule, Provides}

import javax.inject.Singleton

class FirebaseModule extends AbstractModule {

  @Provides
  @Singleton
  def provideFirebaseOptions: FirebaseOptions =
    FirebaseOptions
      .builder()
      .setCredentials(
        GoogleCredentials
          .fromStream(getClass.getResourceAsStream("/fitcentive-1210-firebase-adminsdk-ucirx-3c2610810a.json"))
      )
      .setDatabaseUrl("https://fitcentive-1210.firebaseio.com")
      .build()

  @Provides
  @Singleton
  def provideFirebaseApp(firebaseOptions: FirebaseOptions): FirebaseApp =
    FirebaseApp.initializeApp(firebaseOptions)

}
