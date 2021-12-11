package me.pustinek.interactivemessenger.common.source;

import java.io.File;
import java.util.List;

/**
 * Provide messages based on keys, for example from a language file
 */
public interface MessageProvider {

	File languageFolder = null;
	
	/**
	 * Get the message that is linked to the specified key
	 * @param key The key of the message to get
	 * @return A list with the lines of the message
	 */
	List<String> getMessage(String key);
}
