package com.example.test;

//Http Request 
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.*;
//QR Code
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
	Button b1;
	EditText username;
	EditText password;
	Boolean login = false;
	String saved_username = "default";
	String saved_password = "default";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		b1 = (Button) findViewById(R.id.button1);
		username = (EditText) findViewById(R.id.editText1);
		password = (EditText) findViewById(R.id.editText2);
		b1.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				//Verify login
				saved_username = username.getText().toString();
				saved_password = password.getText().toString();
				login = true;
				String responseText = "failed";

				// Creating HTTP Post
				HttpPost httpPost = new HttpPost(
						"https://ipassstore.com/business/v1/login.php");

				// Building post parameters
				// key and value pair
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
				nameValuePair.add(new BasicNameValuePair("email", saved_username));
				nameValuePair.add(new BasicNameValuePair("password",saved_password));

				// Url Encoding the POST parameters
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
				} catch (UnsupportedEncodingException e) {
					// writing error to Log
					e.printStackTrace();
				}
				submitrequest request = new submitrequest();

				try {
					responseText = request.execute(httpPost).get();
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				
				//Response Parser
				if(responseText.contains("200"))
				{
					try {
						JSONObject response = new JSONObject(responseText);
						String org_id = response.getString("organization_id");
						//Login to Home page
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, HomeActivity.class);
						//Pass in org ID
						Bundle bundle = new Bundle();
						bundle.putString("org_id", org_id);
						intent.putExtras(bundle);
						startActivityForResult(intent, 0);
						overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Toast.makeText(MainActivity.this, "Invalid Organization ID", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
					
				}
				else
				{
					//Do nothing
					Toast.makeText(MainActivity.this, "HTTP Response: "+ responseText, Toast.LENGTH_LONG).show();

				}

			}
		});
	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		username.setText(saved_username);
		password.setText(saved_password);

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

class submitrequest extends AsyncTask<HttpPost,Integer,String>
{
	String result = "failed";
	protected String doInBackground(HttpPost...params )
	{

		HttpClient httpClient = new DefaultHttpClient();
		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(params[0]);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				// writing response to log
				result = EntityUtils.toString(response.getEntity());
			}
			else
			{
				result = Integer.toString(response.getStatusLine().getStatusCode());
			}

		} catch (ClientProtocolException e) {
			// writing exception to log
			result="exception";
			e.printStackTrace();
		} catch (IOException e) {
			// writing exception to log
			result="exception";
			e.printStackTrace();

		}
		return result;
	}

	protected void onPostExecute(String result) {         
	} 
}