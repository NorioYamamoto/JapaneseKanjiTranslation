package jp.co.yamamoto.norio.japanesekanjitranslation;

import static java.net.URLEncoder.encode;

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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
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
    TextView textView_roman;
    TextView textView_trans;

    String targetLanguage = "en";

    int selectedPosition = 2;

    Context context = this;
    ClipboardManager clipboardManager = null;
    Options options;

    String appVersion = "1.6";

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
        textView_roman = findViewById(R.id.textView_roman);
        textView_trans = findViewById(R.id.textView_trans);

        options = (Options) getApplication();

        loadSharedPreferences();

        Locale locale;
        String language;
        locale = Locale.getDefault();
        language = locale.getLanguage();

        Log.d("MainActivity", language + "?????????" + locale + "???????????????");

        final ArrayList<String> langNameList = new ArrayList();
        final ArrayList<String> langCodeList = new ArrayList();

        langNameList.add("??????????????"); // ???????????????
        langCodeList.add("ar");

        langNameList.add("Deutsch"); // ????????????
        langCodeList.add("de");

        langNameList.add("English"); // ??????
        langCodeList.add("en");

        langNameList.add("Espanol"); // ???????????????
        langCodeList.add("es");

        langNameList.add("Fran??ais"); // ???????????????
        langCodeList.add("fr");

        langNameList.add("italiano"); // ???????????????
        langCodeList.add("it");

        langNameList.add("??????"); // ?????????
        langCodeList.add("ko");

        langNameList.add("??????????????"); // ????????????
        langCodeList.add("ru");

        langNameList.add("????????????"); // ?????????
        langCodeList.add("zh");

        spinnerMenu = findViewById(R.id.spinnerLanguage);
        ArrayAdapter<String> adapterMenu = new ArrayAdapter<>(this, R.layout.spinner_item, langNameList);

        adapterMenu.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMenu.setAdapter(adapterMenu);

        if (selectedPosition != -1) {
            spinnerMenu.setSelection(selectedPosition);
        }

        spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //????????????????????????????????????
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;

                int pos = spinner.getSelectedItemPosition();

                targetLanguage = langCodeList.get(pos);
                selectedPosition = pos;

                saveSharedPreferences();
            }

            //??????????????????????????????????????????
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        button_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "button_trans:000");

                textView_hiragana.setText("");
                textView_roman.setText("");
                textView_trans.setText("");

                String kanji = editText_input.getText().toString();

                if (options.isRemoveSpace()){
                    kanji = kanji.replaceAll(" ", ""); // ??????????????????
                    kanji = kanji.replaceAll("???", ""); // ??????????????????
                }

                tranlateHiragana(kanji);

                try {
                    tranlateEnglish(kanji);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    appLog("Error tranlateEnglish,UnsupportedEncodingException");
                }

                appLog(appVersion + ","
                        + selectedPosition + ","
                        + options.isPaste() + ","
                        + options.isCopy() + ","
                        + options.isUseYahooApi() + ","
                        + options.isUseRoman());

                Log.d("MainActivity", "button_trans:999");
            }
        });

        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "button_clear:000");

                editText_input.setText("");
                textView_hiragana.setText("");
                textView_roman.setText("");
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
        Katakana(String katakana, boolean originally) {
            this.katakana = katakana;
            this.originally = originally;
        }

        String katakana;
        boolean originally;
    }

    Tokenizer tokenizer = new Tokenizer();

    public void tranlateHiragana(String kanji) {

        if (options.isUseYahooApi()) {
            // ???????????????(AsyncHttpRequest#doInBackground())???????????????
            try {
                String url = "https://jlp.yahooapis.jp/FuriganaService/V2/furigana";

                Log.d("MainActivity", "changeTextView:000 url=" + url);
                new AsyncHttpRequest(this, kanji).execute(new URL(url));
            } catch (MalformedURLException e) {
                appLog("Error tranlateHiragana");
                e.printStackTrace();
            }
        } else {
            List<Token> tokens = tokenizer.tokenize(kanji);
            //String katakana = "";
            List<Katakana> katakana = new ArrayList();

            for (Token token : tokens) {
                Log.d("Kuromoji", token.getSurface() + "\t" + token.getAllFeatures());
                String surface = token.getSurface();
                String reading = token.getReading();

                boolean originally = false;

                // ???????????????????????????????????????????????????????????????
                if (surface.matches(".*[\\u30A0-\\u30FF]+$") ||
                        surface.matches(".*[\\uFF10-\\uFF19]+$")) {
                    originally = true;
                    reading = surface;
                } else if (reading.equals("*")) {
                    reading = surface;
                }
                katakana.add(new Katakana(reading, originally));
            }

            String hiragana = "";

            Transliterator tl1 = Transliterator.getInstance("Katakana-Hiragana");
            Transliterator tl2 = Transliterator.getInstance("Katakana-Latin");

            for (Katakana k : katakana) {
                if (k.originally) {
                    hiragana += k.katakana;
                } else {
                    hiragana += tl1.transliterate(k.katakana);
                }
            }

            textView_hiragana.setText(hiragana);

            if (options.isUseRoman()) {
                String roman = "";
                for (Katakana k : katakana) {
                    roman += tl2.transliterate(k.katakana) + " ";
                }
                textView_roman.setText(roman);
                Log.d("Kuromoji", roman);
            }
        }
    }

    public void tranlateEnglish(String kanji) throws UnsupportedEncodingException {
        // ???????????????(AsyncHttpRequest#doInBackground())???????????????

        try {
            String url = "https://script.google.com/macros/s/AKfycbyIgrFqfMZcbCdcunVATUFq6yLC2yFXIRifkkRl7PzC6V38HEjG/exec?text=";

            kanji = encode(kanji, "UTF-8");

            url += kanji + "&source=ja&target=" + targetLanguage;

            Log.d("MainActivity", "tranlateEnglish:000 url=" + url);
            new AsyncHttpRequest(this, AsyncHttpRequest.RequestKind.TranslateEnglish).execute(new URL(url));
        } catch (MalformedURLException e) {
            appLog("Error tranlateEnglish");
            e.printStackTrace();
        }
    }

    public void appLog(String text) {
        // ???????????????(AsyncHttpRequest#doInBackground())???????????????
        try {
//            String url = "https://script.google.com/macros/s/AKfycbwtkrfkvOjc54YlQOTlSf2gp5g1mI7okvUwWn2PnWC1ngGEyamAio4mB5Rklc4mJ1r0rg/exec?text=";
            String url = "https://script.google.com/macros/s/AKfycbyyLx_QCt704G-2_EKUxY1T3K5Mhw4uqxvIwiANX58diNua9GNYr_wTvpngFBNLDB7jcQ/exec?text=";
            url += text;
            new AsyncHttpRequest(this, AsyncHttpRequest.RequestKind.AppLog).execute(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    // ????????????????????????
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
                options.setUseRoman(false);
                options.setRemoveSpace(false);
            } else if (settingValues[0].equals("2.00")) {
                Log.d("MainActivity", "loadSharedPreferences:020");
                options.setPaste(Boolean.valueOf(settingValues[2]));
                options.setCopy(Boolean.valueOf(settingValues[3]));
                options.setUseYahooApi(false);
                options.setUseRoman(false);
                options.setRemoveSpace(false);
            } else if (settingValues[0].equals("3.00")) {
                Log.d("MainActivity", "loadSharedPreferences:030");
                options.setPaste(Boolean.valueOf(settingValues[2]));
                options.setCopy(Boolean.valueOf(settingValues[3]));
                options.setUseYahooApi(false);
                options.setUseRoman(false);
                options.setRemoveSpace(false);
            } else if (settingValues[0].equals("4.00")) {
                Log.d("MainActivity", "loadSharedPreferences:040");
                options.setPaste(Boolean.valueOf(settingValues[2]));
                options.setCopy(Boolean.valueOf(settingValues[3]));
                options.setUseYahooApi(false);
                options.setUseRoman(false);
                options.setRemoveSpace(false);
            } else if (settingValues[0].equals("5.00")) {
                Log.d("MainActivity", "loadSharedPreferences:050");
                options.setPaste(Boolean.valueOf(settingValues[2]));
                options.setCopy(Boolean.valueOf(settingValues[3]));
                options.setUseYahooApi(Boolean.valueOf(settingValues[4]));
                options.setUseRoman(false);
                options.setRemoveSpace(false);
            } else if (settingValues[0].equals("6.00")) {
                Log.d("MainActivity", "loadSharedPreferences:060");
                options.setPaste(Boolean.valueOf(settingValues[2]));
                options.setCopy(Boolean.valueOf(settingValues[3]));
                options.setUseYahooApi(Boolean.valueOf(settingValues[4]));
                options.setUseRoman(Boolean.valueOf(settingValues[5]));
                options.setRemoveSpace(false);
            } else { // 7.00
                Log.d("MainActivity", "loadSharedPreferences:060");
                options.setPaste(Boolean.valueOf(settingValues[2]));
                options.setCopy(Boolean.valueOf(settingValues[3]));
                options.setUseYahooApi(Boolean.valueOf(settingValues[4]));
                options.setUseRoman(Boolean.valueOf(settingValues[5]));
                options.setRemoveSpace(Boolean.valueOf(settingValues[6]));
            }
        } else {
            Log.d("MainActivity", "loadSharedPreferences:090");
            options.setPaste(false);
            options.setCopy(false);
            options.setUseYahooApi(false);
            options.setUseRoman(false);
            options.setRemoveSpace(false);
        }

        Log.d("MainActivity", "loadSharedPreferences:999");
    }

    // ??????????????????
    void saveSharedPreferences() {
        Log.d("MainActivity", "saveSharedPreferences:000");

        SharedPreferences data = getSharedPreferences("Data", MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();

        String settingValue = "7.00" + "\t" + selectedPosition + "\t" +
                options.isPaste() + "\t" +
                options.isCopy() + "\t" +
                options.isUseYahooApi() + "\t" +
                options.isUseRoman() + "\t" +
                options.isRemoveSpace();

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
            textView_roman.setClickable(true);
            textView_trans.setClickable(true);
        } else {
            textView_hiragana.setClickable(false);
            textView_roman.setClickable(false);
            textView_trans.setClickable(false);
        }

        ScrollView scrollView_roman = findViewById(R.id.scrollView_roman);
        int weight = 0;
        if (options.isUseRoman()) {
            weight = 1;
        }

        LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams) scrollView_roman.getLayoutParams();
        layout.weight = weight;
        scrollView_roman.setLayoutParams(layout);

        Log.d("MainActivity", "layoutChange:999");
    }
}
