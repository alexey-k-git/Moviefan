package com.restfulrobot.moviefan.structure;

import java.io.Serializable;

public class ActorPicture implements Serializable, Question {
    private String fileName;
    private String name;
    private byte gender;

    public ActorPicture(String name,  byte gender, String fileName) {
      this.name=name;
      this.gender=gender;
      this.fileName=fileName;
    }

    public byte getGender()
    {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getImageFileName() {
        return fileName;
    }
}
