package com.restfulrobot.moviefan.game_activities;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.restfulrobot.moviefan.R;

import static com.restfulrobot.moviefan.logs.MovieFanLogs.LOG_S;

public class HintMovieDialog extends DialogFragment {
    private Boolean wasHintLessVariantsFlag = false;
    private Button movieHintLessVariantsBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.AppThemeDialog);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_S, "onCreateView " + "HintMovieDialog");
        getDialog().setTitle("Использовать подсказку");
        View v = inflater.inflate(R.layout.hint_movie_dialog, null);
        v.findViewById(R.id.btnMovieHintCancel).setOnClickListener((GuessTheMovieActivity) this.getActivity());
        v.findViewById(R.id.btnMovieHintChangeScreen).setOnClickListener((GuessTheMovieActivity) this.getActivity());
        movieHintLessVariantsBtn = (Button) v.findViewById(R.id.btnMovieHintLessVariants);
        movieHintLessVariantsBtn.setOnClickListener((GuessTheMovieActivity) this.getActivity());
        movieHintLessVariantsBtn.setEnabled(!wasHintLessVariantsFlag);
        return v;
    }

    public void setHintsEnable(Boolean wasHintLessVariants) {
        this.wasHintLessVariantsFlag = wasHintLessVariants;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(LOG_S, "onDismiss " + "HintMovieDialog");
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(LOG_S, "onCancel " + "HintMovieDialog");
        super.onCancel(dialog);
    }
}
