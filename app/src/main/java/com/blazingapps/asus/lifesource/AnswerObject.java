package com.blazingapps.asus.lifesource;

class AnswerObject {
    String answer;
    String docId;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public AnswerObject(String answer, String docId) {
        this.answer = answer;
        this.docId = docId;
    }
}
