package com.qbcraft.QBProtect.persistence;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

/**
 * Persistence module for QBProtect
 * Author: SangWolf27
 */
public class PersistenceModule {

    private final String  fileName;
    private final JavaPlugin  plugin;

    private File              configFile;
    private FileConfiguration fileConfiguration;

    public PersistenceModule(JavaPlugin plugin, String fileName){

        if(plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null.");
        if(!plugin.isInitialized())
            throw new IllegalArgumentException("Plugin must be initialized");

        this.plugin     = plugin;
        this.fileName   = fileName;

        File configFolder = plugin.getDataFolder();

        if(configFolder == null)
            throw new IllegalStateException();

        this.configFile = new File (this.plugin.getDataFolder(), this.fileName);
    }

    public void reloadConfig(){

        fileConfiguration = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defConfigStream = this.plugin.getResource(fileName);

        if(defConfigStream != null){

            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            fileConfiguration.setDefaults(defConfig);

        }
    }

    public FileConfiguration getConfig(){

        if(this.fileConfiguration == null){
            this.reloadConfig();
        }

        return (this.fileConfiguration);
    }

    public void saveConfig(){

        if(this.fileConfiguration == null || this.configFile == null)
            return;

        try{

            this.getConfig().save(this.configFile);

        } catch (IOException ex){

            this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, ex);

        }
    }

    public  void saveDefaultConfig(){

        if(!this.configFile.exists()){

            this.plugin.saveResource(this.fileName, false);
        }
    }
}
