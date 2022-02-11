package com.brotherhood.o2o.util;

import android.content.Context;
import android.text.TextUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by laimo.li on 2016/1/12.
 */
public class LocationPickerUtil {

    public static final String PROVINCES_XML = "province.xml";
    public static final String CITY_XML = "city.xml";

    private static List<String> provinces;
    private static List<List<String>> citys;

    public static List<String> getProvinces(Context context) {
        if (provinces == null) {
            InputStream is = null;
            try {
                is = context.getAssets().open(PROVINCES_XML);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (is != null) {
                provinces = getProvinces(is);
            }
            return provinces;
        }
        return provinces;
    }


    public static List<List<String>> getCitys(Context context) {
        if (citys == null) {
            InputStream is = null;
            try {
                is = context.getAssets().open(CITY_XML);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (is != null) {
                citys = getCitys(is);
            }
            return citys;
        }
        return citys;
    }


    private static List<String> getProvinces(InputStream stream) {
        List<String> provinces = new ArrayList<String>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(stream);
            Element root = document.getDocumentElement();
            NodeList items = root.getElementsByTagName("province");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                String pName = item.getAttribute("name");
                if (!TextUtils.isEmpty(pName)) {
                    provinces.add(pName);
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return provinces;
    }


    private static List<List<String>> getCitys(InputStream stream) {
        List<List<String>> cityList = new ArrayList<List<String>>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(stream);
            Element root = document.getDocumentElement();
            NodeList items = root.getElementsByTagName("province");
            for (int i = 0; i < items.getLength(); i++) {
                List<String> list = new ArrayList<String>();
                Element item = (Element) items.item(i);
                NodeList citys = item.getElementsByTagName("city");
                for (int j = 0; j < citys.getLength(); j++) {
                    Element cityItem = (Element) citys.item(j);
                    String name = cityItem.getAttribute("name");
                    if (!TextUtils.isEmpty(name)) {
                        list.add(name);
                    }
                }
                cityList.add(list);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityList;
    }

}
