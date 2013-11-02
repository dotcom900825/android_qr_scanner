package com.example.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;



public class HomeActivity extends Activity {
	Button b1;
	Intent intent;
	String org_id;

	private GestureDetector detector;
	private ViewFlipper flipper;

	class GestureListener implements OnGestureListener
	{
		@Override    
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
		{    
			if ((velocityX > 20)||(velocityX < -20) ) {    
				//Start QR Code Activity
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				startActivityForResult(intent, 0);
				return true;    
			}
			else
			{
				return false;
			}
		}

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}    
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent=this.getIntent();
		Bundle bundle = this.getIntent().getExtras();
		org_id = bundle.getString("org_id");
		setContentView(R.layout.activity_home);
		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		};
		setOnTouchListener(gestureListener);
		detector = new GestureDetector(HomeActivity.this,new GestureListener());

		b1 = (Button) findViewById(R.id.button1);
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//Log out
				HomeActivity.this.setResult(RESULT_OK,intent);
				HomeActivity.this.finish();
			}
		});

	}

	private void setOnTouchListener(OnTouchListener gestureListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return this.detector.onTouchEvent(event);
	}


	@SuppressLint("ResourceAsColor")
	public void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		if (resultCode!=0) 
		{
			// handle scan result
			// Handle successful scan
			String capturedQrValue = intent.getStringExtra("SCAN_RESULT");
			// Handle cancel
			// Creating HTTP Post
			HttpPost httpPost = new HttpPost(
					"https://ipassstore.com/business/v1/validate.php");

			// Building post parameters
			// key and value pair
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
			nameValuePair.add(new BasicNameValuePair("serial_number", capturedQrValue));
			nameValuePair.add(new BasicNameValuePair("organization_id",org_id));
			// Url Encoding the POST parameters
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			} catch (UnsupportedEncodingException e) {
				// writing error to Log
				Toast.makeText(HomeActivity.this, "Encoding exception", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			String result;
			codevalidator request = new codevalidator();
			try {
				result = request.execute(httpPost).get();
//				Toast.makeText(HomeActivity.this, "Validate Result:"+result, Toast.LENGTH_LONG).show();
				//Response Parser
				if(result.contains("200"))
				{

					try {
						JSONObject response = new JSONObject(result);
						String text = response.getString("text");
						if(text.equals("success"))
						{
							Toast.makeText(HomeActivity.this, "Validation success", Toast.LENGTH_SHORT).show();
							TextView home = (TextView)findViewById(R.id.textView1);
							Resources resources = getBaseContext().getResources();
							Drawable drawable = resources.getDrawable(android.R.color.white);
							home.setBackground(drawable);
						}
						else
						{
							//Error handling
							//Set screen color to red
							TextView home = (TextView)findViewById(R.id.textView1);
							Resources resources = getBaseContext().getResources();
							Drawable drawable = resources.getDrawable(android.R.color.holo_red_light);
							home.setBackground(drawable);
							//Put Toast
							Toast.makeText(HomeActivity.this, "Validation failed", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						Toast.makeText(HomeActivity.this, "Invalid Organization ID", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
				else if(result.contains("404"))
				{
					//Error handling
					//Set screen color to red
					TextView home = (TextView)findViewById(R.id.textView1);
					Resources resources = getBaseContext().getResources();
					Drawable drawable = resources.getDrawable(android.R.color.holo_orange_light);
					home.setBackground(drawable);
					Toast.makeText(HomeActivity.this, "Validation failed:Ticket not found", Toast.LENGTH_LONG).show();
				}
				else
				{
					//Error handling
					//Set screen color to red
					TextView home = (TextView)findViewById(R.id.textView1);
					Resources resources = getBaseContext().getResources();
					Drawable drawable = resources.getDrawable(android.R.color.holo_purple);
					home.setBackground(drawable);
					Toast.makeText(HomeActivity.this, "Validation failed:Unexpected error", Toast.LENGTH_LONG).show();
				}
			} catch (InterruptedException e) {
				Toast.makeText(HomeActivity.this, "Interrupted exception", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (ExecutionException e) {
				Toast.makeText(HomeActivity.this, "Execution exception", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


		}else if (resultCode == RESULT_CANCELED) {
			// Handle cancel
			Toast.makeText(HomeActivity.this,
					"Scanned activity cancelled",Toast.LENGTH_LONG)
					.show();
		}
		else
		{
			// Handle cancel
			Toast.makeText(HomeActivity.this,
					"Scanned activity failed, result code:"+resultCode,Toast.LENGTH_LONG)
					.show();
		}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

class codevalidator extends AsyncTask<HttpPost,Integer,String>
{
	String result;
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}
		return result;
	}

	protected void onPostExecute(String result) {         
	} 
}
