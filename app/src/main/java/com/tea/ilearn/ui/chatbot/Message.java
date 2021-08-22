package com.tea.ilearn.ui.chatbot;

class Message {
    String text;
    int who; // 0 for user, 1 for bot

    public Message(String text, int who) {
        this.text = text;
        this.who = who;
    }
}
