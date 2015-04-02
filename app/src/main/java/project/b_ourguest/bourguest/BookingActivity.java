package project.b_ourguest.bourguest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import project.b_ourguest.bourguest.DB.DatabaseOperations;
import project.b_ourguest.bourguest.Model.Floorplan;
import project.b_ourguest.bourguest.Model.tableObject;


/**
 * Created by Robbie on 05/03/2015.
 */
public class BookingActivity extends ActionBarActivity {
    private DatabaseOperations db = new DatabaseOperations();
    private ArrayList<tableObject> tableview = DatabaseOperations.getTables();
    private ArrayList<tableObject> selected = new ArrayList<tableObject>();
    private ArrayList<Floorplan> fplan = DatabaseOperations.getFloorplans();
    private int k=0;
    private int day, month, year, time;
    private Handler h = new Handler();
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(convertToTitleCase(MainActivity.getRestaurantToPass().getName()));
        ScrollView sv = new ScrollView(this);
        try {
         int rl = fplan.get(0).getHeight();

        int cl = fplan.get(0).getWidth();
        System.out.println(rl + "  " + cl);
        TableLayout tableLayout = createTableLayout(rl, cl);
        HorizontalScrollView hsv = new HorizontalScrollView(this);
        hsv.addView(tableLayout);
        sv.addView(hsv);
        sv.setFillViewport(true);
        setContentView(sv);
            Bundle extras = getIntent().getExtras();
            day = extras.getInt("day");
            month = extras.getInt("month");
            year = extras.getInt("year");
            time = extras.getInt("time");
        }catch(Exception e)
        {
            setContentView(R.layout.no_floorplan_layout);
            TextView t = (TextView) findViewById(R.id.noFloorplanToDisplay);
            t.setText(convertToTitleCase(MainActivity.getRestaurantToPass().getName()) +
            " has no floorplan to display. Below is there phone number if you wish to contact them.");

            TextView t2 = (TextView) findViewById(R.id.restNum);
            t2.setTextColor(Color.parseColor("#ff8bd1ff"));
            t2.setText(MainActivity.getRestaurantToPass().getPhoneNum());
            t2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + MainActivity.getRestaurantToPass().getPhoneNum()));
                    startActivity(callIntent);

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish(); //this will finish the activity when the back button is pressed
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.book_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.book){
            if(selected.size() == 0)
                Toast.makeText(BookingActivity.this, "No tables were selected", Toast.LENGTH_SHORT).show();
            else {
                db.confimBookings(selected);
                pd = ProgressDialog.show(BookingActivity.this, "Processing", "Validating your booking..");
                h.postDelayed(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        if(DatabaseOperations.isFound())
                        {
                            new AlertDialog.Builder(BookingActivity.this)
                                    .setTitle("Booking Error")
                                    .setMessage("One or more of your tables has become unavailable")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                        }
                                    })

                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                        else
                        {
                            new AlertDialog.Builder(BookingActivity.this)
                                    .setTitle("Confirm Booking")
                                    .setMessage("Confirm")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            SharedPreferences settings = getSharedPreferences("LoginPrefs", 0);
                                            String userID = settings.getString("email", "").toString();
                                            db.postBooking(selected, userID, day , month, year, time);

                                            // continue with delete
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }

                    }
                }, 3500);
            }

        }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
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

    private TableLayout createTableLayout(int rowCount, int columnCount) {
        // 1) Create a tableLayout and its params
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setBackgroundColor(Color.BLACK);

        // 2) create tableRow params
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.setMargins(2, 2, 2, 2);

        tableRowParams.weight = 2;
        System.out.println(k + " value of k");
        for (k = 0; k < rowCount; k++) {

            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundColor(Color.BLACK);
            for (int i = 0; i < columnCount; i++) {
                final ImageView im = new ImageView(this);
                im.setImageResource(R.drawable.blank);
                for (int j = 0; j < tableview.size(); j++) {
                    //  textView.setText(String.valueOf(j));
                    im.setBackgroundColor(Color.WHITE);
                    if (tableview.get(j).getYcoord() == k && tableview.get(j).getXcoord() == i) {
                        //System.out.println("Found match at " + k + "," + i + " color is " + tableview.get(k).getColor());

                        if (tableview.get(j).getColor() == 1) {
                            im.setImageResource(R.drawable.greentable);
                            im.setTag(R.drawable.greentable);
                            im.setContentDescription("" + j);

                            im.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    System.out.println(im.getContentDescription() + " conent desc");
                                    if (im.getTag().toString().equals("2130837587")) {
                                        im.setImageResource(R.drawable.blutable);
                                        im.setTag(R.drawable.blutable);
                                        selected.add(tableview.get(Integer.parseInt("" + im.getContentDescription())));
                                    } else {
                                        selected.remove(tableview.get(Integer.parseInt("" + im.getContentDescription())));
                                        im.setImageResource(R.drawable.greentable);
                                        im.setTag(R.drawable.greentable);
                                    }

                                }
                            });
                            //System.out.println("color is set to 1");
                        } else {

                            //System.out.println("color is set to 2");
                            im.setImageResource(R.drawable.redtable);
                        }


                    }

                }

                tableRow.addView(im, tableRowParams);

            }

            tableLayout.addView(tableRow, tableLayoutParams);

        }

        return tableLayout;
    }
}
