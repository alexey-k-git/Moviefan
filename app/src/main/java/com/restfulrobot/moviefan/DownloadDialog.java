package com.restfulrobot.moviefan;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import static com.restfulrobot.moviefan.logs.MovieFanLogs.LOG_S;

public class DownloadDialog extends DialogFragment{
    private ProgressBar progressBar;
    private Button btnCancel;
    private int count=100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.AppThemeDialog);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_S, "onCreateView " + "DownloadDialog");
        getDialog().setTitle("Окно загрузки");
        View v = inflater.inflate(R.layout.download_dialog, null);
        btnCancel = (Button) v.findViewById(R.id.btnDownloadCancel);
        btnCancel.setOnClickListener((MainActivity)this.getActivity());
        progressBar = (ProgressBar) v.findViewById(R.id.downloadProgressBar);
        progressBar.setMax(count);
        this.setCancelable(false);
        return v;
    }

    public MainActivity getMainActivity()
    {
       return  (MainActivity)this.getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setCount(int count)
    {
        this.count = count;
        progressBar.setMax(count);
    }

    public void setProgress(int progress)
    {
        progressBar.setProgress(progress);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(LOG_S, "onDismiss " + "DownloadDialog");
        super.onDismiss(dialog);
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(LOG_S, "onCancel " + "DownloadDialog");
        super.onCancel(dialog);
    }

    @Override
    public void onDestroyView()
    {
        Dialog dialog = getDialog();

        // Work around bug: http://code.google.com/p/android/issues/detail?id=17423
        if ((dialog != null) && getRetainInstance())
            dialog.setDismissMessage(null);

        super.onDestroyView();
    }
}

