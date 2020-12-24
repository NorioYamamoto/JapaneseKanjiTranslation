package jp.co.yamamoto.norio.japanesekanjitranslation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;
    EditText editText_input;
    TextView textView_hiragana;
    TextView textView_roman;
    TextView textView_trans;

    String targetLanguage = "en";

    int selectedPosition  = 2;

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


        loadSharedPreferences();

        Locale locale;
        String language;
        locale = Locale.getDefault();
        language = locale.getLanguage();

        Log.d("ASDFG", language+"語で、"+locale+"の言葉です");


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

        Spinner spinnerMenu = findViewById(R.id.spinnerLanguage);
        ArrayAdapter<String> adapterMenu = new ArrayAdapter<>(this, R.layout.spinner_item, langNameList);


        adapterMenu.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMenu.setAdapter(adapterMenu);

        if (selectedPosition != -1){
            spinnerMenu.setSelection(selectedPosition);
        }

        spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Spinner spinner = (Spinner) parent;
//                String item = (String) spinner.getSelectedItem();

//                editTextwReceipName.setText(item);

                int pos = spinner.getSelectedItemPosition();

                targetLanguage = langCodeList.get(pos);

                selectedPosition = pos;
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        editText_input = findViewById(R.id.editText_input);
        textView_hiragana = findViewById(R.id.textView_hiragana);
        textView_roman = findViewById(R.id.textView_roman);
        textView_trans = findViewById(R.id.textView_trans);

        Button button_trans = (Button) findViewById(R.id.button_trans);
        button_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText editText_input = findViewById(R.id.editText_input);
                textView_hiragana.setText("");
                textView_roman.setText("");
                textView_trans.setText("");


                String kanji = editText_input.getText().toString();



                tranlateHiragana(kanji);
                tranlateEnglish(kanji);

                saveSharedPreferences();
            }
        });

        Button button_clear = (Button) findViewById(R.id.button_clear);
        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_input.setText("");
                textView_hiragana.setText("");
                textView_roman.setText("");
                textView_trans.setText("");
            }
        });



    }
    public void tranlateHiragana(String kanji) {
        // 非同期処理(AsyncHttpRequest#doInBackground())を呼び出す
        try {
            String url ="https://jlp.yahooapis.jp/FuriganaService/V1/furigana?appid=";

            String clientId = "dj00aiZpPXVVaVhaVnNraWhENyZzPWNvbnN1bWVyc2VjcmV0Jng9MjU-";

            url += clientId + "&sentence=" + kanji;

            Log.d("ASDFG", "changeTextView:000 url=" + url);
            new AsyncHttpRequest(this).execute(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void tranlateEnglish(String kanji) {
        // 非同期処理(AsyncHttpRequest#doInBackground())を呼び出す
        try {
            String url ="https://script.google.com/macros/s/AKfycbyIgrFqfMZcbCdcunVATUFq6yLC2yFXIRifkkRl7PzC6V38HEjG/exec?text=";

            String clientId = "dj00aiZpPXVVaVhaVnNraWhENyZzPWNvbnN1bWVyc2VjcmV0Jng9MjU-";

            url += kanji + "&source=ja&target=" + targetLanguage;

            Log.d("ASDFG", "changeTextView:000 url=" + url);
            new AsyncHttpRequest(this, AsyncHttpRequest.RequestKind.TranslateEnglish).execute(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    void loadSharedPreferences(){
        // 設定値を読み込む
        SharedPreferences data = getSharedPreferences("Data", MODE_PRIVATE);
        String settingValue = data.getString("SettingValue", null);

        if (settingValue != null) {
            String settingValues[] = settingValue.split("\t");

            selectedPosition = Integer.parseInt(settingValues[1]);

        }

    }


    void saveSharedPreferences(){
        // 設定値を保存
        SharedPreferences data = getSharedPreferences("Data", MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();

        String settingValue = "1.00" + "\t" + selectedPosition + "\t";

        editor.putString("SettingValue", settingValue);

        //editor.commit();
        editor.apply();
    }

}
