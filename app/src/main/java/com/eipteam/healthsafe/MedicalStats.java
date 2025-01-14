package com.eipteam.healthsafe;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.eipteam.healthsafe.nfc_manager.nfc_utils.NfcFunctions;
import com.eipteam.healthsafe.nfc_manager.display.Element;
import com.eipteam.healthsafe.nfc_manager.display.ListElementAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MedicalStats extends AppCompatActivity {

    private final ArrayList<Element> infos = new ArrayList<>();
    private ListElementAdapter adpInfos;
    private HashMap<String, String> map;
    private HashMap<String, String> defaultMap;
    private String[] keys;
    private String[] displayInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_stats);

        ListView listInfos;
        listInfos = findViewById(R.id.LElem);

        keys = getResources().getStringArray(R.array.medical_informations);
        displayInfo = getResources().getStringArray(R.array.explicit_informations);

        Intent intent = getIntent();
        String datas = intent.getStringExtra("data");

        if (!NfcFunctions.checkData(keys, datas)) {
            Intent tmpIntent = new Intent(this, TransferData.class);

            tmpIntent.putExtra("Infos", "NULL");
            TransferData.error(this, "Not good format.");

            map = new HashMap<>();
            for (String s : getResources().getStringArray(R.array.medical_informations)) {
                map.put(s, "N/A");
            }

            startActivity(tmpIntent);
        } else
            map = NfcFunctions.stringToMap(datas);

        defaultMap = map;

        for (int i = 0; i < keys.length; i++) {
            infos.add(new Element(displayInfo[i], map.get(keys[i])));
        }

        adpInfos = new ListElementAdapter(this, infos);
        listInfos.setAdapter(adpInfos);
    }

    public void returnConnect(View view) {
        finish();
    }

    public void transfer(View v) {

        adpInfos.notifyDataSetChanged();

        int nbSame = 0;

        for (Element e : infos) {
            if (defaultMap.get(getKeysId(e.getText())).equals(e.getEdit()) || (defaultMap.get(getKeysId(e.getText())).equals("N/A") && e.getEdit().equals("")))
                nbSame += 1;
            else {
                if (e.getEdit().equals(""))
                    map.put(getKeysId(e.getText()), "N/A");
                else
                    map.put(getKeysId(e.getText()), e.getEdit());
            }
        }

        if (nbSame == infos.size()) {
            TransferData.error(this, "You have not change anything.");
            return ;
        }

        Intent intent = new Intent(this, TransferData.class);

        intent.putExtra("Infos", NfcFunctions.mapToString(map));

        startActivity(intent);
    }

    private String getKeysId(String key) {
        for (int i = 0; i < displayInfo.length; i++) {
            if (displayInfo[i].equals(key))
                return keys[i];
        }
        return "This is not a key (too bad).";
    }
}