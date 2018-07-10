package com.github.kostrovik.matrix.core.application;

import com.github.kostrovik.configurator.core.SettingsParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * project: glc-mtx-core
 * author:  kostrovik
 * date:    09/07/2018
 * github:  https://github.com/kostrovik/glc-mtx-core
 */
public class ApplicationModulesConfigurator {
    private static Logger logger = LogManager.getLogger(ApplicationModulesConfigurator.class);
    private Map<String, Object> config;
    private final String defaultConfigFilePath = "core/configurations/app_modules.properties";
    private SettingsParser parser;

    public ApplicationModulesConfigurator() {
        this.config = parseConfig(getDefaultConfig());
    }

    public ApplicationModulesConfigurator(Properties properties) {
        this.config = parseConfig(properties);
    }

    public ApplicationModulesConfigurator(String configPath) {
        this.config = parseConfig(configPath);
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    private List<String> parseKey(String key) {
        return new ArrayList<>(Arrays.asList(key.split("\\.")));
    }

    /**
     * Создает конфигурацию на основе настроект по умолчанию и переданных пользовательских настроек.
     *
     * @param customSettings the custom settings
     * @return the map
     */
    private Map<String, Object> parseConfig(Properties customSettings) {
        Properties result = getDefaultConfig();

        for (Object key : customSettings.keySet()) {
            if (!result.containsKey(key)) {
                result.setProperty((String) key, customSettings.getProperty((String) key));
            }
        }

        parser = new SettingsParser(result);

        return parser.getConfig();
    }

    private Map<String, Object> parseConfig(String configPath) {
        return parseConfig(getCustomConfig(configPath));
    }

    /**
     * Получает настройки по умолчанию для модуля.
     *
     * @return the default config
     */
    private Properties getDefaultConfig() {
        Properties result = new Properties();

        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(defaultConfigFilePath)) {
            if (inputStream != null) {
                result.load(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            }
        } catch (FileNotFoundException error) {
            logger.error("Не найден файл конфигурации по умолчанию", error);
        } catch (IOException error) {
            logger.error("Не возможно загрузить настройки умолчанию", error);
        }

        return result;
    }

    /**
     * Получает настройки из пользовательского файла.
     *
     * @param configPath the config path
     * @return the custom config
     */
    private Properties getCustomConfig(String configPath) {
        Properties result = new Properties();

        try (FileInputStream inputStream = new FileInputStream(configPath)) {
            result.load(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        } catch (FileNotFoundException error) {
            logger.error(String.format("Не найден файл конфигурации: %s", configPath), error);
        } catch (IOException error) {
            logger.error("Не возможно загрузить конфигурацию", error);
        }

        return result;
    }
}
