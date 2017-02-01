package com.example.kevin.steamworksscout;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.sql.BatchUpdateException;

import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    public static final MediaType FORM_DATA_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    private final String BLUE_CHEESE_URL = "https://docs.google.com/forms/d/1pitt9JZfNmenfGQ4TznAdKFJYjAT8HZPqu-nEs826cU/formResponse";
    private final String[] SPREADSHEET_URLS = {BLUE_CHEESE_URL};
    private int currentSpreadsheet = 0;

    public static final String[] INITALS_KEY = {"entry_1866261740"};
    public static final String[] TEAM_NUMBER_KEY = {"entry_454837117"};
    public static final String[] MATCH_NUMBER_KEY = {"entry_518959206"};
    public static final String[] GEAR_IN_AUTO_KEY = {"entry_1624132469"};
    public static final String[] LOW_SCORE_IN_AUTO_KEY = {"entry_703108754"};
    public static final String[] HIGH_FUEL_AUTO_KEY = {"entry_1358789974"};
    public static final String[] GEARS_DELIVERED_KEY = {"entry_1947458747"};
    public static final String[] LOW_GOAL_CYCLES_KEY = {"entry_1105753301"};
    public static final String[] HIGH_GOAL_CYCLES_KEY = {"entry_526792915"};
    public static final String[] HIGH_GOAL_MISSES_KEY = {"entry_1725977695"};
    public static final String[] CARGO_SIZE_KEY = {"entry_1962237108"};
    public static final String[] DEFENDS_KEY = {"entry_518959206"};
    public static final String[] HANGS_KEY = {"entry_385527112"};
    public static final String[] COMMENTS_KEY = {"entry_483134571"};

    private Context context;
    private ScrollView scrollView;
    private EditText initialsField;
    private EditText matchNumberField;
    private EditText teamNumberField;
    private CheckBox gearInAutoBox;
    private CheckBox lowFuelAutoBox;
    private Button decHighFuelAutoButton;
    private Spinner highFuelAutoSpinner;
    private Button incHighFuelAutoButton;
    private Button decGearsScoredButton;
    private Spinner gearsScoredSpinner;
    private Button incGearsScoredButton;
    private Button decLowFuelCyclesButton;
    private Spinner lowFuelCyclesSpinner;
    private Button incLowFuelCyclesButton;
    private Button decHighFuelCyclesButton;
    private Spinner highFuelCyclesSpinner;
    private Button incHighFuelCyclesButton;
    private Button decHighFuelMissedButton;
    private Spinner highFuelMissedSpinner;
    private Button incHighFuelMissedButton;
    private EditText cargoSizeField;
    private Spinner defendsSpinner;
    private CheckBox hangsBox;
    private EditText commentsField;
    private Button resetButton;
    private Button sendButton;
    private TextView versionText;

    private final String VERSION_NAME = BuildConfig.VERSION_NAME;

    private long longPressTimeout = 1000;

    private boolean canSend = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        initialsField = (EditText) findViewById(R.id.nameField);
        matchNumberField = (EditText) findViewById(R.id.matchNumber);
        teamNumberField = (EditText) findViewById(R.id.teamNumber);
        gearInAutoBox = (CheckBox) findViewById(R.id.gearAutoBox);
        lowFuelAutoBox = (CheckBox) findViewById(R.id.lowInAutoBox);
        decHighFuelAutoButton = (Button) findViewById(R.id.decHighFuelAutoButton);
        highFuelAutoSpinner = (Spinner) findViewById(R.id.highFuelAutoSpinner);
        incHighFuelAutoButton = (Button) findViewById(R.id.incHighFuelAutoButton);
        decGearsScoredButton = (Button) findViewById(R.id.decGearsScoredButton);
        gearsScoredSpinner = (Spinner) findViewById(R.id.gearsScoredSpinner);
        incGearsScoredButton = (Button) findViewById(R.id.incGearsScoredButton);
        decLowFuelCyclesButton = (Button) findViewById(R.id.decLowFuelCyclesButton);
        lowFuelCyclesSpinner = (Spinner) findViewById(R.id.lowFuelCyclesSpinner);
        incLowFuelCyclesButton = (Button) findViewById(R.id.incLowFuelCyclesButton);
        decHighFuelCyclesButton = (Button) findViewById(R.id.decHighFuelCyclesButton);
        highFuelCyclesSpinner = (Spinner) findViewById(R.id.highFuelCyclesSpinner);
        incHighFuelCyclesButton = (Button) findViewById(R.id.incHighFuelCyclesButton);
        decHighFuelMissedButton = (Button) findViewById(R.id.decHighFuelMissedButton);
        highFuelMissedSpinner = (Spinner) findViewById(R.id.highFuelMissedSpinner);
        incHighFuelMissedButton = (Button) findViewById(R.id.incHighFuelMissedButton);
        cargoSizeField = (EditText) findViewById(R.id.cargoSize);
        defendsSpinner = (Spinner) findViewById(R.id.defendsSpinner);
        hangsBox = (CheckBox) findViewById(R.id.hangBox);
        commentsField = (EditText) findViewById(R.id.commentsField);
        resetButton = (Button) findViewById(R.id.resetButton);
        sendButton = (Button) findViewById(R.id.sendButton);
        versionText = (TextView) findViewById(R.id.versionText);

        versionText.setText("Version: " + VERSION_NAME);

        String[] fuelInHighItems = new String[]{"0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50"};
        ArrayAdapter<String> fuelInHighAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, fuelInHighItems);
        highFuelAutoSpinner.setAdapter(fuelInHighAdapter);

        String[] gearsScoredItems = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
        final ArrayAdapter<String> gearsScoredAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, gearsScoredItems);
        gearsScoredSpinner.setAdapter(gearsScoredAdapter);

        String[] lowFuelCyclesItems = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
        ArrayAdapter<String> lowFuelCyclesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lowFuelCyclesItems);
        lowFuelCyclesSpinner.setAdapter(lowFuelCyclesAdapter);

        final String[] highFuelCyclesItems = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
        ArrayAdapter<String> highFuelCyclesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, highFuelCyclesItems);
        highFuelCyclesSpinner.setAdapter(highFuelCyclesAdapter);

        String[] fuelMissedItems = new String[]{"0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50"};
        ArrayAdapter<String> fuelMissedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, fuelMissedItems);
        highFuelMissedSpinner.setAdapter(fuelMissedAdapter);

        String[] defendsItems = new String[]{"None", "Neutral", "Launchpad", "Key"};
        ArrayAdapter<String> defendsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, defendsItems);
        defendsSpinner.setAdapter(defendsAdapter);

        //SCROLL VIEW HACK: fixes annoying bug where the screen scrolls to an EditText view after scrolling/pressing a button
        //DO NOT CHANGE OR REMOVE
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollView.setFocusable(true);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });

        sendButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    long eventDuration = event.getEventTime() - event.getDownTime();
                    if (eventDuration > longPressTimeout) {
                        send(true);
                        return false;
                    } else {
                        send(false);
                        return false;
                    }
                }
                return false;
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayText("Resetting all fields", 2);
                resetFields();
            }
        });

        decHighFuelAutoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (highFuelAutoSpinner.getSelectedItemPosition() > 0) {
                    highFuelAutoSpinner.setSelection(highFuelAutoSpinner.getSelectedItemPosition() - 1);
                }
            }
        });
        incHighFuelAutoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(highFuelAutoSpinner.getSelectedItemPosition() < highFuelAutoSpinner.getCount() - 1){
                    highFuelAutoSpinner.setSelection(highFuelAutoSpinner.getSelectedItemPosition() + 1);
                }
            }
        });

        decGearsScoredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gearsScoredSpinner.getSelectedItemPosition() > 0){
                    gearsScoredSpinner.setSelection(gearsScoredSpinner.getSelectedItemPosition() - 1);
                }
            }
        });
        incGearsScoredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gearsScoredSpinner.getSelectedItemPosition() < gearsScoredSpinner.getCount() - 1){
                    gearsScoredSpinner.setSelection(gearsScoredSpinner.getSelectedItemPosition() + 1);
                }
            }
        });

        decLowFuelCyclesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lowFuelCyclesSpinner.getSelectedItemPosition() > 0){
                    lowFuelCyclesSpinner.setSelection(lowFuelCyclesSpinner.getSelectedItemPosition() - 1);
                }
            }
        });
        incLowFuelCyclesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lowFuelCyclesSpinner.getSelectedItemPosition() < lowFuelCyclesSpinner.getCount() - 1){
                    lowFuelCyclesSpinner.setSelection(lowFuelCyclesSpinner.getSelectedItemPosition() + 1);
                }
            }
        });

        decHighFuelCyclesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(highFuelCyclesSpinner.getSelectedItemPosition() > 0) {
                    highFuelCyclesSpinner.setSelection(highFuelCyclesSpinner.getSelectedItemPosition() - 1);
                }
            }
        });
        incHighFuelCyclesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(highFuelCyclesSpinner.getSelectedItemPosition() < highFuelCyclesSpinner.getCount() - 1){
                    highFuelCyclesSpinner.setSelection(highFuelCyclesSpinner.getSelectedItemPosition() + 1);
                }
            }
        });

        decHighFuelMissedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(highFuelMissedSpinner.getSelectedItemPosition() > 0) {
                    highFuelMissedSpinner.setSelection(highFuelMissedSpinner.getSelectedItemPosition() - 1);
                }
            }
        });
        incHighFuelMissedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(highFuelMissedSpinner.getSelectedItemPosition() < highFuelMissedSpinner.getCount() - 1){
                    highFuelMissedSpinner.setSelection(highFuelMissedSpinner.getSelectedItemPosition() + 1);
                }
            }
        });
    }

    private void send(boolean forceSend) {
    }

    private void resetFields(){
        initialsField.setText("");
        matchNumberField.setText("");
        teamNumberField.setText("");
        gearInAutoBox.setChecked(false);
        lowFuelAutoBox.setChecked(false);
        highFuelAutoSpinner.setSelection(0);
        gearsScoredSpinner.setSelection(0);
        lowFuelCyclesSpinner.setSelection(0);
        highFuelCyclesSpinner.setSelection(0);
        highFuelMissedSpinner.setSelection(0);
        cargoSizeField.setText("");
        defendsSpinner.setSelection(0);
        hangsBox.setChecked(false);
        commentsField.setText("");
    }

    private void displayText(String text, int duration){
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.TOP, 0, 10);
        toast.getView().setBackgroundColor(Color.rgb(255, 30, 30));
        TextView v = (TextView)toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.YELLOW);
        toast.show();
    }


    @Override
    public void processFinish(boolean result) {

    }
}
