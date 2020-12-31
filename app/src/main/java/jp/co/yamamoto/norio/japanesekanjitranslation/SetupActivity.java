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
        Log.d("SetupActivity", options.isPaste() + "," + options.isCopy());

        CheckBox checkBoxPaste = findViewById(R.id.checkBoxPaste);
        checkBoxPaste.setChecked(options.isPaste());

        checkBoxPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SetupActivity", "checkBoxPaste:000");

                options.setPaste(((CheckBox) v).isChecked());
                Log.d("SetupActivity", options.isPaste() + "," + options.isCopy());

                Log.d("SetupActivity", "checkBoxPaste:999");
            }
        });

        CheckBox checkBoxCopy = findViewById(R.id.checkBoxCopy);
        checkBoxCopy.setChecked(options.isCopy());

        checkBoxCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SetupActivity", "checkBoxCopy:000");

                options.setCopy(((CheckBox) v).isChecked());
                Log.d("SetupActivity", options.isPaste() + "," + options.isCopy());

                Log.d("SetupActivity", "checkBoxCopy:999");
            }
        });
    }
}