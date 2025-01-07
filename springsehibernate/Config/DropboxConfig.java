package com.example.springsehibernate.Config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
@Data
public class DropboxConfig {

    @Value("${dropbox.app.key}")
    private String appKey;

    @Value("${dropbox.app.secret}")
    private String appSecret;

    @Value("${dropbox.access.token}")
    private String accessToken;

    private String refreshToken = "N1XDMsYpVy8AAAAAAAAAATPNq4ndlG7SkOdIsnNrzEqW4Ar0bu7lAS4yIToWA1Ut";

}
