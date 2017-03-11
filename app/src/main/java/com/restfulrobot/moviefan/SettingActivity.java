package com.restfulrobot.moviefan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.restfulrobot.moviefan.settings.ResetDialog;

import java.io.File;

import static com.restfulrobot.moviefan.GeneralMethods.makeToast;

public class SettingActivity extends Activity implements View.OnClickListener {
    private ToggleButton toogleThemeBtn;
    private ResetDialog dialog;
    private File cacheFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Intent intent = getIntent();
        cacheFolder = (File) intent.getSerializableExtra("cacheFolder");
        toogleThemeBtn = (ToggleButton) findViewById(R.id.toggleTheme);
        toogleThemeBtn.setOnCheckedChangeListener(new ToggleListener());
    }

    public void resetDataBase(View v) {
        dialog = new ResetDialog();
        dialog.show(getFragmentManager(), "ResetDialog");
    }

    @Override
    public void onClick(View v) {
        if (((Button) v).getId() == R.id.btnResetDialogYes) {
            GeneralMethods.resetDB(this.getApplication().getApplicationContext());
            deleteFilesFromDataBaseDir();
            makeToast(this, "База данных сброшена", Toast.LENGTH_LONG);
        }
        if (dialog != null) {
            dialog.dismiss();
        } else {
            ((ResetDialog) getFragmentManager().findFragmentByTag("ResetDialog")).dismiss();
        }
    }


    private void deleteFilesFromDataBaseDir() {
        try {

            for (File file : cacheFolder.listFiles())
                if (!file.isDirectory())
                    file.delete();

        } catch (RuntimeException re) {
            re.printStackTrace();
        }
    }

    class ToggleListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                makeToast(SettingActivity.this, "Темная тема (Функция не работает)", Toast.LENGTH_SHORT);
            } else {
                makeToast(SettingActivity.this, "Светлая тема (Функция не работает)", Toast.LENGTH_SHORT);
            }

        }
    }

}
