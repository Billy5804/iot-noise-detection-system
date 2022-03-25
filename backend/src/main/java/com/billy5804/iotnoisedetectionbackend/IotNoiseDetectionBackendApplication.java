package com.billy5804.iotnoisedetectionbackend;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@SpringBootApplication
public class IotNoiseDetectionBackendApplication {

	private final static Logger logger = LoggerFactory.getLogger(IotNoiseDetectionBackendApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(IotNoiseDetectionBackendApplication.class, args);

		try {
			InputStream serviceAccount = IotNoiseDetectionBackendApplication.class
					.getResourceAsStream("/serviceAccountKey.json");

			@SuppressWarnings("deprecation")
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

			FirebaseApp.initializeApp(options);

			logger.info("Firebase app {} initalised", FirebaseApp.getInstance().getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
