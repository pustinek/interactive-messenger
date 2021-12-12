package me.pustinek.interactivemessenger.common.source;

import me.pustinek.interactivemessenger.common.Log;
import me.pustinek.interactivemessenger.common.processing.IMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Manager {
     Map<String, List<String>> currentLanguage, defaultLanguage;
     String jarLanguagePath;
     List<String> chatPrefix;




    /**
     * Saves the default language files if not already present
     */
    protected abstract void saveDefaults();

    /**
     * Loads the specified language
     * @param key The language to load
     * @return Map with the messages loaded from the file
     */
    protected Map<String, List<String>> loadLanguage(String key) {
        return loadLanguage(key, true);
    }

    /**
     * Loads the specified language
     * @param key	 The language to load
     * @param convert try conversion or not (infinite recursion prevention)
     * @return Map with the messages loaded from the file
     */
    protected abstract Map<String, List<String>> loadLanguage(String key, boolean convert);
}
