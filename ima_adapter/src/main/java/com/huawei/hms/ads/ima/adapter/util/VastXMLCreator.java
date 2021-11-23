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

import static com.huawei.hms.ads.ima.adapter.constants.AdTagURLVariables.utfStandard;

public class VastXMLCreator {

    private VastXMLCreator() {
    }

    private static final String TAG = "HwImaAdapter";

    public static String create(LinearCreative creative, boolean vast) {

        try {
            Document doc = null;
            ByteArrayInputStream input;

            if (vast) {
                input = new ByteArrayInputStream(VastVmapVariables.hwVast.getBytes(utfStandard));
            } else {
                input = new ByteArrayInputStream(VastVmapVariables.hwVmap.getBytes(utfStandard));
            }
            DocumentBuilder builder = createDocumentBuilder();
            if (builder != null) {
                doc = builder.parse(input);

                if (doc != null) {
                    // Ad > id
                    doc.getElementsByTagName(VastVmapVariables.ad).item(0).getAttributes().getNamedItem(VastVmapVariables.id).setTextContent(creative.id);
                    //Error
                    doc.getElementsByTagName(VastVmapVariables.error).item(0).setTextContent(creative.errorUrlList.get(0));
                    //Impression
                    doc.getElementsByTagName(VastVmapVariables.impression).item(0).setTextContent(creative.errorUrlList.get(0));
                    //SkipOffSet
                    doc.getElementsByTagName(VastVmapVariables.linear).item(0).getAttributes().getNamedItem(VastVmapVariables.skipOffset).setTextContent(convertLongToTime(creative.skipDuration));
                    //Duration
                    doc.getElementsByTagName(VastVmapVariables.duration).item(0).setTextContent(convertLongToTime(creative.duration));
                    //TrackingEvents
                    for (int i = 0; i < creative.trackingEvents.size(); i++) {
                        Element node = doc.createElement(VastVmapVariables.tracking);
                        node.setAttribute(VastVmapVariables.event, "");
                        doc.getElementsByTagName(VastVmapVariables.trackingEvents).item(0).appendChild(node);
                    }
                    int j = 0;
                    NodeList trackingEvents = doc.getElementsByTagName(VastVmapVariables.trackingEvents).item(0).getChildNodes();
                    for (Map.Entry<String, List<Tracking>> entry : creative.trackingEvents.entrySet()) {
                        boolean pass = true;
                        while (pass) {
                            if (trackingEvents.item(j) instanceof Element) {
                                NamedNodeMap trackingElement = (trackingEvents.item(j)).getAttributes();
                                Node nodeTrackingElement = trackingElement.getNamedItem(VastVmapVariables.event);
                                nodeTrackingElement.setTextContent(entry.getKey());
                                (trackingEvents.item(j)).setTextContent(entry.getValue().get(0).url);
                                pass = false;
                            }
                            j++;
                        }
                    }

                    //ClickThrough
                    doc.getElementsByTagName(VastVmapVariables.clickThrough).item(0).setTextContent(creative.clickThrough);
                    //ClickTracking
                    doc.getElementsByTagName(VastVmapVariables.clickTracking).item(0).setTextContent(creative.videoClickTrackingList.get(0).url);
                    //MediaFile
                    NamedNodeMap nodeMapMediaFile = doc.getElementsByTagName(VastVmapVariables.mediaFile).item(0).getAttributes();
                    nodeMapMediaFile.getNamedItem(VastVmapVariables.delivery).setTextContent(creative.delivery);
                    nodeMapMediaFile.getNamedItem(VastVmapVariables.type).setTextContent(creative.type);
                    nodeMapMediaFile.getNamedItem(VastVmapVariables.width).setTextContent(String.valueOf(creative.width));
                    nodeMapMediaFile.getNamedItem(VastVmapVariables.height).setTextContent(String.valueOf(creative.height));
                    doc.getElementsByTagName(VastVmapVariables.mediaFile).item(0).setTextContent(creative.url);
                    //Icon
                    NamedNodeMap iconNamedNodeMap = doc.getElementsByTagName(VastVmapVariables.icon).item(0).getAttributes();
                    iconNamedNodeMap.getNamedItem(VastVmapVariables.width).setTextContent(String.valueOf(creative.icon.width / 2));
                    iconNamedNodeMap.getNamedItem(VastVmapVariables.height).setTextContent(String.valueOf(creative.icon.height / 2));
                    iconNamedNodeMap.getNamedItem(VastVmapVariables.xPosition).setTextContent(String.valueOf(creative.icon.xPosition).equals("-1") ? "top" : String.valueOf(creative.icon.xPosition));
                    iconNamedNodeMap.getNamedItem(VastVmapVariables.yPosition).setTextContent(String.valueOf(creative.icon.yPosition).equals("-1") ? "left" : String.valueOf(creative.icon.yPosition));
                    doc.getElementsByTagName(VastVmapVariables.staticResource).item(0).setTextContent(creative.icon.imgUrl);
                    doc.getElementsByTagName(VastVmapVariables.iconClickThrough).item(0).setTextContent(creative.icon.clickThrough);
                }
            }

            return convertDocumentToString(doc);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return "";
    }

    private static DocumentBuilder createDocumentBuilder() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            return factory.newDocumentBuilder();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
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

            URL url = new URL(ads.getAdUri().toString());

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(url.openConnection().getInputStream(), utfStandard);

            // Returns the type of current event: START_TAG, END_TAG, etc..
            int eventType = xpp.getEventType();

            List<GoogleAds> result = new ArrayList<>();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    GoogleAds resultAd = new GoogleAds();
                    if (xpp.getName().equalsIgnoreCase(VastVmapVariables.vmapAdBreak)) {
                        if (xpp.getAttributeValue(null, VastVmapVariables.breakId) != null) {
                            resultAd.setAdPosition(xpp.getAttributeValue(null, VastVmapVariables.breakId));
                        }
                        if (xpp.getAttributeValue(null, VastVmapVariables.timeOffset) != null) {
                            resultAd.setOffset(xpp.getAttributeValue(null, VastVmapVariables.timeOffset));
                        }

                        result.add(resultAd);
                    }
                    if (xpp.getName().equalsIgnoreCase(VastVmapVariables.vmapAdTagURI)) {
                        Uri adTagUri = Uri.parse(xpp.nextText());
                        result.get(result.size() - 1).setAdUri(adTagUri);

                        Set<String> args = adTagUri.getQueryParameterNames();

                        if (args.contains(AdTagURLVariables.output)) {
                            result.get(result.size() - 1).setXmlOutputType(adTagUri.getQueryParameter(AdTagURLVariables.output));
                        }
                        if (args.contains(AdTagURLVariables.vAdType)) {
                            result.get(result.size() - 1).setAdType(adTagUri.getQueryParameter(AdTagURLVariables.vAdType));
                        }
                        if (args.contains(AdTagURLVariables.maxAdDuration)) {
                            result.get(result.size() - 1).setMaxAdDuration(Integer.parseInt(adTagUri.getQueryParameter(AdTagURLVariables.maxAdDuration)));
                        }
                        if (args.contains(AdTagURLVariables.minAdDuration)) {
                            result.get(result.size() - 1).setMinAdDuration(Integer.parseInt(adTagUri.getQueryParameter(AdTagURLVariables.minAdDuration)));
                        }
                        if (args.contains(AdTagURLVariables.size)) {
                            try {
                                String[] whAdSlot = adTagUri.getQueryParameter(AdTagURLVariables.size).split("x");
                                result.get(result.size() - 1).setWidth(Integer.parseInt(whAdSlot[0]));
                                result.get(result.size() - 1).setHeight(Integer.parseInt(whAdSlot[1]));
                            } catch (Exception e) {
                                Log.e(TAG, "Ad slot size parse failed" + e.toString());
                            }
                        }
                        if (args.contains(AdTagURLVariables.npa)) {
                            ads.setNpaStatus(Integer.parseInt(adTagUri.getQueryParameter(AdTagURLVariables.npa)));
                        }
                        if (args.contains(AdTagURLVariables.tfcd)) {
                            ads.setTcfdStatus(Integer.parseInt(adTagUri.getQueryParameter(AdTagURLVariables.tfcd)));
                        }
                    }
                }

