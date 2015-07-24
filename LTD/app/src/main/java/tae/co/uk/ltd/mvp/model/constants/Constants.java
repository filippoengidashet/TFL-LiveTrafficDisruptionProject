package tae.co.uk.ltd.mvp.model.constants;

/**
 * Created by Filippo-TheAppExpert on 7/23/2015.
 */
public final class Constants {

    public static final String PKG_NAME = "tae.co.uk.ltd";
    public static final String DISRUPTION_FILTER = PKG_NAME + ".disruption";
    public static final String XML_EXTRA_URL = "url";
    public static final String DISRUPTION_EXTRA = "disruption";
    public static final String TAB_NAME_EXTRA = "tab.name";
    public static final String TAB_POSITION_EXTRA = "tab.position";

    public static final String APP_ID = "97aeb5b8";
    public static final String APP_KEY = "249f40d973e95c96092f91b08358bfad";
    public static final String BASE_URL = "https://data.tfl.gov.uk";
    public static final String TIMS = BASE_URL + "/tfl/syndication/feeds/tims_feed.xml?app_id=" + APP_ID + "&app_key=" + APP_KEY;

    public static final class RequestMethod {

        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String PUT = "PUT";
        public static final String DELETE = "DELETE";
    }

    public static final class ConnectionUtil {

        public static final int TIMEOUT = 5000;
    }

    public static final class ResponseCode {

        public static final int SUCCESS = 200;
    }

    public static final class CategoryType {

        public static final String HAZARD = "Hazard(s)";
        public static final String INFRASTRUCTURE = "Infrastructure Issue";
        public static final String SPECIAL_EVENT = "Special and Planned Events";
        public static final String TRAFFIC_INCIDENT = "Traffic Incidents";
        public static final String TRAFFIC_VOLUME = "Traffic Volume";
        public static final String WORK = "Works";
        public static final String ALL = "all";
    }
}
