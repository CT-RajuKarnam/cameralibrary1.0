package com.cartrade.cameraexample.db;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cartrade.cameraexample.AdroitApplication;


public class Pref {
    private static Pref uniqInstance;
    private static SharedPreferences pref;
    public static SharedPreferences.Editor editor;
    static  String login_username="",login_password="";
    public boolean isFCMCalled() {
        return pref.getBoolean("isFCMCalled", false);
    }

    public void setFCMCalled(boolean FCMCalled) {
        editor.putBoolean("isFCMCalled", FCMCalled);
        editor.apply();
    }

    public boolean isFCMCalled;

    public boolean isPreviewEnabled() {
        return pref.getBoolean("isPreviewEnabled", false);
    }

    public void setPreviewEnabled(boolean isPreviewEnabled) {
        editor.putBoolean("isPreviewEnabled", isPreviewEnabled);
        editor.apply();
    }

    public boolean isPreviewEnabled;

    public static Pref getIn() {
        if (uniqInstance == null) {
            uniqInstance = new Pref();
            pref = PreferenceManager.getDefaultSharedPreferences(AdroitApplication.app_ctx);
        }
        editor = pref.edit();
        return uniqInstance;
    }

    public static Pref clearData() {
        if (uniqInstance == null) {
            uniqInstance = new Pref();
            pref = PreferenceManager.getDefaultSharedPreferences(AdroitApplication.app_ctx);
        }
        login_username = pref.getString("login_username","");
        login_password = pref.getString("login_password","");
        ischecked = pref.getBoolean("ischecked",false);
        editor = pref.edit();
        editor.clear().apply();
        editor.putString("login_username",login_username);
        editor.putString("login_password",login_password);
        editor.putBoolean("ischecked", ischecked);
        editor.apply();
        return uniqInstance;
    }

    public String getId() {
        return pref.getString("id", "");
    }

    public void setId(String id) {
        editor.putString("id", id);
        editor.apply();
    }

    private String helpline = "";

    private String id = "";

    public int getTime_frequency() {
        return pref.getInt("time_frequency",time_frequency);
    }

    public void setTime_frequency(int time_frequency) {
        this.time_frequency = time_frequency;
        editor.putInt("time_frequency", time_frequency);
        editor.apply();
    }

    private int time_frequency = 3;

    public int getDistance_frequency() {
        return pref.getInt("distance_frequency",distance_frequency);
    }

    public void setDistance_frequency(int distance_frequency) {
        this.distance_frequency = distance_frequency;
        editor.putInt("distance_frequency", distance_frequency);
        editor.apply();
    }

    private int distance_frequency = 50;


    public boolean isPreview_show_app() {
        return pref.getBoolean("preview_show_app", true);
    }

    public void setPreview_show_app(boolean preview_show_app) {
        editor.putBoolean("preview_show_app", preview_show_app);
        editor.apply();
    }

    private boolean preview_show_app = false;


    public int getPreview_transition() {
        return pref.getInt("preview_transition", 1);
    }

    public void setPreview_transition(int preview_transition) {
        editor.putInt("preview_transition", preview_transition);
        editor.apply();
    }

    private int preview_transition = 0;

    public String getState() {
        return pref.getString("state", "");
    }

    public void setState(String state) {
        editor.putString("state", state);
        editor.apply();
    }

    public String getFcm_token() {
        return pref.getString("fcm_token", "");
    }

    public void setFcm_token(String fcm_token) {
        editor.putString("fcm_token", fcm_token);
        editor.apply();
    }

    public String getCity() {
        return pref.getString("city", "");
    }

    public void setCity(String city) {
        editor.putString("city", city);
        editor.apply();
    }

    private String state = "";
    private String city = "";

    public String getBypass_screen4() {
        return pref.getString("bypass_screen4", "n");
    }

    public void setBypass_screen4(String bypass_screen4) {
        this.bypass_screen4 = bypass_screen4;
        editor.putString("bypass_screen4", bypass_screen4);
        editor.apply();
    }

    private String bypass_screen4 = "n";

    public String getCo_user() {
        return pref.getString("co_user", "");
    }

