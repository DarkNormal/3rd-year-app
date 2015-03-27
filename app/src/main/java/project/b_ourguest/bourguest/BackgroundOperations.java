package project.b_ourguest.bourguest;

import android.os.AsyncTask;

import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbie on 31/01/2015.
 */
public class BackgroundOperations extends AsyncTask<List<Restaurant>, Void, List<Restaurant>> {
    private static MobileServiceTable<Restaurant> restaurantsTable = StartActivity.getRestaurantsTable();
    private List<Restaurant> restaurants = new ArrayList<Restaurant>();
    
    @Override
    protected List<Restaurant> doInBackground(List<Restaurant>... params) {
        //retrieve data from azure database
        //http://azure.microsoft.com/en-us/documentation/articles/mobile-services-android-how-to-use-client-library/
        restaurantsTable.execute(new TableQueryCallback<Restaurant>() {
            public void onCompleted(List<Restaurant> result, int count,
                                    Exception exception, ServiceFilterResponse response) {
                System.out.println("In Thread------------------------------");
                if (exception == null) {
                    restaurants.clear();
                    for (Restaurant item : result) {
                        restaurants.add(item);
                        System.out.println("Restaurant name: " + item.getName() + "\nRestaurant ID: " + item.getId());
                    }
                }
                else
                {
                    System.out.println("ERROR BackgroundOperations---------------");
                }
            }
        });
        return restaurants;
    }
}
