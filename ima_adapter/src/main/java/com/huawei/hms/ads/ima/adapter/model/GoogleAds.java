package com.huawei.hms.ads.ima.adapter.model;

import android.net.Uri;

public class GoogleAds {

    /**
     * Uri of Ad
     */
    public Uri adUri;

    /**
     * Uri of content
     */
    public Uri contentUri;

    /**
     * Reklam post-pre-mid
     */
    public String adPosition;

    /**
     * Linear-NonLinear
     */
    public String adType;

    /**
     * Vast-Vmap
     */
    public String xmlOutputType;

    /**
     * Maks duration of Ad
     */
    public int maxAdDuration;

    /**
     * Min duration of Ad
     */
    public int minAdDuration;

    /**
     * Min duration of Ad
     */
    public String offset;

    /**
     * Size of master video ad slot width
     */
    public int width;

    /**
     * Size of master video ad slot width
     */
    public int height;

    /**
     * The non-personalized ads parameter
     */
    public int npaStatus;

    /**
     * The child-directed parameter
     */
    public int tcfdStatus;
}
