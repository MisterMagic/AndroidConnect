package com.example.thomas.androidconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainScreenActivity extends Activity {

    Button buttonToevoegen, buttonAddPicture;
    ListView listView;
    ArrayList<String> wedstrijden = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        context = getApplicationContext();

        buttonToevoegen = (Button) findViewById(R.id.buttonToevoegen);
        buttonAddPicture = (Button) findViewById(R.id.buttonAddPicture);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, R.layout.item_layout, wedstrijden);
        listView.setAdapter(adapter);
        LoadAllGames task = new LoadAllGames();
        task.execute(null, null, null);

        buttonToevoegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddGame.class);
                startActivity(i);
                finish();
            }
        });

        buttonAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddPicture.class);
                startActivity(i);
                finish();
            }
        });
    }

    public class LoadAllGames extends AsyncTask<Void, Void, Void> {
        public ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            /*pDialog = new ProgressDialog(getApplicationContext());
            pDialog.setTitle("Loading games");
            pDialog.show();*/
            super.onPreExecute();

        }

        /**
         * getting All products from url
         * */

        @Override
        protected Void doInBackground(Void... args) {

            HttpClient client = new DefaultHttpClient();
            //List<NameValuePair> params = new ArrayList<NameValuePair>();
            //String paramString = URLEncodedUtils.format(params, "utf-8");
            //url += "?" + paramString;
            HttpGet getRequest = new HttpGet("http://unuzeleirstest.netau.net/android_wedstrijd/get_all_wedstrijden.php");
            try {

                HttpResponse response = client.execute(getRequest);
                Log.i("response", response.toString());
                if(response.getStatusLine().getStatusCode()!=200) return null;
                InputStream jsonStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));
                StringBuilder builder = new StringBuilder();
                String line;

                while((line = reader.readLine())!=null)
                {
                    builder.append(line);
                }

                jsonStream.close();
                if(builder.indexOf("success\":0")>=0) return null;
                String jsonData = builder.substring(builder.indexOf("{\"wedstrijden"));
                JSONObject jsonObject = null;
                try {
                    Log.i("data", jsonData);
                    jsonObject = new JSONObject(jsonData);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //wedstrijden.add(jsonData);

                try {
                    JSONArray j = jsonObject.getJSONArray("wedstrijden");
                    for(int i=0;i<j.length();i++) {
                        JSONObject object = (JSONObject) j.get(i);
                        int id = object.getInt("_id");
                        String thuis = object.getString("Thuisploeg");
                        int scoreThuis = object.getInt("ScoreThuis");
                        int scoreUit = object.getInt("ScoreUit");
                        String uit = object.getString("Uitploeg");
                        wedstrijden.add(id + ": " + thuis + " " + scoreThuis + " - " + scoreUit + " " + uit);
                    }
                    //wedstrijden.add(thuis +" " + uit);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(Void result) {
            // dismiss the dialog after getting all products
            //pDialog.dismiss();

            adapter.notifyDataSetChanged();
            super.onPostExecute(result);

        }

    }


}
