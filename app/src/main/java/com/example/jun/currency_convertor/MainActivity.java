package com.example.jun.currency_convertor;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String currencies_filename = "Currencies.txt";

        //Read "Currencies.txt" and use data to fill in Spinner values
        try
        {
            ArrayList<String> currencies = this.readLines(currencies_filename);
            Spinner c1 = (Spinner) findViewById(R.id.currency_1);
            Spinner c2 = (Spinner) findViewById(R.id.currency_2);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item,
                    currencies);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            c1.setAdapter(adapter);
            c2.setAdapter(adapter);
        }
        catch(IOException e)
        {
            // Print out the exception that occurred
            System.out.println("Unable to create "+ currencies_filename +": "+e.getMessage());
        }

    }

    public ArrayList<String> readLines(String filename) throws IOException
    {
        FileReader fileReader = new FileReader(filename);

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        ArrayList<String> lines = new ArrayList<String>();
        String line = null;

        while ((line = bufferedReader.readLine()) != null)
        {
            lines.add(line);
        }

        bufferedReader.close();

        return lines;
    }
}
