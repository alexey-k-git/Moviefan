package com.restfulrobot.moviefan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.restfulrobot.moviefan.achievement.ResetDialog;
import com.restfulrobot.moviefan.achievement.UserStatistics;

import java.util.Map;

import static com.restfulrobot.moviefan.GeneralMethods.makeToast;

public class AchievementActivity extends Activity  implements View.OnClickListener {

    private TextView totalAnswersTextView;
    private TextView trueMovieAnswersTextView;
    private TextView falseMovieAnswersTextView;
    private TextView trueMovieActorsAnswersTextView;
    private TextView falseMovieActorsAnswersTextView;
    private TextView trueActorAnswersTextView;
    private TextView falseActorAnswersTextView;
    private TextView blitzRecordTextView;
    private TextView userNameTextView;
    private ResetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        totalAnswersTextView = (TextView) findViewById(R.id.totalAnswers);
        trueMovieAnswersTextView = (TextView) findViewById(R.id.trueMovieAnswers);
        falseMovieAnswersTextView = (TextView) findViewById(R.id.falseMovieAnswers);
        trueActorAnswersTextView = (TextView) findViewById(R.id.trueActorAnswers);
        falseActorAnswersTextView = (TextView) findViewById(R.id.falseActorAnswers);
        trueMovieActorsAnswersTextView = (TextView) findViewById(R.id.trueMovieActorsAnswers);
        falseMovieActorsAnswersTextView = (TextView) findViewById(R.id.falseMovieActorsAnswers);
        blitzRecordTextView = (TextView) findViewById(R.id.blitzRecord);
        userNameTextView = (TextView) findViewById(R.id.userName);
        userNameTextView.setText(UserStatistics.getUserName(this));
        updateStatistics();
    }

    private void updateStatistics() {
        Map<String, Integer> map = UserStatistics.getTrueFalseAnswersMap(this);
        int trueMovieAnswers = map.get("TRUE_ANSWER_MOVIE");
        int falseMovieAnswers  = map.get("FALSE_ANSWER_MOVIE");
        int trueMovieActorsAnswers  = map.get("TRUE_ANSWER_MOVIE_ACTORS");
        int falseMovieActorsAnswers  = map.get("FALSE_ANSWER_MOVIE_ACTORS");
        int trueActorAnswers  = map.get("TRUE_ANSWER_ACTOR");
        int falseActorAnswers  = map.get("FALSE_ANSWER_ACTOR");
        float blitzRecord = UserStatistics.getBlitzRecord(this);
        trueMovieAnswersTextView.setText(String.valueOf(trueMovieAnswers));
        falseMovieAnswersTextView.setText(String.valueOf(falseMovieAnswers));
        trueActorAnswersTextView.setText(String.valueOf(trueActorAnswers));
        falseActorAnswersTextView.setText(String.valueOf(falseActorAnswers));
        trueMovieActorsAnswersTextView.setText(String.valueOf(trueMovieActorsAnswers));
        falseMovieActorsAnswersTextView.setText(String.valueOf(falseMovieActorsAnswers));
        blitzRecordTextView.setText(String.valueOf(blitzRecord));
        totalAnswersTextView.setText(String.valueOf(
                trueMovieAnswers+falseMovieAnswers+
                trueMovieActorsAnswers+falseMovieActorsAnswers+
                trueActorAnswers+falseActorAnswers));
    }

    public void resetAchievements(View v) {
        dialog = new ResetDialog();
        dialog.show(getFragmentManager(),"ResetDialog");
    }

    @Override
    public void onClick(View v) {
        if (((Button)v).getId()==R.id.btnResetDialogYes)
        {
            UserStatistics.resetStatistics(this);
            makeToast(this, "Статистика сброшена", Toast.LENGTH_LONG);
            updateStatistics();
        }
        if (dialog!=null)
        {
            dialog.dismiss();
        }
        else
        {
            ((ResetDialog)getFragmentManager().findFragmentByTag("ResetDialog")).dismiss();
        }
    }
}
