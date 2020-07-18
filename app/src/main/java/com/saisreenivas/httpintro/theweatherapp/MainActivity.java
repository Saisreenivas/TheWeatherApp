package com.saisreenivas.httpintro.theweatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import Model.Place;
import Model.Weather;
import Util.Utils;
import data.CityPreference;
import data.JSONWeatherParser;
import data.WeatherHttpClient;

public class MainActivity extends AppCompatActivity {

    private TextView cityName, temp, description, humidity, pressure, wind, sunrise, sunset, updated;
    private ImageView iconView;
    Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView) findViewById(R.id.cityName);
        temp = (TextView) findViewById(R.id.temp_present);
        description = (TextView) findViewById(R.id.conditionValue);
        humidity = (TextView) findViewById(R.id.humidityValue);
        pressure = (TextView) findViewById(R.id.pressureValue);
        wind = (TextView) findViewById(R.id.windValue);
        sunrise = (TextView) findViewById(R.id.sunRiseValue);
        sunset = (TextView) findViewById(R.id.sunSetValue);
        updated = (TextView) findViewById(R.id.lastUpdatedValue);
        iconView = (ImageView) findViewById(R.id.imageId);

        renderWeatherData("Seattle,US");

        weather.iconData = weather.currentCondition.getIcon();
        new DownloadImageAsyncTask().execute(weather.iconData);
    }

    public void renderWeatherData(String city){
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{ city + "&units=metric"});
    }

    private class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImage(params[0]);
        }

        private Bitmap downloadImage(String code) {
            final DefaultHttpClient client = new DefaultHttpClient();

            final HttpGet getRequest = new HttpGet(Utils.ICON_URL + code + ".png");
            //final HttpGet getRequest = new HttpGet("http://www.9ori.com/store/media/images/8ab579a656.jpg");

            try {
                HttpResponse response = client.execute(getRequest);

                final int StatusCode = response.getStatusLine().getStatusCode();

                if(StatusCode != HttpStatus.SC_OK){
                    Log.e("DownloadImage", "Error: " + StatusCode);
                    return null;
                }
                final HttpEntity entity = response.getEntity();
                if(entity != null){
                    InputStream inputStream = null;
                    inputStream = entity.getContent();

                    //decode contents from the stream
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class WeatherTask extends AsyncTask<String, Void, Weather> {
        @Override
        protected Weather doInBackground(String... params) {

            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));

            weather = JSONWeatherParser.getWeather(data);

            Log.v("Data: ", weather.currentCondition.getDescription());




            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            DateFormat df = DateFormat.getTimeInstance();

            String sunRiseDate = df.format(new Date(weather.place.getSunrise()));
            String sunSetDate = df.format(new Date(weather.place.getSunset()));
            String lastUpdate = df.format(new Date(weather.place.getLastupdate()));

            DecimalFormat decimalFormat = new DecimalFormat("#.#");

            String temperture = decimalFormat.format(weather.currentCondition.getTemperature());

            cityName.setText(weather.place.getCity() + "," + weather.place.getCountry());
            temp.setText("" + temperture + "C");
            humidity.setText(weather.currentCondition.getHumidity() + "%");
            pressure.setText(weather.currentCondition.getPressure()+ "hPa");
            wind.setText(weather.wind.getSpeed() + "mps");
            sunrise.setText(sunRiseDate + "");
            sunset.setText(sunSetDate+"");
            updated.setText(lastUpdate + "");
            description.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescription() + ")");
        }
    }

    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change City");

        final EditText cityInput = new EditText(MainActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Portland,US");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPref = new CityPreference(MainActivity.this);
                cityPref.setCity(cityInput.getText().toString());

                String newCity = cityPref.getCity();

                renderWeatherData(newCity);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.change_city){
            showInputDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
