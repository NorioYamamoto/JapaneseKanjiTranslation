package jp.co.yamamoto.norio.japanesekanjitranslation;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
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
        Log.d("ParseResult", "ParseResult:000");

        try {
            Log.d("ParseResult", "ParseResult:010 result=" + result);
            JSONObject json = new JSONObject(result);

            JSONObject item = json.getJSONObject("result");

            JSONArray datas = item.getJSONArray("word");

            furigana = "";
            roman = "";

            for (int i = 0; i < datas.length(); i++) {
                JSONObject data = datas.getJSONObject(i);

                String surface = data.getString("surface");
                String furigana = surface;
                String roman = surface;

                if (data.has("furigana")) {
                    furigana = data.getString("furigana");
                }
                if (data.has("roman")) {
                    roman = data.getString("roman");
                }

                this.furigana += furigana;
                this.roman += roman + " ";
            }

            Log.d("ParseResult", "ParseResult:100 furigana=" + this.furigana);

        } catch (Exception e) {
            String msg = e.getMessage();
            Log.d("ParseResult", "ParseResult:e=" + msg);
            e.printStackTrace();
        }

    }

    String getFurigana() {
        return this.furigana;
    }

    String getRoman() {
        return this.roman;
    }
}
