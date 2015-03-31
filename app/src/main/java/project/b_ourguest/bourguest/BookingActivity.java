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
    ImageView im;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ScrollView sv = new ScrollView(this);


            int rl = fplan.get(0).getHeight() -1;
            int cl = fplan.get(0).getHeight() -1;
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
        System.out.println("going in");
        for (int k = 0; k < tableview.size(); k++) {
            System.out.println("in first loop");
            for(int i = 0; i < rowCount; i ++){
                System.out.println("in second loop");
                TableRow tableRow = new TableRow(this);
                tableRow.setBackgroundColor(Color.WHITE);
                for (int j = 0; j < columnCount; j++){
                    System.out.println("in third loop");
                    ImageView im = new ImageView(this);
                    //  textView.setText(String.valueOf(j));
                    im.setBackgroundColor(Color.WHITE);
                    if(tableview.get(k).getXcoord() == i && tableview.get(k).getYcoord() == j){
                        System.out.println("FOund mach at " + i + "," + j + " color is " + tableview.get(k).getColor());

                        if(tableview.get(k).getColor() == 1) {
                            im.setImageResource(R.drawable.greentable);
                            System.out.println("color is set to 1");
                        }
                        else{
                            im.setImageResource(R.drawable.redtable);
                        }

                    }
                    tableRow.addView(im, tableRowParams);
                }
                tableLayout.addView(tableRow, tableLayoutParams);
            }
        }

        return tableLayout;
    }

//    private TableLayout buildTable(int x, int y, int row, int col){
//
//        for (int i = 0; i < row; i++) {
//            // 3) create tableRow
//            TableRow tableRow = new TableRow(this);
//            tableRow.setBackgroundColor(Color.BLACK);
//
//            for (int j = 0; j < col; j++) {
//                // 4) create textView
//                im = new ImageView(this);
//                if(x == i && y == j){
////                    if (tableview.get(j).getColor() == 1) {
////                        System.out.println("color is green");
////                        im.setImageResource(R.drawable.greentable);
////                        im.setOnClickListener(new View.OnClickListener() {
////
////                            @Override
////                            public void onClick(View v) {
////                                System.out.println("click me harder");
////                                im.setImageResource(R.drawable.blutable);
////                            }
////                        });
//                    System.out.println("Found match");
//                    } else {
////                    System.out.println("color is red");
////                        im.setImageResource(R.drawable.redtable);
//                    }
//
//                } else {
//                    im.setImageResource(R.drawable.blutable);
//                }
//                // 5) add textView to tableRow
//
//            }
//
//            // 6) add tableRow to tableLayout
//            //tableLayout.addView(tableRow, tableLayoutParams);
//        }
//    }
private TableLayout createTableLayout(String [] rv, String [] cv,int rowCount, int columnCount) {
    // 1) Create a tableLayout and its params
    TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
    TableLayout tableLayout = new TableLayout(this);
    tableLayout.setBackgroundColor(Color.BLACK);

    // 2) create tableRow params
    TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
    tableRowParams.setMargins(1, 1, 1, 1);
    tableRowParams.weight = 1;

    for (int i = 0; i < rowCount; i++) {
        // 3) create tableRow
        TableRow tableRow = new TableRow(this);
        tableRow.setBackgroundColor(Color.BLACK);

        for (int j= 0; j < columnCount; j++) {
            // 4) create textView
            TextView textView = new TextView(this);
            //  textView.setText(String.valueOf(j));
            textView.setBackgroundColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);

            String s1 = Integer.toString(i);
            String s2 = Integer.toString(j);
            String s3 = s1 + s2;
            int id = Integer.parseInt(s3);
            Log.d("TAG", "-___>"+id);
            if (i ==0 && j==0){
                textView.setText("0==0");
            } else if(i==0){
                Log.d("TAAG", "set Column Headers");
                textView.setText(cv[j-1]);
            }else if( j==0){
                Log.d("TAAG", "Set Row Headers");
                textView.setText(rv[i-1]);
            }else {
                textView.setText(""+id);
                // check id=23
                if(id==23){
                    textView.setText("ID=23");

                }
            }

            // 5) add textView to tableRow
            tableRow.addView(textView, tableRowParams);
        }

        // 6) add tableRow to tableLayout
        tableLayout.addView(tableRow, tableLayoutParams);
    }

    return tableLayout;
}


}
