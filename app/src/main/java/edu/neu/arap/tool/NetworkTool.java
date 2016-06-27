package edu.neu.arap.tool;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 16/6/27
 * Project: ARAP
 * Package: edu.neu.arap.tool
 */

public class NetworkTool {
	private RequestQueue requestQueue;
	private Context mContext;

	private OnResponseListener mOnResponseListener;

	public NetworkTool(Context context){
		mContext = context;
		requestQueue = Volley.newRequestQueue(context);
	}

	public void setOnResponseListener(OnResponseListener onResponseListener) {
		this.mOnResponseListener = onResponseListener;
	}

	public void requestMuseumMainData(final int code, double lat, double lng){
		// http://219.216.125.72:8080/AugumentReality/getInfo.html?longitude=123.425&latitude=41.77

		String url = "http://219.216.125.72:8080/AugumentReality/getInfo.html?longitude=" +
				String.valueOf(lng) +
				"&latitude=" +
				String.valueOf(lat);

		JsonObjectRequest request = new JsonObjectRequest(
				Request.Method.GET,
				url,
				null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if(mOnResponseListener!=null){
							mOnResponseListener.onResponse(code, response);
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if(mOnResponseListener!=null){
							mOnResponseListener.onError(code, error);
						}
					}
				}
		);

		requestQueue.add(request);
	}

	public void requestMuseumData(final int code, int id){
		// http://219.216.125.72:8080/AugumentReality/getInfo/14.html

		String url = "http://219.216.125.72:8080/AugumentReality/getInfo/" +
				String.valueOf(id) +
				".html";

		JsonObjectRequest request = new JsonObjectRequest(
				Request.Method.GET,
				url,
				null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if(mOnResponseListener!=null){
							mOnResponseListener.onResponse(code, response);
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if(mOnResponseListener!=null){
							mOnResponseListener.onError(code, error);
						}
					}
				}
		);

		requestQueue.add(request);
	}

	public void getImageResource(final int code, String url, ImageView targetView){
		Picasso.with(mContext).load(url).centerCrop().into(targetView, new Callback() {
			@Override
			public void onSuccess() {
				mOnResponseListener.onResponse(code, null);
			}

			@Override
			public void onError() {
				mOnResponseListener.onResponse(code, null);
			}
		});
	}

	public interface OnResponseListener{
		void onResponse(int code, JSONObject response);
		void onError(int code, VolleyError error);
	}
}
