package project.b_ourguest.bourguest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
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
import project.b_ourguest.bourguest.Model.Bookings;
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
    private int k = 0;
    private int a = 0,totalPeople = 0;
    private int day, month, year, time;
    private long timeInMillis;
    private Handler h = new Handler();
    private ProgressDialog pd;
    String userID;
    ScrollView sv;
    TextView te;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences("LoginPrefs", 0);
         userID = settings.getString("email", "").toString();
        RelativeLayout.LayoutParams rlp;
        ScrollView scroll = new ScrollView(this);
        setTitle(convertToTitleCase(MainActivity.getRestaurantToPass().getName()));
        RelativeLayout rel = new RelativeLayout(this);
        if (fplan.size() == 0) {
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
        } else {
            for (a = 0; a < fplan.size(); a++) {
                if (a == 0) {
                    rlp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    te = new TextView(this);
                    te.setTextAppearance(this, android.R.style.TextAppearance_Large);
                    te.setId(View.generateViewId());
                    te.setText(fplan.get(a).getPlanName());
                    te.setLayoutParams(rlp);
                    rel.addView(te);
                } else {
                    rlp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    rlp.addRule(RelativeLayout.BELOW, sv.getId());
                    te = new TextView(this);
                    te.setTextAppearance(this, android.R.style.TextAppearance_Large);
                    te.setId(View.generateViewId());
                    te.setText(fplan.get(a).getPlanName());
                    te.setLayoutParams(rlp);
                    rel.addView(te);
                }
                rlp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlp.addRule(RelativeLayout.BELOW, te.getId());
                rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);

                sv = new ScrollView(this);
                sv.setId(View.generateViewId());
                sv.setLayoutParams(rlp);

                System.out.println("fplan size: " + fplan.size());
                int rl = fplan.get(a).getHeight();

                int cl = fplan.get(a).getWidth();
                System.out.println(rl + "  " + cl);
                TableLayout tableLayout = createTableLayout(rl, cl);
                HorizontalScrollView hsv = new HorizontalScrollView(this);
                hsv.addView(tableLayout);
                sv.addView(hsv);
                sv.setFillViewport(true);

                rel.addView(sv);

                Bundle extras = getIntent().getExtras();
                day = extras.getInt("day");
                month = extras.getInt("month");
                year = extras.getInt("year");
                time = extras.getInt("time");
                timeInMillis = extras.getLong("timeInMillis");
            }
            scroll.addView(rel);
            setContentView(scroll);
        }

    }

    @Override
    public void onBackPressed() {
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
        int id = item.getItemId();
        if (id == R.id.book) {
            if (selected.size() == 0)
                Toast.makeText(BookingActivity.this, "No tables were selected", Toast.LENGTH_SHORT).show();
            else {
                new AlertDialog.Builder(BookingActivity.this)
                        .setTitle("Confirm Booking")
                        .setMessage("Confirm")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.postBooking(selected, userID, day, month, year, time,totalPeople);
                                validateBooking();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();


            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void validateBooking() {
            pd = ProgressDialog.show(BookingActivity.this, "Processing", "Validating your booking..");
            h.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    //check whether the insert was done successfully
                    //if it wasnt tell the user of this error
                    Bookings b = new Bookings(selected.size(), userID,totalPeople,day,month,year,time,MainActivity.getRestaurantToPass().getId());
                    SignInActivity.getBookings().add(b);
                    Intent intent = new Intent(BookingActivity.this, User_Bookings_Activity.class);
                    intent.putExtra("time", time);
                    intent.putExtra("userID", userID);
                    intent.putExtra("day", day);
                    intent.putExtra("month", month);
                    intent.putExtra("year", year);
                    intent.putExtra("timeInMillis", timeInMillis);
                    intent.putExtra("numPeople", totalPeople);
                    intent.putExtra("numTables", selected.size());
                    intent.putExtra("fromBooking", true);
                    startActivity(intent);
                    finish();
                }
            }, 3500);

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
                    if (fplan.get(a).getId() == tableview.get(j).getFloorplanID()) {
                        if (tableview.get(j).getYcoord() == k && tableview.get(j).getXcoord() == i) {
                            //System.out.println("Found match at " + k + "," + i + " color is " + tableview.get(k).getColor());


                            int capacity;       //capacity is to be used in the future
                            /*following code sorts through the object type, from highest to lowest
                            //within each size bracket 30,20,10 it sorts further and sets the appropriate image resource
                            */
                            if (tableview.get(j).getObjType() > 30) {
                                capacity = tableview.get(j).getObjType() - 30;
                                if (capacity == 2) {
                                    im.setImageResource(R.drawable.tworndrot);
                                    im.setTag(2);
                                } else {
                                    im.setImageResource(R.drawable.foursqtrot);
                                    im.setTag(4);
                                }
                            } else if (tableview.get(j).getObjType() > 20) {
                                capacity = tableview.get(j).getObjType() - 20;
                                if (capacity == 2) {
                                    im.setImageResource(R.drawable.twornd);
                                    im.setTag(2);
                                } else {
                                    im.setImageResource(R.drawable.foursqt);
                                    im.setTag(4);
                                }
                            } else if (tableview.get(j).getObjType() > 10) {
                                capacity = tableview.get(j).getObjType() - 10;
                                if (capacity == 2) {
                                    im.setImageResource(R.drawable.twosqtrot);
                                    im.setTag(2);
                                } else if (capacity == 4) {
                                    im.setImageResource(R.drawable.fourrndrot);
                                    im.setTag(4);
                                } else {
                                    im.setImageResource(R.drawable.sixsqtrot);
                                    im.setTag(6);
                                }
                            } else {
                                capacity = tableview.get(j).getObjType();
                                if (capacity == 2) {
                                    im.setImageResource(R.drawable.twosqt);
                                    im.setTag(2);
                                } else if (capacity == 4) {
                                    im.setImageResource(R.drawable.fourrnd);
                                    im.setTag(4);
                                } else {
                                    im.setImageResource(R.drawable.sixsqt);
                                    im.setTag(6);

                                }

                            }
                            //set a color filter of green to all the images
                            final PorterDuffColorFilter green = new PorterDuffColorFilter(Color.argb(100, 0, 255, 0), PorterDuff.Mode.SRC_ATOP);
                            im.setColorFilter(green);
                            im.setContentDescription("" + j);
                            if (tableview.get(j).getColor() == 2) {     //sets color filter to red if any are booked (with color of 2)
                                im.setColorFilter(Color.argb(100,255,0,0));
                            }
                            else {
                                im.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        final PorterDuffColorFilter blue = new PorterDuffColorFilter(Color.argb(100, 0, 0, 255), PorterDuff.Mode.SRC_ATOP);
                                        //this method of changing color filters cuts out the image swaps we were doing
                                        //if we didn't do this we would have 30+ images to swap through, red green and blue
                                        if (im.getColorFilter().equals(green)) {
                                            im.setColorFilter(blue);
                                            totalPeople += Integer.parseInt(im.getTag().toString());
                                            selected.add(tableview.get(Integer.parseInt("" + im.getContentDescription())));

                                        } else {
                                            im.setColorFilter(green);
                                            totalPeople -= Integer.parseInt(im.getTag().toString());
                                            selected.remove(tableview.get(Integer.parseInt("" + im.getContentDescription())));
                                        }

                                    }
                                });
                            }
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
