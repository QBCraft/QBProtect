package com.qbcraft.QBProtect;

//import org.bukkit.Bukkit;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.player.PlayerPickupItemEvent;

import com.qbcraft.QBProtect.commands.AccessExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import com.qbcraft.QBProtect.modules.access.AccessModule;


/*********************************************************************
 Your one-stop permissions, ranks and protection plugin
 optimized for Creative servers.

 @author SangWolf27
 @version 1.0
 *********************************************************************/
public class QBProtect extends JavaPlugin{
    public enum context{NONE,USER,GROUP}
    public      AccessModule accessModule = new AccessModule(this);

    @Override
    public void onEnable(){
        this.saveDefaultConfig();

        getServer().getPluginManager().registerEvents(accessModule, this);
        //Set the executors for all the commands.
        getCommand("qa").setExecutor(new AccessExecutor(this));
    }
    @Override
    public void onDisable(){
        this.saveDefaultConfig();

        this.accessModule.removeAllAttachments();
    }
}