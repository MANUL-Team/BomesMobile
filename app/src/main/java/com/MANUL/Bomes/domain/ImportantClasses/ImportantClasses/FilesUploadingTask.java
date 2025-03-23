package com.MANUL.Bomes.domain.ImportantClasses.ImportantClasses;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FilesUploadingTask extends AsyncTask<Void, Void, String> {

    private String lineEnd = "\r\n";
    private String twoHyphens = "--";
    private String boundary =  "----WebKitFormBoundary9xFB2hiUhzqbBQ4M";

    private int bytesRead, bytesAvailable, bufferSize;
    private byte[] buffer;
    private int maxBufferSize = 1*1024*1024;

    private String filePath;

    public static final String API_FILES_UPLOADING_PATH = "https://bomes.ru:5000/upload";

    public static final String FORM_FILE_NAME = "file1";

    public FilesUploadingTask(String filePath) {
        this.filePath = filePath;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = null;

        try {
            URL uploadUrl = new URL(API_FILES_UPLOADING_PATH);

            HttpURLConnection connection = (HttpURLConnection) uploadUrl.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());


            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                    FORM_FILE_NAME + "\"; filename=\"" + filePath + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
            outputStream.writeBytes(lineEnd);

            FileInputStream fileInputStream = new FileInputStream(new File(filePath));

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            int serverResponseCode = connection.getResponseCode();

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            if(serverResponseCode == 200) {
                result = readStream(connection.getInputStream());
            } else {
                result = readStream(connection.getErrorStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String readStream(InputStream inputStream) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        return buffer.toString();
    }
}