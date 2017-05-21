package armando.app_tfg_armconcal2017;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MenuActivity extends Activity {
    HttpClient httpclient = new DefaultHttpClient();
    ArrayList<NameValuePair> nameValuePairs;
    HttpPost httppost;

    
    ImageButton restaurants, events, settings, exit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        restaurants = (ImageButton) findViewById(R.id.btnMenuRestaurants);
        events = (ImageButton) findViewById(R.id.btnMenuEvents);
        settings = (ImageButton) findViewById(R.id.btnMenuSettings);
        exit = (ImageButton) findViewById(R.id.btnMenuExit);
    }


    public void accessListRestaurants(View view){
        Intent intent = new Intent(this, RestaurantsActivity.class);
        Bundle b = getIntent().getExtras();
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }


    public void accessListEvents(View view){
        Intent intent = new Intent(this, EventsActivity.class);
        Bundle b = getIntent().getExtras();
        intent.putExtras(b);
        startActivity(intent);
        finish();

    }



    public void logOut(View view){


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(this, "You have been logout successfully",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    public void settings(View view){

        Bundle b = getIntent().getExtras();
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }


    private Boolean quit = false;
    @Override
    public void onBackPressed() {
        if (quit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            quit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    quit = false;
                }
            }, 3 * 1000);

        }

    }


}
