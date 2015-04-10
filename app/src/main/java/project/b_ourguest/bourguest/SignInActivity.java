package project.b_ourguest.bourguest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.b_ourguest.bourguest.DB.DatabaseOperations;
import project.b_ourguest.bourguest.Model.Bookings;
import project.b_ourguest.bourguest.Model.Restaurant;

/**
 * Created by Robbie on 30/01/2015.
 */
public class SignInActivity extends Activity {
    private int counter = 0;
    private DatabaseOperations db = new DatabaseOperations();
    private DatabaseOperations db2 = new DatabaseOperations(this);
    private ProgressDialog pd;
    private Handler h = new Handler();
    private static List<Bookings> bookings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //http://stackoverflow.com/questions/28265136/
        //scrollview-not-displaying-all-elements-of-a-relative-layout?noredirect=1#comment44886941_28265136
        if(!isNetworkAvailable())
            setContentView(R.layout.no_network_available);
        else
        {
            setContentView(R.layout.login_layout);

            //code for SharedPreferences was taken from
            //https://github.com/junal/Android-SharedPreferences/blob/master/SharedPreferences/src/junalontherun/com/Login.java
             SharedPreferences settings = getSharedPreferences("LoginPrefs", 0);
            String userID = settings.getString("email", "").toString();
            System.out.println("USER ID WHEN SETTING CONTENT VIEW OF SIGN IN: " + userID);
            bookings = db.getBookingsForIndividualUser(userID);
            if (settings.getString("loggedIn", "").toString().equals("loggedIn")) {
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
    private boolean isNetworkAvailable() {
        //http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void signIn(View view)
    {
        final EditText email = (EditText) findViewById(R.id.usersEmail);
        final EditText password = (EditText) findViewById(R.id.usersPassword);
        pd = ProgressDialog.show(SignInActivity.this, "Singing in....", "Verifying details");

                if(db2.validateSignIn(email.getText().toString(),password.getText().toString()))
                {
                    keepUserLoggedIn(email.getText().toString());
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }

        h.postDelayed(new Runnable() {
            public void run() {

                    if(db2.validateSignIn(email.getText().toString(),password.getText().toString()))
                    {
                        keepUserLoggedIn(email.getText().toString());
                        System.out.println("USER ID IN SIGN IN ACTIVITY2: " + email.getText().toString());
                        bookings = db.getBookingsForIndividualUser(email.getText().toString());
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    Toast.makeText(SignInActivity.this, "Email or password is incorrect",
                            Toast.LENGTH_LONG).show();
                pd.dismiss();
                }

                // To dismiss the dialog

        }, 2000);

    }

    public void signUp(View v)
    {
        final EditText email = (EditText) findViewById(R.id.signUpEmail);
        if (!isValidEmail(email.getText().toString())) {
            email.setError("Invalid Email");
        }

        final EditText pword = (EditText) findViewById(R.id.signUpPassword);
        if (!isValidPassword(pword.getText().toString())) {
            pword.setError("Invalid Password\nPassword must be greater than 6 characters");
        }


            if (isValidEmail(email.getText().toString()) && isValidPassword(pword.getText().toString())) {
                pd = ProgressDialog.show(SignInActivity.this, "Signing up", "Verifying details");
                db.signUserUp(email.getText().toString(), pword.getText().toString());

                h.postDelayed(new Runnable() {
                    public void run() {
                        if (DatabaseOperations.getSignUpCode() == 1) {
                            SharedPreferences settings = getSharedPreferences("LoginPrefs", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("accountVerified",false);
                            keepUserLoggedIn(email.getText().toString());
                            bookings = db.getBookingsForIndividualUser(email.getText().toString());
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (DatabaseOperations.getSignUpCode() == 400) {
                            Toast.makeText(SignInActivity.this, "Please enter the correct information", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(SignInActivity.this, "This email already exists", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        // To dismiss the dialog
                    }
                }, 3000);
            }


    }

    private boolean isValidPassword(String password) {
        if (password != null && password.length() > 6) {
            return true;
        }
        return false;
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static List<Bookings> getBookings() {
        return bookings;
    }

    public void keepUserLoggedIn(String email)
    {
        SharedPreferences settings = getSharedPreferences("LoginPrefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("email",email);
        editor.putString("loggedIn", "loggedIn");
        editor.commit();
    }

    @Override
    public void onBackPressed() {

        if(counter == 1)
        {
            setContentView(R.layout.login_layout);
            counter = 0;
        }
        else
            moveTaskToBack(true);
    }

    public void changeLayout(View v)
    {
        counter = 1;
        setContentView(R.layout.sign_up_layout);

    }
}
