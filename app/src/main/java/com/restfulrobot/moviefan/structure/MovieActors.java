package com.restfulrobot.moviefan.structure;


import java.io.Serializable;
import java.util.Random;

public class MovieActors implements Serializable, Question {

    private String[] actors;
    private String movie;

    public MovieActors(String movie, String[] actors) {
        this.movie = movie;
        this.actors = actors;
    }

    public String getMovie() {
        return movie;
    }

    public String[] getRandomActors() {

        Random r = new Random();
        int count = r.nextInt(actors.length+1);
        if (count==0)
        {
            count=1;
        }
        String[] ractors =  new String[count];
        for (int i=0;i<ractors.length;i++)
        {
            ractors[i]=actors[i];
        }
        return ractors;
    }
}
