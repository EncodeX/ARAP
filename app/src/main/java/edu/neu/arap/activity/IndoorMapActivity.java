package edu.neu.arap.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAResourceManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.neu.arap.R;

public class IndoorMapActivity extends AppCompatActivity {
	private final int CODE_WIFI_AND_LOCATION = 6920;
	private final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 6921;



	private IALocationManager mIALocationManager;
	private IALocationListener mIALocationListener;
	private IAResourceManager mIAResourceManager;
	private IALocation mIALocation;

	private IARegion.Listener mRegionListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indoor_map);

		ButterKnife.bind(this);

		ensurePermissions();

		initView();
		initActivity();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("IALocation", "onResume");
		mIALocationManager.requestLocationUpdates(IALocationRequest.create(), mIALocationListener);
		mIALocationManager.registerRegionListener(mRegionListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mIALocationManager.removeLocationUpdates(mIALocationListener);
		mIALocationManager.unregisterRegionListener(mRegionListener);
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

	private void ensurePermissions() {
		if (
				(ContextCompat.checkSelfPermission(this,
						Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
				(ContextCompat.checkSelfPermission(this,
						Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) ||
				(ContextCompat.checkSelfPermission(this,
						Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
			){
			String[] neededPermissions = {
					Manifest.permission.CHANGE_WIFI_STATE,
					Manifest.permission.ACCESS_WIFI_STATE,
					Manifest.permission.ACCESS_COARSE_LOCATION
			};
			ActivityCompat.requestPermissions( this, neededPermissions, CODE_WIFI_AND_LOCATION);
		}

		if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
		}
	}

	private void initView(){

	}

	private void initActivity(){
		Bundle bundle = new Bundle(2);
		bundle.putString(IALocationManager.EXTRA_API_KEY, getString(R.string.atlas_api_key));
		bundle.putString(IALocationManager.EXTRA_API_SECRET, getString(R.string.atlas_api_secret));

		mIALocationManager = IALocationManager.create(this, bundle);
		mIALocationListener = new IALocationListener() {
			@Override
			public void onLocationChanged(IALocation iaLocation) {
				Log.i("IALocation", "Latitude: " + iaLocation.getLatitude());
				Log.i("IALocation", "Longitude: " + iaLocation.getLongitude());
			}

			@Override
			public void onStatusChanged(String s, int i, Bundle bundle) {
				Log.i("IALocation", "onStatusChanged");

				switch (i){
					case IALocationManager.STATUS_AVAILABLE:
						Log.i("IALocation", "STATUS_AVAILABLE");
						break;
					case IALocationManager.STATUS_CALIBRATION_CHANGED:
						Log.i("IALocation", "STATUS_CALIBRATION_CHANGED");
						break;
					case IALocationManager.STATUS_LIMITED:
						Log.i("IALocation", "STATUS_LIMITED");
						break;
					case IALocationManager.STATUS_OUT_OF_SERVICE:
						Log.i("IALocation", "STATUS_OUT_OF_SERVICE");
						break;
					case IALocationManager.STATUS_TEMPORARILY_UNAVAILABLE:
						Log.i("IALocation", "STATUS_TEMPORARILY_UNAVAILABLE");
						break;
				}
			}
		};

		mRegionListener = new IARegion.Listener() {
			@Override
			public void onEnterRegion(IARegion region) {
//				if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
//					fetchFloorPlan(region.getId());
//				}
				Log.i("IALocation","Region Entered");
			}

			@Override
			public void onExitRegion(IARegion region) {
				// leaving a previously entered region
			}
		};

		mIAResourceManager = IAResourceManager.create(this);

		mIALocation = IALocation.from(IARegion.floorPlan("02588c9d-6c01-45ad-9528-ad244b9a2fce"));

		mIALocationManager.setLocation(mIALocation);
	}
}
