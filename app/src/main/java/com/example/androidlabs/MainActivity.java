package com.example.androidlabs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView catImageView;
    private ProgressBar catProgressBar;
    private CatImages catTask;

    private static final String CAT_JSON_URL = "https://cataas.com/cat?json=true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        catImageView = findViewById(R.id.catImageView);
        catProgressBar = findViewById(R.id.catProgressBar);

        catTask = new CatImages();
        catTask.execute(CAT_JSON_URL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (catTask != null) {
            catTask.cancel(true);
        }
    }

    private class CatImages extends AsyncTask<String, Integer, String> {

        private Bitmap currentCatPicture;
        private boolean newPictureReady = false;

        @Override
        protected String doInBackground(String... strings) {

            while (!isCancelled()) {
                try {
                    URL jsonUrl = new URL(strings[0]);
                    HttpURLConnection jsonConnection =
                            (HttpURLConnection) jsonUrl.openConnection();

                    jsonConnection.connect();

                    InputStream jsonInputStream =
                            new BufferedInputStream(jsonConnection.getInputStream());

                    String jsonText = readStream(jsonInputStream);

                    JSONObject jsonObject = new JSONObject(jsonText);

                    String id = jsonObject.getString("id");
                    String imageUrl = jsonObject.getString("url");

                    String safeFileName = id.replaceAll("[^a-zA-Z0-9._-]", "_") + ".jpg";
                    File imageFile = new File(getFilesDir(), safeFileName);

                    if (imageFile.exists()) {
                        currentCatPicture = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    } else {
                        String fullImageUrl;

                        if (imageUrl.startsWith("http")) {
                            fullImageUrl = imageUrl;
                        } else {
                            fullImageUrl = "https://cataas.com" + imageUrl;
                        }

                        downloadImageToFile(fullImageUrl, imageFile);
                        currentCatPicture = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    }

                    newPictureReady = true;
                    publishProgress(0);

                    for (int i = 0; i <= 100; i++) {
                        if (isCancelled()) {
                            break;
                        }

                        publishProgress(i);
                        Thread.sleep(30);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    try {
                        Thread.sleep(2000);
                    } catch (Exception ignored) {
                    }
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            catProgressBar.setProgress(values[0]);

            if (newPictureReady && currentCatPicture != null) {
                catImageView.setImageBitmap(currentCatPicture);
                newPictureReady = false;
            }
        }

        private String readStream(InputStream inputStream) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();

            return builder.toString();
        }

        private void downloadImageToFile(String imageUrl, File imageFile) throws Exception {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.connect();

            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
        }
    }
}