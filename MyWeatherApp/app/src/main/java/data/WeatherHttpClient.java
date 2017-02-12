package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Util.Utils;

/**
 * Created by USER on 31.01.2017.
 */

public class WeatherHttpClient {

    public String getWeatherData(String place){
        HttpURLConnection connection;
        InputStream inputStream;

        try {

            connection = (HttpURLConnection) (new URL(Utils.BASE_URL + place+Utils.APPID)).openConnection();

            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoInput(true);
            connection.connect();
            StringBuffer stringBuffer = new StringBuffer();
            inputStream=connection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = bufferedReader.readLine())!=null){
                stringBuffer.append(line+"\r\n");
            }

            inputStream.close();
            connection.disconnect();

            return stringBuffer.toString();

        }catch (IOException e){

            e.printStackTrace();
        }
        return null;
    }

}
