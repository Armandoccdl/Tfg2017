package armando.app_tfg_armconcal2017;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class EventsActivity extends Activity{
    String user = "";
    String idEvent;

    ListView list;
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost;
    ArrayList<Event> events = new ArrayList<Event>();
    Event event;
    Button recommend;
    Activity ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        if(getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            String u = b.getString("User");
            user = u;
        }

        new List(EventsActivity.this).execute();

        list = (ListView) findViewById(R.id.listEvents);
        recommend = (Button) findViewById(R.id.btnEventsRecommend);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Event event = (Event) list.getAdapter().getItem(position);
                idEvent = String.valueOf(event.getId());
                Intent intent = new Intent(ctx, EventActivity.class);
                Bundle b = getIntent().getExtras();
                b.putString("Id", idEvent);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });

        recommend.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, EventsRecommendedActivity.class);
                Bundle b = getIntent().getExtras();
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });

    }
    //Se solicitan todos los eventos
    public String log() {
        httppost = new HttpPost("http://armconcaltfg.esy.es/php/getEvents.php");
        HttpResponse response;
        String result = "";

        try {
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

    //Se ordena la informacion y se crea una lista de eventos
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

    //Se añaden los restaurantes a la lista de la vista
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
                        list.setAdapter(new EventsAdapter(EventsActivity.this, R.layout.row_event, events) {
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
                    Intent intent = new Intent(ctx, MenuActivity.class);
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
        new Back(EventsActivity.this).execute();
    }

}
