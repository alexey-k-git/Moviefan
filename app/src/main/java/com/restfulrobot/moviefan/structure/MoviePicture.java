package com.restfulrobot.moviefan.structure;

import java.io.Serializable;
import java.util.Random;

public class MoviePicture implements Serializable, Question {
    private String fileName1;
    private String fileName2;
    private String answer;

    public MoviePicture(String fileName1, String fileName2, String answer) {
        this.fileName1 = fileName1;
        this.fileName2 = fileName2;
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public String getImageFileName() {
        return ((new Random()).nextBoolean()) ? fileName1 : fileName2;
    }
}
