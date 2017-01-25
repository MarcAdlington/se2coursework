package uk.ac.uea.testsigh;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.util.List;
import uk.ac.uea.framework.CSVreader;


/**
 * Created by Marc Adlington 100088190 on 14/12/2016.
 */

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private TextView textView;
    private ImageButton backButton;
    private ItemArrayAdapter itemArrayAdapter;
    public float lat;
    public float longg;
    public String name;
    public boolean goneThrough = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        // Landing Page view

        setContentView(R.layout.welcome_page);
        //Create the text view
        /*
        *   CopyRight Marc Adlington statement
         */
        TextView copyRightTextView = (TextView) findViewById(R.id.copyRightTextView);
        // textView.setText(message);

        Copyright CPRight = new Copyright();
        copyRightTextView.setText(CPRight.getCopyright());
        System.out.println(CPRight.getCopyright());

        ImageView imageView = (ImageView) findViewById(R.id.imageView1);

        /*
        *    This Code detects that the user has gone through the application already -
        *    The code then prevents the landing page from displaying when the home button is pressed
         *   (saving time for the user)
         */
        Intent intent = getIntent();
        goneThrough = intent.getBooleanExtra("goneThrough", false);

        if (goneThrough) {
            loadHome(null);
            goneThrough = false;
        }

    }


    /*
    *   Restarts the view
     */
    public void restartView(View v) {
        setContentView(R.layout.activity_main);
    }


    /*
  *   Sets the title (if needed)
   */
    public void setTitle() {
        textView = (TextView) findViewById(R.id.setListTitle);
        textView.setText("Academic");
    }

    /*
  *   Loads the welcome page
   */
    public void loadWelcome(View view) {
        setContentView(R.layout.welcome_page);
    }


    /*
    *   Loads the home page
     */
    public void loadHome(View view) {
        setContentView(R.layout.activity_main);
        backButton = (ImageButton) findViewById(R.id.setListImage);
    }

    /*
    *   All methods beginning with 'filter' are used to navigate the
    *   user to the specific location they select. I.e. filterAcademic
    *   navigates the user to the academic departments in the list.
     */

    public void filterAcademic(View v) {
        setContentView(R.layout.listscreen);
        textView = (TextView) findViewById(R.id.setListTitle);
        textView.setText("Academic Departments");
        setList();
        itemArrayAdapter.setFilter("academic_dept");
        listView.setSelectionFromTop(0, 0);

    }

    public void filterHub(View v) {
        setContentView(R.layout.listscreen);
        textView = (TextView) findViewById(R.id.setListTitle);
        textView.setText("Hub Buildings");
        setList();
        itemArrayAdapter.setFilter("building");
        listView.setSelectionFromTop(90, 0);

    }

    public void filterFoodandDrink(View v) {
        setContentView(R.layout.listscreen);
        textView = (TextView) findViewById(R.id.setListTitle);
        textView.setText("Food and Drink");
        setList();
        itemArrayAdapter.setFilter("cafe");
        listView.setSelectionFromTop(130, 0);
    }

    public void filterAccom(View v) {
        setContentView(R.layout.listscreen);
        textView = (TextView) findViewById(R.id.setListTitle);
        textView.setText("Accommodation");
        setList();
        itemArrayAdapter.setFilter("accommodation");
        //amount and set to the size of the array


        System.out.println(itemArrayAdapter.getAmount());
        listView.setSelectionFromTop(54, 0);


    }
/*
*   Sets the list from the Excel spreadsheet (uses CSVreader and ItemArrayAdapter)
 */
    public void setList() {
        listView = (ListView) findViewById(R.id.list_view);
        itemArrayAdapter = new ItemArrayAdapter(getApplicationContext(), R.layout.simple_list_item);

        Parcelable state = listView.onSaveInstanceState();
        listView.setAdapter(itemArrayAdapter);
        listView.onRestoreInstanceState(state);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    lat = itemArrayAdapter.getLat(position);
                    longg = itemArrayAdapter.getlong(position);
                    name = itemArrayAdapter.getName(position);
                    System.out.println("Position: " + position + ". Lat : " + lat + ". Long : " + longg);

                    startMaps();
                } catch (ArrayIndexOutOfBoundsException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong, please select a different option", Toast.LENGTH_LONG).show();
                    loadHome(null);
                }


            }
        });

        InputStream inputStream = getResources().openRawResource(R.raw.mydata);

        CSVreader csv = new CSVreader(inputStream);
        List<String[]> scoreList = csv.read();


        for (String[] scoreData : scoreList) {
            System.out.println(scoreList.size());
            itemArrayAdapter.add(scoreData);
        }

    }

    /*
    *   Start maps is called to start a new maps activity.
     */

    public void startMaps() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("long", longg);
        intent.putExtra("name", name);
        startActivity(intent);
    }

}
