package jp.co.yamamoto.norio.japanesekanjitranslation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.icu.text.Transliterator;

import com.ibm.icu.text.Transliterator;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;

    Spinner spinnerMenu;

    Button button_trans;
    Button button_clear;
    Button button_paste;
    Button button_setup;

    EditText editText_input;
    TextView textView_hiragana;
    TextView textView_trans;

    String targetLanguage = "en";

    int selectedPosition = 2;

    Context context = this;
    ClipboardManager clipboardManager = null;
    Options options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        context = this;
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        button_trans = findViewById(R.id.button_trans);
        button_clear = findViewById(R.id.button_clear);
        button_paste = findViewById(R.id.button_paste);
        button_setup = findViewById(R.id.button_setup);

        editText_input = findViewById(R.id.editText_input);

        textView_hiragana = findViewById(R.id.textView_hiragana);
        textView_trans = findViewById(R.id.textView_trans);

        options = (Options) getApplication();

        loadSharedPreferences();

        Locale locale;
        String language;
        locale = Locale.getDefault();
        language = locale.getLanguage();

        Log.d("MainActivity", language + "語で、" + locale + "の言葉です");

        final ArrayList<String> langNameList = new ArrayList();
        final ArrayList<String> langCodeList = new ArrayList();

        langNameList.add("العربية"); // アラビア語
        langCodeList.add("ar");

        langNameList.add("Deutsch"); // ドイツ語
        langCodeList.add("de");

        langNameList.add("English"); // 英語
        langCodeList.add("en");

        langNameList.add("Espanol"); // スペイン語
        langCodeList.add("es");

        langNameList.add("Français"); // フランス語
        langCodeList.add("fr");

        langNameList.add("italiano"); // イタリア語
        langCodeList.add("it");

        langNameList.add("한국"); // 韓国語
        langCodeList.add("ko");

        langNameList.add("русский"); // ロシア語
        langCodeList.add("ru");

        langNameList.add("简体中文"); // 中国語
        langCodeList.add("zh");

        spinnerMenu = findViewById(R.id.spinnerLanguage);
        ArrayAdapter<String> adapterMenu = new ArrayAdapter<>(this, R.layout.spinner_item, langNameList);

        adapterMenu.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMenu.setAdapter(adapterMenu);

        if (selectedPosition != -1) {
            spinnerMenu.setSelection(selectedPosition);
        }

        spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;

                int pos = spinner.getSelectedItemPosition();

                targetLanguage = langCodeList.get(pos);
                selectedPosition = pos;

                saveSharedPreferences();
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        button_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "button_trans:000");

                textView_hiragana.setText("");
                textView_trans.setText("");

                String kanji = editText_input.getText().toString();


                tranlateHiragana(kanji);


                tranlateEnglish(kanji);

                appLog("Options:" + options.isPaste() + ","+ options.isCopy() );

                Log.d("MainActivity", "button_trans:999");
            }
        });

        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "button_clear:000");

                editText_input.setText("");
                textView_hiragana.setText("");
                textView_trans.setText("");

                Log.d("MainActivity", "button_clear:999");
            }
        });

        button_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "button_paste:001");

                if (null == clipboardManager) return;

                ClipData cd = clipboardManager.getPrimaryClip();
                if (cd != null) {
                    ClipData.Item item = cd.getItemAt(0);
                    editText_input.setText(item.getText());
                }

                Log.d("MainActivity", "button_paste:999");
            }
        });

        button_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "button_setup:000");

                Intent intent = new Intent(getApplication(), SetupActivity.class);
                Log.d("MainActivity", "button_setup:10");
                startActivity(intent);

                Log.d("MainActivity", "button_setup:999");
            }
        });

        changeLayout();
    }

    class Katakana {
        Katakana(String katakana, boolean originally){
            this.katakana = katakana;
            this.originally = originally;
        }
        String katakana;
        boolean originally;
    }

    Tokenizer tokenizer = new Tokenizer();

    public void tranlateHiragana(String kanji) {

        if (options.isUseYahooApi()) {
            // 非同期処理(AsyncHttpRequest#doInBackground())を呼び出す
            try {
                String url = "https://jlp.yahooapis.jp/FuriganaService/V2/furigana";

                Log.d("MainActivity", "changeTextView:000 url=" + url);
                new AsyncHttpRequest(this, kanji).execute(new URL(url));
            } catch (MalformedURLException e) {
                appLog("Error tranlateHiragana");
                e.printStackTrace();
            }
        }
        else{
            List<Token> tokens = tokenizer.tokenize(kanji);
            //String katakana = "";
            List<Katakana> katakana = new ArrayList();

            for (Token token : tokens) {
                Log.d("Kuromoji", token.getSurface() + "\t" + token.getAllFeatures());
                String surface = token.getSurface();
                String reading = token.getReading();

                boolean originally = false;

                if (surface.matches(".*[\\u30A0-\\u30FF]+.*") ||
                    surface.matches(".*[\\uFF10-\\uFF19]+.*")){
                    originally = true;
                    reading = surface;
                }
                else if (reading.equals("*")) {
                    reading = surface;
                }
                katakana.add(new Katakana(reading, originally));
            }

            String hiragana = "";

            Transliterator transliterator;
            transliterator = Transliterator.getInstance("Katakana-Hiragana");

            for (Katakana k : katakana){
                if (k.originally){
                    hiragana += k.katakana;
                }
                else{
                    hiragana += transliterator.transliterate(k.katakana);
                }
            }
            textView_hiragana.setText(hiragana);
            /*
            for (int i=0; i < katakana.length; i++){
                hiragana += transliterator.transliterate(katakanaArray[i]);
                if (!((i + 1)  < katakanaArray.length)) break;
                hiragana += "ー";
            }
            textView_hiragana.setText(hiragana);

            */
        }
    }

    public void tranlateEnglish(String kanji) {
        // 非同期処理(AsyncHttpRequest#doInBackground())を呼び出す

        try {
            String url = "https://script.google.com/macros/s/AKfycbyIgrFqfMZcbCdcunVATUFq6yLC2yFXIRifkkRl7PzC6V38HEjG/exec?text=";

            url += kanji + "&source=ja&target=" + targetLanguage;

            Log.d("MainActivity", "tranlateEnglish:000 url=" + url);
            new AsyncHttpRequest(this, AsyncHttpRequest.RequestKind.TranslateEnglish).execute(new URL(url));
        } catch (MalformedURLException e) {
            appLog("Error tranlateEnglish");
            e.printStackTrace();
        }


    }

    public void appLog(String text) {
        // 非同期処理(AsyncHttpRequest#doInBackground())を呼び出す
        try {
//            String url = "https://script.google.com/macros/s/AKfycbwtkrfkvOjc54YlQOTlSf2gp5g1mI7okvUwWn2PnWC1ngGEyamAio4mB5Rklc4mJ1r0rg/exec?text=";
            String url = "https://script.google.com/macros/s/AKfycbyyLx_QCt704G-2_EKUxY1T3K5Mhw4uqxvIwiANX58diNua9GNYr_wTvpngFBNLDB7jcQ/exec?text=";
            url += text;
            new AsyncHttpRequest(this, AsyncHttpRequest.RequestKind.AppLog).execute(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    // 設定値を読み込む
    void loadSharedPreferences() {
        Log.d("MainActivity", "loadSharedPreferences:000");

        SharedPreferences data = getSharedPreferences("Data", MODE_PRIVATE);
        String settingValue = data.getString("SettingValue", null);

        if (settingValue != null) {
            String settingValues[] = settingValue.split("\t");
            Log.d("MainActivity", "Version=" + settingValues[0]);
            selectedPosition = Integer.parseInt(settingValues[1]);
            if (settingValues[0].equals("1.00")) {
                Log.d("MainActivity", "loadSharedPreferences:010");
                options.setPaste(false);
                options.setCopy(false);
                options.setUseYahooApi(false);
            }
            else if (settingValues[0].equals("2.00")) {
                Log.d("MainActivity", "loadSharedPreferences:020");
                options.setPaste(Boolean.valueOf(settingValues[2]));
                options.setCopy(Boolean.valueOf(settingValues[3]));
                options.setUseYahooApi(false);
            } else if (settingValues[0].equals("3.00")) {
                Log.d("MainActivity", "loadSharedPreferences:030");
                options.setPaste(Boolean.valueOf(settingValues[2]));
                options.setCopy(Boolean.valueOf(settingValues[3]));
                options.setUseYahooApi(false);
            } else if (settingValues[0].equals("4.00")) {
                Log.d("MainActivity", "loadSharedPreferences:040");
                options.setPaste(Boolean.valueOf(settingValues[2]));
                options.setCopy(Boolean.valueOf(settingValues[3]));
                options.setUseYahooApi(false);
            }else{
                Log.d("MainActivity", "loadSharedPreferences:050");
                options.setPaste(Boolean.valueOf(settingValues[2]));
                options.setCopy(Boolean.valueOf(settingValues[3]));
                options.setUseYahooApi(Boolean.valueOf(settingValues[4]));
            }
        }
        else{
            Log.d("MainActivity", "loadSharedPreferences:090");
            options.setPaste(false);
            options.setCopy(false);
            options.setUseYahooApi(false);
        }

        // Log.d("MainActivity", settingValue);
        Log.d("MainActivity", options.isPaste() + "," + options.isCopy() + "," + options.isUseYahooApi());

        Log.d("MainActivity", "loadSharedPreferences:999");
    }

    // 設定値を保存
    void saveSharedPreferences() {
        Log.d("MainActivity", "saveSharedPreferences:000");

        SharedPreferences data = getSharedPreferences("Data", MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();


        //String settingValue = "4.00" + "\t" + selectedPosition + "\t" +
        //        options.isPaste() + "\t" + options.isCopy();

        String settingValue = "5.00" + "\t" + selectedPosition + "\t" +
                options.isPaste() + "\t" + options.isCopy() + "\t" + options.isUseYahooApi();

        Log.d("MainActivity", settingValue);

        editor.putString("SettingValue", settingValue);

        //editor.commit();
        editor.apply();

        Log.d("MainActivity", "saveSharedPreferences:999");
    }

    void copyClipboard(String text) {
        if (null == clipboardManager) return;

        clipboardManager.setPrimaryClip(ClipData.newPlainText("", text));

        Toast.makeText(context, "Copy to clipboard", Toast.LENGTH_LONG).show();
    }

    public void onClick1(View v) {
        Log.d("MainActivity", "onClick1:000");

        TextView t = (TextView) v;

        String text = t.getText().toString();

        copyClipboard(text);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d("MainActivity", "onWindowFocusChanged:000");

        changeLayout();

        saveSharedPreferences();

        Log.d("MainActivity", "onWindowFocusChanged:999");
    }

    void changeLayout() {
        Log.d("MainActivity", "layoutChange:000");

        Log.d("MainActivity", options.isPaste() + "," + options.isCopy());

        LinearLayout.LayoutParams params;

        if (!options.isPaste()) {
            button_paste.setVisibility(View.GONE);
            button_trans.setTextSize(12.0f);
            button_clear.setTextSize(12.0f);
            button_setup.setTextSize(12.0f);

            params = (LinearLayout.LayoutParams) spinnerMenu.getLayoutParams();
            params.weight = 2.0f;
            spinnerMenu.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) button_trans.getLayoutParams();
            params.weight = 1.0f;
            button_trans.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) button_clear.getLayoutParams();
            params.weight = 1.0f;
            button_clear.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) button_setup.getLayoutParams();
            params.weight = 1.0f;
            button_setup.setLayoutParams(params);
        } else {
            button_paste.setVisibility(View.VISIBLE);

            button_trans.setTextSize(9.0f);
            button_clear.setTextSize(9.0f);
            button_paste.setTextSize(9.0f);
            button_setup.setTextSize(9.0f);

            params = (LinearLayout.LayoutParams) spinnerMenu.getLayoutParams();
            params.weight = 2.5f;
            spinnerMenu.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) button_trans.getLayoutParams();
            params.weight = 1.0f;
            button_trans.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) button_clear.getLayoutParams();
            params.weight = 1.0f;
            button_clear.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) button_paste.getLayoutParams();
            params.weight = 1.0f;
            button_paste.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) button_setup.getLayoutParams();
            params.weight = 1.0f;
            button_setup.setLayoutParams(params);
        }

        if (options.isCopy()) {
            textView_hiragana.setClickable(true);
            textView_trans.setClickable(true);
        } else {
            textView_hiragana.setClickable(false);
            textView_trans.setClickable(false);
        }

        Log.d("MainActivity", "layoutChange:999");
    }
}
