package utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Trace;

/**
 * Created by mailt on 3/3/2017.
 */

public class UserSessionDetails {

    public final static String USER_PREFER = "user_data";
    public final static String NAME = "name";
    public final static String EMAIL = "email";
    public final static String IMG_URL = "img_url";
    public final static int PRIVATE_MODE = 0;
    public final static String IS_LOGGED_IN = "is_logged_in";
    public final static String MOBILE = "mobile";
    public final static String SESSION_KEY = "sessionKey";
    final static String ID = "id";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public void initilisePrefer(String p, Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(p, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public  String getSessionKey() {
        return sharedPreferences.getString(SESSION_KEY, "");
    }

    public void loginUser(String name, String email, String img_url, String sessionkey) {
        editor.putString(NAME,name);
        editor.putString(EMAIL, email);
        editor.putString(IMG_URL, img_url);
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(SESSION_KEY, sessionkey);

        editor.commit();

    }

    public void logoutUser() {
        editor.clear();
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.commit();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public String getName() {
        return sharedPreferences.getString(NAME, "");
    }

    public String getEmail() {
        return sharedPreferences.getString(EMAIL, "");
    }

    public String getImgUrl() {
        return sharedPreferences.getString(IMG_URL, "");
    }

    public void saveMobile(String mobile) {
        editor.putString(MOBILE, mobile);
        editor.commit();
    }

    public void saveName(String name) {
        editor.putString(NAME, name);
        editor.commit();
    }

    public void saveEmail(String email) {
        editor.putString(EMAIL, email);
        editor.commit();
    }

    public String getMobile() {
        return sharedPreferences.getString(MOBILE, "");
    }
}
