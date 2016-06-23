package edu.neu.arap.activity;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;

import edu.neu.arap.R;

public class IndoorMapActivity extends AppCompatActivity {
	private final int CODE_PERMISSIONS = 6920;

	private IALocationManager mIALocationManager;
	private IALocationListener mIALocationListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indoor_map);

		String[] neededPermissions = {
				Manifest.permission.CHANGE_WIFI_STATE,
				Manifest.permission.ACCESS_WIFI_STATE,
				Manifest.permission.ACCESS_COARSE_LOCATION
		};
		ActivityCompat.requestPermissions( this, neededPermissions, CODE_PERMISSIONS );

		mIALocationManager = IALocationManager.create(this);
		mIALocationListener = new IALocationListener() {
			@Override
			public void onLocationChanged(IALocation iaLocation) {
				Log.i("IALocation", "Latitude: " + iaLocation.getLatitude());
				Log.i("IALocation", "Longitude: " + iaLocation.getLongitude());
			}

			@Override
			public void onStatusChanged(String s, int i, Bundle bundle) {

			}
		};
		Log.i("IALocation", "Initialized");
	}

	@Override
	protected void onResume() {
		super.onResume();
		mIALocationManager.requestLocationUpdates(IALocationRequest.create(), mIALocationListener);
		Log.i("IALocation", "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		mIALocationManager.removeLocationUpdates(mIALocationListener);
	}

	@Override
	protected void onDestroy() {
		mIALocationManager.destroy();
		super.onDestroy();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		//Handle if any of the permissions are denied, in grantResults
		Log.i("IALocation", "onRequestPermissionsResult");
	}
}
