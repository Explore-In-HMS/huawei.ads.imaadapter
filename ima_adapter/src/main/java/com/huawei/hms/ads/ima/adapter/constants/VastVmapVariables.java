package com.huawei.hms.ads.ima.adapter.constants;

public class VastVmapVariables {

    private VastVmapVariables() {
    }

    public static final String hwVast =
            "<VAST xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"3.0\">" +
                    "<Ad id=\"\"> <InLine> <AdSystem></AdSystem> <AdTitle></AdTitle> " +
                    "<Description></Description> " +
                    "<Error></Error> " +
                    "<Impression></Impression> " +
                    "<Creatives> " +
                    "<Creative> " +
                    "<Linear skipoffset=\"\"> " +
                    "<Duration></Duration> " +
                    "<TrackingEvents> " +
                    "</TrackingEvents> " +
                    "<VideoClicks> " +
                    "<ClickThrough></ClickThrough> " +
                    "<ClickTracking></ClickTracking> " +
                    "</VideoClicks> " +
                    "<MediaFiles> " +
                    "<MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"1280\" height=\"720\" type=\"video/mp4\" bitrate=\"450\" scalable=\"true\" maintainAspectRatio=\"true\"></MediaFile> " +
                    "</MediaFiles> " +
                    "<Icons> " +
                    "<Icon program=\"AdChoices\" height=\"16\" width=\"16\" xPosition=\"right\" yPosition=\"top\"> " +
                    "<StaticResource creativeType=\"image/png\"></StaticResource> " +
                    "<IconClicks> <IconClickThrough></IconClickThrough> </IconClicks> </Icon> " +
                    "</Icons> " +
                    "</Linear> " +
                    "</Creative> " +
                    "<Creative id=\"\" sequence=\"\"> " +
                    "<CompanionAds> " +
                    "<Companion id=\"\" width=\"\" height=\"\"> " +
                    "<StaticResource creativeType=\"image/png\"></StaticResource> " +
                    "<TrackingEvents> <Tracking event=\"creativeView\"></Tracking> </TrackingEvents> " +
                    "<CompanionClickThrough> </CompanionClickThrough> </Companion>" +
                    " </CompanionAds> </Creative> " +
                    "</Creatives> <Extensions> " +
                    "<Extension type=\"waterfall\" fallback_index=\"0\"/> <Extension type=\"geo\"> " +
                    "<Country></Country> " +
                    "<Bandwidth></Bandwidth> " +
                    "<BandwidthKbps></BandwidthKbps> " +
                    "</Extension> " +
                    "<Extension type=\"metrics\"> " +
                    "<FeEventId></FeEventId> " +
                    "<AdEventId></AdEventId> </Extension> " +
                    "<Extension type=\"ShowAdTracking\"> " +
                    "<CustomTracking> <Tracking event=\"show_ad\"> " +
                    "</Tracking> </CustomTracking> </Extension> " +
                    "<Extension type=\"video_ad_loaded\"> " +
                    "<CustomTracking> <Tracking event=\"loaded\"> " +
                    "</Tracking> </CustomTracking> </Extension> " +
                    "</Extensions> " +
                    "</InLine> </Ad>" +
                    " </VAST>";

    public static final String hwVmap =
            "<vmap:VMAP xmlns:vmap=\"http://www.iab.net/videosuite/vmap\" version=\"1.0\">" +
                    "<vmap:AdBreak timeOffset=\"start\" breakType=\"linear\" breakId=\"preroll\">" +
                    "<vmap:AdSource id=\"preroll-ad-1\" allowMultipleAds=\"false\" followRedirects=\"true\">" +
                    "<vmap:VASTAdData>" +
                    hwVast +
                    "</vmap:VASTAdData>" +
                    "</vmap:AdSource>" +
                    "</vmap:AdBreak>" +
                    "</vmap:VMAP>";

    public static final String hwVmapDynamic =
            "<vmap:VMAP version=\"1.0\">" +
                    "</vmap:VMAP>";

    public static final String hwVmapDynamicElement =
            "<vmap:AdBreak timeOffset=\"\" breakType=\"\" breakId=\"\">" +
                    "<vmap:AdSource id=\"\" allowMultipleAds=\"\" followRedirects=\"\">" +
                    "<vmap:VASTAdData>" +
                    "</vmap:VASTAdData>" +
                    "</vmap:AdSource>" +
                    "</vmap:AdBreak>";

    public static final String ad = "Ad";
    public static final String error = "Error";
    public static final String impression = "Impression";
    public static final String linear = "Linear";
    public static final String duration = "Duration";
    public static final String tracking = "Tracking";
    public static final String trackingEvents = "TrackingEvents";
    public static final String clickThrough = "ClickThrough";
    public static final String clickTracking = "ClickTracking";
    public static final String mediaFile = "MediaFile";
    public static final String icon = "Icon";
    public static final String staticResource = "StaticResource";
    public static final String iconClickThrough = "IconClickThrough";
    public static final String id = "id";
    public static final String skipOffset = "skipoffset";
    public static final String event = "event";
    public static final String delivery = "delivery";
    public static final String type = "type";
    public static final String width = "width";
    public static final String height = "height";
    public static final String xPosition = "xPosition";
    public static final String yPosition = "yPosition";
    public static final String breakId = "breakId";
    public static final String breakType = "breakType";
    public static final String timeOffset = "timeOffset";
    public static final String linearS = "linear";
    public static final String vmapAdBreak = "vmap:AdBreak";
    public static final String vmapAdTagURI = "vmap:AdTagURI";
    public static final String vmapVmap = "vmap:VMAP";
    public static final String vmapVastAdData = "vmap:VASTAdData";
}
