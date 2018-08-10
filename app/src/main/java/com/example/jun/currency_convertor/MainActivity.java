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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;


public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://frankfurter.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String currencies_filename = "Currencies.txt";


        //Read "Currencies.txt" and use data to fill in Spinner values
        try
        {
            ArrayList<String> currencies = this.readLines(currencies_filename);
            Spinner c1_spinner = (Spinner) findViewById(R.id.currency_1);
            Spinner c2_spinner = (Spinner) findViewById(R.id.currency_2);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item,
                    currencies);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //Give spinner adapter values and set their default positions to USD and EUR
            c1_spinner.setAdapter(adapter);
            c2_spinner.setAdapter(adapter);
            c1_spinner.setSelection(adapter.getPosition("USD \tUnited States Dollar"));
            c2_spinner.setSelection(adapter.getPosition("EUR \tEuro"));
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
    private static String Get_Conversion_URL(String base_currency, String converted_currency_abbreviation) {
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
    private static String Get_Converted_Currency_Abbreviation(String converted_currency) {
        String [] temp = converted_currency.split(" ");
        return temp[0];
    }

    //Given the URL, return the corresponding JSON object
    private static JsonObject Get_JSON_Object(String string_url) throws IOException {
        try {
            URL url = new URL(string_url);
            URLConnection request = url.openConnection();
            JsonParser j_parser = new JsonParser(); //GSON library exclusive

            //Converts the content into an InputStream and then converts InputStream into a JSONElement
            JsonElement root = j_parser.parse(new InputStreamReader((InputStream) request.getContent()));
            return root.getAsJsonObject();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }


    }

    //Given the URL, use the JSON to get the conversion rate for the currency
    private static double Get_Conversion_Rate(JsonObject json_object, String converted_currency) {
        return 0;
    }


}
