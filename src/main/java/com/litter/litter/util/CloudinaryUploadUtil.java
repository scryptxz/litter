package com.litter.litter.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Minimal Cloudinary upload implementation using only JDK HTTP client.
 */
public final class CloudinaryUploadUtil {

    private CloudinaryUploadUtil() {}

    public static String uploadImageToCloudinary(byte[] fileBytes,
                                                  String originalFilename,
                                                  String cloudName,
                                                  String apiKey,
                                                  String apiSecret,
                                                  String folder) {
        try {
            String uploadUrl = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload";
            String timestamp = String.valueOf(Instant.now().getEpochSecond());

            // Unsigned upload using an upload preset.
            // This avoids signature calculation issues.
            String boundary = "----CloudinaryBoundary" + System.currentTimeMillis();
            String filename = (originalFilename == null || originalFilename.isBlank()) ? "upload" : originalFilename;

            byte[] multipartBody = MultipartBuilder.build(
                    boundary,
                    new MultipartBuilder.Part("api_key", apiKey),
                    new MultipartBuilder.Part("upload_preset", "ml_default"),
                    new MultipartBuilder.Part("folder", folder),
                    new MultipartBuilder.FilePart("file", filename, "application/octet-stream", fileBytes)
            );


            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Cloudinary upload failed: HTTP " + response.statusCode() + " - " + response.body());
            }

            String secureUrl = extractJsonStringField(response.body(), "secure_url");
            if (secureUrl == null || secureUrl.isBlank()) {
                throw new RuntimeException("Cloudinary upload succeeded but secure_url missing: " + response.body());
            }
            return secureUrl;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    // Minimal JSON extractor for a single string field.
    private static String extractJsonStringField(String json, String fieldName) {
        if (json == null) return null;
        String needle = "\"" + fieldName + "\"";
        int idx = json.indexOf(needle);
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx + needle.length());
        if (colon < 0) return null;
        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote < 0) return null;
        int secondQuote = json.indexOf('"', firstQuote + 1);
        if (secondQuote < 0) return null;
        return json.substring(firstQuote + 1, secondQuote);
    }

    private static final class MultipartBuilder {
        private MultipartBuilder() {}

        record Part(String name, String value) {}

        record FilePart(String name, String filename, String contentType, byte[] bytes) {}

        static byte[] build(String boundary, Object... parts) throws IOException {
            var out = new java.io.ByteArrayOutputStream();
            String lineBreak = "\r\n";

            for (Object p : parts) {
                if (p instanceof Part part) {
                    out.write(("--" + boundary + lineBreak).getBytes(StandardCharsets.UTF_8));
                    out.write(("Content-Disposition: form-data; name=\"" + part.name() + "\"" + lineBreak)
                            .getBytes(StandardCharsets.UTF_8));
                    out.write(lineBreak.getBytes(StandardCharsets.UTF_8));
                    out.write(part.value().getBytes(StandardCharsets.UTF_8));
                    out.write(lineBreak.getBytes(StandardCharsets.UTF_8));
                } else if (p instanceof FilePart filePart) {
                    out.write(("--" + boundary + lineBreak).getBytes(StandardCharsets.UTF_8));
                    out.write(("Content-Disposition: form-data; name=\"" + filePart.name() + "\"; filename=\"" + filePart.filename() + "\"" + lineBreak)
                            .getBytes(StandardCharsets.UTF_8));
                    out.write(("Content-Type: " + filePart.contentType() + lineBreak).getBytes(StandardCharsets.UTF_8));
                    out.write(lineBreak.getBytes(StandardCharsets.UTF_8));
                    out.write(filePart.bytes());
                    out.write(lineBreak.getBytes(StandardCharsets.UTF_8));
                }
            }

            out.write(("--" + boundary + "--" + lineBreak).getBytes(StandardCharsets.UTF_8));
            return out.toByteArray();
        }
    }
}

