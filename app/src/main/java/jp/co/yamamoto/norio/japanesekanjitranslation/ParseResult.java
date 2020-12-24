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
    private String  furigana;
    private String  roman;

    ParseResult(String result) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 2. DocumentBuilderのインスタンスを取得する
            DocumentBuilder builder = factory.newDocumentBuilder();
            // 3. DocumentBuilderにXMLを読み込ませ、Documentを作る
            InputStream bais = new ByteArrayInputStream(result.getBytes("utf-8"));
            Document document = builder.parse(bais);
            // 4. Documentから、ルート要素(BookList)を取得する
            Element ResultSet = document.getDocumentElement();
            // 5. BookList配下にある、Book要素を取得する
            NodeList Result = ResultSet.getChildNodes();

            NodeList WordList = Result.item(1).getChildNodes();
            NodeList Word = WordList.item(1).getChildNodes();

            surface = "";
            furigana = "";
            roman = "";

            int length = Word.getLength();
            for (int i = 0; i <length; i++){
                Node node = Word.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;

                Element elm = (Element)node;

                  //surface.add(elm.getElementsByTagName("Surface").item(0).getTextContent());

                String s = elm.getElementsByTagName("Surface").item(0).getTextContent();

                surface += s + " ";

                NodeList nodeList = elm.getElementsByTagName("Furigana");
                if (nodeList.item(0) != null){
                    furigana += nodeList.item(0).getTextContent()+ " ";
                }
                else{
                    furigana += s + " ";
                }

                nodeList = elm.getElementsByTagName("Roman");
                if (nodeList.item(0) != null){
                    roman += nodeList.item(0).getTextContent()+ " ";
                }
                else{
                    roman += s + " ";
                }

                //furigana += elm.getElementsByTagName("Furigana").item(0).getTextContent()+ " ";


                //roman += elm.getElementsByTagName("Roman").item(0).getTextContent()+ " ";

                  //furigana.add(elm.getElementsByTagName("Furigana").item(0).getTextContent());



                //roman.add(elm.getElementsByTagName("Roman").item(0).getTextContent());
            }


        } catch (Exception e) {
            String msg =  e.getMessage();
            Log.d("ASDFG", "ParseResult:e=" + msg);
            e.printStackTrace();

        }
    }

    String getSurface(){
        return surface;
    }

    String  getFurigana(){
        return furigana;
    }

    String getRoman(){
        return roman;
    }
}
