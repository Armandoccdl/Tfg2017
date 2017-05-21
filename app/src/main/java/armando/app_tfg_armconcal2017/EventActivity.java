package armando.app_tfg_armconcal2017;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.ArrayList;

public class EventActivity extends Activity {
    String idEvent = "";
    String user = "";
    TextView name, description, restaurant, date, price, assistants;
    HttpClient httpclient = new DefaultHttpClient();
    ArrayList<NameValuePair> nameValuePairs;
    HttpPost httppost;
    ArrayList<Object> info = new ArrayList<>();
    Activity ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        if(getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            String i = b.getString("Id");
            String u = b.getString("User");
            idEvent = i;
            user = u;
        }

        name = (TextView) findViewById(R.id.txtEventName);
        description = (TextView) findViewById(R.id.txtEventDescription);
        restaurant = (TextView) findViewById(R.id.txtEventRestaurant);
        date = (TextView) findViewById(R.id.txtEventDate);
        price = (TextView) findViewById(R.id.txtEventPrice);
        assistants = (TextView) findViewById(R.id.txtEventAssistants);

        new EventActivity.Info(EventActivity.this).execute();




    }


    public String log() {
        httppost = new HttpPost("http://armconcaltfg.esy.es/php/getEvent.php");
        nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("id", idEvent));
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
        System.out.println("Returns: " + data);
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try{
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("info");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    info.add(jsonArrayChild.optString("name"));
                    info.add(jsonArrayChild.optString("description"));
                    info.add(jsonArrayChild.optString("restaurant"));
                    info.add(jsonArrayChild.optString("date"));
                    info.add(jsonArrayChild.optString("price"));
                    info.add(jsonArrayChild.optString("assistants"));
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
                        description.setText(info.get(1).toString());
                        restaurant.setText(info.get(2).toString());
                        date.setText(info.get(3).toString());
                        price.setText(info.get(4).toString());
                        assistants.setText(info.get(5).toString());
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





}
