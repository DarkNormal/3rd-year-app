package project.b_ourguest.bourguest;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * Created by Robbie on 28/03/2015.
 */
public class ReviewsTab  extends Fragment {

    private TableLayout t;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("IN REVIEWS TAB------------------------------------");
        View view = inflater.inflate(R.layout.reviews_frag, container, false);
        t = (TableLayout) view.findViewById(R.id.tableLayout);
        if (DatabaseOperations.isReviewExists() == true)
        {
            System.out.println("HERE------------------------------------");
            t.setVisibility(View.INVISIBLE);
        }

        return view;
    }

}
