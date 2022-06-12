package tk.yjservers.gamemaster;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tk.yjservers.gamemaster.GameMaster.plugin;

public class GameServer {

    /**
     * Read a property in server.properties.
     * <p>
     * You should use {@link #checkAndEditServerProperties(String, String, String, String)} if you want to edit server.properties.
     * @param propertyName Name of the property to check.
     * @return Value of the property.
     */
    public String readServerProperties(String propertyName) throws IOException {
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
     * @param newProperty What should the correct line look like.
     * @return If a change was required.
     */
    public boolean checkAndEditServerProperties(String propertyToCheck, String correctConfig, String oldContent, String newProperty) throws IOException {
        String checkedproperty = readServerProperties(propertyToCheck);

        if (!Objects.equals(checkedproperty, correctConfig)) {
            File propertiesFile = new File(Bukkit.getWorldContainer(), "server.properties");
            String newcontent = readFile(propertiesFile).replaceAll(oldContent, newProperty);
            writeFile(newcontent, propertiesFile);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Reads a property in a YAML file.
     * <p>
     * You should use {@link #checkAndEditYAML(File, String, String, String)} if you want to edit the YAML file.
     * @param ymlFile The file to check.
     * @param property The property in the file to check. This should be a regex.
     * @return The property value.
     */
    public String readYMLFile(File ymlFile, String property) throws FileNotFoundException {
        FileReader fr = new FileReader(ymlFile);
        Yaml yaml = new Yaml();
        String fullyaml = yaml.load(fr).toString();

        Pattern pattern = Pattern.compile(property);
        Matcher matcher = pattern.matcher(fullyaml);
        // neededproperty is the line where the property is concerned
        String neededProperty = null;
        if (matcher.find()) {
            neededProperty = matcher.group(0);
        } else {
            Bukkit.getLogger().severe("Something went wrong while editing " + ymlFile.getName() + ", This is a plugin issue!");
        }

        return Objects.requireNonNull(neededProperty).substring(neededProperty.lastIndexOf("=") + 1);
    }

    /**
     * Check and edit a property in a YAML file.
     * @param fileToCheck The file to check.
     * @param propertyToCheck The property in the file to check. This should be a regex.
     * @param correctConfig What should propertyToCheck be.
     * @param newProperty What should the correct line look like.
     * @return If a change was required.
     */
    public boolean checkAndEditYAML(File fileToCheck, String propertyToCheck, String correctConfig, String newProperty) throws IOException {
        String valueOfProperty = readYMLFile(fileToCheck, propertyToCheck);
        // the value of neededProperty

        if (!Objects.equals(valueOfProperty, correctConfig)) {
            String newcontent = readFile(fileToCheck).replaceAll(propertyToCheck, newProperty);
            writeFile(newcontent, fileToCheck);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Restarts a server.
     */
    public void restart() {
        Bukkit.spigot().restart();
    }

    /**
     * Reads a file and returns the string of the contents.
     * @param file The file to read.
     * @return The string of contents. This includes line separators.
     */
    public String readFile(File file) {
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
    public void writeFile(String content, File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }

    /**
     * Get the servers jar file. (Example: spigot-1.18.2.jar)
     * @return Server's jar file.
     */
    public File getServerJar() {
        return new File(org.bukkit.craftbukkit.bootstrap.Main.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath());

    }

    /**
     * Check and edits common server properties.
     * @param disableSpawnProtection Disable spawn protection?
     * @param disableNether Disable nether?
     * @param disableEnd Disable end?
     * @param enableFlight Disable minecraft's anticheat? (it sucks)
     * @return If a change was required.
     */
    public boolean checkForServerProperties(boolean disableSpawnProtection, boolean disableNether, boolean disableEnd, boolean enableFlight) throws IOException {
        boolean neededChange = false;
        if (disableSpawnProtection) {
            neededChange = checkAndEditServerProperties("spawn-protection", "0", "spawn-protection=\\d+", "spawn-protection=0");
        }
        if (disableNether) {
            neededChange = checkAndEditServerProperties("allow-nether", "false", "allow-nether=[a-zA-Z]+", "allow-nether=false");
        }
        if (disableEnd) {
            neededChange = checkAndEditYAML(new File("bukkit.yml"), "allow-end=[a-zA-Z]+", "false", "allow-end: false");
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
    public boolean checkForServerProperties() throws IOException {
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
    public OSTypes getOS() {
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
        public String getString() {return lineTerminators;}
    }

    /**
     * Attempts to setup a batch/bash script for spigot to use when restarting.
     * @apiNote Mac and Solaris setups are UNTESTED! They will probably break lol
     * @param OS The OS of the system. {@link #getOS()}
     * @return If the setup was required.
     */
    public boolean setupRestart(OSTypes OS) throws IOException, IllegalArgumentException{
        if (OS.equals(OSTypes.Unknown)) {
            throw new IllegalArgumentException("Unknown operating system given as parameter!");
        }
        String restartScriptName = readYMLFile(new File("spigot.yml"), "restart-script: .+");
        File restartScript = new File(restartScriptName);
        if (restartScript.exists()) {
            return false;
        }

        File restartBatch = new File("restart.bat");
        File restartUnix = new File("restart.sh");
        if (OS.equals(OSTypes.Windows)) {
            restartBatch.createNewFile();
        } else {
            restartBatch.createNewFile();
        }
        switch (OS) {
            case Windows:
                writeFile("java -jar " + getServerJar().getName() + " --nogui", restartBatch);
            case Mac:
            case Unix:
                writeFile("#!/bin/sh" + lineTerminators.Unix + "java -jar " + getServerJar().getName() + " --nogui", restartUnix);
            case Solaris:
                writeFile("#!/usr/xpg4/bin/sh" + lineTerminators.Unix + "java -jar " + getServerJar().getName() + " --nogui", restartUnix);
        }

        if (OS.equals(OSTypes.Windows)) {
            checkAndEditYAML(new File("spigot.yml"), "restart-script: .+", restartScriptName, "restart-script: restart.bat");
        } else {
            checkAndEditYAML(new File("spigot.yml"), "restart-script: .+", restartScriptName, "restart-script: restart.sh");
        }
        return true;
    }

    /**
     * Get a FileConfiguration.
     * If the config file doesn't exist, it will create the file.
     * @param plugin Your plugin.
     * @param filename The config name.
     * @return The FileConfiguration of the file.
     */
    private FileConfiguration getConfig(JavaPlugin plugin, String filename) {
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
