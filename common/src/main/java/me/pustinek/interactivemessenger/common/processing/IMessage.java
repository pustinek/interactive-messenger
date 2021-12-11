package me.pustinek.interactivemessenger.common.processing;

public interface IMessage {


    String getKey();

    static String getMessageStart(IMessage message, int index) {
        return null;
    }
}
