package com.restfulrobot.moviefan.game_activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.restfulrobot.moviefan.R;


public class BlitzTimeFragment extends Fragment implements View.OnClickListener {

    private FragListener listener;
    private int seconds = 30;
    private float points;
    private boolean running;
    private boolean wasRunning;
    private TextView timeTextView;
    private TextView pointTextView;
    private boolean rotateFlag = false;
    private boolean endOfGameFlag = false;
    private Button startBtn;


    @Override
    public void onClick(View view) {
        running = true;
        startBtn.setEnabled(false);
        listener.blitzStartTimeClicked();
    }

    public void setPoints(float points) {
        this.points = points;
        pointTextView.setText(String.valueOf(points));
    }

    public void cutTime() {
        if (seconds > 2) {
            seconds -= 2;
        }
    }

    static interface FragListener {
        void blitzStartTimeClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.listener = ((BlitzActivity) context);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = ((BlitzActivity) activity);
    }

    @Override
    public void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wasRunning) {
            running = true;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_blitz_time, container, false);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            startBtn = (Button) view.findViewById(R.id.startBlitzButton);
            startBtn.setOnClickListener(this);
            timeTextView = (TextView) view.findViewById(R.id.time_view);
            pointTextView = (TextView) view.findViewById(R.id.point_view);
            if (rotateFlag) {
                if (running) {
                    startBtn.setEnabled(false);
                }
                setPoints(points);
            }
            if (endOfGameFlag) {
                startBtn.setEnabled(false);
                String time = String.format("%d секунд", seconds);
                timeTextView.setText(time);
            } else {
                runTimer(view);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
        savedInstanceState.putBoolean("endOfGameFlag", endOfGameFlag);
        savedInstanceState.putFloat("points", points);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
            endOfGameFlag = savedInstanceState.getBoolean("endOfGameFlag");
            if (wasRunning) {
                running = true;
            }
            points = savedInstanceState.getFloat("points");
            rotateFlag = true;
        }
    }

    private void runTimer(View view) {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    seconds--;
                }
                String time = String.format("%d секунд", seconds);
                timeTextView.setText(time);
                if (seconds == 0) {
                    ((BlitzActivity) getActivity()).stopGame();
                    running = false;
                    endOfGameFlag = true;
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }


}
