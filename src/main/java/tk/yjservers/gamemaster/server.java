package tk.yjservers.gamemaster;

import org.bukkit.Bukkit;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class server {

    /**
     * Check and edit a property in the server's server.properties.
     * @param propertyToCheck The property in the file to check. This should be a regex.
     * @param correctConfig What should propertyToCheck be.
     * @param newProperty What should the correct line look like.
     * @param attemptMessage What message to log into the console when attempting to change the config.
     * @param successMessage What message to log into the console when successfully changing the config.
     * @return If a change was successful.
     */
    public boolean editServerProperties(String propertyToCheck, String correctConfig, String oldContent, String newProperty, String attemptMessage, String successMessage) {
        String checkedproperty = null;
        File serverpropfile = new File("server.properties");
        try {
            BufferedReader is = new BufferedReader(new FileReader(serverpropfile.getName()));
            Properties props = new Properties();
            props.load(is);
            checkedproperty = props.getProperty(propertyToCheck);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!Objects.equals(checkedproperty, correctConfig)) {
            Bukkit.getLogger().severe(attemptMessage);
            String newcontent = readFile(serverpropfile).replaceAll(oldContent, newProperty);
            return writeFile(newcontent, serverpropfile);
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
     * @param attemptMessage What message to log into the console when attempting to change the config.
     * @param successMessage What message to log into the console when successfully changing the config.
     * @return If a change was successful.
     */
    public boolean checkAndEditYAML(File fileToCheck, String propertyToCheck, String correctConfig, String newProperty, String attemptMessage, String successMessage) {
        try {
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
                Bukkit.getLogger().severe("Something went wrong while editing " + fileToCheck.getName() + ", This is a plugin issue, please wait for a new update! Inform me in spigot forums, when i check it lol");
            }

            assert neededProperty != null;
            String valueOfProperty = neededProperty.substring(neededProperty.lastIndexOf("=") + 1);
            // the value of neededProperty
            if (!Objects.equals(valueOfProperty, correctConfig)) {
                Bukkit.getLogger().severe(attemptMessage);
                String newcontent = readFile(fileToCheck).replaceAll(neededProperty, newProperty);
                return writeFile(newcontent, fileToCheck);
            } else {
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
     * @return Whether this operation was successful.
     */
    public boolean writeFile(String content, File file) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
