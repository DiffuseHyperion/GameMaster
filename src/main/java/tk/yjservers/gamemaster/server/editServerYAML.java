package tk.yjservers.gamemaster.server;

import org.bukkit.Bukkit;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class editServerYAML {
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
}
