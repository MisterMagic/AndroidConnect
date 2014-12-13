package com.example.thomas.androidconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class PictureItem extends Activity {

    ImageView imageView;
    int position=0;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_item);
        imageView = (ImageView) findViewById(R.id.imageView);
        position = getIntent().getIntExtra("position", position);
        new LoadPictures().execute();

        //
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
                    JSONObject object = (JSONObject) j.get(position);
                    String foto_string = object.getString("picture");
                    Log.i("foto voor decode",foto_string);
                    byte [] foto_stream = Base64.decode(foto_string,Base64.DEFAULT);
                    //Log.i("foto na decode", foto_stream.toString());
                    bitmap = BitmapFactory.decodeByteArray(foto_stream, 0, foto_stream.length);




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
        protected void onPostExecute(Void aVoid) {
            // dismiss the dialog after getting all products
            //pDialog.dismiss();
            imageView.setImageBitmap(bitmap);

            super.onPostExecute(aVoid);

        }

    }



}
