package armando.app_tfg_armconcal2017;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SettingsActivity extends Activity {
    HttpClient httpclient = new DefaultHttpClient();
    ArrayList<NameValuePair> nameValuePairs;
    HttpPost httppost;

    EditText user, pass, email, phone;
    Button submit, cancel;
    String u,p,pho,e;

    String us;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = (EditText) findViewById(R.id.txtRegisterUser);
        pass = (EditText) findViewById(R.id.txtRegisterPass);
        email = (EditText) findViewById(R.id.txtRegisterEmail);
        phone = (EditText) findViewById(R.id.txtRegisterPhone);
        submit = (Button) findViewById(R.id.btnRegisterSubmit);
        cancel = (Button) findViewById(R.id.btnRegisterCancel);


        if(getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            u = b.getString("User");
            p = b.getString("Pass");
            pho = b.getString("Phone");
            e = b.getString("Email");
            user.setText(u);
            pass.setText(p);
            email.setText(e);
            phone.setText(pho);
        }





        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new EditUser(SettingsActivity.this).execute();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                new SettingsCancel(SettingsActivity.this).execute();
            }

        });
    }


    public boolean getData(){

        String v1 = user.getText().toString();
        String v2 = pass.getText().toString();
        String v3 = phone.getText().toString();
        String v4 = email.getText().toString();




        if(!v1.equals("") && !v2.equals("") && !v3.equals("") && !v4.equals("")){
            httppost = new HttpPost("http://armconcaltfg.esy.es/php/editUser.php");
            nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("user", v1));
            nameValuePairs.add(new BasicNameValuePair("pass", v2));
            nameValuePairs.add(new BasicNameValuePair("phone", v3));
            nameValuePairs.add(new BasicNameValuePair("email", v4));


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

        }
        return false;
    }

    public class EditUser extends AsyncTask<String, Float, String> {

        private Activity ctx;


        EditUser(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(getData()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "Your user has been update", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ctx, MenuActivity.class);
                        Bundle b = new Bundle();
                        b.putString("User", user.getText().toString());
                        b.putString("Pass", pass.getText().toString());
                        b.putString("Phone", phone.getText().toString());
                        b.putString("Email", email.getText().toString());
                        intent.putExtras(b);
                        startActivity(intent);
                        finish();
                    }
                });
            }else{
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "please complete all fields", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
    }

    public class SettingsCancel extends AsyncTask<String, Float, String> {

        private Activity ctx;


        SettingsCancel(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ctx, MenuActivity.class);
                    Bundle b = new Bundle();
                    b.putString("User", user.getText().toString());
                    b.putString("Pass", pass.getText().toString());
                    b.putString("Phone", phone.getText().toString());
                    b.putString("Email", email.getText().toString());
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
            });

            return null;
        }
    }

    public void onBackPressed() {
        new SettingsCancel(SettingsActivity.this).execute();
    }

}