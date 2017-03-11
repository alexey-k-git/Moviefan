package com.restfulrobot.moviefan.achievement;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.restfulrobot.moviefan.AchievementActivity;
import com.restfulrobot.moviefan.R;

public class ResetDialog extends DialogFragment  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.AppThemeDialog);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Внимание!");
        View v = inflater.inflate(R.layout.reset_achievement_dialog, null);
        v.findViewById(R.id.btnResetDialogYes).setOnClickListener((AchievementActivity)this.getActivity());
        v.findViewById(R.id.btnResetDialogNo).setOnClickListener((AchievementActivity)this.getActivity());
        return v;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}