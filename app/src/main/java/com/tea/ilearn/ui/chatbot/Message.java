package com.tea.ilearn.ui.chatbot;

class ChatMessage {
    String text;
    int who; // 0 for user, 1 for bot

    public ChatMessage(String text, int who) {
        this.text = text;
        this.who = who;
    }
}
