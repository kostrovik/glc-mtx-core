package com.github.kostrovik.matrix.core.common.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    13/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 */
public class ServerConnector {
    private static Logger logger = LogManager.getLogger(ServerConnector.class);
    private String serverUrl;

    public ServerConnector(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String sendGet(String apiUrl) {
        StringBuilder response = new StringBuilder();
        StringBuilder responseHeaders = new StringBuilder();

        try {
            URL serverApiUrl = new URL(serverUrl + apiUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) serverApiUrl.openConnection();
            httpConnection.setRequestMethod("GET");

            responseHeaders.append(String.format("Запрос: %s\n", serverApiUrl.toExternalForm()));
            responseHeaders.append(String.format("Meтoд: %s\n", httpConnection.getRequestMethod()));
            responseHeaders.append(String.format("Koд ответа: %s\n", httpConnection.getResponseCode()));
            responseHeaders.append(String.format("Oтвeт: %s\n", httpConnection.getResponseMessage()));

            InputStream input = httpConnection.getInputStream();

            int c;
            while (((c = input.read()) != -1)) {
                response.append((char) c);
            }
            input.close();

        } catch (IOException e) {
            logger.error(String.format("Ошибка выполнения запроса\n%s", responseHeaders), e);
        }

        return response.toString();
    }

    public String sendPost(String apiUrl, String json) {
        StringBuilder response = new StringBuilder();
        StringBuilder responseHeaders = new StringBuilder();

        try {
            URL serverApiUrl = new URL(serverUrl + apiUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) serverApiUrl.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setRequestMethod("POST");

            OutputStream output = httpConnection.getOutputStream();
            output.write(json.getBytes(Charset.forName("UTF-8")));
            output.close();

            responseHeaders.append(String.format("Запрос: %s\n", serverApiUrl.toExternalForm()));
            responseHeaders.append(String.format("Meтoд: %s\n", httpConnection.getRequestMethod()));
            responseHeaders.append(String.format("Koд ответа: %s\n", httpConnection.getResponseCode()));
            responseHeaders.append(String.format("Oтвeт: %s\n", httpConnection.getResponseMessage()));

            InputStream input = httpConnection.getInputStream();

            int c;
            while (((c = input.read()) != -1)) {
                response.append((char) c);
            }
            input.close();

        } catch (IOException e) {
            logger.error(String.format("Ошибка выполнения запроса\n%s", responseHeaders), e);
        }

        return response.toString();
    }
}
