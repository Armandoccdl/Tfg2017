package armando.app_tfg_armconcal2017;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SignUpActivity extends Activity {
    HttpClient httpclient = new DefaultHttpClient();
    ArrayList<NameValuePair> nameValuePairs;
    HttpPost httppost;
    EditText user, pass, email, phone;
    Button submit, cancel;

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

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Register(SignUpActivity.this).execute();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                finish();
            }

        });
    }

    public boolean addUser(){
        String u = user.getText().toString();
        String p = pass.getText().toString();
        String em = email.getText().toString();
        String ph = phone.getText().toString();

        if(!u.equals("") && !p.equals("") && !em.equals("") && !ph.equals("")){
            httppost = new HttpPost("http://armconcaltfg.esy.es/php/insertUser.php");
            nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("user", u));
            nameValuePairs.add(new BasicNameValuePair("password", p));
            nameValuePairs.add(new BasicNameValuePair("email", em));
            nameValuePairs.add(new BasicNameValuePair("phone", ph));

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

    public class Register extends AsyncTask<String, String, String> {

        private Activity ctx;

        Register(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(addUser()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "New user created", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.putExtra("User", user.getText().toString());
                        intent.putExtra("Pass", pass.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }else{
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "Complete all the fields", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }
}