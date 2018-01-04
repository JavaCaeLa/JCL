package sensor;

import sensor.JCL_Camera.OnSendPhoto
        ;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class JCL_Gps implements LocationListener {
    private OnSendGps onSendGps;
    private LocationManager locationManager;

    public interface OnSendGps {
        public void sendLocation(Location location);
    }

    public JCL_Gps(OnSendGps onSendGps, Context context) {
        this.onSendGps = onSendGps;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

    }
    public JCL_Gps( Context context) {
        //this.onSendGps = onSendGps;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

    }

    public void startListener(Context context) {
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

    }

    public void stopListener(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onLocationChanged(Location location) {
        Log.e("GPS", "Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
        if (onSendGps!=null)
            onSendGps.sendLocation(location);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }
}
