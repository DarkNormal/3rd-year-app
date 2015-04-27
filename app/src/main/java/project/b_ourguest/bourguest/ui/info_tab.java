package project.b_ourguest.bourguest.ui;

/**
 * Created by Mark on 3/28/2015.
 */
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import project.b_ourguest.bourguest.StartActivity;

public class info_tab extends Fragment {
    Restaurant r = MainActivity.getRestaurantToPass();
    MapView mMapView;
    GoogleMap map;
    DecimalFormat df = new DecimalFormat("##.##");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.info_tab, container, false);
        try {
            mMapView = (MapView) view.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            map = mMapView.getMap();
            map.setMyLocationEnabled(true);
        }catch(Exception e){
            e.printStackTrace();
        }

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            System.out.println("PROBLEM WITH GOOGLE MAP");
            e.printStackTrace();
        }
        LatLng location = new LatLng(Double.parseDouble(MainActivity.getRestaurantToPass().getLatitude()),
                Double.parseDouble(MainActivity.getRestaurantToPass().getLongitude()));
        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 12);
        //Zoom may get a bit annoying, consider setting it to pre-zoomed / no animation
        Marker loc = map.addMarker(new MarkerOptions().position(location).title(r.getName()));
        map.animateCamera(cameraUpdate);
        if(!r.isWifi())
        {
            ImageView im = (ImageView) view.findViewById(R.id.wifiLogo);
            im.setVisibility(View.INVISIBLE);
        }
        if(!r.getWheelchairAccessible())
        {
            ImageView im = (ImageView) view.findViewById(R.id.wheelchairLogo);
            im.setVisibility(View.INVISIBLE);
        }
        if(!r.isVegan())
        {
            ImageView im = (ImageView) view.findViewById(R.id.leafLogo);
            im.setVisibility(View.INVISIBLE);
        }
        TextView about = (TextView) view.findViewById(R.id.about);
        about.setText("About " + convertToTitleCase(r.getName()));

        TextView bio = (TextView) view.findViewById(R.id.restaurantBio);
        bio.setText(r.getBio());

        TextView dist = (TextView) view.findViewById(R.id.distance);
        if(StartActivity.getLat() == 0 && StartActivity.getLon() == 0)
        {
            TextView text = (TextView) view.findViewById(R.id.distanceFromYou);
            text.setVisibility(View.GONE);
            dist.setVisibility(View.GONE);
            RelativeLayout rel = (RelativeLayout) view.findViewById(R.id.infoTabRelativLayout);
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.FILL_PARENT);
            rlp.addRule(RelativeLayout.BELOW, bio.getId());
            rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            TextView t = (TextView) view.findViewById(R.id.contactDetails);
            t.setLayoutParams(rlp);
        }
        else
        {
            dist.setText(df.format(r.getDistance()) + "km from you");
        }




        final TextView phone = (TextView) view.findViewById(R.id.phoneNum);
        phone.setText(r.getPhoneNum());
        phone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                phone.setTextColor(Color.BLUE);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + r.getPhoneNum()));
                startActivity(callIntent);
                phone.setTextColor(Color.parseColor("#ff8bd1ff"));
            }
        });

        TextView email = (TextView) view.findViewById(R.id.email);
        email.setText(r.getEmail());
        TextView open = (TextView) view.findViewById(R.id.openingHours);
        open.setText(r.getOpeningHours());
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