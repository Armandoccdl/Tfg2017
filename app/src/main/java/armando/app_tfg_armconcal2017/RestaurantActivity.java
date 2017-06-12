package armando.app_tfg_armconcal2017;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.net.URL;
import java.util.ArrayList;

public class RestaurantActivity extends Activity{
    String idRestaurant = "";
    String user = "";
    ImageView photo;
    TextView name, address, phone, web, food;
    ImageButton like, dislike;
    Button events;
    HttpClient httpclient = new DefaultHttpClient();
    ArrayList<NameValuePair> nameValuePairs;
    HttpPost httppost;
    ArrayList<Object> info = new ArrayList<>();
    ArrayList<Object> likeChecked = new ArrayList<>();
    ArrayList<Object> dislikeChecked = new ArrayList<>();
    Activity ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        if(getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            String i = b.getString("Id");
            String u = b.getString("User");
            idRestaurant = i;
            user = u;
        }

        name = (TextView) findViewById(R.id.txtRestaurantName);
        photo = (ImageView) findViewById(R.id.imgRestaurantPhoto);
        address = (TextView) findViewById(R.id.txtRestaurantAddress);
        phone = (TextView) findViewById(R.id.txtRestaurantPhone);
        web = (TextView) findViewById(R.id.txtRestaurantWeb);
        food = (TextView) findViewById(R.id.txtRestaurantFood);
        like = (ImageButton) findViewById(R.id.btnRestaurantLike);
        dislike = (ImageButton) findViewById(R.id.btnRestaurantDislike);
        events = (Button) findViewById(R.id.btnRestaurantEvent);

        new Info(RestaurantActivity.this).execute();
        new CheckLike(RestaurantActivity.this).execute();
        new CheckDislike(RestaurantActivity.this).execute();

        like.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new CreateLike(RestaurantActivity.this).execute();
            }
        });

        dislike.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new CreateDislike(RestaurantActivity.this).execute();
            }
        });

        events.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new getEvents(RestaurantActivity.this).execute();
            }
        });




    }

    public String log() {
        httppost = new HttpPost("http://armconcaltfg.esy.es/php/getRestaurant.php");
        nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("id", idRestaurant));
        HttpResponse response;
        String result = "";

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            result = convertStreamToString(instream);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean filter(){
        String data = log();
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try{
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("info");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    info.add(jsonArrayChild.optString("name"));
                    info.add(jsonArrayChild.optString("photo"));
                    info.add(jsonArrayChild.optString("address"));
                    info.add(jsonArrayChild.optString("phone"));
                    info.add(jsonArrayChild.optString("web"));
                    info.add(jsonArrayChild.optString("food"));
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public class Info extends AsyncTask<String, Float, String> {

        private Activity ctx;

        Info(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(filter()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        name.setText(info.get(0).toString());
                        Picasso.with(ctx).load(info.get(1).toString()).into(photo);
                        address.setText(info.get(2).toString());
                        phone.setText(info.get(3).toString());
                        web.setText(info.get(4).toString());
                        food.setText(info.get(5).toString());
                    }
                });
            }
            return null;
        }
    }

    public String checkLike() {
        httppost = new HttpPost("http://armconcaltfg.esy.es/php/checkLike.php");
        nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("idR", idRestaurant));
        nameValuePairs.add(new BasicNameValuePair("user", user));
        HttpResponse response;
        String result = "";

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            result = convertStreamToString(instream);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean likeStatus(){
        String data = checkLike();
        if(!data.equals("[]\n")){
            JSONObject json;
            try{
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("info");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    likeChecked.add(jsonArrayChild.optString("user"));
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public class CheckLike extends AsyncTask<String, Float, String> {

        private Activity ctx;

        CheckLike(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(likeStatus()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String userId = likeChecked.get(0).toString();
                        if(userId.equals(user)){
                            like.setClickable(false);
                            like.setEnabled(false);
                            like.setImageResource(R.drawable.like_check);
                            dislike.setImageResource(R.drawable.dislike);
                            dislike.setClickable(true);
                            dislike.setEnabled(true);


                        }
                    }
                });
            }
            return null;
        }
    }

    public String checkDislike() {
        httppost = new HttpPost("http://armconcaltfg.esy.es/php/checkDislike.php");
        nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("idR", idRestaurant));
        nameValuePairs.add(new BasicNameValuePair("user", user));
        HttpResponse response;
        String result = "";

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            result = convertStreamToString(instream);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean dislikeStatus(){
        String data = checkDislike();
        if(!data.equals("[]\n")){
            JSONObject json;
            try{
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("info");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    dislikeChecked.add(jsonArrayChild.optString("user"));
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public class CheckDislike extends AsyncTask<String, Float, String> {

        private Activity ctx;

        CheckDislike(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(dislikeStatus()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String userId = dislikeChecked.get(0).toString();
                        if(userId.equals(user)){
                            dislike.setClickable(false);
                            dislike.setEnabled(false);
                            dislike.setImageResource(R.drawable.dislike_check);
                            like.setImageResource(R.drawable.like);
                            like.setClickable(true);
                            like.setEnabled(true);
                        }
                    }
                });
            }
            return null;
        }
    }

    public boolean like(){

        httppost = new HttpPost("http://armconcaltfg.esy.es/php/createLike.php");
        nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("idR", idRestaurant));
        nameValuePairs.add(new BasicNameValuePair("user", user));

        try{
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpclient.execute(httppost);
            return true;
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (ClientProtocolException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public class CreateLike extends AsyncTask<String, String, String> {

        private Activity ctx;

        CreateLike(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(like()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "Like!", Toast.LENGTH_LONG).show();
                        new CheckLike(RestaurantActivity.this).execute();
                    }
                });
            }else{
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "Error creating like", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }

    public boolean dislike(){

        httppost = new HttpPost("http://armconcaltfg.esy.es/php/createDislike.php");
        nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("idR", idRestaurant));
        nameValuePairs.add(new BasicNameValuePair("user", user));

        try{
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpclient.execute(httppost);
            return true;
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (ClientProtocolException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public class CreateDislike extends AsyncTask<String, String, String> {

        private Activity ctx;

        CreateDislike(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(dislike()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "Dislike!", Toast.LENGTH_LONG).show();
                        new CheckDislike(RestaurantActivity.this).execute();
                    }
                });
            }else{
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "Error creating dislike", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }

    public String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        }else{
            return "";
        }
    }



    public class getEvents extends AsyncTask<String, Float, String> {

        private Activity ctx;


        getEvents(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ctx, EventsByRestaurantActivity.class);
                    Bundle b = getIntent().getExtras();
                    b.putString("nameRestaurant", info.get(0).toString());
                    b.putString("User", user);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
            });

            return null;
        }
    }

    public class Back extends AsyncTask<String, Float, String> {

        private Activity ctx;


        Back(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ctx, RestaurantsActivity.class);
                    Bundle b = getIntent().getExtras();
                    b.putString("Id", idRestaurant);
                    b.putString("User", user);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
            });

            return null;
        }
    }


    public void onBackPressed() {
        new Back(RestaurantActivity.this).execute();
    }

}