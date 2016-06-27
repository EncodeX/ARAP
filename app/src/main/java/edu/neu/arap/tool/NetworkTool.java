package edu.neu.arap.tool;

import android.content.Context;
import android.widget.ImageView;

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

	public NetworkTool(Context context){
		mContext = context;
		requestQueue = Volley.newRequestQueue(context);
	}

	public void requestMuseumMainData(double lat, double lng, OnResponseListener listener){
		// http://219.216.125.72:8080/AugumentReality/getInfo.html?longitude=123.425&latitude=41.77

		final Request req = new Request();

		req.setOnResponseListener(listener);

		String url = "http://219.216.125.72:8080/AugumentReality/getInfo.html?longitude=" +
				String.valueOf(lng) +
				"&latitude=" +
				String.valueOf(lat);

		JsonObjectRequest request = new JsonObjectRequest(
				com.android.volley.Request.Method.GET,
				url,
				null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if(req.mOnResponseListener!=null){
							req.mOnResponseListener.onResponse(response);
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if(req.mOnResponseListener!=null){
							req.mOnResponseListener.onError(error);
						}
					}
				}
		);

		requestQueue.add(request);
	}

	public void requestMuseumData(int id, OnResponseListener listener){
		// http://219.216.125.72:8080/AugumentReality/getInfo/show/14.html

		final Request req = new Request();

		req.setOnResponseListener(listener);

		String url = "http://219.216.125.72:8080/AugumentReality/getInfo/show/" +
				String.valueOf(id) +
				".html";

		JsonObjectRequest request = new JsonObjectRequest(
				com.android.volley.Request.Method.GET,
				url,
				null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if(req.mOnResponseListener!=null){
							req.mOnResponseListener.onResponse(response);
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if(req.mOnResponseListener!=null){
							req.mOnResponseListener.onError(error);
						}
					}
				}
		);

		requestQueue.add(request);
	}

	public void getImageResource(String url, ImageView targetView, OnResponseListener listener){

		final Request req = new Request();

		req.setOnResponseListener(listener);

		Picasso.with(mContext).load(url).centerCrop().into(targetView, new Callback() {
			@Override
			public void onSuccess() {
				if(req.mOnResponseListener!=null){
					req.mOnResponseListener.onResponse(null);
				}
			}

			@Override
			public void onError() {
				if(req.mOnResponseListener!=null){
					req.mOnResponseListener.onError(null);
				}
			}
		});
	}

	public interface OnResponseListener{
		void onResponse(JSONObject response);
		void onError(VolleyError error);
	}

	class Request{
		OnResponseListener mOnResponseListener;

		void setOnResponseListener(OnResponseListener onResponseListener) {
			this.mOnResponseListener = onResponseListener;
		}
	}
}