    public void setCo_user(String co_user) {
        editor.putString("co_user", co_user);
        editor.apply();
    }

    private String co_user = "";

    public String getVideo_timestamp() {
        return video_timestamp;
    }

    public void setVideo_timestamp(String video_timestamp) {
        this.video_timestamp = video_timestamp;
    }

    private String video_timestamp = "y";

    public String getErrorApi() {
        return pref.getString("errorApi", "https://ops1.adroitauto.in/adroit_apis/internal/logdata.php");
    }

    public void setErrorApi(String errorApi) {
        editor.putString("errorApi", errorApi);
        editor.apply();
    }

    private String errorApi = "";

    public String getHide_fields() {
        return pref.getString("hide_fields", "");
    }

    public void setHide_fields(String hide_fields) {
        editor.putString("hide_fields", hide_fields);
        editor.apply();
    }

    private String hide_fields = "";

    public String getLoginType() {
        return pref.getString("loginType", "");
    }

    public void setLoginType(String loginType) {
        editor.putString("loginType", loginType);
        editor.apply();
    }


    public String getAmount_msg() {
        return pref.getString("amount_msg", "Rs <amount> amount has to be collected");
    }

    public void setAmount_msg(String amount_msg) {
        editor.putString("amount_msg", amount_msg);
        editor.apply();
    }

    private String amount_msg = "";
    private String loginType = "";

    public String getIgnore_branch_users_response() {
        return pref.getString("ignore_branch_users_response", "n");
    }

    public void setIgnore_branch_users_response(String ignore_branch_users_response) {
        editor.putString("ignore_branch_users_response", ignore_branch_users_response);
        editor.apply();
    }

    private String ignore_branch_users_response = "";

    public boolean getUserLoginStatus() {
        return pref.getBoolean("logged_in", false);
    }

    public void setUserLoginStatus(boolean status) {
        editor.putBoolean("logged_in", status);
        editor.apply();
    }

    public boolean getAssistancestatus() {
        return pref.getBoolean("assist", false);
    }

    public void setAssistancestatus(boolean status) {
        editor.putBoolean("assist", status);
        editor.apply();
    }

    public String getUserName() {
        return pref.getString("user_name", "");
    }

    public void setUserName(String userName) {
        editor.putString("user_name", userName);
        editor.apply();
    }

    public String getLogin_username() {
        return pref.getString("login_username", "");
    }

    public void setLogin_username(String login_username) {
        this.login_username = login_username;
        editor.putString("login_username", login_username);
        editor.apply();
    }

    public String getLogin_password() {
        return pref.getString("login_password", "");
    }

    public void setLogin_password(String login_password) {
        this.login_password = login_password;
        editor.putString("login_password", login_password);
        editor.apply();
    }

    public boolean isIschecked() {
        return pref.getBoolean("ischecked",false);
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
        editor.putBoolean("ischecked", ischecked);
        editor.apply();
    }

    static boolean ischecked=false;



    public String getUserType() {
        return pref.getString("user_type", "");
    }

    public void setUserType(String user_type) {
        editor.putString("user_type", user_type);
        editor.apply();
    }

    public String getRole() {
        return pref.getString("role", "");
    }

    public void setRole(String role) {
        editor.putString("role", role);
        editor.apply();
    }

    public String getRoleTypeValue() {
        return pref.getString("role_type_value", "");
    }

    public void setRoleTypeValue(String role_type_value) {
        editor.putString("role_type_value", role_type_value);
        editor.apply();
    }

    public String getComp_id() {
        return pref.getString("comp_id", "");
    }

    public void setComp_id(String comp_id) {
        editor.putString("comp_id", comp_id);
        editor.apply();
    }

    public String getRegion_id() {
        return pref.getString("region_id", "");
    }

    public void setRegion_id(String region_id) {
        editor.putString("region_id", region_id);
        editor.apply();
    }

    public String getBranch_id() {
        return pref.getString("branch_id", "");
    }

    public void setBranch_id(String branch_id) {
        editor.putString("branch_id", branch_id);
        editor.apply();
    }

