package project.b_ourguest.bourguest.ui;

/**
 * Created by Mark on 3/28/2015.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

import project.b_ourguest.bourguest.DB.DatabaseOperations;
import project.b_ourguest.bourguest.MainActivity;
import project.b_ourguest.bourguest.Model.Restaurant;
import project.b_ourguest.bourguest.Model.Reviews;
import project.b_ourguest.bourguest.R;
import project.b_ourguest.bourguest.StartActivity;

public class reviews_tab extends Fragment {
    private TextView t;
    private ImageView im;
    private ArrayList<Reviews> reviews = StartActivity.getReviews();
    Restaurant r = MainActivity.getRestaurantToPass();
    boolean hasReview = false;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        System.out.println("IN THE REVIEWS TAB------------------------------------");
        View v = inflater.inflate(R.layout.reviews_tab,container,false);
        t = (TextView) v.findViewById(R.id.ratingText);
        im = (ImageView) v.findViewById(R.id.rating);
        for(int i = 0; i < reviews.size();i++)
        {
            if(reviews.get(i).getId().equals(r.getId()))
            {
                hasReview = true;
                t.setText(reviews.get(i).getRating() + " stars");
            }

        }

        if(!hasReview)
        {
            im.setVisibility(View.INVISIBLE);
            t.setText("Be the first to post a review of " + convertToTitleCase(r.getName()));
        }
        return v;
    }

    public String convertToTitleCase(String name) {
        String[] partOfName = name.split(" ");
        char upperCaseLetter;
        name = "";
        String sub;
        for (int i = 0; i < partOfName.length; i++) {
            upperCaseLetter = Character.toUpperCase(partOfName[i].charAt(0));
            sub = partOfName[i].substring(1, partOfName[i].length());
            name = name + (upperCaseLetter + sub) + " ";
        }
        return name;
    }
}
