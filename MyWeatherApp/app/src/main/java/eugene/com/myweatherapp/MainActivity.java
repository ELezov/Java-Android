package eugene.com.myweatherapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


import Util.Utils;
import data.CityPreference;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;

public class MainActivity extends AppCompatActivity {

    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humididty;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;

    Weather weather = new Weather();


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("It's fast");
        progressDialog.show();

        cityName = (TextView) findViewById(R.id.cityText);
        iconView = (ImageView) findViewById(R.id.icon_img);
        temp = (TextView) findViewById(R.id.tempText);
        description = (TextView) findViewById(R.id.cloudText);
        humididty = (TextView) findViewById(R.id.humidText);
        pressure = (TextView) findViewById(R.id.pressureText);
        wind = (TextView) findViewById(R.id.windText);
        sunrise = (TextView) findViewById(R.id.riseText);
        sunset = (TextView) findViewById(R.id.setText);
        updated = (TextView) findViewById(R.id.updateText);

        CityPreference cityPreference=new CityPreference(MainActivity.this);
        Log.v("Log",cityPreference.getCity());
        renderWeatherData(cityPreference.getCity());

        //renderWeatherData("Kaliningrad,RU");


    }


    public void renderWeatherData(String city) {
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(city + "&units=metric");
    }


    private class WeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... strings) {

            String data = ((new WeatherHttpClient()).getWeatherData(strings[0]));

            Log.e("Log", data);

            weather = JSONWeatherParser.getWeather(data);
            Log.v("Data: ", weather.currentCondition.getDescription());
            //Log.e("Log",weather.place.getCity());
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

           // DateFormat df =DateFormat.getTimeInstance();
          // String sunriseDate= df.format(weather.place.getSunrise());
            //String sunsetData=df.format(weather.place.getSunset());
            //String updateDate=df.format(new Date(weather.place.getLastupdate()));

            DecimalFormat decimalFormat=new DecimalFormat("#.#");
            String tempFormat=decimalFormat.format(weather.currentCondition.getTemperature());

            cityName.setText(weather.place.getCity() + "," + weather.place.getCounty());
            temp.setText("" + tempFormat+ "C");
            humididty.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
            pressure.setText("Pressure: " + weather.currentCondition.getPressure() + "hPa");
            wind.setText("Wind: " + weather.wind.getSpeed() + "mps");
            sunrise.setText("Sunrise: " + formatTime(weather.place.getSunrise()));
            sunset.setText("Sunset: " + formatTime(weather.place.getSunset()));
            updated.setText("Last Updated: " + formatTime(weather.place.getLastupdate()));
            description.setText("Condition: " + weather.currentCondition.getCondition() +
                    " (" + weather.currentCondition.getDescription() + ")");
            Log.v("ICON",Utils.ICON_URL+weather.currentCondition.getIcon()+".png");
            Picasso.with(getApplicationContext()).load(Utils.ICON_URL+weather.currentCondition.getIcon()+".png").error(R.drawable.common_google_signin_btn_icon_light_pressed).into(iconView);
            progressDialog.dismiss();
        }

        public String formatTime(Long unixDate){
            Date date = new Date(unixDate*1000L); // *1000 is to convert seconds to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+2")); // give a timezone reference for formating (see comment at the bottom
            String formattedDate = sdf.format(date);
            return formattedDate;
        }
    }

    private void showInputDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Change city");

        final EditText cityInput = new EditText(MainActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Moscow,RU");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPreference=new CityPreference(MainActivity.this);
                cityPreference.setCity(cityInput.getText().toString());

                String newCity=cityPreference.getCity();
                progressDialog.show();
                renderWeatherData(newCity);
            }
        });
        builder.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.change_cityID) {
            showInputDialog();
        }

        return super.onOptionsItemSelected(item);
    }
}
