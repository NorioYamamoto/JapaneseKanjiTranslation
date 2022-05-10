package jp.co.yamamoto.norio.japanesekanjitranslation;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 非同期処理を行うクラス.
 */
public final class AsyncHttpRequest extends AsyncTask<URL, Void, String> {
    private int TODAY_FORCAST_INDEX = 0;
    private Activity mainActivity;

    enum RequestKind {
        TranslateHiragana,
        TranslateEnglish,
        AppLog
    }

    RequestKind requestKind;
    String text = null;

    public AsyncHttpRequest(Activity activity, String text) {
        this.requestKind = RequestKind.TranslateHiragana;
        this.text = text;

        Log.d("AsyncHttpRequest", "AsyncHttpRequest:000-1");
        // 呼び出し元のアクティビティ
        this.mainActivity = activity;
    }

    public AsyncHttpRequest(Activity activity, RequestKind requestKind) {
        this.requestKind = requestKind;

        Log.d("AsyncHttpRequest", "AsyncHttpRequest:000-2");
        // 呼び出し元のアクティビティ
        this.mainActivity = activity;
    }

    @Override
    protected String doInBackground(URL... urls) {
        Log.d("AsyncHttpRequest", "doInBackground:000");

        final URL url = urls[0];
        HttpURLConnection con = null;

        try {
            Log.d("AsyncHttpRequest", "doInBackground:010");
            con = (HttpURLConnection) url.openConnection();
            Log.d("AsyncHttpRequest", "doInBackground:011");

            if (requestKind == RequestKind.TranslateHiragana){
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.addRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("User-Agent", "Yahoo AppID: dj00aiZpPXVVaVhaVnNraWhENyZzPWNvbnN1bWVyc2VjcmV0Jng9MjU-");
                Log.d("AsyncHttpRequest", "doInBackground:011-1");
                con.connect();
                Log.d("AsyncHttpRequest", "doInBackground:011-2");
                PrintStream ps = new PrintStream(con.getOutputStream());
                Log.d("AsyncHttpRequest", "doInBackground:011-3");
                String text = "私は貴方を愛しています";
                String text1 = this.text.replaceAll("\"", "\\\\\"");
                Log.d("AsyncHttpRequest", "doInBackground:011-4 text1=" + text1);
                String text2 = "\"" + text1 + "\"";

                // "\"q\": " + "\"私は貴方を愛しています\""+ "," +
                String json =
                        "{" +
                          "\"id\": \"1234-1\"," +
                          "\"jsonrpc\": \"2.0\"," +
                          "\"method\": \"jlp.furiganaservice.furigana\"," +
                          "\"params\": {" +
                            "\"q\": " +  text2 + "," +
                            "\"grade\": 1" +
                          "}" +
                        "}" ;

                Log.d("AsyncHttpRequest", "doInBackground:011-3 json=" + json);
                ps.print(json);
                ps.close();
                Log.d("AsyncHttpRequest", "doInBackground:011-9");
            }
            else{
                con.setRequestMethod("GET");
                con.connect();
            }

            Log.d("AsyncHttpRequest", "doInBackground:012");
            // リダイレクトを自動で許可しない設定
//            con.setInstanceFollowRedirects(false);
           // con.connect();
            Log.d("AsyncHttpRequest", "doInBackground:013");

            final int statusCode = con.getResponseCode();
            Log.d("AsyncHttpRequest", "doInBackground:014 statusCode:" + statusCode);

            if (statusCode != HttpURLConnection.HTTP_OK) {
                Log.d("AsyncHttpRequest", "正常に接続できていません。statusCode:" + statusCode);
                return null;
            }

            Log.d("AsyncHttpRequest", "doInBackground:030");
            // レスポンス(JSON文字列)を読み込む準備
            final InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            Log.d("AsyncHttpRequest", "doInBackground:035");
            if (null == encoding) {
                Log.d("AsyncHttpRequest", "doInBackground:040");
                encoding = "UTF-8";
            }
            Log.d("AsyncHttpRequest", "doInBackground:050");
            final InputStreamReader inReader = new InputStreamReader(in, encoding);
            final BufferedReader bufReader = new BufferedReader(inReader);
            StringBuilder response = new StringBuilder();
            String line = null;
            // 1行ずつ読み込む
            while ((line = bufReader.readLine()) != null) {
                Log.d("AsyncHttpRequest", "doInBackground:060 line=" + line);
                response.append(line);
            }
            bufReader.close();
            inReader.close();
            in.close();

            Log.d("AsyncHttpRequest", "doInBackground:070=" + response.toString());

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("AsyncHttpRequest", "doInBackground:910");
            return null;
        } finally {
            if (con != null) {
                con.disconnect();

                Log.d("AsyncHttpRequest", "doInBackground:930");
            }
        }
    }

    /**
     * 非同期処理が終わった後の処理.
     *
     * @param result 非同期処理の結果得られる文字列
     */
    @Override
    protected void onPostExecute(String result) {
        Log.d("AsyncHttpRequest", "onPostExecute:000 result=" + result);

        switch (requestKind) {
            case TranslateHiragana:
                ParseResult parseResult = new ParseResult(result);
                Log.d("AsyncHttpRequest", "doInBackground:072=" + parseResult.getFurigana());
                TextView textView_hiragana = mainActivity.findViewById(R.id.textView_hiragana);
                textView_hiragana.setText(parseResult.getFurigana());
                break;
            case TranslateEnglish:
                TextView textView_trans = mainActivity.findViewById(R.id.textView_trans);
                textView_trans.setText(result);
                break;
        }
    }
}
