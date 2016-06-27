package edu.neu.arap.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IALatLng;
import com.indooratlas.android.sdk.resources.IAResourceManager;
import com.indooratlas.android.sdk.resources.IAResult;
import com.indooratlas.android.sdk.resources.IAResultCallback;
import com.indooratlas.android.sdk.resources.IATask;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.neu.arap.R;
import edu.neu.arap.view.BlueDotView;

public class IndoorMapActivity extends AppCompatActivity {
	private final int CODE_PERMISSION = 6920;
	// blue dot radius in meters
	private static final float dotRadius = 1.0f;

	@Bind(R.id.indoor_map_view)
	BlueDotView mIndoorMapView;

	private IALocationManager mIALocationManager;
	private IALocationListener mIALocationListener;
	private IAResourceManager mIAResourceManager;
	private IALocation mIALocation;
	private IATask<IAFloorPlan> mPendingAsyncResult;
	private IAFloorPlan mIAFloorPlan;

	private IARegion.Listener mRegionListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indoor_map);

		initView();
		initActivity();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("IALocation", "onResume");

		ensurePermissions();

		mIALocationManager.requestLocationUpdates(IALocationRequest.create(), mIALocationListener);
		mIALocationManager.registerRegionListener(mRegionListener);

		mIALocationManager.setLocation(mIALocation);


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
						Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) ||
				(ContextCompat.checkSelfPermission(this,
						Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			){
			String[] neededPermissions = {
					Manifest.permission.CHANGE_WIFI_STATE,
					Manifest.permission.ACCESS_WIFI_STATE,
					Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.WRITE_EXTERNAL_STORAGE
			};
			ActivityCompat.requestPermissions( this, neededPermissions, CODE_PERMISSION);
		}
	}

	private void initView(){
		ButterKnife.bind(this);


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

				if (mIndoorMapView != null && mIndoorMapView.isReady()) {
					IALatLng latLng = new IALatLng(iaLocation.getLatitude(), iaLocation.getLongitude());
					PointF point = mIAFloorPlan.coordinateToPoint(latLng);
					mIndoorMapView.setDotCenter(point);
					mIndoorMapView.postInvalidate();
				}
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

		Log.d("IALocation", "fetching floor plan...");
		fetchFloorPlan("02588c9d-6c01-45ad-9528-ad244b9a2fce");
	}

	private void fetchFloorPlan(String id) {
		cancelPendingNetworkCalls();
		final IATask<IAFloorPlan> asyncResult = mIAResourceManager.fetchFloorPlanWithId(id);
		mPendingAsyncResult = asyncResult;
		if (mPendingAsyncResult != null) {
			mPendingAsyncResult.setCallback(new IAResultCallback<IAFloorPlan>() {
				@Override
				public void onResult(IAResult<IAFloorPlan> result) {
					Log.d("IALocation", "fetch floor plan result:" + result);
					if (result.isSuccess() && result.getResult() != null) {
						mIAFloorPlan = result.getResult();

						mIndoorMapView.setRadius(mIAFloorPlan.getMetersToPixels() * dotRadius);

						Picasso.with(IndoorMapActivity.this)
								.load(mIAFloorPlan.getUrl())
								.into(mIndoorMapView);
					} else {
						// do something with error
						if (!asyncResult.isCancelled()) {
							Log.i("IALocation",result.getError() != null
									? "error loading floor plan: " + result.getError()
									: "access to floor plan denied");
						}
					}
				}
			}, Looper.getMainLooper()); // deliver callbacks in main thread
		}
	}

	private void cancelPendingNetworkCalls() {
		if (mPendingAsyncResult != null && !mPendingAsyncResult.isCancelled()) {
			mPendingAsyncResult.cancel();
		}
	}
}
