package edu.neu.arap.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.neu.navigation.Enum.Instruction;
import com.edu.neu.navigation.View.IAView;
import com.edu.neu.navigation.listener.IndoorListener;
import com.edu.neu.navigation.util.Point;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.neu.arap.R;
import edu.neu.arap.view.SquareImageView;

public class IndoorMapActivity extends AppCompatActivity{
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
	@Bind(R.id.indoor_guide_card)
	CardView mIndoorGuideCard;
	@Bind(R.id.indoor_item_name)
	TextView mIndoorItemName;
	@Bind(R.id.indoor_direction)
	ImageView mIndoorDirection;
	@Bind(R.id.indoor_direction_hint)
	TextView mIndoorDirectionHint;

	private ArrayList<POI> mItemList;
	private ArrayList<String> mItemJsonList;
	private int mCurrentItemIndex;

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

		mIndoorMapView.setIndoorLocationListener(new IndoorListener() {
			@Override
			public void onLocationChange(Point p, int index) {
				Log.i("IAView", "LocationChanged");
				Toast.makeText(IndoorMapActivity.this, "到达" + p.getLatitude() +", " + p.getLongitude(), Toast.LENGTH_LONG).show();

				POI point = mItemList.get(index);
				Picasso.with(IndoorMapActivity.this).load(point.imageUrl).into(mIndoorItemImage);
				mIndoorItemName.setText(point.name);

				mCurrentItemIndex = index;

				if(mIndoorGuideCard.getVisibility()== View.INVISIBLE){
					mIndoorGuideCard.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onFloorPlanReadyToDownload(String url) {

			}

			@Override
			public void onDrawReady(Canvas c, PointF p) {
				sendPoints();
			}

			@Override
			public void onNavigationReady(List<Instruction> instructions) {
				Log.i("IndoorMapActivity", instructions.toString());

				if(instructions.size()==1){
					switch (instructions.get(0)){
						case STRAIGHT:
							mIndoorDirection.setImageResource(R.drawable.ic_up);
							mIndoorDirectionHint.setText("正前方");
							break;
						case RIGHT:
							mIndoorDirection.setImageResource(R.drawable.ic_right);
							mIndoorDirectionHint.setText("正右方");
							break;
						case LEFT:
							mIndoorDirection.setImageResource(R.drawable.ic_left);
							mIndoorDirectionHint.setText("正左方");
							break;
						case BACK:
							mIndoorDirection.setImageResource(R.drawable.ic_down);
							mIndoorDirectionHint.setText("正后方");
							break;
					}
				}else {
					int dir = 0;
					switch (instructions.get(0)){
						case LEFT:
							dir |= 1;
							break;
						case BACK:
							dir |= 2;
							break;
					}
					switch (instructions.get(1)){
						case LEFT:
							dir |= 1;
							break;
						case BACK:
							dir |= 2;
							break;
					}
					switch (dir){
						case 0:
							mIndoorDirection.setImageResource(R.drawable.ic_up_right);
							mIndoorDirectionHint.setText("右前方");
							break;
						case 1:
							mIndoorDirection.setImageResource(R.drawable.ic_up_left);
							mIndoorDirectionHint.setText("左前方");
							break;
						case 2:
							mIndoorDirection.setImageResource(R.drawable.ic_down_right);
							mIndoorDirectionHint.setText("右后方");
							break;
						case 3:
							mIndoorDirection.setImageResource(R.drawable.ic_down_left);
							mIndoorDirectionHint.setText("左后方");
							break;
					}
				}
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
				intent.putExtra("JSONObject", mItemJsonList.get(mCurrentItemIndex));
				startActivity(intent);
			}
		});

		mIndoorGuideCard.setVisibility(View.INVISIBLE);

	}

	private void initActivity(){
		mCurrentItemIndex = -1;
	}

	private void sendPoints(){
		List<Point> points = new ArrayList<>();

		for(POI item:mItemList){
			points.add(item.point);
		}
		// todo: 在这里设置点集
		mIndoorMapView.setPoiList(points);
	}

	private class POI{
		Point point;
		String imageUrl;
		String name;
		String address;
		String arMaterials;
	}
}
