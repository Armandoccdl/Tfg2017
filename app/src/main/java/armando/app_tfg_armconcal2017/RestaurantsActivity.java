package armando.app_tfg_armconcal2017;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class RestaurantsActivity extends Activity{
    String user = "";
    String idRestaurant;

    ListView list;
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost;
    ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
    Restaurant restaurant;
    Activity ctx = this;
    Button recommend;
    ImageButton btnSearch;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        if(getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            String u = b.getString("User");
            user = u;
        }

        new List(RestaurantsActivity.this).execute();

        list = (ListView) findViewById(R.id.listRestaurants);
        recommend = (Button) findViewById(R.id.btnRestaurantsRecommend);
        btnSearch = (ImageButton) findViewById(R.id.btnRestaurantsSearch);
        search = (EditText) findViewById(R.id.txtRestaurantsSearch);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Restaurant restaurant = (Restaurant) list.getAdapter().getItem(position);
                idRestaurant = String.valueOf(restaurant.getId());
                Intent intent = new Intent(ctx, RestaurantActivity.class);
                Bundle b = getIntent().getExtras();
                b.putString("Id", idRestaurant);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });

        recommend.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
               Intent intent = new Intent(ctx, RestaurantsRecommendedActivity.class);
                Bundle b = getIntent().getExtras();
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, RestaurantsSearchActivity.class);
                Bundle b = getIntent().getExtras();
                b.putString("searchString", search.getText().toString());
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }


        });


    }
    //ordenamos todos los restaurantes
    public boolean getRestaurantsList(){
        restaurants.clear();
        String data = log();
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try{
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("info");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    restaurant = new Restaurant(jsonArrayChild.optInt("id"),jsonArrayChild.optInt("likes"),jsonArrayChild.optInt("dislikes"),jsonArrayChild.optString("name"),jsonArrayChild.optString("phone"));
                    restaurants.add(restaurant);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    //Solicitamos todos los restaurantes
    public String log() {
        httppost = new HttpPost("http://armconcaltfg.esy.es/php/getRestaurants.php");
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
    //Mostramos los restaurantes en la lista de la vista
    public class List extends AsyncTask<String, Float, String> {

        private Activity ctx;

        List(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(getRestaurantsList()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list.setAdapter(new RestaurantsAdapter(RestaurantsActivity.this, R.layout.row_restaurant, restaurants) {
                            @Override
                            public void onEntrance(Object entrance, View view) {
                                if (entrance != null) {
                                    TextView nameR = (TextView) view.findViewById(R.id.txtRowRestaurantName);
                                    if (nameR != null)
                                        nameR.setText(""+((Restaurant) entrance).getName());

                                    TextView phoneR = (TextView) view.findViewById(R.id.txtRowRestaurantPhone);
                                    if (phoneR != null)
                                        phoneR.setText(""+ ((Restaurant) entrance).getPhone());

                                    TextView likesR = (TextView) view.findViewById(R.id.txtRowRestaurantLikes);
                                    if (likesR != null)
                                        likesR.setText(""+((Restaurant) entrance).getLikes());

                                    TextView dislikesR = (TextView) view.findViewById(R.id.txtRowRestaurantDislikes);
                                    if (dislikesR != null)
                                        dislikesR.setText(""+((Restaurant) entrance).getDislikes());
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
        new Back(RestaurantsActivity.this).execute();
    }

}
