package project.b_ourguest.bourguest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
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
    private ArrayList<Floorplan> fplan = DatabaseOperations.getFloorplans();

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ScrollView sv = new ScrollView(this);
        int rl= fplan.get(0).getHeight() ;
        int cl= fplan.get(0).getWidth() ;

    System.out.println(rl + "  " + cl);
            TableLayout tableLayout = createTableLayout( rl, cl);
            HorizontalScrollView hsv = new HorizontalScrollView(this);
            hsv.addView(tableLayout);
            sv.addView(hsv);
            setContentView(sv);
        }

        @Override
        public void onBackPressed() {
            finish(); //this will finish the activity when the back button is pressed
            super.onBackPressed();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.empty_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            //int id = item.getItemId();

            //noinspection SimplifiableIfStatement


            return super.onOptionsItemSelected(item);
        }

    public void makeCellEmpty(TableLayout tableLayout, int rowIndex, int columnIndex) {
        // get row from table with rowIndex
        TableRow tableRow = (TableRow) tableLayout.getChildAt(rowIndex);

        // get cell from row with columnIndex
        TextView textView = (TextView)tableRow.getChildAt(columnIndex);

        // make it black
        textView.setBackgroundColor(Color.BLACK);
    }
    public void setHeaderTitle(TableLayout tableLayout, int rowIndex, int columnIndex){

        // get row from table with rowIndex
        TableRow tableRow = (TableRow) tableLayout.getChildAt(rowIndex);

        // get cell from row with columnIndex
        TextView textView = (TextView)tableRow.getChildAt(columnIndex);

        textView.setText("Hello");
    }

    private TableLayout createTableLayout(int rowCount, int columnCount) {
        // 1) Create a tableLayout and its params
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setBackgroundColor(Color.BLACK);

        // 2) create tableRow params
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.setMargins(1, 1, 1, 1);
        tableRowParams.weight = 1;
        for (int k = 0; k < rowCount; k++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundColor(Color.WHITE);
            for(int i = 0; i < columnCount; i ++){
                final ImageView im = new ImageView(this);
                im.setImageResource(R.drawable.blank);
                for (int j = 0; j < tableview.size(); j++){
                    //  textView.setText(String.valueOf(j));
                    im.setBackgroundColor(Color.WHITE);
                    if(tableview.get(j).getYcoord() == k && tableview.get(j).getXcoord() == i){
                        System.out.println("Found match at " + k + "," + i + " color is " + tableview.get(k).getColor());

                        if(tableview.get(j).getColor() == 1) {
                            im.setImageResource(R.drawable.greentable);
                            im.setTag(R.drawable.greentable);

                            im.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    if(im.getTag().toString().equals("2130837587"))
                                    {
                                        im.setImageResource(R.drawable.blutable);
                                        im.setTag(R.drawable.blutable);
                                    }
                                    else {
                                        im.setImageResource(R.drawable.greentable);
                                        im.setTag(R.drawable.greentable);
                                    }

                                }
                            });
                            System.out.println("color is set to 1");
                        }
                        else{
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
