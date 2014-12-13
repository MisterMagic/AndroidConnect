package com.example.thomas.androidconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Picture;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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


public class PictureListView extends Activity {
    ListView listView;
    ArrayList<String> fotos = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list_view);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, R.layout.item_layout, fotos);
        listView.setAdapter(adapter);
        new LoadPictures().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), PictureItem.class);
                i.putExtra("position", position);
                startActivity(i);
            }
        });
    }

    public class LoadPictures extends AsyncTask<Void, Void, Void> {
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

            HttpGet getRequest = new HttpGet("http://unuzeleirstest.netau.net/android_picture/get_all_pictures.php");
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
                String jsonData = builder.substring(builder.indexOf("{\"pictures"));
                JSONObject jsonObject = null;
                try {
                    Log.i("data", jsonData);
                    jsonObject = new JSONObject(jsonData);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //wedstrijden.add(jsonData);

                try {
                    JSONArray j = jsonObject.getJSONArray("pictures");
                    for(int i=0;i<j.length();i++) {
                        JSONObject object = (JSONObject) j.get(i);
                        int id = object.getInt("_id");
                        fotos.add(id+"");
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

        @Override
        protected void onPostExecute(Void result) {
            // dismiss the dialog after getting all products
            //pDialog.dismiss();

            adapter.notifyDataSetChanged();
            super.onPostExecute(result);

        }

    }



}
