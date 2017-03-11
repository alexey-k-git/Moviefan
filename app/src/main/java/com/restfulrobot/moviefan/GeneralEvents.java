package com.restfulrobot.moviefan;


import android.content.Context;
import android.content.res.Resources;

import java.util.Random;

public class GeneralEvents {

    public static String trueAnswer(Context context) {
        Resources resources = context.getResources();
        String array[] = resources.getStringArray(R.array.true_answer);
        int random = new Random().nextInt(array.length);
        return array[random];
    }


    public static String almostTrueAnswer(Context context) {
        Resources resources = context.getResources();
        String array[] = resources.getStringArray(R.array.almost_true_answer);
        int random = new Random().nextInt(array.length);
        return array[random];
    }

    public static  String falseAnswer(Context context) {
        Resources resources = context.getResources();
        String array[] = resources.getStringArray(R.array.false_answer);
        int random = new Random().nextInt(array.length);
        return array[random];
    }

}
