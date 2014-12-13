package com.example.thomas.androidconnect;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class AddGame extends Activity {
    EditText thuisploeg, uitploeg, scoreThuis, scoreUit;
    Button addGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);
        thuisploeg = (EditText) findViewById(R.id.textThuisploeg);
        uitploeg = (EditText) findViewById(R.id.textUitploeg);
        scoreThuis = (EditText) findViewById(R.id.textScoreThuis);
        scoreUit = (EditText) findViewById(R.id.textScoreUit);
        addGame = (Button) findViewById(R.id.buttonAdd);
        addGame.setEnabled(false);
        addGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] wedstrijd = {thuisploeg.getText().toString().trim(), scoreThuis.getText().toString().trim(), scoreUit.getText().toString().trim(), uitploeg.getText().toString().trim()};
                new WriteGame().execute(wedstrijd);
            }
        });

        thuisploeg.addTextChangedListener(new CustomTextWatcher());
        uitploeg.addTextChangedListener(new CustomTextWatcher());
        scoreThuis.addTextChangedListener(new CustomTextWatcher());
        scoreUit.addTextChangedListener(new CustomTextWatcher());
        //if(thuisploeg.getText().toString().trim().isEmpty() )
    }

    public class CustomTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(s.toString().trim().isEmpty()) addGame.setEnabled(false);
            else addGame.setEnabled(true);

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.toString().trim().isEmpty()) addGame.setEnabled(false);
            else addGame.setEnabled(true);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().trim().isEmpty()) addGame.setEnabled(false);
            else addGame.setEnabled(true);
        }
    }

    public class WriteGame extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(getApplicationContext(),MainScreenActivity.class);
            startActivity(intent);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... params) {
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("Thuisploeg",params[0]));
            parameters.add(new BasicNameValuePair("ScoreThuis",params[1]));
            parameters.add(new BasicNameValuePair("ScoreUit",params[2]));
            parameters.add(new BasicNameValuePair("Uitploeg",params[3]));


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://unuzeleirstest.netau.net/android_wedstrijd/create_wedstrijd.php");

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(parameters));
                HttpResponse response = httpClient.execute(httpPost);

                if(response.getStatusLine().getStatusCode() != 200)
                {
                    return null;
                }

                InputStream jsonStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));
                StringBuilder builder = new StringBuilder();
                String line;


                while((line = reader.readLine())!=null)
                {
                    builder.append(line);
                }
                Log.i("response", builder.toString());
                jsonStream.close();
                String jsonData = builder.substring(builder.indexOf("{\"succes"));
                JSONObject jsonObject = null;
                try {
                    Log.i("data", jsonData);
                    jsonObject = new JSONObject(jsonData);
                    Log.i("succes",jsonObject.getString("succes"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
