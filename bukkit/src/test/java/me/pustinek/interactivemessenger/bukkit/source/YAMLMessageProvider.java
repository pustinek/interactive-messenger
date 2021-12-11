package me.pustinek.interactivemessenger.bukkit.source;

import com.google.common.base.Charsets;

import me.pustinek.interactivemessenger.common.Log;
import me.pustinek.interactivemessenger.common.source.MessageProvider;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YAMLMessageProvider implements MessageProvider {
	private Map<String, List<String>> messages;

	/**
	 * Constructor
	 * @param file File to read the messages from
	 */
	public YAMLMessageProvider(File file) {
		messages = loadLanguage(file);
	}

	/**
	 * Get the message for a certain key (result can be modified)
	 * @param key The key of the message to get
	 * @return The message as a list of strings
	 */
	@Override
	public List<String> getMessage(String key) {
		List<String> message = messages.get(key);
		if(message == null) {
			return new ArrayList<>();
		}
		return new ArrayList<>(message);
	}

	/**
	 * Loads the specified language
	 * @param file YAML rile to load from
	 * @return Map with the messages loaded from the file
	 */
	private Map<String, List<String>> loadLanguage(File file) {
		HashMap<String, List<String>> result = new HashMap<>();

		// Load the language file
		try(
				InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)
		) {
			YamlConfiguration ymlFile = YamlConfiguration.loadConfiguration(reader);
			if(ymlFile.getKeys(false).isEmpty()) {
				Log.warn("Language file has zero messages:", file.getAbsolutePath());
				return result;
			}
			for(String messageKey : ymlFile.getKeys(false)) {
				if (ymlFile.isList(messageKey)) {
					result.put(messageKey, new ArrayList<>(ymlFile.getStringList(messageKey)));
				} else {
					result.put(messageKey, new ArrayList<>(Collections.singletonList(ymlFile.getString(messageKey))));
				}
			}
		} catch(IOException e) {
			Log.warn("Could not load language file: " + file.getAbsolutePath());
		}

		return result;
	}

}

































