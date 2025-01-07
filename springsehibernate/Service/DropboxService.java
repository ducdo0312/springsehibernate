package com.example.springsehibernate.Service;

import com.example.springsehibernate.Config.DropboxConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


@Service
public class DropboxService {

    @Autowired
    private DropboxConfig dropboxConfig;

    public Resource downloadFileFromURL(String filePath) throws Exception {
        String downloadUrl = getDownloadUrl(filePath); // Giả sử bạn có phương thức này để lấy URL tải xuống
        URL url = new URL(downloadUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        InputStream inputStream = connection.getInputStream();
        MediaType mediaType = determineMediaType(filePath);

        return new InputStreamResource(inputStream) {
            @Override
            public String getFilename() {
                return filePath.substring(filePath.lastIndexOf('/') + 1);
            }

            @Override
            public long contentLength() {
                return connection.getContentLengthLong();
            }

        };
    }
    public MediaType determineMediaType(String filePath) {
        String fileExtension = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
        switch (fileExtension) {
            case "pdf":
                return MediaType.APPLICATION_PDF;
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "doc":
                return MediaType.parseMediaType("application/msword");
            case "docx":
                return MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            default:
                return MediaType.APPLICATION_OCTET_STREAM; // Loại này dùng cho các file không rõ loại
        }
    }

    private String getDownloadUrl(String dropboxFilePath) throws IOException {
        String ACCESS_TOKEN;
        try {
             ACCESS_TOKEN = getUpdatedAccessToken(dropboxConfig.getRefreshToken());
        } catch (Exception e) {
            ACCESS_TOKEN = null;
        }
        if (ACCESS_TOKEN == null) {
            throw new IllegalStateException("Access Token is null");
        }
        URL url = new URL("https://api.dropboxapi.com/2/sharing/create_shared_link_with_settings");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("path", dropboxFilePath);
        JSONObject settings = new JSONObject();
        settings.put("requested_visibility", "public");
        jsonBody.put("settings", settings);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            JSONObject jsonResponse = new JSONObject(response.toString());
            String sharedLink = jsonResponse.getJSONObject("metadata").getString("url");
            return sharedLink.replace("www.dropbox.com", "dl.dropboxusercontent.com");
        } else {
            String errorResponse = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject errorObject = new JSONObject(errorResponse);

            if (errorObject.getString("error_summary").startsWith("shared_link_already_exists/")) {
                // Thực hiện yêu cầu đến API để lấy liên kết chia sẻ hiện có
                return retrieveExistingSharedLink(dropboxFilePath);
            } else {
                throw new IOException("Error in API call: " + errorResponse);
            }
        }
    }

    private String retrieveExistingSharedLink(String dropboxFilePath) throws IOException {
        String ACCESS_TOKEN;
        try {
            ACCESS_TOKEN = getUpdatedAccessToken(dropboxConfig.getRefreshToken());
        } catch (Exception e) {
            ACCESS_TOKEN = null;
        }
        if (ACCESS_TOKEN == null) {
            throw new IllegalStateException("Access Token is null");
        }
        URL url = new URL("https://api.dropboxapi.com/2/sharing/list_shared_links");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("path", dropboxFilePath);
        jsonBody.put("direct_only", true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray links = jsonResponse.getJSONArray("links");
            if (links.length() > 0) {
                return links.getJSONObject(0).getString("url").replace("www.dropbox.com", "dl.dropboxusercontent.com");
            }
        } else {
            String errorResponse = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            throw new IOException("Error retrieving existing shared link: " + errorResponse);
        }

        return null;
    }

    public String getUpdatedAccessToken(String refreshToken) throws Exception {
        String clientId = dropboxConfig.getAppKey(); // Thay thế với Client ID của ứng dụng Dropbox
        String clientSecret = dropboxConfig.getAppSecret(); // Thay thế với Client Secret của ứng dụng Dropbox

        // Tạo URL để yêu cầu làm mới token
        URL url = new URL("https://api.dropbox.com/oauth2/token");
        String params = "grant_type=refresh_token&refresh_token=" + refreshToken +
                "&client_id=" + clientId + "&client_secret=" + clientSecret;
        System.out.println(params);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));

        try {
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Đọc phản hồi từ Dropbox
                String response = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                JSONObject jsonResponse = new JSONObject(response);

                // Kiểm tra và lấy access_token mới
                if (jsonResponse.has("access_token")) {
                    return jsonResponse.getString("access_token");
                }
            } else {
                // Xử lý trường hợp phản hồi không thành công
                String errorResponse = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Error response: " + errorResponse);
                throw new Exception("Failed to refresh access token with response code: " + responseCode);
            }
        } finally {
            conn.disconnect();
        }
        throw new Exception("Failed to refresh access token");
    }
}



