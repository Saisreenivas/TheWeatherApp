package util;

import com.saisreenivas.httpintro.theweatherapp.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sai sreenivas on 2/20/2017.
 */

public class Utils {

    public static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?appid=" + BuildConfig.WEATHER_API_KEY + "&q=";
    public static final String ICON_URL = "https://openweathermap.org/img/w/";



    public static JSONObject getObject(String tagName, JSONObject jsonObject) throws JSONException {
        JSONObject jObj = jsonObject.getJSONObject(tagName);
        return jObj;
    }

    public static String getString(String tagName, JSONObject jsonObject) throws JSONException {
        return  jsonObject.getString(tagName);
    }

    public static float getFloat(String tagName, JSONObject jsonObject) throws JSONException {
        return (float) jsonObject.getDouble(tagName);
    }

    public static double getDouble(String tagName, JSONObject jsonObject) throws JSONException {
        return (float) jsonObject.getDouble(tagName);
    }

    public static int getInt(String tagName, JSONObject jsonObject) throws JSONException {
        return jsonObject.getInt(tagName);
    }
}
