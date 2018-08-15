package com.example.jun.currency_convertor;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;


public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://frankfurter.app";
    public EditText edit;
    public TextView text;
    private Spinner base_currency_spinner;
    private Spinner converted_currency_spinner;
    private ArrayAdapter<String> adapter;
    private Intent intent;

    //On Click method for the Convert Button; Converts base amount and outputs converted amount into TextView
    public void doConversion(View view) {
        String base_amount_str = edit.getText().toString();
        //Base Case, User pressed Convert without any input
        if(TextUtils.isEmpty(base_amount_str)) {
            edit.setError("Amount must be entered first!");
        }
        else {
            //Get user inputs
            double base_amount = Double.parseDouble(base_amount_str);
            String base_currency = Get_Converted_Currency_Abbreviation(base_currency_spinner.getSelectedItem().toString());
            String converted_currency = Get_Converted_Currency_Abbreviation(converted_currency_spinner.getSelectedItem().toString());

            //Combines user selections to get the URL that contains the JSON used for user selected conversion
            String final_url  = Get_Conversion_URL(base_currency, converted_currency);

            //Connects to URL and fetches data in the background, also updates the TextView with converted amount
            FetchJSON process =  new FetchJSON(this, base_amount, final_url, converted_currency);
            process.execute();

            /*
            //JSON object based on user selected currencies
            try {
                String final_url  = Get_Conversion_URL(base_currency, converted_currency);
                URL url = new URL(final_url);
                URLConnection request = url.openConnection();
                JsonParser j_parser = new JsonParser(); //GSON library exclusive
                request.getContent();

                //Converts the content into an InputStream and then converts InputStream into a JSONElement
                //JsonElement root = j_parser.parse(new InputStreamReader((InputStream) request.getContent()));   //ERROR LINE!!!!!


                //JsonObject json_object = Get_JSON_Object(Get_Conversion_URL(base_currency, converted_currency));
               // double output = base_amount * Get_Conversion_Rate(json_object, converted_currency);
                //text.setText(Double.toString(output));
            }
            catch(Exception e) {
                Log.e("MYAPP", "exception", e);
                System.out.println(e.getMessage());
            }

            */

        }

    }

    //On Click method for Reset Button; Clears text fields and sets Sliders back to USD -> Euro
    public void Clear(View view) {
        edit.getText().clear();
        text.setText(null);
        base_currency_spinner.setSelection(adapter.getPosition("USD \tUnited States Dollar"));
        converted_currency_spinner.setSelection(adapter.getPosition("EUR \tEuro"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit = (EditText) findViewById(R.id.currency_input);
        text = (TextView) findViewById(R.id.converted_amount);
        base_currency_spinner = (Spinner) findViewById(R.id.currency_1);
        converted_currency_spinner = (Spinner) findViewById(R.id.currency_2);


        String currencies_filename = "Currencies.txt";


        //Read "Currencies.txt" and use data to fill in Spinner values
        try
        {
            ArrayList<String> currencies = this.readLines(currencies_filename);
            base_currency_spinner = (Spinner) findViewById(R.id.currency_1);
            converted_currency_spinner = (Spinner) findViewById(R.id.currency_2);

            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item,
                    currencies);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //Give spinner adapter values and set their default positions to USD and EUR
            base_currency_spinner.setAdapter(adapter);
            converted_currency_spinner.setAdapter(adapter);
            base_currency_spinner.setSelection(adapter.getPosition("USD \tUnited States Dollar"));
            converted_currency_spinner.setSelection(adapter.getPosition("EUR \tEuro"));
        }
        catch(IOException e)
        {
            // Print out the exception that occurred
            System.out.println("Unable to create "+ currencies_filename +": "+e.getMessage());
            Toast.makeText(MainActivity.this, "Unable to create "+ currencies_filename +": "+e.getMessage()
                    , Toast.LENGTH_SHORT).show();
        }

    }

    //Reads the lines of the file and returns an ArrayList of String with each line of the file
    public ArrayList<String> readLines(String filename) throws IOException
    {
        final InputStream currencies_file = getAssets().open(filename);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(currencies_file));
        ArrayList<String> lines = new ArrayList<String>();
        String line = null;

        while ((line = bufferedReader.readLine()) != null)
        {
            lines.add(line);
        }

        bufferedReader.close();

        return lines;
    }

    //Given the base currency and the currency we will convert to, returns the URL that will
    //return the JSON required for the conversion
    public static String Get_Conversion_URL(String base_currency, String converted_currency_abbreviation) {
        StringBuilder temp = new StringBuilder();
        temp.append(BASE_URL);
        temp.append("/current?from=");
        temp.append(base_currency);
        temp.append("&to=");
        temp.append(converted_currency_abbreviation);
        return temp.toString();
    }

    //Splits the String and returns the first index
    //ex: "USD United States Dollar" will give us "USD"
    public static String Get_Converted_Currency_Abbreviation(String converted_currency) {
        String [] temp = converted_currency.split(" ");
        return temp[0];
    }

    //Given the URL, return the corresponding JSON object
    public static JsonObject Get_JSON_Object(String string_url) throws IOException {
        try {
            URL url = new URL(string_url);
            URLConnection request = url.openConnection();
            JsonParser j_parser = new JsonParser(); //GSON library exclusive

            //Converts the content into an InputStream and then converts InputStream into a JSONElement
            JsonElement root = j_parser.parse(new InputStreamReader((InputStream) request.getContent()));   //ERROR LINE!!!!!
            return root.getAsJsonObject();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    //Given the URL, use the JSON to get the conversion rate for the currency
    public static double Get_Conversion_Rate(JsonObject json_object, String converted_currency) {
        try {
            double temp = json_object.get("rates").getAsJsonObject().get(converted_currency).getAsDouble();
            return temp;
        }
        catch(Exception e) {
            System.out.println("TYPE CAST ERROR?");
            throw e;
        }

    }




}
