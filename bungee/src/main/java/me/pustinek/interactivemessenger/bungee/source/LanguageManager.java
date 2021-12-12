package me.pustinek.interactivemessenger.bungee.source;

import com.google.common.base.Charsets;

import me.pustinek.interactivemessenger.bungee.processing.Message;
import me.pustinek.interactivemessenger.common.Log;
import me.pustinek.interactivemessenger.common.processing.IMessage;
import me.pustinek.interactivemessenger.common.source.Manager;
import me.pustinek.interactivemessenger.common.source.MessageProvider;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.md_5.bungee.api.plugin.Plugin;


public class LanguageManager extends Manager implements MessageProvider {
	private Plugin plugin;
	private Map<String, List<String>> currentLanguage, defaultLanguage;
	private File languageFolder;
	private String jarLanguagePath;
	private List<String> chatPrefix;

	/**
	 * Constructor
	 * @param plugin The plugin creating this LanguageManager (used for logging and finding the language files in the jar)
	 * @param jarLanguagePath The path in the jar to the folder with the language files
	 * @param currentLanguageName The name of the language that should be active (without '.yml')
	 * @param defaultLanguageName The name of the language that
	 * @param chatPrefix The chat prefix for Message#prefix()
	 */
	public LanguageManager(Plugin plugin, String jarLanguagePath, String currentLanguageName, String defaultLanguageName, List<String> chatPrefix) {
		this.plugin = plugin;
		this.jarLanguagePath = jarLanguagePath;
		this.chatPrefix = chatPrefix;
		this.languageFolder = new File(plugin.getDataFolder() + File.separator + jarLanguagePath);

		Message.init(this, plugin.getLogger());
		saveDefaults();
		currentLanguage = loadLanguage(currentLanguageName);
		if(defaultLanguageName.equals(currentLanguageName)) {
			defaultLanguage = currentLanguage;
		} else {
			defaultLanguage = loadLanguage(defaultLanguageName);
		}
	}

	@Override
	public List<String> getMessage(String key) {
		List<String> message;
		if(key.equalsIgnoreCase(IMessage.CHATLANGUAGEVARIABLE)) {
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

	protected void saveDefaults() {
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


	protected Map<String, List<String>> loadLanguage(String key, boolean convert) {
		Map<String, List<String>> result = new HashMap<>();

		// Load the language file
		boolean convertFromTransifex = false;
		File file = new File(languageFolder.getAbsolutePath()+File.separator+key+".yml");
		try(
				InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)
		) {
			// Detect empty language files, happens when the YAML parsers prints an exception (it does return an empty YamlConfiguration though)
			ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
			Configuration cfg = provider.load(reader);

			if(cfg.getKeys().isEmpty()) {
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




























