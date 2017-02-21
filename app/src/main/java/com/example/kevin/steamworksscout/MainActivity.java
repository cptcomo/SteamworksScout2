package com.example.kevin.steamworksscout;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.BatchUpdateException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    public static final MediaType FORM_DATA_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    private final String BLUE_CHEESE_URL = "https://docs.google.com/forms/d/1pitt9JZfNmenfGQ4TznAdKFJYjAT8HZPqu-nEs826cU/formResponse";
    private final String G3_URL = "https://docs.google.com/forms/d/e/1FAIpQLSfx_g9zbqds3KcdYsoa0gLq7behhZsWfXk1e3u-_-h7EBuy3A/formResponse";
    private final String[] SPREADSHEET_URLS = {BLUE_CHEESE_URL, G3_URL};
    private int currentSpreadsheet = 1;

    public static final String[] INITALS_KEY = {"entry_1866261740", "entry_1789585754"};
    public static final String[] TEAM_NUMBER_KEY = {"entry_454837117", "entry_148913451"};
    public static final String[] MATCH_NUMBER_KEY = {"entry_518959206", "entry_1844915886"};
    public static final String[] GEAR_IN_AUTO_KEY = {"entry_1624132469", "entry_1024106334"};
    public static final String[] LOW_SCORE_IN_AUTO_KEY = {"entry_703108754", "entry_1205206372"};
    public static final String[] HIGH_FUEL_AUTO_KEY = {"entry_1358789974", "entry_111459469"};
    public static final String[] GEARS_DELIVERED_KEY = {"entry_1947458747", "entry_749764785"};
    public static final String[] LOW_GOAL_CYCLES_KEY = {"entry_1105753301", "entry_1401458059"};
    public static final String[] HIGH_GOAL_CYCLES_KEY = {"entry_526792915", "entry_82527184"};
    public static final String[] HIGH_GOAL_MISSES_KEY = {"entry_1725977695", "entry_395459313"};
    public static final String[] CARGO_SIZE_KEY = {"entry_1962237108", "entry_1276450365"};
    public static final String[] DEFENDS_KEY = {"entry_1165597892", "entry_703827150"};
    public static final String[] HANGS_KEY = {"entry_385527112", "entry_658280372"};
    public static final String[] COMMENTS_KEY = {"entry_483134571", "entry_694395040"};

    private RelativeLayout teamSelection, mainApp;
    private Button submitTeamNumberButton;
    private EditText teamField;
    private Context context;
    private ScrollView scrollView;
    private EditText initialsField;
    private EditText matchNumberField;
    private EditText teamNumberField;
    private RadioGroup gearAutoGroup;
    private RadioGroup lowAutoGroup;
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

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    private String[] outputs;

    private final String VERSION_NAME = BuildConfig.VERSION_NAME;

    private long longPressTimeout = 1000;

    private boolean canSend = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        teamSelection = (RelativeLayout)findViewById(R.id.teamSelection);
        mainApp = (RelativeLayout)findViewById(R.id.mainApp);
        teamField = (EditText)findViewById(R.id.teamField);
        submitTeamNumberButton = (Button)findViewById(R.id.submitTeamNumberButton);

        settings = getSharedPreferences("AppData", 0);
        int teamNumber = settings.getInt("teamNumber", 0);
        if(teamNumber != 0){
            teamSelection.setVisibility(View.GONE);
            mainApp.setVisibility(View.VISIBLE);
            teamConfig(teamNumber);
        }
        else {
            teamSelection.setVisibility(View.VISIBLE);
            mainApp.setVisibility(View.GONE);
        }

        context = this;
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        initialsField = (EditText) findViewById(R.id.nameField);
        matchNumberField = (EditText) findViewById(R.id.matchNumber);
        teamNumberField = (EditText) findViewById(R.id.teamNumber);
        gearAutoGroup = (RadioGroup) findViewById(R.id.gearAutoGroup);
        lowAutoGroup = (RadioGroup) findViewById(R.id.lowAutoGroup);
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

        String[] fuelInHighItems = new String[]{"No Attempt", "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50"};
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
                if(highFuelAutoSpinner.getSelectedItemPosition() > 0) {
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
        submitTeamNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v){
                int teamNumber;
                try {
                    teamNumber = Integer.parseInt(teamField.getText().toString());
                    System.out.println(teamNumber);
                }catch(Exception e){
                    e.printStackTrace();
                    displayText("Not a valid number", 3);
                    return;
                }
                if(teamNumber == 1086 || teamNumber == 1648){
                    editor = settings.edit();
                    editor.putInt("teamNumber", teamNumber);
                    editor.commit();
                    teamConfig(teamNumber);
                    teamSelection.setVisibility(View.GONE);
                    mainApp.setVisibility(View.VISIBLE);
                }
                else {
                    displayText("The team number you entered is not supported by this app.", 3);
                }
            }
        });
    }

    private void teamConfig(int teamNumber){
        System.out.println("Calling team config with number: " + teamNumber);
        if(teamNumber == 1086){
            currentSpreadsheet = 0;
        }
        else if(teamNumber == 1648){
            currentSpreadsheet = 1;
        }
        else {
            currentSpreadsheet = 0;
        }
    }

    //This method is here so that you can press on the background and the keyboard goes away.
    @Override public boolean dispatchTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
    private void send(boolean forceSend) {
        if(!canSend) return;

        //Make sure that all fields are filled with values
        if(!forceSend){
            if(TextUtils.isEmpty(initialsField.getText().toString())){
                displayText("Please enter your initials", 2);
                return;
            }
            if(TextUtils.isEmpty(teamNumberField.getText().toString())){
                displayText("Please enter a team number", 2);
                return;
            }
            if(TextUtils.isEmpty(matchNumberField.getText().toString())){
                displayText("Please enter in the qualification match number", 2);
                return;
            }
            if(TextUtils.isEmpty(cargoSizeField.getText().toString())){
                displayText("Please estimate robot cargo size for fuel", 2);
                return;
            }
        }

        canSend = false;

        PostDataTask postDataTask = new PostDataTask(this);

        outputs = new String[14];
        outputs[0] = teamNumberField.getText().toString();
        outputs[1] = matchNumberField.getText().toString();
        outputs[2] = Integer.toString(gearAutoGroup.indexOfChild(findViewById(gearAutoGroup.getCheckedRadioButtonId())) - 2);
        outputs[3] = Integer.toString(lowAutoGroup.indexOfChild(findViewById(lowAutoGroup.getCheckedRadioButtonId())) - 2);
        outputs[4] = highFuelAutoSpinner.getSelectedItemPosition() == 0 ? "-1" : highFuelAutoSpinner.getSelectedItem().toString();
        outputs[5] = gearsScoredSpinner.getSelectedItem().toString();
        outputs[6] = lowFuelCyclesSpinner.getSelectedItem().toString();
        outputs[7] = highFuelCyclesSpinner.getSelectedItem().toString();
        outputs[8] = highFuelMissedSpinner.getSelectedItem().toString();
        outputs[9] = cargoSizeField.getText().toString();
        outputs[10] = Integer.toString(defendsSpinner.getSelectedItemPosition());
        outputs[11] = hangsBox.isChecked() ? "1" : "0";
        outputs[12] = commentsField.getText().toString();
        outputs[13] = initialsField.getText().toString();

        postDataTask.execute(SPREADSHEET_URLS[currentSpreadsheet], outputs[0], outputs[1], outputs[2], outputs[3], outputs[4], outputs[5], outputs[6], outputs[7],
                outputs[8], outputs[9], outputs[10], outputs[11], outputs[12], outputs[13]);
    }

    private boolean writeToFile(){
        if(isExternalStorageWritable()){
            File fileDirectory = new File(Environment.getExternalStorageDirectory() + "/Documents");
            boolean isPresent = true;
            if(!fileDirectory.exists()){
                isPresent = fileDirectory.mkdir();
            }
            File file;
            if(isPresent){
                file = new File(fileDirectory.getAbsolutePath(), "Steamworks Scouting Data.txt");
            }
            else {
                displayText("ERROR: unable to create file", 3);
                return false;
            }
            FileOutputStream out;
            String output = "";
            output += outputs[12] + "|" + outputs[0] + "|"  + outputs[1] + "|" + outputs[2] + "|" + outputs[3] + "|"
                    + outputs[4] + "|" + outputs[5] + "|" + outputs[6] + "|" + outputs[7] + "|" + outputs[8] + "|"
                    + outputs[9] + "|" + outputs[10] + "|" + outputs[11] + "|" + outputs[13] + "\n";
            try {
                out = new FileOutputStream(file, true);
                out.write(output.getBytes());
                out.close();
                displayText("Due to no Internet access, data has been stored in a local file. Contact " +
                            "a scouting admin for further assistance", 5);
            } catch(IOException e){
                displayText("ERROR: Error writing data to file", Toast.LENGTH_LONG);
                e.printStackTrace();
                return false;
            }
        }
        else {
            displayText("ERROR: External storage not readable", 3);
            return false;
        }
        return true;
    }

    private boolean isExternalStorageWritable(){
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private void resetFields(){
        initialsField.setText("");
        matchNumberField.setText("");
        teamNumberField.setText("");
        gearAutoGroup.check(R.id.gearNotAttemptedAutoButton);
        lowAutoGroup.check(R.id.lowNotAttemptedButton);
        highFuelAutoSpinner.setSelection(0);
        gearsScoredSpinner.setSelection(0);
        lowFuelCyclesSpinner.setSelection(0);
        highFuelCyclesSpinner.setSelection(0);
        highFuelMissedSpinner.setSelection(0);
        cargoSizeField.setText("");
        defendsSpinner.setSelection(0);
        hangsBox.setChecked(false);
        commentsField.setText("");
        scrollView.scrollTo(0,0);
    }

    private void displayText(String text, int duration){
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.TOP, 0, 10);
       // toast.getView().setBackgroundColor(Color.rgb(255, 30, 30));
        //TextView v = (TextView)toast.getView().findViewById(android.R.id.message);
        //v.setTextColor(Color.YELLOW);
        toast.show();
    }

    private class PostDataTask extends AsyncTask<String, Void, Boolean> {
        public AsyncResponse delegate;
        public PostDataTask(AsyncResponse a){
            this.delegate = a;
        }

        @Override protected Boolean doInBackground(String... contactData){
            Boolean result = true;
            String url = contactData[0];
            String postBody = "";
            try {
                postBody = TEAM_NUMBER_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[1], "UTF-8") +
                        "&" + MATCH_NUMBER_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[2], "UTF-8") +
                        "&" + GEAR_IN_AUTO_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[3], "UTF-8") +
                        "&" + LOW_SCORE_IN_AUTO_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[4], "UTF-8") +
                        "&" + HIGH_FUEL_AUTO_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[5], "UTF-8") +
                        "&" + GEARS_DELIVERED_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[6], "UTF-8") +
                        "&" + LOW_GOAL_CYCLES_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[7], "UTF-8") +
                        "&" + HIGH_GOAL_CYCLES_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[8], "UTF-8") +
                        "&" + HIGH_GOAL_MISSES_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[9], "UTF-8") +
                        "&" + CARGO_SIZE_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[10], "UTF-8") +
                        "&" + DEFENDS_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[11], "UTF-8") +
                        "&" + HANGS_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[12], "UTF-8") +
                        "&" + COMMENTS_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[13], "UTF-8") +
                        "&" + INITALS_KEY[currentSpreadsheet] + "=" + URLEncoder.encode(contactData[14], "UTF-8");
            } catch (UnsupportedEncodingException e){
                result = false;
            }
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(FORM_DATA_TYPE, postBody);
                Request request = new Request.Builder().url(url).post(body).build();
                Response response = client.newCall(request).execute();
            } catch(IOException e){
                result = false;
            }
            return result;
        }
        @Override protected void onPostExecute(Boolean result){
            delegate.processFinish(result);
        }
    }


    @Override
    public void processFinish(boolean result) {
        if(result){
            displayText("Data successfully sent!", 2);
            resetFields();
        }
        else {
            if(writeToFile()){
                resetFields();
            }
            else {
                displayText("Both sending data and writing to a file failed. This is a problem.", 5);
            }
        }
        canSend = true;
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_MENU){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
