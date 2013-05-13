package cm.aptoide.pt;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 09-05-2013
 * Time: 16:20
 * To change this template use File | Settings | File Templates.
 */
public class CountryCode {

    Context mContext;

    LocationManager locationManager;

    public CountryCode(Context context) {

        mContext = context;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        Geocoder coder = new Geocoder(context);
        List<Address> addressList = null;
        try {
            addressList = coder.getFromLocation(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(addressList.get(0).getCountryCode());


    }
}
