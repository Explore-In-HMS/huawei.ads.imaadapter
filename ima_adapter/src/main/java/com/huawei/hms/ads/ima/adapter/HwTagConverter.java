package com.huawei.hms.ads.ima.adapter;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.huawei.hms.ads.ima.adapter.constants.AdTagURLVariables;
import com.huawei.hms.ads.ima.adapter.listeners.TagConverterListener;
import com.huawei.hms.ads.ima.adapter.model.GoogleAds;
import com.huawei.hms.ads.ima.adapter.util.TagRequestHandler;
import com.huawei.hms.ads.ima.adapter.util.VastXMLCreator;
import com.huawei.hms.ads.vast.application.requestinfo.CreativeMatchStrategy;
import com.huawei.hms.ads.vast.openalliance.ad.beans.parameter.RequestOptions;
import com.huawei.hms.ads.vast.player.api.VastAdPlayer;
import com.huawei.hms.ads.vast.player.model.LinearCreative;
import com.huawei.hms.ads.vast.player.model.adslot.AdsData;
import com.huawei.hms.ads.vast.player.model.adslot.LinearAdSlot;
import com.huawei.hms.ads.vast.player.model.remote.RequestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HwTagConverter {

    private static HwTagConverter instance;
    private final static String TAG = "HwImaPluginLatest";


    private Uri googleAdTag;
    private TagConverterListener listener;


    private String adUnitID;
    private GoogleAds googleAds = new GoogleAds();
    private LinearAdSlot mLinearAdSlot;
    private final String mimeType = "application/xml";


    public static void init(@NonNull String TagUrl, @NonNull String adUnitId, TagConverterListener tagConverterListener) {

        if (instance == null) {
            instance = new HwTagConverter();
        }

        instance.adUnitID = adUnitId;
        instance.googleAdTag = Uri.parse(TagUrl);

        instance.listener = tagConverterListener;

        instance.clearInstance();
        instance.analyseGoogleAds();
        instance.requestVastSDK();
    }

    private void analyseGoogleAds() {

        try {
            Set<String> args = instance.googleAdTag.getQueryParameterNames();

            googleAds.adUri = instance.googleAdTag;

            if (args.contains(AdTagURLVariables.output)) {
                googleAds.xmlOutputType = instance.googleAdTag.getQueryParameter(AdTagURLVariables.output);
            }
            if (args.contains(AdTagURLVariables.vPos)) {
                googleAds.adPosition = instance.googleAdTag.getQueryParameter(AdTagURLVariables.vPos);
            }
            if (args.contains(AdTagURLVariables.vAdType)) {
                googleAds.adType = instance.googleAdTag.getQueryParameter(AdTagURLVariables.vAdType);
            }
            if (args.contains(AdTagURLVariables.maxAdDuration)) {
                googleAds.maxAdDuration = Integer.parseInt(instance.googleAdTag.getQueryParameter(AdTagURLVariables.maxAdDuration));
            }
            if (args.contains(AdTagURLVariables.minAdDuration)) {
                googleAds.minAdDuration = Integer.parseInt(instance.googleAdTag.getQueryParameter(AdTagURLVariables.minAdDuration));
            }
            if (args.contains(AdTagURLVariables.size)) {
                try {
                    String[] whAdSlot = instance.googleAdTag.getQueryParameter(AdTagURLVariables.size).split("x");
                    googleAds.width = Integer.parseInt(whAdSlot[0]);
                    googleAds.height = Integer.parseInt(whAdSlot[1]);
                } catch (Exception e) {
                    Log.e(TAG, "Ad URL: Slot size parse failed");
                }
            }
            if (args.contains(AdTagURLVariables.npa)) {
                googleAds.npaStatus = Integer.parseInt(instance.googleAdTag.getQueryParameter(AdTagURLVariables.npa));
            }
            if (args.contains(AdTagURLVariables.tfcd)) {
                googleAds.tcfdStatus = Integer.parseInt(instance.googleAdTag.getQueryParameter(AdTagURLVariables.tfcd));
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }

    private void clearInstance() {
        mLinearAdSlot = null;
        googleAds = new GoogleAds();
    }

    private void requestVastSDK() {

        if (googleAds.xmlOutputType.equals("vmap")) {
            List<GoogleAds> vmapAds = VastXMLCreator.getGoogleAdsFromVMAP(googleAds);

            if (vmapAds != null) {
                makeRequestForMultipleAd(vmapAds, googleAds);
            }

        } else if (googleAds.xmlOutputType.equals("vast")) {
            configureVastRequest(googleAds);
            makeRequestForSingleAd(googleAds);
        }
    }

    private void makeRequestForSingleAd(GoogleAds ad) {

        VastAdPlayer.getInstance().loadLinearAd(mLinearAdSlot, new RequestCallback() {
            @Override
            public void onAdsLoadedSuccess(AdsData adsData) {

                if (adsData != null && adsData.linearCreations != null && adsData.linearCreations.size() != 0) {
                    List<LinearCreative> linearAds = adsData.linearCreations;
                    TagRequestHandler handler = new TagRequestHandler();

                    for (LinearCreative creative : linearAds) {

                        if (creative.skipDuration != 0) {
                            //check google ad contain offset
                            if (!handler.checkGoogleHasOffset(ad)) {
                                creative.skipDuration = -1;
                            }
                        }

                        String vXML = VastXMLCreator.create(creative, false);

                        Uri uri = Uri.parse("data:" + mimeType + ";base64," + Base64.encodeToString(vXML.getBytes(), Base64.NO_WRAP));

                        if (uri != null) {
                            listener.onSuccess(uri);
                            break;
                        } else {
                            listener.onSuccess(googleAdTag);
                        }
                    }
                } else {
                    Log.d(TAG, "Ads Data null,fallback trigger.");
                }
            }

            @Override
            public void onAdsLoadFailed() {
                Log.d(TAG, "load failed,fallback trigger.");
            }
        });
    }


    private void makeRequestForMultipleAd(List<GoogleAds> ads, GoogleAds vmapAdTagUrlInfo) {

        List<String> hwAdXMLs = new ArrayList<>();

        for (GoogleAds vmapAd : ads) {
            configureVastRequest(vmapAd);

            VastAdPlayer.getInstance().loadLinearAd(mLinearAdSlot, new RequestCallback() {
                @Override
                public void onAdsLoadedSuccess(AdsData adsData) {

                    if (adsData != null && adsData.linearCreations != null && adsData.linearCreations.size() != 0) {

                        List<LinearCreative> linearAds = adsData.linearCreations;
                        TagRequestHandler handler = new TagRequestHandler();


                        for (LinearCreative creative : linearAds) {
                            if (creative.skipDuration != 0) {
                                if (!handler.checkGoogleHasOffset(vmapAd)) {
                                    creative.skipDuration = -1;
                                }
                            }
                            String vXML = VastXMLCreator.create(creative, true);
                            hwAdXMLs.add(vXML);
                        }


                    } else {
                        hwAdXMLs.add("");
                    }

                    if (hwAdXMLs.size() == ads.size()) {

                        updateTagsForVmap(hwAdXMLs, ads, vmapAdTagUrlInfo);

                    }
                }

                @Override
                public void onAdsLoadFailed() {
                    hwAdXMLs.add("");

                    if (hwAdXMLs.size() == ads.size()) {
                        updateTagsForVmap(hwAdXMLs, ads, vmapAdTagUrlInfo);

                    }
                    Log.e(TAG, "failed");
                }
            });
        }
    }

    private void configureVastRequest(GoogleAds ads) {

        ads = checkParameterValues(ads);

        if (ads.maxAdDuration / 1000 > 1) {
            ads.maxAdDuration = ads.maxAdDuration / 1000;
        }
        Log.i(TAG, "height: " + ads.height);
        Log.i(TAG, "width: " + ads.width);
        Log.i(TAG, "maxAdDuration: " + ads.maxAdDuration);

        CreativeMatchStrategy creativeMatchStrategy =
                new CreativeMatchStrategy(CreativeMatchStrategy.CreativeMatchType.ANY);

        RequestOptions requestOptions = new RequestOptions.Builder().setRequestLocation(true)
                .setNonPersonalizedAd(0)
                .setConsent("")
                .setAdContentClassification("A")
                .setTagForChildProtection(ads.tcfdStatus)
                .setTagForUnderAgeOfPromise(-1)
                .build();

        mLinearAdSlot = new LinearAdSlot();
        mLinearAdSlot.setSlotId(this.adUnitID);
        mLinearAdSlot.setTotalDuration(ads.maxAdDuration);
        mLinearAdSlot.setCreativeMatchStrategy(creativeMatchStrategy);
        mLinearAdSlot.setAllowMobileTraffic(true);
        mLinearAdSlot.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mLinearAdSlot.setRequestOptions(requestOptions);
        mLinearAdSlot.setSize(ads.width, ads.height);
    }

    private GoogleAds checkParameterValues(GoogleAds ad) {
        if (ad.height == 0) {
            ad.height = 640;
        }
        if (ad.width == 0) {
            ad.width = 360;
        }
        if (ad.maxAdDuration == 0) {
            ad.maxAdDuration = 20;
        }
        if (ad.tcfdStatus == 0)
            ad.tcfdStatus = -1;
        return ad;
    }

    private void updateTagsForVmap(List<String> hwAdXMLs, List<GoogleAds> ads, GoogleAds vmapAdTagUrlInfo) {

        for (int i = 0; i < ads.size(); i++) {
            /**
             * if there is no ad xml, use google ad
             */
            if (hwAdXMLs.get(i).equals("")) {
                GoogleAds googleBackupAd = ads.get(i);
                hwAdXMLs.remove(i);
                hwAdXMLs.add(i, TagRequestHandler.getResponseXML(googleBackupAd));
            }
        }

        String vXML = VastXMLCreator.setVastToVmap(hwAdXMLs, ads);
        Uri uri = Uri.parse("data:" + mimeType + ";base64," + Base64.encodeToString(vXML.getBytes(), Base64.NO_WRAP));

        if (uri != null)
            listener.onSuccess(uri);
        else {

            listener.onSuccess(googleAdTag);
        }
    }

}
