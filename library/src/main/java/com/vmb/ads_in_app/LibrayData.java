package com.vmb.ads_in_app;

public class LibrayData {

    public static final int ID_NOTIFICATION = 0;

    public class AdsSize {
        public static final String BANNER = "BANNER";
        public static final String MEDIUM_RECTANGLE = "MEDIUM_RECTANGLE";
        public static final String NATIVE_ADS = "NATIVE_ADS";
    }

    public class Url {
        public static final String URL_ROOT = "http://gamemobileglobal.com/api/";
        public static final String URL_BASE = URL_ROOT + "apps/";
        public static final String URL_API_CONTROL_ADS = "control_apps.php";

        public static final String URL_API_SEND_TOKEN = "register_token.php";
        public static final String URL_API_PUSH_BACK_NOTIFY = "update_time_push_notify.php";

        public static final String URL_API_FEEDBACK = "feedback.php";
    }

    public class RequestCode {
        public static final int REQUEST_CODE_NOTIFICATON = 1000;
        public static final int REQUEST_CODE_UPDATE = 1001;

        public static final int REQUEST_CODE_RATE_APP = 1002;
        public static final int REQUEST_CODE_SHARE_APP = 1003;
        public static final int REQUEST_CODE_MORE_APP = 1004;
    }

    public class KeySharePrefference {
        public static final String COUNT_PLAY = "count_play";
        public static final String SHOW_RATE = "show_rate";

        public static final String NOTI_TOKEN = "noti_token";
        public static final String SEND_TOKEN = "send_token";
    }

    public class KeyIntentData {
        public static final String KEY_ADS_ACTIVITY = "type_ads";
    }

    public class FileName {
        public static final String FILE_ADS_CONFIG = "ads_config.txt";
    }
}