package com.huawei.hms.ads.ima.adapter.constants;

public class VastVmapVariables {

    public static String hw_vast =
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


    public static String hw_vmap =
            "<vmap:VMAP xmlns:vmap=\"http://www.iab.net/videosuite/vmap\" version=\"1.0\">" +
                    "<vmap:AdBreak timeOffset=\"start\" breakType=\"linear\" breakId=\"preroll\">" +
                    "<vmap:AdSource id=\"preroll-ad-1\" allowMultipleAds=\"false\" followRedirects=\"true\">" +
                    "<vmap:VASTAdData>" +
                    hw_vast +
                    "</vmap:VASTAdData>" +
                    "</vmap:AdSource>" +
                    "</vmap:AdBreak>" +
                    "</vmap:VMAP>";

    public static String hw_vmap_dynamic =
            "<vmap:VMAP version=\"1.0\">" +

                    "</vmap:VMAP>";

    public static String hw_vmap_dynamic_element =
            "<vmap:AdBreak timeOffset=\"\" breakType=\"\" breakId=\"\">" +
                    "<vmap:AdSource id=\"\" allowMultipleAds=\"\" followRedirects=\"\">" +
                    "<vmap:VASTAdData>" +
                    "</vmap:VASTAdData>" +
                    "</vmap:AdSource>" +
                    "</vmap:AdBreak>";
}
