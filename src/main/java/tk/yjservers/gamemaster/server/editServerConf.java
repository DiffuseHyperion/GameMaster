package tk.yjservers.gamemaster.server;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

public class editServerConf {
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
}
