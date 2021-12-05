package me.wiefferink.interactivemessenger.source;

import com.google.common.base.Charsets;
import me.wiefferink.interactivemessenger.Log;
import me.wiefferink.interactivemessenger.processing.Message;
import me.wiefferink.interactivemessenger.translation.Transifex;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import org.apache.commons.lang.exception.ExceptionUtils;


import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LanguageManager implements MessageProvider {
	private Object plugin;
	private Map<String, List<String>> currentLanguage, defaultLanguage;
	private File languageFolder;
	private String jarLanguagePath;
	private List<String> chatPrefix;


	public static boolean isBungee = false;

	public static boolean isPresent(String className) {
		try {
			Class<?> aClass = Class.forName(className);

		} catch (Exception e) {
			return false;
		}


		return true;
	}

	/**
	 * Constructor
	 * @param plugin The plugin creating this LanguageManager (used for logging and finding the language files in the jar)
	 * @param jarLanguagePath The path in the jar to the folder with the language files
	 * @param currentLanguageName The name of the language that should be active (without '.yml')
	 * @param defaultLanguageName The name of the language that
	 * @param chatPrefix The chat prefix for Message#prefix()
	 */
	public LanguageManager(Object plugin, String jarLanguagePath, String currentLanguageName, String defaultLanguageName, List<String> chatPrefix) {
		this.plugin = plugin;
		this.jarLanguagePath = jarLanguagePath;
		this.chatPrefix = chatPrefix;

		if(isPresent("org.bukkit.plugin.java.JavaPlugin")){
			isBungee = false;
			this.languageFolder = new File(((JavaPlugin) plugin).getDataFolder() + File.separator + jarLanguagePath);
			Message.init(this, ((JavaPlugin) plugin).getLogger());
		}else{
			isBungee = true;
			this.languageFolder = new File(((Plugin) plugin).getDataFolder() + File.separator + jarLanguagePath);
			Message.init(this, ((Plugin) plugin).getLogger());
		}

		saveDefaults();
		currentLanguage = loadLanguage(currentLanguageName);
		if(defaultLanguageName.equals(currentLanguageName)) {
			defaultLanguage = currentLanguage;
		} else {
			defaultLanguage = loadLanguage(defaultLanguageName);
		}

	}






	/**
	 * Get the message for a certain key (result can be modified)
	 * @param key The key of the message to get
	 * @return The message as a list of strings
	 */
	@Override
	public List<String> getMessage(String key) {
		List<String> message;
		if(key.equalsIgnoreCase(Message.CHATLANGUAGEVARIABLE)) {
			message = chatPrefix;
		} else if(currentLanguage.containsKey(key)) {
			message = currentLanguage.get(key);
		} else {
			message = defaultLanguage.get(key);
		}
		if(message == null) {
			Log.warn("Did not find message '" + key + "' in the current or default language");
			return new ArrayList<>();
		}
		return new ArrayList<>(message);
	}

	/**
	 * Saves the default language files if not already present
	 */
	private void saveDefaults() {
		// Create the language folder if it not exists
		File langFolder;
		if(!languageFolder.exists()) {
			if(!languageFolder.mkdirs()) {
				Log.warn("Could not create language directory: " + languageFolder.getAbsolutePath());
				return;
			}
		}

		try {
			// Read jar as ZIP file
			File jarPath = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			ZipFile jar = new ZipFile(jarPath);
			Enumeration<? extends ZipEntry> entries = jar.entries();

			// Each entry is a file or directory
			while(entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				// Filter to YAML files in the language directory
				if(!entry.isDirectory() && entry.getName().startsWith(jarLanguagePath+"/") && entry.getName().endsWith(".yml")) {
					// Save the file to disk
					File targetFile = new File(languageFolder.getAbsolutePath() + File.separator + entry.getName().substring(entry.getName().lastIndexOf("/")));
					try(
							InputStream input = jar.getInputStream(entry);
							OutputStream output = new FileOutputStream(targetFile)
					) {
						int read;
						byte[] bytes = new byte[1024];
						while((read = input.read(bytes)) != -1) {
							output.write(bytes, 0, read);
						}
					} catch(IOException e) {
						Log.warn("Something went wrong saving a default language file: " + targetFile.getAbsolutePath());
					}
				}
			}
		} catch(URISyntaxException e) {
			Log.error("Failed to find location of jar file:", ExceptionUtils.getStackTrace(e));
		} catch(IOException e) {
			Log.error("Failed to read zip file:", ExceptionUtils.getStackTrace(e));
		}
	}

	/**
	 * Loads the specified language
	 * @param key The language to load
	 * @return Map with the messages loaded from the file
	 */
	private Map<String, List<String>> loadLanguage(String key) {
		return loadLanguage(key, true);
	}

	/**
	 * Loads the specified language
	 * @param key	 The language to load
	 * @param convert try conversion or not (infinite recursion prevention)
	 * @return Map with the messages loaded from the file
	 */
	private Map<String, List<String>> loadLanguage(String key, boolean convert) {
		Map<String, List<String>> result = new HashMap<>();

		// Load the language file
		boolean convertFromTransifex = false;
		File file = new File(languageFolder.getAbsolutePath()+File.separator+key+".yml");
		try(
				InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)
		) {
			// Detect empty language files, happens when the YAML parsers prints an exception (it does return an empty YamlConfiguration though)

			if(isPresent("org.bukkit.configuration.file.YamlConfiguration")){
				YamlConfiguration ymlFile = YamlConfiguration.loadConfiguration(reader);
				if(ymlFile.getKeys(false).isEmpty()) {
					Log.warn("Language file " + key + ".yml has zero messages.");
					return result;
				}

				// Retrieve the messages from the YAML file and create the result
				if(!convert || !Transifex.needsConversion(ymlFile)) {
					for(String messageKey : ymlFile.getKeys(false)) {
						if(ymlFile.isList(messageKey)) {
							result.put(messageKey, new ArrayList<>(ymlFile.getStringList(messageKey)));
						} else {
							result.put(messageKey, new ArrayList<>(Collections.singletonList(ymlFile.getString(messageKey))));
						}
					}
				} else {
					convertFromTransifex = true;
				}
			}else{
				// Is bungeecord
				Configuration configuration = ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).load(reader);
				if(configuration.getKeys().isEmpty()) {
					Log.warn("Language file " + key + ".yml has zero messages.");
					return result;
				}
				//TODO:  transifex support for Bungeecord
				for(String messageKey : configuration.getKeys()) {
					if(configuration.getStringList(messageKey).isEmpty()) {
						result.put(messageKey, new ArrayList<>(configuration.getStringList(messageKey)));
					} else {
						result.put(messageKey, new ArrayList<>(Collections.singletonList(configuration.getString(messageKey))));
					}
				}
			}
		} catch(IOException e) {
			Log.warn("Could not load language file: " + file.getAbsolutePath());
		}

		// Do conversion (after block above closed the reader)
		if(convertFromTransifex) {
			if(!Transifex.convertFrom(file)) {
				Log.warn("Failed to convert " + file.getName() + " from the Transifex layout to the AreaShop layout, check the errors above");
			}
			return loadLanguage(key, false);
		}

		return result;
	}

}

































