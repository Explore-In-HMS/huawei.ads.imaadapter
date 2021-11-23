package com.huawei.hms.ads.ima.adapter.model;

import android.net.Uri;

public class GoogleAds {

    public Uri getAdUri() {
        return adUri;
    }

    public void setAdUri(Uri adUri) {
        this.adUri = adUri;
    }

    public Uri getContentUri() {
        return contentUri;
    }

    public void setContentUri(Uri contentUri) {
        this.contentUri = contentUri;
    }

    public String getAdPosition() {
        return adPosition;
    }

    public void setAdPosition(String adPosition) {
        this.adPosition = adPosition;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getXmlOutputType() {
        return xmlOutputType;
    }

    public void setXmlOutputType(String xmlOutputType) {
        this.xmlOutputType = xmlOutputType;
    }

    public int getMaxAdDuration() {
        return maxAdDuration;
    }

    public void setMaxAdDuration(int maxAdDuration) {
        this.maxAdDuration = maxAdDuration;
    }

    public int getMinAdDuration() {
        return minAdDuration;
    }

    public void setMinAdDuration(int minAdDuration) {
        this.minAdDuration = minAdDuration;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getNpaStatus() {
        return npaStatus;
    }

    public void setNpaStatus(int npaStatus) {
        this.npaStatus = npaStatus;
    }

    public int getTcfdStatus() {
        return tcfdStatus;
    }

    public void setTcfdStatus(int tcfdStatus) {
        this.tcfdStatus = tcfdStatus;
    }

    /**
     * Uri of Ad
     */
    private Uri adUri;

    /**
     * Uri of content
     */
    private Uri contentUri;

    /**
     * Reklam post-pre-mid
     */
    private String adPosition;

    /**
     * Linear-NonLinear
     */
    private String adType;

    /**
     * Vast-Vmap
     */
    private String xmlOutputType;

    /**
     * Maks duration of Ad
     */
    private int maxAdDuration;

    /**
     * Min duration of Ad
     */
    private int minAdDuration;

    /**
     * Min duration of Ad
     */
    private String offset;

    /**
     * Size of master video ad slot width
     */
    private int width;

    /**
     * Size of master video ad slot width
     */
    private int height;

    /**
     * The non-personalized ads parameter
     */
    private int npaStatus;

    /**
     * The child-directed parameter
     */
    private int tcfdStatus;
}
