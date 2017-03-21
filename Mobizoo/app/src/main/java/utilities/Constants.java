package utilities;

/**
 * Created by mailt on 3/15/2017.
 */

public class Constants {
    public static final String BASE_URL = "http://10.10.32.119:8000";
    public static final String LOGIN_URL = BASE_URL+"/api/user_api/user/login/?format=json&type=user";
    public static final String FETCH_USER_DETASILS_URL = BASE_URL+ "/api/user_api/user/fetch_user_details/?format=json&type=user";
    public static final String REQUEST_OTP_URL = BASE_URL+"/api/user_api/user/request_otp/?format=json&type=user";
    public static final String VERIFY_OTP_URL = BASE_URL+"/api/user_api/user/verify_otp/";
    public static final String RESEND_OTP_URL = BASE_URL+"/api/user_api/user/resend_otp/?format=json&type=user";
    public static final String SAVE_BANK_DETAILS_URL = BASE_URL+"/api/user_api/bank_details/save_bank/?format=json&type=user";
    public static final String FETCH_BANKS_URL = BASE_URL+"/api/user_api/bank_details/?format=json&type=user";

    public static final String FETCH_BILL_URL = BASE_URL;
}
