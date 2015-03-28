package project.b_ourguest.bourguest.ui;

/**
 * Created by Mark on 3/28/2015.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import project.b_ourguest.bourguest.DatabaseOperations;
import project.b_ourguest.bourguest.MainActivity;
import project.b_ourguest.bourguest.R;
import project.b_ourguest.bourguest.Restaurant;

public class reviews_tab extends Fragment {
    Restaurant r = MainActivity.getRestaurantToPass();
    DatabaseOperations db = new DatabaseOperations();
    SharedPreferences settings;
    String userID;
    TableLayout t;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.reviews_tab,container,false);
        settings = this.getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userID = settings.getString("email", "").toString();
        db.getReview(userID,r.getId());
        t = (TableLayout) v.findViewById(R.id.tableLayout);
        if (DatabaseOperations.isReviewExists() == true)
        {
            System.out.println("HERE------------------------------------");
            t.setVisibility(View.INVISIBLE);
        }
        else{
            t.setVisibility(View.VISIBLE);
            System.out.println("REVIEW NOT PASSED FROM DB OPS");
        }

        return v;
    }




}
