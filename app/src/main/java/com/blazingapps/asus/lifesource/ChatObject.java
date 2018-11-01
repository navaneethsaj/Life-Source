package com.blazingapps.asus.lifesource;

import java.util.ArrayList;

public class ChatObject {
    String question;
    ArrayList<AnswerObject> answers;

    public ChatObject(String question, ArrayList<AnswerObject> answers) {
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<AnswerObject> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<AnswerObject> answers) {
        this.answers = answers;
    }
}
