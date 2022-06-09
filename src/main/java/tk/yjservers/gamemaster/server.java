package tk.yjservers.gamemaster;

import org.bukkit.Bukkit;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class server {

    public boolean editServerProperties(String propertyToCheck, String correctConfig, String oldContent, String newContent, String attemptMessage, String successMessage) {
        String checkedproperty = null;
        try {
            BufferedReader is = new BufferedReader(new FileReader("server.properties"));
            Properties props = new Properties();
            props.load(is);
            checkedproperty = props.getProperty(propertyToCheck);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!Objects.equals(checkedproperty, correctConfig)) {
            Bukkit.getLogger().severe(attemptMessage);
            StringBuilder oldcontent = new StringBuilder();
            try {
                File serverpropfile = new File("server.properties");
                BufferedReader br = new BufferedReader(new FileReader(serverpropfile));
                String line = br.readLine();
                while (line != null)
                {
                    oldcontent.append(line).append(System.lineSeparator());
                    line = br.readLine();
                }
                String newcontent = oldcontent.toString().replaceAll(oldContent, newContent);
                FileWriter writer = new FileWriter(serverpropfile);
                writer.write(newcontent);
                br.close();
                writer.close();
                Bukkit.getLogger().severe(successMessage);
                return true;
            } catch (IOException e) {
                Bukkit.getLogger().severe("Something went wrong while trying to disable " + propertyToCheck + "! Error log below: ");
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean editServerPropertiesYAML(File filetocheck, String propertyToCheck, Object correctConfig, String oldContent, String newContent, String attemptMessage, String successMessage) {
        try {
            FileReader fr = new FileReader(filetocheck);
            Yaml yaml = new Yaml();
            String fullyaml = yaml.load(fr).toString();
            Pattern pattern = Pattern.compile(propertyToCheck);
            Matcher matcher = pattern.matcher(fullyaml);
            String neededproperty = null;
            if (matcher.find()) {
                neededproperty = matcher.group(0);
            } else {
                Bukkit.getLogger().severe("Something went wrong while editing " + filetocheck.getName() + ", This is a plugin issue, please wait for a new update! Inform me in spigot fourms, when i check it lol");
            }
            assert neededproperty != null;
            String valueofproperty = neededproperty.substring(neededproperty.lastIndexOf("=") + 1);
            if (!Objects.equals(valueofproperty, correctConfig)) {
                Bukkit.getLogger().severe(attemptMessage);
                StringBuilder oldcontent = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(filetocheck));
                    String line = br.readLine();
                    while (line != null)
                    {
                        oldcontent.append(line).append(System.lineSeparator());
                        line = br.readLine();
                    }
                    String newcontent = oldcontent.toString().replaceAll(oldContent, newContent);
                    FileWriter writer = new FileWriter(filetocheck);
                    writer.write(newcontent);
                    br.close();
                    writer.close();
                    Bukkit.getLogger().severe(successMessage);
                    return true;
                } catch (IOException e) {
                    Bukkit.getLogger().severe("Something went wrong while trying to disable " + propertyToCheck + "! Error log below: ");
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void restartForConfig() {
        Bukkit.getLogger().severe("Restarting the server now for edits to take effect. This might take a while!");
        Bukkit.spigot().restart();
    }
}
