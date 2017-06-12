package armando.app_tfg_armconcal2017;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

public class EventsByRestaurantActivity extends Activity{
    String user = "";
    String idEvent;

    ListView list;
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost;
    ArrayList<Event> events = new ArrayList<Event>();
    ArrayList<NameValuePair> nameValuePairs;
    Event event;
    Activity ctx = this;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        if(getIntent().getExtras() != null){
            b = getIntent().getExtras();
            String u = b.getString("User");
            user = u;
        }

        new List(EventsByRestaurantActivity.this).execute();

        list = (ListView) findViewById(R.id.listEvents);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Event event = (Event) list.getAdapter().getItem(position);
                idEvent = String.valueOf(event.getId());
                Intent intent = new Intent(ctx, EventByRestaurantActivity.class);
                Bundle b = getIntent().getExtras();
                b.putString("IdE", idEvent);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });
    }
    //Se recuperan los eventos de cierto restaurante
    public String log() {
        httppost = new HttpPost("http://armconcaltfg.esy.es/php/getEventsByRestaurant.php");
        nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("nameRestaurant", b.getString("nameRestaurant")));
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

    //Se crea una lista de eventos con esos eventos
    public boolean getEventsList(){
        events.clear();
        String data = log();
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try{
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("info");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    event = new Event(jsonArrayChild.optInt("id"),jsonArrayChild.optString("restaurant"),jsonArrayChild.optString("name"),jsonArrayChild.optString("date"),jsonArrayChild.optDouble("price"));
                    events.add(event);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //Se muestran los eventos en la lista de la vista
    public class List extends AsyncTask<String, Float, String> {

        private Activity ctx;

        List(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(getEventsList()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list.setAdapter(new EventsAdapter(EventsByRestaurantActivity.this, R.layout.row_event, events) {
                            @Override
                            public void onEntrance(Object entrance, View view) {
                                if (entrance != null) {
                                    TextView nameE = (TextView) view.findViewById(R.id.txtRowEventName);
                                    if (nameE != null)
                                        nameE.setText(""+((Event) entrance).getName());

                                    TextView restaurantE = (TextView) view.findViewById(R.id.txtRowEventRestaurant);
                                    if (restaurantE != null)
                                        restaurantE.setText(""+ ((Event) entrance).getRestaurant());

                                    TextView dateE = (TextView) view.findViewById(R.id.txtRowEventDate);
                                    if (dateE != null)
                                        dateE.setText(""+((Event) entrance).getDate());

                                    TextView priceE = (TextView) view.findViewById(R.id.txtRowEventPrice);
                                    if (priceE != null)
                                        priceE.setText(""+((Event) entrance).getPrice());
                                }
                            }
                        });
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
                    Intent intent = new Intent(ctx, RestaurantActivity.class);
                    Bundle b = getIntent().getExtras();
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
            });

            return null;
        }
    }

    public void onBackPressed() {
        new Back(EventsByRestaurantActivity.this).execute();
    }

}
