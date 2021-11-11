package com.huawei.hms.ads.ima.adapter;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.huawei.hms.ads.ima.adapter.constants.AdTagURLVariables;
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
import java.util.Map;
import java.util.Set;

public class HwImaAdapter {

    private final static String TAG = "HwImaPluginLatest";

    private static HwImaAdapter instance;

    private SimpleExoPlayer player;
    private String adUnitID;
    private LinearAdSlot mLinearAdSlot;
    private final List<GoogleAds> googleAds = new ArrayList<>();
    private final List<MediaItem> hwMediaItems = new ArrayList<>();
    private final Multimap<Uri, Uri> contentAdTagUris = ArrayListMultimap.create();

    public static void init(@NonNull PlayerView playerView, @NonNull String hwAdUnitId) {
        if (playerView.getPlayer() instanceof SimpleExoPlayer)
            init((SimpleExoPlayer) playerView.getPlayer(), hwAdUnitId);
    }

    public static void init(@NonNull SimpleExoPlayer player, @NonNull String hwAdUnitId) {
        if (instance == null)
            instance = new HwImaAdapter();

        instance.player = player;
        instance.adUnitID = hwAdUnitId;

        instance.clearInstance();
        instance.analyseMediaItems();
        instance.analyseGoogleAds();

        instance.player.clearMediaItems();
        instance.requestVastSDK();
    }

    private void clearInstance() {
        mLinearAdSlot = null;
        googleAds.clear();
        hwMediaItems.clear();
        contentAdTagUris.clear();
    }

    private void analyseMediaItems() {

        for (int i = 0; i < player.getMediaItemCount(); i++) {
            MediaItem item = player.getMediaItemAt(i);
            if (item.playbackProperties != null && item.playbackProperties.adsConfiguration != null) {
                contentAdTagUris.put(item.playbackProperties.uri, item.playbackProperties.adsConfiguration.adTagUri);
            }
        }
    }

