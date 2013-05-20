package cm.aptoide.pt;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 17-05-2013
 * Time: 9:25
 * To change this template use File | Settings | File Templates.
 */
public class Geolocation {

    static String getCountryCode(Context context){
        String countryCode = "";
        try{
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            countryCode = manager.getSimCountryIso();

        }catch (Exception e){

            countryCode = getCountryCodeByGps(context);

        }



        return countryCode;
    }

    private static String getCountryCodeByGps(Context context) {
        String countryCode = "";
        try{

            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Geocoder coder = new Geocoder(context);

            List<Address> addressList = coder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            countryCode = addressList.get(0).getCountryCode();

        } catch (Exception e){
            Log.w("Aptoide-Geolocation", "Unable to get country code.");
            e.printStackTrace();
        }

        return countryCode;
    }

}
