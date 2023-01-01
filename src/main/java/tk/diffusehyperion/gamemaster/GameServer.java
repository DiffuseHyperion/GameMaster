package tk.diffusehyperion.gamemaster;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

public class GameServer {

    /**
     * Read a property in server.properties.
     * <p>
     * You should use {@link #checkAndEditServerProperties(String, String, String, String)} if you want to edit server.properties.
     * @param propertyName Name of the property to check.
     * @return Value of the property.
     */
    public static String readServerProperties(String propertyName) throws IOException {
        File propertiesFile = new File(Bukkit.getWorldContainer(), "server.properties");
        FileInputStream stream = new FileInputStream(propertiesFile);
        Properties properties = new Properties();
        properties.load(stream);
        return properties.getProperty(propertyName);
    }
    /**
     * Check and edit a property in the server's server.properties.
     * @param propertyToCheck The property in the file to check. (Example: level-name)
     * @param correctConfig What should propertyToCheck be.
     * @param oldContent What an old line could look like. This should be a regex. (Example: spawn-protection=\\d+)
     * @param newContent What should the correct line look like.
     * @return If a change was required.
     */
    public static boolean checkAndEditServerProperties(String propertyToCheck, String correctConfig, String oldContent, String newContent) throws IOException {
        if (!Objects.equals(readServerProperties(propertyToCheck), correctConfig)) {
            File propertiesFile = new File(Bukkit.getWorldContainer(), "server.properties");
            String newcontent = readFile(propertiesFile).replaceAll(oldContent, newContent);
            writeFile(newcontent, propertiesFile);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Reads a property in a YAML file.
     * <p>
     * You should use {@link #checkAndEditYAML(File, String, String, String, String)} if you want to edit the YAML file.
     * @param ymlFile The file to check.
     * @param property The property in the file to check. (Example: settings.allow-end)
     * @return The property value.
     */
    public static String readYMLFile(File ymlFile, String property) throws IOException, InvalidConfigurationException {
        YamlConfiguration ymlConfig = new YamlConfiguration();
        ymlConfig.load(ymlFile);
        return ymlConfig.getString(property);
    }

    /**
     * Check and edit a property in a YAML file.
     * @param fileToCheck The file to check.
     * @param propertyToCheck The property in the file to check. (Example: settings.allow-end)
     * @param correctConfig What should propertyToCheck be.
     * @param oldContent What an old line could look like. This should be a regex.
     * @param newContent What should the correct line look like.
     * @return If a change was required.
     */
    public static boolean checkAndEditYAML(File fileToCheck, String propertyToCheck, String correctConfig, String oldContent, String newContent) throws IOException, InvalidConfigurationException {
        String valueOfProperty = readYMLFile(fileToCheck, propertyToCheck);
        // the value of neededProperty

        if (!Objects.equals(valueOfProperty, correctConfig)) {
            String newcontent = readFile(fileToCheck).replaceAll(oldContent, newContent);
            writeFile(newcontent, fileToCheck);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Restarts a server.
     */
    public static void restart() {
        Bukkit.spigot().restart();
    }

    /**
     * Reads a file and returns the string of the contents.
     * @param file The file to read.
     * @return The string of contents. This includes line separators.
     */
    public static String readFile(File file) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null) {
                builder.append(line).append(System.lineSeparator());
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

    /**
     * Replace EVERYTHING in a file with a string.
     * Use line terminators if you want to use additional lines.
     * @see lineTerminators
     * @param content The content to write.
     * @param file The file to be written to.
     */
    public static void writeFile(String content, File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }

    /**
     * Get the servers jar file. (Example: spigot-1.18.2.jar)
     * This will fail if there is another jar file in the server's root directory.
     * @return Server's jar file.
     */
    public static File getServerJar() {
        for (final File file : Objects.requireNonNull(Bukkit.getWorldContainer().listFiles())) {
            final String[] split = file.getName().split("\\.");
            if (split[split.length - 1].equals("jar")) {
                return file;
            }
        }
        return null;
    }

    /**
     * Check and edits common server properties.
     * @param disableSpawnProtection Disable spawn protection?
     * @param disableNether Disable nether?
     * @param disableEnd Disable end?
     * @param enableFlight Disable minecraft's anticheat? (it sucks)
     * @return If a change was required.
     */
    public static boolean checkForServerProperties(boolean disableSpawnProtection, boolean disableNether, boolean disableEnd, boolean enableFlight) throws IOException, InvalidConfigurationException {
        boolean neededChange = false;
        if (disableSpawnProtection) {
            neededChange = checkAndEditServerProperties("spawn-protection", "0", "spawn-protection=\\d+", "spawn-protection=0");
        }
        if (disableNether) {
            neededChange = checkAndEditServerProperties("allow-nether", "false", "allow-nether=[a-zA-Z]+", "allow-nether=false");
        }
        if (disableEnd) {
            neededChange = checkAndEditYAML(new File("bukkit.yml"), "settings.allow-end", "false", "allow-end: [a-zA-Z]+", "allow-end: false");
        }
        if (enableFlight) {
            neededChange = checkAndEditServerProperties("allow-flight", "true", "allow-flight=[a-zA-Z]+", "allow-flight=true");
        }
        return neededChange;
    }

    /**
     * Checks and edits common server properties.
     * This will disable spawn protection, nether, end and minecraft's anticheat.
     * @see #checkForServerProperties(boolean, boolean, boolean, boolean)
     * @return If a change was required.
     */
    public static boolean checkForServerProperties() throws IOException, InvalidConfigurationException {
        return checkForServerProperties(true, true, true, true);
    }

    /**
     * All recognised types of operating system.
     * @see #getOS()
     */
    public enum OSTypes {
        Windows,
        Mac,
        Unix,
        Solaris,
        Unknown
    }

    /**
     * Get the system's operating system.
     * @see OSTypes
     * @return The operating system.
     */
    public static OSTypes getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return OSTypes.Windows;
        } else if (os.contains("mac")) {
            return OSTypes.Mac;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return OSTypes.Unix;
        } else if (os.contains("sunos")) {
            return OSTypes.Solaris;
        } else {
            return OSTypes.Unknown;
        }
    }

    /**
     * Line terminators for writing files.
     * @see #writeFile(String, File)
     */
    public enum lineTerminators {
        /**
         * Line terminator for windows.
         */
        Windows("\r\n"),
        /**
         * Line terminator for unix.
         */
        Unix("\n");

        private final String lineTerminators;
        lineTerminators(String str) {lineTerminators = str;}
        public String toString() {return lineTerminators;}
    }

    /**
     * Attempts to set up a batch/bash script for spigot to use when restarting.
     * <p>
     * The OS will be automatically gotten. If it is unknown, it will throw {@link IllegalArgumentException}
     * <p>
     * The server jar file will be automatically gotten. This will likely fail!
     * @apiNote Mac and Solaris setups are UNTESTED! They will probably break lol
     * @return If the setup was required.
     */
    public static boolean setupRestart() throws IOException, IllegalArgumentException, InvalidConfigurationException {
        return setupRestart(getOS(), getServerJar().getName());
    }

    /**
     * Attempts to set up a batch/bash script for spigot to use when restarting.
     * <p>
     * The server jar file will be automatically detected. This will likely fail!
     * @apiNote Mac and Solaris setups are UNTESTED! They will probably break lol
     * @param OS The OS of the system. {@link #getOS()}
     * @return If the setup was required.
     */
    public static boolean setupRestart(OSTypes OS) throws IOException, IllegalArgumentException, InvalidConfigurationException {
        return setupRestart(OS, getServerJar().getName());
    }

    /**
     * Attempts to set up a batch/bash script for spigot to use when restarting.
     * <p>
     * The OS will be automatically detected. If it is unknown, it will throw {@link IllegalArgumentException}
     * @apiNote Mac and Solaris setups are UNTESTED! They will probably break lol
     * @return If the setup was required.
     */
    public static boolean setupRestart(String serverJar) throws IOException, IllegalArgumentException, InvalidConfigurationException {
        return setupRestart(getOS(), serverJar);
    }

    /**
     * Attempts to set up a batch/bash script for spigot to use when restarting.
     * @apiNote Mac and Solaris setups are UNTESTED! They will probably break lol
     * @return If the setup was required.
     */
    public static boolean setupRestart(OSTypes OS, String serverJar) throws IOException, IllegalArgumentException, InvalidConfigurationException {
        if (restartSetup()) {
            return false;
        }
        File restartBatch = new File("restart.bat");
        File restartUnix = new File("restart.sh");
        if (OS.equals(OSTypes.Windows)) {
            restartBatch.createNewFile();
        } else {
            restartUnix.createNewFile();
        }
        switch (OS) {
            case Windows:
                writeFile("java -jar " + serverJar + " --nogui", restartBatch);
                break;
            case Mac:
            case Unix:
                writeFile("#!/bin/sh" + lineTerminators.Unix + "java -jar " +   serverJar + " --nogui", restartUnix);
                break;
            case Solaris:
                writeFile("#!/usr/xpg4/bin/sh" + lineTerminators.Unix + "java -jar " +  serverJar + " --nogui", restartUnix);
                break;
        }

        File spigotyml = new File("spigot.yml");
        String newcontent;
        if (OS.equals(OSTypes.Windows)) {
            newcontent = readFile(spigotyml).replaceAll("restart-script: .*", "restart-script: restart.bat");
        } else {
            newcontent = readFile(spigotyml).replaceAll("restart-script: .*", "restart-script: ./restart.sh");
        }
        writeFile(newcontent, spigotyml);
        return true;
    }

    /**
     * Check if restarting is configured.
     * @return If it is configured.
     */
    public static boolean restartSetup() throws IOException, InvalidConfigurationException {
        String value = readYMLFile(new File("spigot.yml"), "settings.restart-script");
        String restartScriptName = value.replace("./", "");
        File restartScript = new File(restartScriptName);
        return restartScript.exists();
    }

    /**
     * Get a FileConfiguration.
     * If the config file doesn't exist, it will create the file.
     * @param plugin Your plugin.
     * @param filename The config name.
     * @return The FileConfiguration of the file.
     */
    public static FileConfiguration getConfig(JavaPlugin plugin, String filename) {
        // load the file's config
        YamlConfiguration config = new YamlConfiguration();
        File file = new File(plugin.getDataFolder(), filename);
        if (!file.exists()) {
            // file no exist
            file.getParentFile().mkdirs();
            plugin.saveResource(filename, true);
        }
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return config;
    }
}
