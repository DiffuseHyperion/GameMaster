package tk.yjservers.gamemaster;

import org.bukkit.Bukkit;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameServer {

    /**
     * Read a property in server.properties.
     * <p>
     * You should use {@link #editServerProperties(String, String, String, String)} if you want to edit server.properties.
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
    public boolean editServerProperties(String propertyToCheck, String correctConfig, String oldContent, String newProperty) throws IOException {
        String checkedproperty = readServerProperties(propertyToCheck);
        File propertiesFile = new File(Bukkit.getWorldContainer(), "server.properties");
        if (!Objects.equals(checkedproperty, correctConfig)) {
            String newcontent = readFile(propertiesFile).replaceAll(oldContent, newProperty);
            writeFile(newcontent, propertiesFile);
            return true;
        } else {
            return false;
        }
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
        FileReader fr = new FileReader(fileToCheck);
        Yaml yaml = new Yaml();
        String fullyaml = yaml.load(fr).toString();

        Pattern pattern = Pattern.compile(propertyToCheck);
        Matcher matcher = pattern.matcher(fullyaml);
        // neededproperty is the line where the property is concerned
        String neededProperty = null;
        if (matcher.find()) {
            neededProperty = matcher.group(0);
        } else {
            Bukkit.getLogger().severe("Something went wrong while editing " + fileToCheck.getName() + ", This is a plugin issue!");
        }

        String valueOfProperty = Objects.requireNonNull(neededProperty).substring(neededProperty.lastIndexOf("=") + 1);
        // the value of neededProperty

        if (!Objects.equals(valueOfProperty, correctConfig)) {
            String newcontent = readFile(fileToCheck).replaceAll(neededProperty, newProperty);
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
            neededChange = this.editServerProperties("spawn-protection", "0", "spawn-protection=\\d+", "spawn-protection=0");
        }
        if (disableNether) {
            neededChange = this.editServerProperties("allow-nether", "false", "allow-nether=[a-zA-Z]+", "allow-nether=false");
        }
        if (disableEnd) {
            neededChange = this.checkAndEditYAML(new File("bukkit.yml"), "allow-end=[a-zA-Z]+", "false", "allow-end: false");
        }
        if (enableFlight) {
            neededChange = this.editServerProperties("allow-flight", "true", "allow-flight=[a-zA-Z]+", "allow-flight=true");
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
}