    public String getParent_id() {
        return pref.getString("parent_id", "");
    }

    public void setParent_id(String parent_id) {
        editor.putString("parent_id", parent_id);
        editor.apply();
    }

    public String getClient_cat_id() {
        return pref.getString("client_cat_id", "");
    }

    public void setClient_cat_id(String client_cat_id) {
        editor.putString("client_cat_id", client_cat_id);
        editor.apply();
    }

    public int isTnc_shown() {
        return pref.getInt("tnc_shown", 0);
    }

    public void setTnc_shown(int tnc_shown) {
        editor.putInt("tnc_shown", tnc_shown);
        editor.apply();
    }

    public String getTnc_text() {
        return pref.getString("tnc_text", "");
    }

    public void setTnc_text(String tnc_text) {
        editor.putString("tnc_text", tnc_text);
        editor.apply();
    }

    public String getAgency_id() {
        return pref.getString("agency_id", "");
    }

    public void setAgency_id(String agency_id) {
        editor.putString("agency_id", agency_id);
        editor.apply();
    }

    public String getPhone_number() {
        return pref.getString("phone_number", "");
    }

    public void setPhone_number(String phone_number) {
        editor.putString("phone_number", phone_number);
        editor.apply();
    }

    public boolean isCustomer() {
        return pref.getBoolean("user_flag", false);
    }

    public void setCustomerFlag(boolean flag) {
        editor.putBoolean("user_flag", flag);
        editor.apply();
    }

    public void setCrt(String access) {
        editor.putString("crt", access);
        editor.apply();
    }

    public String getCrt() {
        return pref.getString("crt", "");
    }

    public String getEmail() {
        return pref.getString("emp_email", "");
    }

    public void setEmail(String emp_email) {
        editor.putString("emp_email", emp_email);
        editor.apply();
    }


    public String getMobile() {
        return pref.getString("emp_mobile", "");
    }

    public void setMobile(String emp_mobile) {
        editor.putString("emp_mobile", emp_mobile);
        editor.apply();
    }


    public String getEmp_id() {
        return pref.getString("emp_id", "");
    }

    public void setEmp_id(String emp_id) {
        editor.putString("emp_id", emp_id);
        editor.apply();
    }

    public String getAgent_id() {
        return pref.getString("agent_id", "");
    }

    public void setAgent_id(String emp_id) {
        editor.putString("agent_id", emp_id);
        editor.apply();
    }

    public String getCreated_by_user_id() {
        return pref.getString("created_by_user_id", "");
    }

    public void setCreated_by_user_id(String created_by_user_id) {
        editor.putString("created_by_user_id", created_by_user_id);
        editor.apply();
    }

    private String created_by_user_id = "";

/*
    public boolean isLogin() {
        return pref.getBoolean("isLogin", false);
    }

    public void setLogin(boolean emp_id) {
        editor.putBoolean("isLogin", emp_id);
        editor.apply();
    }
*/

    public void setIsCritical(boolean isCritical) {
        editor.putBoolean("iscritical", isCritical);
        editor.apply();
    }

    public boolean getIsCritical() {
        return pref.getBoolean("iscritical", true);
    }

    int version;

    public int getPlaystore_version() {
        return pref.getInt("playstore_version", 0);
    }

    public void setPlaystore_version(int playstore_version) {
        editor.putInt("playstore_version", playstore_version);
        editor.apply();
    }

    int playstore_version;

    public void setVersion(int version) {
        editor.putInt("version", version);
        editor.apply();
    }

    public int getVersion() {
        return pref.getInt("version", 0);
    }

    public void setNewVersion(int newversion) {
        editor.putInt("newversion", newversion);
        editor.apply();
    }

    public int getNewVersion() {
        return pref.getInt("newversion", 0);
    }

    public int getQuick_count() {
        return pref.getInt("quick_count", 5);
    }

    public void setQuick_count(int quick_count) {
        editor.putInt("quick_count", quick_count);
        editor.apply();
    }

    public int getJourney_yard_count() {
        return pref.getInt("journey_yard_count", 5);
    }

