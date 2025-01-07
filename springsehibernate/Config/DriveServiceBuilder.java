package com.example.springsehibernate.Config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class DriveServiceBuilder {

    public static Drive getDriveService() throws IOException, GeneralSecurityException {
        InputStream in = DriveServiceBuilder.class.getResourceAsStream("/path/to/your/service_account_credentials.json");
        GoogleCredential credential = GoogleCredential.fromStream(in)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/drive"));

        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new Drive.Builder(new NetHttpTransport(), jsonFactory, credential)
                .setApplicationName("Your Application Name")
                .build();
    }
}

