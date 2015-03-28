package project.b_ourguest.bourguest.ui;

/**
 * Created by Mark on 3/28/2015.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import project.b_ourguest.bourguest.MainActivity;
import project.b_ourguest.bourguest.R;
import project.b_ourguest.bourguest.Restaurant;

public class info_tab extends Fragment {
    Restaurant r = MainActivity.getRestaurantToPass();
    MapView mMapView;
    GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.info_tab, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        map = mMapView.getMap();

        //map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LatLng location = new LatLng(Double.parseDouble(MainActivity.getRestaurantToPass().getLatitude()),
                Double.parseDouble(MainActivity.getRestaurantToPass().getLongitude()));
        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 12);
        //Zoom may get a bit annoying, consider setting it to pre-zoomed / no animation
        Marker loc = map.addMarker(new MarkerOptions().position(location).title(r.getName()));
        map.animateCamera(cameraUpdate);
        TextView bio = (TextView) view.findViewById(R.id.restaurantBio);
        bio.setText(r.getBio());
        TextView bio2 = (TextView) view.findViewById(R.id.test);
        bio2.setText(r.getBio());


        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}