    public void setJourney_yard_count(int journey_yard_count) {
        editor.putInt("journey_yard_count", journey_yard_count);
        editor.apply();
    }

    public int getDraftCount() {
        return pref.getInt("draftcount", 0);
    }

    public void setDraftCount(int draftcount) {
        editor.putInt("draftcount", draftcount);
        editor.apply();
    }

    public int getCurrentId() {
        return pref.getInt("current_id", 0);
    }

    public void setCurrentId(int current_id) {
        editor.putInt("current_id", current_id);
        editor.apply();
    }

    public String getSync_updatedate() {
        return pref.getString("sync_updatedate", "");
    }

    public void setSync_updatedate(String sync_updatedate) {
        this.sync_updatedate = sync_updatedate;
        editor.putString("sync_updatedate", sync_updatedate);
        editor.apply();
    }

    public String getSync_updatedate_guest() {
        return pref.getString("sync_updatedate_guest", "");
    }

    public void setSync_updatedate_guest(String sync_updatedate_guest) {
        editor.putString("sync_updatedate_guest", sync_updatedate_guest);
        editor.apply();
    }

    private String sync_updatedate_guest = "";
    private String sync_updatedate = "";

    public String getIcon_sync_updatedate() {
        return pref.getString("icon_sync_updatedate", "");
    }

    public void setIcon_sync_updatedate(String icon_sync_updatedate) {
        this.icon_sync_updatedate = icon_sync_updatedate;
        editor.putString("icon_sync_updatedate", icon_sync_updatedate);
        editor.apply();
    }

    private String icon_sync_updatedate = "";

    public String getHelpline() {
        return pref.getString("helpline", "02233292363");
    }

    public void setHelpline(String helpline) {
        editor.putString("helpline", helpline);
        editor.apply();
    }

    public String getAppCode() {
        return pref.getString("app_code", "");
    }

    public void setAppCode(String app_code) {
        editor.putString("app_code", app_code);
        editor.apply();
    }

    public boolean getSync() {
        return pref.getBoolean("sync_done", false);
    }

    public void setSync(boolean sync_done) {
        editor.putBoolean("sync_done", sync_done);
        editor.apply();
    }

    public void saveDeviceId(String id) {
        editor.putString("device_id", id);
        editor.apply();
    }

    public String getDeviceId() {
        return pref.getString("device_id", "");
    }


    public boolean is_sslSet() {
        return pref.getBoolean("is_sslSet", false);
    }

    public void set_sslSet(boolean is_sslSet) {
        editor.putBoolean("is_sslSet", is_sslSet);
        editor.apply();
    }

    public boolean is_pinning() {
        return pref.getBoolean("is_pinning", false);
    }

    public void set_pinning(boolean is_pinning) {
        editor.putBoolean("is_pinning", is_pinning);
        editor.apply();
    }

    public String getQuickRepoContinueLan() {
        return pref.getString("quick_repo_continue_lan", "");
    }

    public void setQuickRepoContinueLan(String quick_repo_continue_lan) {
        editor.putString("quick_repo_continue_lan", quick_repo_continue_lan);
        editor.apply();
    }

    public boolean isOrientationFlag() {
        return pref.getBoolean("orientation_flag", false);
    }

    public void setOrientationFlag(boolean orientation_flag) {
        editor.putBoolean("orientation_flag", orientation_flag);
        editor.apply();
    }

    public void setCameraFlash(String flash) {
        editor.putString("flash", flash);
        editor.apply();
    }

    public String getCameraFlash() {
        return pref.getString("flash", "");
    }

    public void setLatitude(String lat) {
        editor.putString("lat", lat);
        editor.apply();
    }

    public String getLatitude() {
        return pref.getString("lat", "");
    }

    public void setLongitude(String longitude) {
        editor.putString("longitude", longitude);
        editor.apply();
    }

    public String getLongitude() {
        return pref.getString("longitude", "");
    }

    public String getListingId() {
        return pref.getString("listing_id", "");
    }

    public void setListingId(String listingid) {
        editor.putString("listing_id", listingid);
        editor.apply();
    }

