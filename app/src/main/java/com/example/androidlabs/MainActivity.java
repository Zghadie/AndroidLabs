package com.example.androidlabs;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView characterList;
    private ArrayList<StarWarsCharacter> characters = new ArrayList<>();
    private CharacterAdapter adapter;
    private FetchCharactersTask fetchTask;

    private static final String STAR_WARS_URL = "https://swapi.py4e.com/api/people/?format=json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        characterList = findViewById(R.id.characterList);

        adapter = new CharacterAdapter();
        characterList.setAdapter(adapter);

        characterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StarWarsCharacter selectedCharacter = characters.get(position);

                Bundle dataToPass = new Bundle();
                dataToPass.putString("name", selectedCharacter.name);
                dataToPass.putString("height", selectedCharacter.height);
                dataToPass.putString("mass", selectedCharacter.mass);

                View fragmentLocation = findViewById(R.id.fragmentLocation);

                if (fragmentLocation == null) {
                    // Phone layout: open new activity
                    Intent nextActivity = new Intent(MainActivity.this, EmptyActivity.class);
                    nextActivity.putExtras(dataToPass);
                    startActivity(nextActivity);
                } else {
                    // Tablet layout: show fragment on the right side
                    DetailsFragment fragment = new DetailsFragment();
                    fragment.setArguments(dataToPass);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentLocation, fragment)
                            .commit();
                }
            }
        });

        fetchTask = new FetchCharactersTask();
        fetchTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (fetchTask != null) {
            fetchTask.cancel(true);
        }
    }

    private class FetchCharactersTask extends AsyncTask<Void, Void, ArrayList<StarWarsCharacter>> {

        @Override
        protected ArrayList<StarWarsCharacter> doInBackground(Void... voids) {
            ArrayList<StarWarsCharacter> resultList = new ArrayList<>();

            try {
                URL url = new URL(STAR_WARS_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("User-Agent", "AndroidLabs");

                connection.connect();

                int responseCode = connection.getResponseCode();

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e("SWAPI_ERROR", "HTTP response code: " + responseCode);
                    return resultList;
                }

                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                String responseText = readStream(inputStream);

                Log.d("SWAPI_RESPONSE", responseText);

                JSONObject jsonObject = new JSONObject(responseText);
                JSONArray results = jsonObject.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject characterObject = results.getJSONObject(i);

                    String name = characterObject.getString("name");
                    String height = characterObject.getString("height");
                    String mass = characterObject.getString("mass");

                    StarWarsCharacter character = new StarWarsCharacter(name, height, mass);
                    resultList.add(character);
                }

                inputStream.close();
                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SWAPI_ERROR", "Problem loading Star Wars API", e);
            }

            return resultList;
        }

        @Override
        protected void onPostExecute(ArrayList<StarWarsCharacter> resultList) {
            super.onPostExecute(resultList);

            characters.clear();
            characters.addAll(resultList);
            adapter.notifyDataSetChanged();

            if (characters.size() == 0) {
                Toast.makeText(MainActivity.this,
                        "API failed. Check Logcat for SWAPI_ERROR",
                        Toast.LENGTH_LONG).show();
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
    }

    private class CharacterAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return characters.size();
        }

        @Override
        public Object getItem(int position) {
            return characters.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View oldView, ViewGroup parent) {
            TextView rowTextView;

            if (oldView == null) {
                rowTextView = new TextView(MainActivity.this);
                rowTextView.setTextSize(24);
                rowTextView.setPadding(8, 8, 8, 8);
                rowTextView.setTextColor(Color.BLACK);
                rowTextView.setBackgroundColor(Color.WHITE);
            } else {
                rowTextView = (TextView) oldView;
            }

            StarWarsCharacter character = characters.get(position);
            rowTextView.setText(character.name);

            return rowTextView;
        }
    }

    private static class StarWarsCharacter {
        String name;
        String height;
        String mass;

        StarWarsCharacter(String name, String height, String mass) {
            this.name = name;
            this.height = height;
            this.mass = mass;
        }
    }
}