                eventType = xpp.next();
            }

            return result;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    public static String setVastToVmap(List<String> vastXMLs, List<GoogleAds> ads) {
        try {
            Document doc = null;
            DocumentBuilder builder = createDocumentBuilder();
            if (builder != null) {
                ByteArrayInputStream input = new ByteArrayInputStream(VastVmapVariables.hwVmapDynamic.getBytes(utfStandard));
                doc = builder.parse(input);
                if (doc != null) {
                    //TrackingEvents
                    for (int i = 0; i < vastXMLs.size(); i++) {
                        Document valueDoc = builder.parse(
                                new InputSource(new StringReader(VastVmapVariables.hwVmapDynamicElement)));
                        Node valueElement = doc.importNode(valueDoc.getDocumentElement(), true);
                        doc.getElementsByTagName(VastVmapVariables.vmapVmap).item(0).appendChild(valueElement);

                        NamedNodeMap nodeMapMediaFile = doc.getElementsByTagName(VastVmapVariables.vmapAdBreak).item(i).getAttributes();
                        nodeMapMediaFile.getNamedItem(VastVmapVariables.timeOffset).setTextContent(ads.get(i).getOffset());
                        nodeMapMediaFile.getNamedItem(VastVmapVariables.breakType).setTextContent(VastVmapVariables.linearS);

                        Document value = builder.parse(
                                new InputSource(new StringReader(vastXMLs.get(i))));
                        Node nodeVast = doc.importNode(value.getDocumentElement(), true);
                        doc.getElementsByTagName(VastVmapVariables.vmapVastAdData).item(i).appendChild(nodeVast);


                    }
                }
            }

            return convertDocumentToString(doc);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return "";
    }
}
