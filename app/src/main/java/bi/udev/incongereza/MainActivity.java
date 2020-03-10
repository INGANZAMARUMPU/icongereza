package bi.udev.incongereza;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.FormatException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Array;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView champ_search;
    LinearLayout layout_traduction;
    ToggleButton toggle_lang, toggle_num;
    HRArrayAdapter<String> adapterRun;
    ArrayAdapter<String> adapterEn;
    ListView listView;
    Boolean double_backed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        champ_search = findViewById(R.id.champ_search);
        layout_traduction = findViewById(R.id.layout_traduction);
        toggle_lang = findViewById(R.id.toggle_lang);
        toggle_num = findViewById(R.id.toggle_num);
        listView = new ListView(this);

        final String[] arrayEn = getResources().getStringArray(R.array.en);
        final String[] arrayRun = getResources().getStringArray(R.array.run);

        Arrays.sort(arrayEn, Collator.getInstance());
        Arrays.sort(arrayRun, Collator.getInstance());

        adapterEn = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, arrayEn);

        adapterRun = new HRArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, arrayRun);

        correctToggle();

        toggle_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correctToggle();
            }
        });
        toggle_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correctToggle();
            }
        });

        champ_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(toggle_num.isChecked()){
                    rechercher();
                } else {
                    generateNumber();
                }
            }
        });
    }

    private void correctToggle(){
        if(toggle_num.isChecked()) {
            if (toggle_lang.isChecked()) {
                champ_search.setHint("Search");
                champ_search.setAdapter(adapterEn);
            } else {
                champ_search.setHint("rondera");
                champ_search.setAdapter(adapterRun);
            }
            champ_search.setInputType(InputType.TYPE_CLASS_TEXT);
            champ_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId==EditorInfo.IME_ACTION_DONE) rechercher();
                    return false;
                }
            });
            loadDef();
        }else{
            if (toggle_lang.isChecked()) {
                champ_search.setHint("Search");
            } else {
                champ_search.setHint("rondera");
            }
            champ_search.setInputType(InputType.TYPE_CLASS_NUMBER);
            champ_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId==EditorInfo.IME_ACTION_DONE) rechercher();
                    return false;
                }
            });
        }
    }

    private void loadDef(){
        champ_search.setDropDownHeight(0);
        layout_traduction.removeAllViews();
        if(toggle_lang.isChecked()){
            listView.setAdapter(adapterEn);
        } else{
            listView.setAdapter(adapterRun);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                translate((String) parent.getItemAtPosition(position));
            }
        });
        listView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
//        listView.setPadding();
        layout_traduction.addView(listView);
    }

    private void generateNumber() {
    }

    private void rechercher(){
        String key = champ_search.getText().toString();
        translate(key);
    }

    private void translate(String key) {
        champ_search.setText("");
        layout_traduction.removeAllViews();
        champ_search.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        if (toggle_num.isChecked()) {
            if (toggle_lang.isChecked()) {
                TextView txt_source = new TextView(this), txt_translation = new TextView(this);
                txt_translation.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10);
                txt_source.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10);
                layout_traduction.addView(txt_source);
                layout_traduction.addView(txt_translation);

                String traduction = "", source = "";
                ArrayList<Kirundi> translations = new WordsDB(MainActivity.this).getRun(key);
                for (Kirundi translation : translations) {
                    source = "<b>" + translation.en + "<b/>";
                    traduction += translation.ki + "<br/><br/>";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        txt_source.setText(Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY));
                        txt_translation.setText(Html.fromHtml(traduction, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        txt_source.setText(Html.fromHtml(source));
                        txt_translation.setText(Html.fromHtml(traduction));
                    }
                }
            } else {
                String traduction = "", singulier = "", pluriel = "", type;
                ArrayList<English> translations = new WordsDB(MainActivity.this).getEn(key);
                for (English translation : translations) {
                    LinearLayout singpl = new LinearLayout(this);
                    TextView txt_singulier = new TextView(this),
                            txt_pluriel = new TextView(this),
                            txt_translation = new TextView(this);
                    txt_translation.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10);
                    singpl.setOrientation(LinearLayout.HORIZONTAL);
                    singpl.addView(txt_singulier);
                    singpl.addView(txt_pluriel);
                    layout_traduction.addView(singpl);
                    layout_traduction.addView(txt_translation);
                    singulier = "singular<br/>";
                    pluriel = "plural<br/>";
                    traduction = translation.en + "<br/><br/>";
                    try {
                        translation.type.equalsIgnoreCase("");
                        type = translation.type;
                    } catch (NullPointerException e) {
                        type = "";
                    }
                    if (type.equalsIgnoreCase("N")) {
                        singulier += translation.ki1.replace(translation.base, "<b>" + translation.base + "</b>") + "<br/>";
                        if (!translation.ki2.trim().isEmpty())
                            singulier += translation.ki2.replace(translation.base, "<b>" + translation.base + "</b>") + "<br/>";
                        if (!translation.ki3.trim().isEmpty())
                            singulier += translation.ki3.replace(translation.base, "<b>" + translation.base + "</b>");
                        pluriel += translation.mod1.replace(translation.base, "<b>" + translation.base + "</b>") + "<br/>";
                        if (!translation.mod2.trim().isEmpty())
                            pluriel += translation.mod2.replace(translation.base, "<b>" + translation.base + "</b>") + "<br/>";
                        if (!translation.mod3.trim().isEmpty())
                            pluriel += translation.mod3.replace(translation.base, "<b>" + translation.base + "</b>");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txt_pluriel.setText(Html.fromHtml(pluriel, Html.FROM_HTML_MODE_LEGACY));
                            txt_singulier.setText(Html.fromHtml(singulier, Html.FROM_HTML_MODE_LEGACY));
                            txt_translation.setText(Html.fromHtml(traduction, Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            txt_pluriel.setText(Html.fromHtml(pluriel));
                            txt_singulier.setText(Html.fromHtml(singulier));
                            txt_translation.setText(Html.fromHtml(traduction));
                        }
                    } else {
                        singulier = translation.ki1.replace(translation.base, "<b>" + translation.base + "</b>") + "<br/>";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txt_singulier.setText(Html.fromHtml(singulier, Html.FROM_HTML_MODE_LEGACY));
                            txt_translation.setText(Html.fromHtml(traduction, Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            txt_singulier.setText(Html.fromHtml(singulier));
                            txt_translation.setText(Html.fromHtml(traduction));
                        }
                    }
                }
            }
        }else{
            if (toggle_lang.isChecked()) {
                TextView txt = new TextView(this);
                txt.setText(NumberWordConverter.convert(Integer.parseInt(key)));
                txt.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10);
                layout_traduction.addView(txt);
            } else {
                TextView txt = new TextView(this);
                txt.setText(NumberWordConverter.convert(Integer.parseInt(key)));
                txt.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10);
                layout_traduction.addView(txt);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (double_backed) {
            super.onBackPressed();
            return;
        }
        double_backed = true;
//        Toast.makeText(this, "fyonda kandi nimba mukeneye kwugara", Toast.LENGTH_LONG).show();
        correctToggle();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                double_backed=false;
            }
        }, 2000);
    }
}
