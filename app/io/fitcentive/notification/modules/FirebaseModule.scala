package io.fitcentive.notification.modules

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import com.google.inject.{AbstractModule, Provides}
import io.fitcentive.notification.services.SettingsService

import java.io.ByteArrayInputStream
import javax.inject.Singleton

class FirebaseModule extends AbstractModule {

  @Provides
  @Singleton
  def provideFirebaseOptions(settingsService: SettingsService): FirebaseOptions =
    FirebaseOptions
      .builder()
      .setCredentials(
        GoogleCredentials
          .fromStream(new ByteArrayInputStream(settingsService.firebaseConfig.serviceAccountCredentials.getBytes()))
      )
      //      .setDatabaseUrl(settingsService.firebaseConfig.databaseUrl) // seems like this is not required
      .build()

  @Provides
  @Singleton
  def provideFirebaseApp(firebaseOptions: FirebaseOptions): FirebaseApp =
    FirebaseApp.initializeApp(firebaseOptions)

}
