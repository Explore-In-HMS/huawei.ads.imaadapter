package com.huawei.hms.ads.ima.adapter.util;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.exoplayer2.extractor.ts.AdtsExtractor;
import com.huawei.hms.ads.ima.adapter.constants.AdTagURLVariables;
import com.huawei.hms.ads.ima.adapter.constants.VastVmapVariables;
import com.huawei.hms.ads.ima.adapter.model.GoogleAds;
import com.huawei.hms.ads.vast.domain.advertisement.Tracking;
import com.huawei.hms.ads.vast.player.model.LinearCreative;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class VastXMLCreator {

    public static String create(LinearCreative creative, boolean vast) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = null;
            ByteArrayInputStream input;

            if (vast) {
                input = new ByteArrayInputStream(VastVmapVariables.hw_vast.getBytes("UTF-8"));
            } else {
                input = new ByteArrayInputStream(VastVmapVariables.hw_vmap.getBytes("UTF-8"));
            }
            doc = builder.parse(input);

            if (doc != null) {
                // Ad > id
                doc.getElementsByTagName("Ad").item(0).getAttributes().getNamedItem("id").setTextContent(creative.id);

                //Error
                doc.getElementsByTagName("Error").item(0).setTextContent(creative.errorUrlList.get(0));

                //Impression
                doc.getElementsByTagName("Impression").item(0).setTextContent(creative.errorUrlList.get(0));

                //SkipOffSet
                doc.getElementsByTagName("Linear").item(0).getAttributes().getNamedItem("skipoffset").setTextContent(convertLongToTime(creative.skipDuration));

                //Duration
                doc.getElementsByTagName("Duration").item(0).setTextContent(convertLongToTime(creative.duration));

                //TrackingEvents
                for (int i = 0; i < creative.trackingEvents.size(); i++) {
                    Element node = doc.createElement("Tracking");
                    node.setAttribute("event", "");
                    doc.getElementsByTagName("TrackingEvents").item(0).appendChild(node);
                }
                int j = 0;
                NodeList trackingEvents = doc.getElementsByTagName("TrackingEvents").item(0).getChildNodes();
                for (Map.Entry<String, List<Tracking>> entry : creative.trackingEvents.entrySet()) {
                    boolean pass = true;
                    while (pass) {
                        if (trackingEvents.item(j) instanceof Element) {
                            NamedNodeMap trackingElement = (trackingEvents.item(j)).getAttributes();
                            Node nodeTrackingElement = trackingElement.getNamedItem("event");
                            nodeTrackingElement.setTextContent(entry.getKey());
                            (trackingEvents.item(j)).setTextContent(entry.getValue().get(0).url);
                            pass = false;
                        }
                        j++;
                    }
                }

                //ClickThrough
                doc.getElementsByTagName("ClickThrough").item(0).setTextContent(creative.clickThrough);

                //ClickTracking
                doc.getElementsByTagName("ClickTracking").item(0).setTextContent(creative.videoClickTrackingList.get(0).url);

                //MediaFile
                NamedNodeMap nodeMapMediaFile = doc.getElementsByTagName("MediaFile").item(0).getAttributes();
                nodeMapMediaFile.getNamedItem("delivery").setTextContent(creative.delivery);
                nodeMapMediaFile.getNamedItem("type").setTextContent(creative.type);
                nodeMapMediaFile.getNamedItem("width").setTextContent(String.valueOf(creative.width));
                nodeMapMediaFile.getNamedItem("height").setTextContent(String.valueOf(creative.height));
                doc.getElementsByTagName("MediaFile").item(0).setTextContent(creative.url);

                //Icon
                NamedNodeMap iconNamedNodeMap = doc.getElementsByTagName("Icon").item(0).getAttributes();
                iconNamedNodeMap.getNamedItem("width").setTextContent(String.valueOf(creative.icon.width / 2));
                iconNamedNodeMap.getNamedItem("height").setTextContent(String.valueOf(creative.icon.height / 2));
                iconNamedNodeMap.getNamedItem("xPosition").setTextContent(String.valueOf(creative.icon.xPosition).equals("-1") ? "top" : String.valueOf(creative.icon.xPosition));
                iconNamedNodeMap.getNamedItem("yPosition").setTextContent(String.valueOf(creative.icon.yPosition).equals("-1") ? "left" : String.valueOf(creative.icon.yPosition));
                iconNamedNodeMap.getNamedItem("yPosition").setTextContent(String.valueOf(creative.icon.yPosition));
                doc.getElementsByTagName("StaticResource").item(0).setTextContent(creative.icon.imgUrl);
                doc.getElementsByTagName("IconClickThrough").item(0).setTextContent(creative.icon.clickThrough);
            }


            return convertDocumentToString(doc);
        } catch (Exception e) {
            Log.e("HwImagePluginLatest", e.toString());
        }

        return "";
    }

    private static String convertDocumentToString(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (Exception e) {
            return "";
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static String convertLongToTime(Long time) {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    public static List<GoogleAds> getGoogleAdsFromVMAP(GoogleAds ads) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URL url = new URL(ads.adUri.toString());

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(url.openConnection().getInputStream(), "UTF_8");

            // Returns the type of current event: START_TAG, END_TAG, etc..
            int eventType = xpp.getEventType();

            List<GoogleAds> result = new ArrayList<>();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    GoogleAds resultAd = new GoogleAds();
                    if (xpp.getName().equalsIgnoreCase("vmap:AdBreak")) {
                        if (xpp.getAttributeValue(null, "breakId") != null) {
                            resultAd.adPosition = xpp.getAttributeValue(null, "breakId");
                        }
                        if (xpp.getAttributeValue(null, "timeOffset") != null) {
                            resultAd.offset = xpp.getAttributeValue(null, "timeOffset");
                        }

                        result.add(resultAd);
                    }
                    if (xpp.getName().equalsIgnoreCase("vmap:AdTagURI")) {
                        Uri adTagUri = Uri.parse(xpp.nextText());
                        result.get(result.size() - 1).adUri = adTagUri;

                        Set<String> args = adTagUri.getQueryParameterNames();

                        if (args.contains(AdTagURLVariables.output)) {
                            result.get(result.size() - 1).xmlOutputType = adTagUri.getQueryParameter(AdTagURLVariables.output);
                        }
                        if (args.contains(AdTagURLVariables.vAdType)) {
                            result.get(result.size() - 1).adType = adTagUri.getQueryParameter(AdTagURLVariables.vAdType);
                        }
                        if (args.contains(AdTagURLVariables.maxAdDuration)) {
                            result.get(result.size() - 1).maxAdDuration = Integer.parseInt(adTagUri.getQueryParameter(AdTagURLVariables.maxAdDuration));
                        }
                        if (args.contains(AdTagURLVariables.minAdDuration)) {
                            result.get(result.size() - 1).minAdDuration = Integer.parseInt(adTagUri.getQueryParameter(AdTagURLVariables.minAdDuration));
                        }
                        if (args.contains(AdTagURLVariables.size)) {
                            try {
                                String[] whAdSlot = adTagUri.getQueryParameter(AdTagURLVariables.size).split("x");
                                result.get(result.size() - 1).width = Integer.parseInt(whAdSlot[0]);
                                result.get(result.size() - 1).height = Integer.parseInt(whAdSlot[1]);
                            } catch (Exception e) {
                                Log.e("HwImaPlugin", "Ad slot size parse failed" + e.toString());
                            }
                        }
                        if (args.contains(AdTagURLVariables.npa)){
                            ads.npaStatus = Integer.parseInt(adTagUri.getQueryParameter(AdTagURLVariables.npa));
                        }
                        if (args.contains(AdTagURLVariables.tfcd)){
                            ads.tcfdStatus = Integer.parseInt(adTagUri.getQueryParameter(AdTagURLVariables.tfcd));
                        }
                    }
                }

                eventType = xpp.next();
            }

            return result;
        } catch (Exception e) {
            Log.e("HwImaPluginLatest", e.toString());
        }
        return null;
    }

    public static String setVastToVmap(List<String> vastXMLs, List<GoogleAds> ads) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(VastVmapVariables.hw_vmap_dynamic.getBytes("UTF-8"));
            Document doc = builder.parse(input);

            if (doc != null) {
                //TrackingEvents
                for (int i = 0; i < vastXMLs.size(); i++) {
                    Document valueDoc = builder.parse(
                            new InputSource(new StringReader(VastVmapVariables.hw_vmap_dynamic_element)));
                    Node valueElement = doc.importNode(valueDoc.getDocumentElement(), true);
                    doc.getElementsByTagName("vmap:VMAP").item(0).appendChild(valueElement);

                    NamedNodeMap nodeMapMediaFile = doc.getElementsByTagName("vmap:AdBreak").item(i).getAttributes();
                    nodeMapMediaFile.getNamedItem("timeOffset").setTextContent(ads.get(i).offset);
                    nodeMapMediaFile.getNamedItem("breakType").setTextContent("linear");

                    Document value = builder.parse(
                            new InputSource(new StringReader(vastXMLs.get(i))));
                    Node nodeVast = doc.importNode(value.getDocumentElement(), true);
                    doc.getElementsByTagName("vmap:VASTAdData").item(i).appendChild(nodeVast);


                }
            }

            return convertDocumentToString(doc);

        } catch (Exception e) {
            Log.e("HwImaPluginLatest", e.toString());
        }

        return "";
    }
}
