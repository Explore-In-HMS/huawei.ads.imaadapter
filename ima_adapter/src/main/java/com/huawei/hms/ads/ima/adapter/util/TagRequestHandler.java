package com.huawei.hms.ads.ima.adapter.util;

import android.os.StrictMode;
import android.util.Log;

import com.huawei.hms.ads.ima.adapter.constants.VastVmapVariables;
import com.huawei.hms.ads.ima.adapter.model.GoogleAds;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import static com.huawei.hms.ads.ima.adapter.constants.AdTagURLVariables.utfStandard;

public class TagRequestHandler {

    private static final String TAG = "HwImaAdapter";

    public static XmlPullParser makeRequest(GoogleAds ad) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URL url = new URL(ad.getAdUri().toString());

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(url.openConnection().getInputStream(), utfStandard);

            return xpp;

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public static String getResponseXML(GoogleAds ad) {

        try {
            URL url = new URL(ad.getAdUri().toString());
            InputStream inputStream = url.openConnection().getInputStream();

            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }

            return total.toString();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return "";
        }
    }

    public boolean checkGoogleHasOffset(GoogleAds ad) {
        boolean result = false;
        try {
            XmlPullParser xpp = makeRequest(ad);
            if(xpp != null){
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && xpp.getName().equalsIgnoreCase(VastVmapVariables.linear)) {
                        if (xpp.getAttributeValue(null, VastVmapVariables.skipOffset) != null && !xpp.getAttributeValue(null, VastVmapVariables.skipOffset).equals("00:00:00")) {
                            result = true;
                            break;
                        }
                    }
                    eventType = xpp.next();
                }
            }

            return result;

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }
}
