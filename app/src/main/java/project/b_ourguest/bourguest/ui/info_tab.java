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


import java.text.DecimalFormat;

import project.b_ourguest.bourguest.MainActivity;
import project.b_ourguest.bourguest.R;
import project.b_ourguest.bourguest.Model.Restaurant;

public class info_tab extends Fragment {
    Restaurant r = MainActivity.getRestaurantToPass();
    MapView mMapView;
    GoogleMap map;
    DecimalFormat df = new DecimalFormat("##.###");
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
        TextView about = (TextView) view.findViewById(R.id.about);
        about.setText("About " + convertToTitleCase(r.getName()));
        TextView bio = (TextView) view.findViewById(R.id.restaurantBio);
        bio.setText(r.getBio());
        TextView bio2 = (TextView) view.findViewById(R.id.distance);
        bio2.setText(df.format(r.getDistance()) + "km from you");


        return view;
    }

    public String convertToTitleCase(String name) {
        String[] partOfName = name.split(" ");
        char upperCaseLetter;
        name = "";
        String sub;
        for(int i = 0; i < partOfName.length; i++)
        {
            upperCaseLetter = Character.toUpperCase(partOfName[i].charAt(0));
            sub = partOfName[i].substring(1,partOfName[i].length());
            name = name + (upperCaseLetter + sub) + " ";
        }
        return name;
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