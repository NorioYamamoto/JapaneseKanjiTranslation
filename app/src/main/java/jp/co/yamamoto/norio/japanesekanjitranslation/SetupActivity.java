package jp.co.yamamoto.norio.japanesekanjitranslation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class SetupActivity extends AppCompatActivity {
    Options options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Button buttonBack = findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        options = (Options) getApplication();

        CheckBox checkBoxPaste = findViewById(R.id.checkBoxPaste);
        checkBoxPaste.setChecked(options.isPaste());
        checkBoxPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.setPaste(((CheckBox) v).isChecked());
            }
        });

        CheckBox checkBoxCopy = findViewById(R.id.checkBoxCopy);
        checkBoxCopy.setChecked(options.isCopy());
        checkBoxCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.setCopy(((CheckBox) v).isChecked());
            }
        });

        CheckBox checkBoxUseYahooApi = findViewById(R.id.checkBoxUseYahooApi);
        checkBoxUseYahooApi.setChecked(options.isUseYahooApi());
        checkBoxUseYahooApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.setUseYahooApi(((CheckBox) v).isChecked());
            }
        });

        CheckBox checkBoxUseRoman = findViewById(R.id.checkBoxUseRoman);
        checkBoxUseRoman.setChecked(options.isUseRoman());
        checkBoxUseRoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.setUseRoman(((CheckBox) v).isChecked());
            }
        });

        CheckBox checkBoxRemoveSpace = findViewById(R.id.checkBoxRemoveSpace);
        checkBoxRemoveSpace.setChecked(options.isRemoveSpace());
        checkBoxRemoveSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.setRemoveSpace(((CheckBox) v).isChecked());
            }
        });

    }
}