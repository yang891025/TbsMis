package com.tbs.tbsmis.util;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class CurrentLocation {

	public static String cityName; // 城市名

	private static Geocoder geocoder; // 此对象能通过经纬度来获取相应的城市等信息

	private static String latLongString;

	private static LocationManager locationManager;

	public static void getCNBylocation(Context context) {
		// 用于获取Location对象，以及其他

		String serviceName = Context.LOCATION_SERVICE;
		// 实例化一个LocationManager对象
        CurrentLocation.locationManager = (LocationManager) context
				.getSystemService(serviceName);
        CurrentLocation.geocoder = new Geocoder(context, Locale.CHINA);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // 精度
		criteria.setAltitudeRequired(false); // 不要求海拔
		criteria.setBearingRequired(false); // 不要求方位
		criteria.setCostAllowed(false); // 不允许有话费
		criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
		String provider = CurrentLocation.locationManager.getBestProvider(criteria, true);
		System.out.println("Best provider-->"+provider);
        CurrentLocation.openGPS();
//		locationManager.requestLocationUpdates(provider, 30000, 50,
//				locationListener);
		// 通过最后一次的地理位置来获得Location对象
		Location location = CurrentLocation.locationManager.getLastKnownLocation(provider);
//		while (location == null) {
//			location = locationManager.getLastKnownLocation(provider);
//		}
		String queryed_name = CurrentLocation.updateWithNewLocation(location);
		if (queryed_name != null && 0 != queryed_name.length()) {
            CurrentLocation.cityName = queryed_name;
		}
		// locationManager.requestLocationUpdates(provider, 30000, 50,
		// locationListener);
		// 移除监听器，在只有一个widget的时候，这个还是适用的
//		locationManager.removeUpdates(locationListener);
	}

    // 判断是否开启GPS，若未开启，打开GPS设置界面
    private static void openGPS() {       
        if (CurrentLocation.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        || CurrentLocation.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        ) {
        	System.out.println("位置源已设置！");
           //Toast.makeText(this, "位置源已设置！", Toast.LENGTH_SHORT).show();
            return;
        }
        System.out.println("位置源未设置！");
    }
	
	@SuppressWarnings("unused")
    private static final LocationListener locationListener = new LocationListener() {
		String tempCityName;

		@Override
		public void onLocationChanged(Location location) {
            this.tempCityName = CurrentLocation.updateWithNewLocation(location);
			if (this.tempCityName != null && this.tempCityName.length() != 0) {
                CurrentLocation.cityName = this.tempCityName;
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
            this.tempCityName = CurrentLocation.updateWithNewLocation(null);
			if (this.tempCityName != null && this.tempCityName.length() != 0) {
                CurrentLocation.cityName = this.tempCityName;
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private static String updateWithNewLocation(Location location) {
		String mcityName = "";
		double lat = 0;
		double lng = 0;
		List<Address> addList = null;
		if (location != null) {
			lat = location.getLatitude();
			lng = location.getLongitude();
            CurrentLocation.latLongString = "纬度:" + lat + "\n经度:" + lng + "\n";
			System.out.println("latLongString-->"+ CurrentLocation.latLongString);
		} else {
			System.out.println("无法获取地理信息");
		}
		try {
			addList = CurrentLocation.geocoder.getFromLocation(lat, lng, 1); // 解析经纬度
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (addList != null && addList.size() > 0) {
			for (int i = 0; i < addList.size(); i++) {
				Address add = addList.get(i);
				mcityName += add.getLocality();
			}
		}
		if (mcityName.length() != 0) {
			return mcityName.substring(0, mcityName.length() - 1);
		} else {
			return mcityName;
		}
	}

}