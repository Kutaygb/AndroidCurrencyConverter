package com.kutay.currency;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.kutay.currency.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String baseCurrency = "EUR";
    String convertedToCurrency = "USD";
    float conversionRate = 0f;
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        spinnerSetup();
        textChangedStuff();
    }

    private void textChangedStuff() {
        binding.etFirstConversion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("Main", "Before Text Changed");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("Main", "OnTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    getApiResult();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Type a value", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getApiResult() {
        if (binding.etSecondConversion != null && !binding.etFirstConversion.getText().toString().isEmpty() && !binding.etFirstConversion.getText().toString().trim().equals("")) {
            String API = "https://api.exchangerate.host/convert?from=" + baseCurrency + "&to=" + convertedToCurrency;

            if (baseCurrency.equals(convertedToCurrency)) {
                Toast.makeText(
                        getApplicationContext(),
                        "Please pick a currency to convert",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(API);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.connect();

                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder stringBuilder = new StringBuilder();
                            String line = "";
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line);
                            }

                            String apiResult = stringBuilder.toString();
                            JSONObject jsonObject = new JSONObject(apiResult);
                            conversionRate = Float.parseFloat(jsonObject.getJSONObject("info").getString("rate"));

                            Log.d("Main", String.valueOf(conversionRate));
                            Log.d("Main", apiResult);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String text = String.valueOf(Float.parseFloat(binding.etFirstConversion.getText().toString()) * conversionRate);
                                    binding.etSecondConversion.setText(text);
                                }
                            });
                        } catch (Exception e) {
                            Log.e("Main", String.valueOf(e));
                        }
                    }
                }).start();
            }
        }
    }

// :)
    private void spinnerSetup() {
        Spinner spinner = binding.spinnerFirstConversion;
        Spinner spinner2 = binding.spinnerSecondConversion;

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.currencies2, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                baseCurrency = parent.getItemAtPosition(position).toString();
                getApiResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convertedToCurrency = parent.getItemAtPosition(position).toString();
                getApiResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }



}