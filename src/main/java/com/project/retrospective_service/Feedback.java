package com.project.retrospective_service;

import java.util.List;

public class Feedback {


    private String name;
    private String body;
    private String feedbackType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public boolean checkAllNull() {
        if (name != null) {
            return false;
        }
        if (body != null) {
            return false;
        }
        return feedbackType == null;
    }
}

