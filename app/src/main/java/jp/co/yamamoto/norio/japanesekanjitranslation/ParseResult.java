package jp.co.yamamoto.norio.japanesekanjitranslation;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ParseResult {

    private String surface;
    private String furigana;
    private String roman;

    ParseResult(String result) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream bais = new ByteArrayInputStream(result.getBytes("utf-8"));
            Document document = builder.parse(bais);
            Element ResultSet = document.getDocumentElement();
            NodeList Result = ResultSet.getChildNodes();

            NodeList WordList = Result.item(1).getChildNodes();
            NodeList Word = WordList.item(1).getChildNodes();

            surface = "";
            furigana = "";
            roman = "";

            int length = Word.getLength();
            for (int i = 0; i < length; i++) {
                Node node = Word.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;

                Element elm = (Element) node;

                String s = elm.getElementsByTagName("Surface").item(0).getTextContent();

                surface += s + " ";

                NodeList nodeList = elm.getElementsByTagName("Furigana");
                if (nodeList.item(0) != null) {
                    furigana += nodeList.item(0).getTextContent() + " ";
                } else {
                    furigana += s + " ";
                }

                nodeList = elm.getElementsByTagName("Roman");
                if (nodeList.item(0) != null) {
                    roman += nodeList.item(0).getTextContent() + " ";
                } else {
                    roman += s + " ";
                }
            }

        } catch (Exception e) {
            String msg = e.getMessage();
            Log.d("ParseResult", "ParseResult:e=" + msg);
            e.printStackTrace();
        }
    }

    String getSurface() {
        return surface;
    }

    String getFurigana() {
        return furigana;
    }

    String getRoman() {
        return roman;
    }
}