    private void analyseGoogleAds() {
        try {
            for (Map.Entry<Uri, Uri> entry : contentAdTagUris.entries()) {

                GoogleAds ads = new GoogleAds();

                Uri adTagUri = entry.getValue();
                Set<String> args = adTagUri.getQueryParameterNames();

                ads.adUri = adTagUri;
                ads.contentUri = entry.getKey();

                if (args.contains(AdTagURLVariables.output)) {
                    ads.xmlOutputType = adTagUri.getQueryParameter(AdTagURLVariables.output);
                }
                if (args.contains(AdTagURLVariables.vPos)) {
                    ads.adPosition = adTagUri.getQueryParameter(AdTagURLVariables.vPos);
                }
                if (args.contains(AdTagURLVariables.vAdType)) {
                    ads.adType = adTagUri.getQueryParameter(AdTagURLVariables.vAdType);
                }
                if (args.contains(AdTagURLVariables.maxAdDuration)) {
                    ads.maxAdDuration = Integer.parseInt(adTagUri.getQueryParameter(AdTagURLVariables.maxAdDuration));
                }
                if (args.contains(AdTagURLVariables.minAdDuration)) {
                    ads.minAdDuration = Integer.parseInt(adTagUri.getQueryParameter(AdTagURLVariables.minAdDuration));
                }
                if (args.contains(AdTagURLVariables.size)) {
                    try {
                        String[] whAdSlot = adTagUri.getQueryParameter(AdTagURLVariables.size).split("x");
                        ads.width = Integer.parseInt(whAdSlot[0]);
                        ads.height = Integer.parseInt(whAdSlot[1]);
                    } catch (Exception e) {
                        Log.e(TAG, "Ad URL: Slot size parse failed");
                    }
                }
                if (args.contains(AdTagURLVariables.npa)) {
                    ads.npaStatus = Integer.parseInt(adTagUri.getQueryParameter(AdTagURLVariables.npa));
                }
                if (args.contains(AdTagURLVariables.tfcd)) {
                    ads.tcfdStatus = Integer.parseInt(adTagUri.getQueryParameter(AdTagURLVariables.tfcd));
                }

                googleAds.add(ads);
            }

        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }

    private void requestVastSDK() {

        for (GoogleAds ads : googleAds) {
            if (ads.xmlOutputType.equals(AdTagURLVariables.vast)) {
                configureSDKRequest(ads);
                makeRequestForVast(ads);
            } else if (ads.xmlOutputType.equals(AdTagURLVariables.vmap)) {
                List<GoogleAds> vmapAds = VastXMLCreator.getGoogleAdsFromVMAP(ads);

                if (vmapAds != null) {
                    makeRequestForVmap(vmapAds, ads);
                }
            }
        }
    }

    private void makeRequestForVast(GoogleAds ad) {

        VastAdPlayer.getInstance().loadLinearAd(mLinearAdSlot, new RequestCallback() {
            @Override
            public void onAdsLoadedSuccess(AdsData adsData) {

                if (adsData != null && adsData.linearCreations != null && adsData.linearCreations.size() != 0) {
                    List<LinearCreative> linearAds = adsData.linearCreations;
                    List<Uri> hwAdsUris = new ArrayList<>();
                    TagRequestHandler handler = new TagRequestHandler();

                    for (LinearCreative creative : linearAds) {

                        if (creative.skipDuration != 0) {
                            //check google ad contain offset
                            if (!handler.checkGoogleHasOffset(ad)) {
                                creative.skipDuration = -1;
                            }
                        }

                        String vXML = VastXMLCreator.create(creative, false);
                        Uri uri = Util.getDataUriForString(MimeTypes.BASE_TYPE_TEXT, vXML); //XML has created in client side based on the VAST SDK's response
                        hwAdsUris.add(uri);
                    }

                    createMediaItemForVast(ad.contentUri, hwAdsUris.get(0));
                } else {
                    /**
                     * if there is no ad from vast sdk, set media item that contains google ad
                     */
                    createMediaItemForVast(ad.contentUri, ad.adUri);
                }
            }

            @Override
            public void onAdsLoadFailed() {
                /**
                 * if there is no ad from vast sdk, set media item that contains google ad
                 */
                createMediaItemForVast(ad.contentUri, ad.adUri);
            }
        });
    }

    private void createMediaItemForVast(Uri contentUri, Uri adUri) {
        MediaItem item = new MediaItem.Builder().setUri(contentUri).setAdTagUri(adUri).build();
        hwMediaItems.add(item);

        checkCallBackState();
    }

    private void makeRequestForVmap(List<GoogleAds> ads, GoogleAds vmapAdTagUrlInfo) {

        List<String> hwAdXMLs = new ArrayList<>();

        for (GoogleAds vmapAd : ads) {
            configureSDKRequest(vmapAd);

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

                        createMediaItemsForVmap(hwAdXMLs, ads, vmapAdTagUrlInfo);
                        checkCallBackState();
                    }
                }

                @Override
                public void onAdsLoadFailed() {
                    hwAdXMLs.add("");

                    if (hwAdXMLs.size() == ads.size()) {
                        createMediaItemsForVmap(hwAdXMLs, ads, vmapAdTagUrlInfo);
                        checkCallBackState();
                    }

                    Log.e("HwImaPluginLatest", "failed");
                }
            });
        }
    }

    private void createMediaItemsForVmap(List<String> hwAdXMLs, List<GoogleAds> ads, GoogleAds vmapAdTagUrlInfo) {

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
        Uri uri = Util.getDataUriForString(MimeTypes.BASE_TYPE_TEXT, vXML); //XML has created in client side based on the VAST SDK's response
        MediaItem mediaItem = new MediaItem.Builder().setUri(vmapAdTagUrlInfo.contentUri).setAdTagUri(uri).build();

        hwMediaItems.add(mediaItem);
    }

    private void checkCallBackState() {
        Log.i(TAG, "item size : " + hwMediaItems.size() + " google ad size : " + googleAds.size());

        if (hwMediaItems.size() == googleAds.size()) { //TODO check

            for (MediaItem item : hwMediaItems) {
                if (player.getMediaItemCount() == 0) {
                    player.setMediaItem(item);
                } else
                    player.addMediaItem(item);
            }

            player.prepare();
            player.setPlayWhenReady(true);
        }
    }

    private void configureSDKRequest(GoogleAds ad) {

        ad = checkParameterValues(ad);

        if (ad.maxAdDuration / 1000 > 1) {
            ad.maxAdDuration = ad.maxAdDuration / 1000;
        }

        Log.i(TAG, "height: " + ad.height);
        Log.i(TAG, "width: " + ad.width);
        Log.i(TAG, "maxAdDuration: " + ad.maxAdDuration);

        CreativeMatchStrategy creativeMatchStrategy =
                new CreativeMatchStrategy(CreativeMatchStrategy.CreativeMatchType.ANY);

        RequestOptions requestOptions = new RequestOptions.Builder().setRequestLocation(true)
                .setNonPersonalizedAd(ad.npaStatus)
                .setConsent("")
                .setAdContentClassification("A")
                .setTagForChildProtection(ad.tcfdStatus)
                .setTagForUnderAgeOfPromise(-1)
                .build();

        mLinearAdSlot = new LinearAdSlot();
        mLinearAdSlot.setSlotId(this.adUnitID);
        mLinearAdSlot.setTotalDuration(ad.maxAdDuration);
        mLinearAdSlot.setCreativeMatchStrategy(creativeMatchStrategy);
        mLinearAdSlot.setAllowMobileTraffic(true);
        mLinearAdSlot.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mLinearAdSlot.setRequestOptions(requestOptions);
        mLinearAdSlot.setSize(ad.width, ad.height);
    }

    private GoogleAds checkParameterValues(GoogleAds ad) {

        if (ad.height == 0)
            ad.height = 640;
        if (ad.width == 0)
            ad.width = 360;
        if (ad.maxAdDuration == 0)
            ad.maxAdDuration = 20;
        if (ad.tcfdStatus == 0)
            ad.tcfdStatus = -1;
        return ad;
    }
}