    public boolean isService_running() {
        return pref.getBoolean("service_running", false);
    }

    public void setService_running(boolean service_running) {
        editor.putBoolean("service_running", service_running);
        editor.apply();
    }

    public String getLan() {
        return pref.getString("lan", "");
    }

    public void setLan(String lan) {
        editor.putString("lan", lan);
        editor.apply();
    }

    public boolean isSaved() {
        return pref.getBoolean("isSaved", false);
    }

    public void setSaved(boolean saved) {
        editor.putBoolean("isSaved", saved);
        editor.apply();
    }

    public void firstTimeAskingPermission(String permission, boolean isFirstTime) {
        editor.putBoolean(permission, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeAskingPermission(String permission) {
        return pref.getBoolean(permission, true);
    }

    public String getNumber() {
        return pref.getString("number", "9223020015");
    }

    public void setNumber(String number) {
        editor.putString("number", number);
        editor.apply();
    }

    public String getRequest_customer() {
        return pref.getString("request_customer", "n");
    }

    public void setRequest_customer(String request_customer) {
        this.request_customer = request_customer;
        editor.putString("request_customer", request_customer);
        editor.apply();
    }

    public String getRequest_adroit() {
        return pref.getString("request_adroit", "n");
    }

    public void setRequest_adroit(String request_adroit) {
        this.request_adroit = request_adroit;
        editor.putString("request_adroit", request_adroit);
        editor.apply();
    }

    public int getTemplate_count() {
        return pref.getInt("template_count", 150);
    }

    public void setTemplate_count(int template_count) {
        this.template_count = template_count;
        editor.putInt("template_count", template_count);
        editor.apply();
    }

    public int template_count = 150;

    public int getBranchusercount() {
        return pref.getInt("branchusercount", 10);
    }

    public void setBranchusercount(int branchusercount) {
        this.branchusercount = branchusercount;
        editor.putInt("branchusercount", branchusercount);
        editor.apply();
    }

    public int branchusercount = 10;

    public String getRequest_internal() {
        return pref.getString("request_internal", "n");
    }

    public void setRequest_internal(String request_internal) {
        this.request_internal = request_internal;
        editor.putString("request_internal", request_internal);
        editor.apply();
    }

    public String getCreate_case_flow() {
        return pref.getString("create_case_flow", "n");
    }

    public void setCreate_case_flow(String create_case_flow) {
        this.create_case_flow = create_case_flow;
        editor.putString("create_case_flow", create_case_flow);
        editor.apply();
    }

    public String getVariant_optional() {
        return pref.getString("variant_optional", "n");
    }

    public void setVariant_optional(String variant_optional) {
        this.variant_optional = variant_optional;
        editor.putString("variant_optional", variant_optional);
        editor.apply();
    }

    private String variant_optional="n";


    private boolean compressflag = false;
    private int mincompression = 10;

    public boolean isCompressflag() {
        return pref.getBoolean("compressflag", false);
    }

    public void setCompressflag(boolean compressflag) {
        this.compressflag = compressflag;
        editor.putBoolean("compressflag", compressflag);
        editor.apply();

    }

    public int getMincompression() {
        return pref.getInt("mincompression", 10);
    }

    public void setMincompression(int mincompression) {
        this.mincompression = mincompression;
        editor.putInt("mincompression", mincompression);
        editor.apply();
    }

    public int getMediumcompression() {
        return pref.getInt("mediumcompression", 25);
    }

    public void setMediumcompression(int mediumcompression) {
        this.mediumcompression = mediumcompression;
        editor.putInt("mediumcompression", mediumcompression);
        editor.apply();
    }

    private int mediumcompression = 25;

    public int getMaxcompression() {
        return pref.getInt("maxcompression", 50);
    }

    public void setMaxcompression(int maxcompression) {
        this.maxcompression = maxcompression;
        editor.putInt("maxcompression", maxcompression);
        editor.apply();
    }

    private int maxcompression = 50;


    private String create_case_flow = "";

    private String request_customer = "n";
    private String request_adroit = "n";
    private String request_internal = "n";

    public String getRequest_self() {
        return pref.getString("request_self", "n");
    }

    public void setRequest_self(String request_self) {
        this.request_self = request_self;
        editor.putString("request_self", request_self);
        editor.apply();
    }

    private String request_self = "n";
    private int offline_disable_time = 0;

    public void setOffline_disable_time(int offline_disable_time) {
        editor.putInt("offline_disable_time", offline_disable_time);
        editor.apply();
    }

    public int getOffline_disable_time() {
        return pref.getInt("offline_disable_time", 8);
    }

    public void setSecureKeys(String shareKey, boolean flag, String mdKey) {
        editor.putString("sharedkey", shareKey);
        editor.putBoolean("en_de_flag", flag);
        editor.putString("mdkey", mdKey);
        editor.apply();
    }

    public String getSharedkey() {
        return pref.getString("sharedkey", "");
    }

    public boolean getEn_de_flag() {
        return pref.getBoolean("en_de_flag", false);
    }

    public String getMdkey() {
        return pref.getString("mdkey", "");
    }


   /* public boolean getConsumerLoginStatus() {
        return pref.getBoolean("is_consumer", false);
    }

    public void setConsumerLoginStatus(boolean status) {
        editor.putBoolean("is_consumer", status);
        editor.apply();
    }*/

    public int getPrefInt(String string) {
        return pref.getInt(string, 0);
    }

    public void setPrefInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public String getPrefString(String string) {
        return pref.getString(string, "");
    }

    public void setPrefString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    //    private String new_version= "";
    private String offline_delete_time = "";
    private String popup_description = "";
    private String popup_header = "";
    private String video_link = "";
    private String tcflag = "";
    private String tc_text = "";
    private String helpline_east = "";
    private String helpline_west = "";
    private String helpline_north = "";
    private String helpline_south = "";
    private String year = "";

    /*public String getNew_version() {
        return pref.getString("new_version", "0");
    }

    public void setNew_version(String new_version) {
        this.new_version = new_version;
        editor.putString("new_version", new_version);
        editor.apply();
    }*/
    public String getOffline_delete_time() {
        return pref.getString("offline_delete_time", "0");
    }

    public void setOffline_delete_time(String offline_delete_time) {
        this.offline_delete_time = offline_delete_time;
        editor.putString("offline_delete_time", offline_delete_time);
        editor.apply();
    }

    public String getPopup_description() {
        return pref.getString("popup_description", "It looks like your are using an older version of Adroit Auto app. Please update your app at once to the latest version");
    }

    public void setPopup_description(String popup_description) {
        this.popup_description = popup_description;
        editor.putString("popup_description", popup_description);
        editor.apply();
    }

    public String getPopup_header() {
        return pref.getString("popup_header", "Adroit Auto App update notification");
    }

    public void setPopup_header(String popup_header) {
        this.popup_header = popup_header;
        editor.putString("popup_header", popup_header);
        editor.apply();
    }

    public String getVideo_link() {
        return pref.getString("video_link", "");
    }

    public void setVideo_link(String video_link) {
        this.video_link = video_link;
        editor.putString("video_link", video_link);
        editor.apply();
    }

    public String getTcflag() {
        return pref.getString("tcflag", "");
    }

    public void setTcflag(String tcflag) {
        this.tcflag = tcflag;
        editor.putString("tcflag", tcflag);
        editor.apply();
    }

    public String getTc_text() {
        return pref.getString("tc_text", "");
    }

    public void setTc_text(String tc_text) {
        this.tc_text = tc_text;
        editor.putString("tc_text", tc_text);
        editor.apply();
    }

    public String getHelpline_east() {
        return pref.getString("helpline_east", "");
    }

    public void setHelpline_east(String helpline_east) {
        this.helpline_east = helpline_east;
        editor.putString("helpline_east", helpline_east);
        editor.apply();
    }

    public String getHelpline_west() {
        return pref.getString("helpline_west", "");
    }

    public void setHelpline_west(String helpline_west) {
        this.helpline_west = helpline_west;
        editor.putString("helpline_west", helpline_west);
        editor.apply();
    }

    public String getHelpline_south() {
        return pref.getString("helpline_south", "");
    }

    public void setHelpline_south(String helpline_south) {
        this.helpline_south = helpline_south;
        editor.putString("helpline_south", helpline_south);
        editor.apply();
    }

    public String getExpress_flow() {
        return pref.getString("express_flow", "");
    }

    public void setExpress_flow(String express_flow) {
        this.express_flow = express_flow;
        editor.putString("express_flow", express_flow);
        editor.apply();
    }

    public String express_flow = "";

    public String getDirect_flow() {
        return pref.getString("direct_flow", "n");
    }

    public void setDirect_flow(String direct_flow) {
        this.direct_flow = direct_flow;
        editor.putString("direct_flow", direct_flow);
        editor.apply();
    }

    public String direct_flow = "";

    public String getInsuff_flow() {
        return pref.getString("insuff_flow", "n");
    }

    public void setInsuff_flow(String insuff_flow) {
        this.insuff_flow = insuff_flow;
        editor.putString("insuff_flow", insuff_flow);
        editor.apply();
    }

    public String insuff_flow = "n";

    public String getShow_offline_gallery() {
        return pref.getString("show_offline_gallery", "");
    }

    public void setShow_offline_gallery(String show_offline_gallery) {
        this.show_offline_gallery = show_offline_gallery;
        editor.putString("show_offline_gallery", show_offline_gallery);
        editor.apply();
    }

    public String show_offline_gallery = "";

    public void setMFGYear(String year) {
        this.year = year;
        editor.putString("mfg_year", year);
        editor.apply();
    }

    public String getMFGYear() {
        return pref.getString("mfg_year", "");
    }

    public String getHelpline_north() {
        return pref.getString("helpline_north", "");
    }

    public void setHelpline_north(String helpline_north) {
        this.helpline_north = helpline_north;
        editor.putString("helpline_north", helpline_north);
        editor.apply();
    }

    String homeApi_Update = "";

    public String getHomeApi_Update() {
        return pref.getString("homeApi_Update", "");
    }

    public void setHomeApi_Update(String homeApi_Update) {
        this.homeApi_Update = homeApi_Update;
        editor.putString("homeApi_Update", homeApi_Update);
        editor.apply();
    }

    public String getCaseName() {
        return pref.getString("case_text", "Case");
    }

    public void setCaseName(String name) {
        editor.putString("case_text", name);
        editor.apply();
    }

    public boolean isBGVVuser() {
        return pref.getBoolean("bgv_user", false);
    }

    public void setBGVUser(boolean bgv_user) {
        editor.putBoolean("bgv_user", bgv_user);
        editor.apply();
    }

    public String getPassword_login() {
        return pref.getString("password_login", "");
    }

    public void setPassword_login(String password_login) {
        this.password_login = password_login;
        editor.putString("password_login", password_login);
        editor.apply();
    }

    String password_login = "";

    String fe_reject_reasons = "";

    public int getUser_category() {
        return pref.getInt("user_category", 1);
    }

    public void setUser_category(int user_category) {
        this.user_category = user_category;
        editor.putInt("user_category", user_category);
        editor.apply();
    }

    int user_category ;

    public String getFe_reject_reasons() {
        return pref.getString("fe_reject_reasons", "");
    }

    public void setFe_reject_reasons(String fe_reject_reasons) {
        this.fe_reject_reasons = fe_reject_reasons;
        editor.putString("fe_reject_reasons", fe_reject_reasons);
        editor.apply();
    }

    public String getTimercount() {
        return pref.getString("timercount", "10m");
    }

    public void setTimercount(String timercount) {
        this.timercount = timercount;
        editor.putString("timercount", timercount);
        editor.apply();
    }

    String timercount = "";

    String fe_reject_flow = "";

    public String getFe_reject_flow() {
        return pref.getString("fe_reject_flow", "");
    }

    public void setFe_reject_flow(String fe_reject_flow) {
        this.fe_reject_flow = fe_reject_flow;
        editor.putString("fe_reject_flow", fe_reject_flow);
        editor.apply();
    }

}
