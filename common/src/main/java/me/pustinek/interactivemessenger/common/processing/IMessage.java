package me.pustinek.interactivemessenger.common.processing;

import java.util.regex.Pattern;

public interface IMessage {


    String CHATLANGUAGEVARIABLE = "prefix";

    String getKey();

    static String getMessageStart(IMessage message, int index) {
        return null;
    }



}
