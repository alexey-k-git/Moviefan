package com.restfulrobot.moviefan.game_activities;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.restfulrobot.moviefan.R;

public class HintActorDialog extends DialogFragment {
    private Boolean wasHintLessVariantsFlag = false;
    private Button actorHintLessVariantsBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.AppThemeDialog);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Использовать подсказку");
        View v = inflater.inflate(R.layout.hint_actor_dialog, null);
        v.findViewById(R.id.btnActorHintCancel).setOnClickListener((GuessTheActorActivity) this.getActivity());
        actorHintLessVariantsBtn = (Button) v.findViewById(R.id.btnActorHintLessVariants);
        actorHintLessVariantsBtn.setOnClickListener((GuessTheActorActivity) this.getActivity());
        actorHintLessVariantsBtn.setEnabled(!wasHintLessVariantsFlag);
        return v;
    }


    public void setHintsEnable(Boolean wasHintLessVariants) {
        this.wasHintLessVariantsFlag = wasHintLessVariants;
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