package mx.ken.devf.uper.Interfaces;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Ken on 05/03/2015.
 */
public interface GeocoderCallBack {

    public void onResponseSuccess(LatLng latitud);

    public void onError();
}
