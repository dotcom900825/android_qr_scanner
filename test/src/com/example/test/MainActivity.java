package com.example.test;

//Http Request 
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;



public class MainActivity extends Activity {
	Button b1;
	EditText username;
	EditText password;
	Boolean login = false;
	String saved_username;
	String saved_password;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		b1 = (Button) findViewById(R.id.button1);
		username = (EditText) findViewById(R.id.editText2);
		password = (EditText) findViewById(R.id.editText1);
		b1.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				//Verify login
				saved_username = username.getText().toString();
				saved_password = password.getText().toString();
				login = true;
				
				// Creating HTTP Post
				HttpPost httpPost = new HttpPost(
						"https://www.ipassstore.com/lib/ws/v1/http_responder.php");

				// Building post parameters
				// key and value pair
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
				nameValuePair.add(new BasicNameValuePair("username", saved_username));
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
						String responseText = request.execute(httpPost).get();
						Toast.makeText(MainActivity.this, "HTTP Response: "+ responseText, Toast.LENGTH_LONG).show();

					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}


				//Start QR Code Activity
				Intent intent = new Intent(MainActivity.this,
						CaptureActivity.class);
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, 0);
			}
		});
	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == 1) {
				// Handle successful scan
				String capturedQrValue = intent.getStringExtra("RESULT");
				//String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Toast.makeText(MainActivity.this,
						"Scan Result:" + capturedQrValue, Toast.LENGTH_SHORT)
						.show();

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Toast.makeText(MainActivity.this,
						"Scanned activity cancelled",Toast.LENGTH_LONG)
						.show();
			}
		} else {

		}
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

			// writing response to log
			result = EntityUtils.toString(response.getEntity());

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();

		}
		return result;
	}
	
	protected void onPostExecute(String result) {         
} 
}