package edu.neu.arap.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.edu.neu.navigation.View.IAView;
import com.edu.neu.navigation.View.IndoorLocationListener;
import com.edu.neu.navigation.util.Point;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.neu.arap.R;
import edu.neu.arap.view.BlueDotView;
import edu.neu.arap.view.SquareImageView;

public class IndoorMapActivity extends AppCompatActivity {
	private final int CODE_PERMISSION = 6920;
	// blue dot radius in meters
	private static final float dotRadius = 1.0f;

	@Bind(R.id.indoor_map_view)
	IAView mIndoorMapView;
	@Bind(R.id.indoor_item_image)
	SquareImageView mIndoorItemImage;
	@Bind(R.id.indoor_description_button)
	Button mIndoorDescriptionButton;
	@Bind(R.id.indoor_ar_button)
	Button mIndoorARButton;
	@Bind(R.id.indoor_back_button)
	ImageButton mIndoorBackButton;

	private ArrayList<POI> mItemList;
	private ArrayList<String> mItemJsonList;

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
//		Log.i("IALocation", "onResume");

		ensurePermissions();

		mIndoorMapView.initResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mIndoorMapView.initPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mIndoorMapView.initDestroy();
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

		mIndoorMapView.setIndoorLocationListener(new IndoorLocationListener() {
			@Override
			public void onLocationChange(Point p) {
				Log.i("IAView", "LocationChanged");
			}
		});

		try {
			Intent intent = getIntent();
			JSONArray itemList = new JSONArray(intent.getStringExtra("json"));

			mItemList = new ArrayList<>();
			mItemJsonList = new ArrayList<>();

			for(int i = 0; i<itemList.length();i++){
				JSONObject item = itemList.optJSONObject(i);

				POI poi = new POI();
				poi.point = new Point(item.optDouble("ar_latitude"), item.optDouble("ar_longitude"), item.optString("title"));
				poi.name = item.optString("title");
				poi.address = item.optString("ar_address");
				poi.imageUrl = item.optJSONArray("ar_material").optJSONObject(0).optString("material_address");
				poi.arMaterials = item.optJSONArray("ar_material").toString();
				mItemList.add(poi);

				mItemJsonList.add(item.toString());
			}

			String imgUrl = itemList.optJSONObject(0).optJSONArray("ar_material").optJSONObject(0).optString("material_address");
			Picasso.with(this).load(imgUrl).into(mIndoorItemImage);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIndoorBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		mIndoorARButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(IndoorMapActivity.this, AugmentedActivity.class);
				intent.putExtra("JSONObject", mItemJsonList.get(0));
				startActivity(intent);
			}
		});
	}

	private void initActivity(){

	}

	private class POI{
		Point point;
		String imageUrl;
		String name;
		String address;
		String arMaterials;
	}
}
