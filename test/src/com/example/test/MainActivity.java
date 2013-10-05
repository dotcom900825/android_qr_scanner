package com.example.test;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.*;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		b1 = (Button) findViewById(R.id.button1);
		b1.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						CaptureActivity.class);
				// Intent intent = new
				// Intent("com.google.zxing.client.android.SCAN");
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
