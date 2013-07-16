package com.qbcraft.QBProtect.modules.access;

import com.qbcraft.QBProtect.persistence.PersistenceModule;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Access control module for QBProtect
 * Author: SangWolf27
 */
public class AccessModule implements Listener{

    public HashMap<String,PermissionAttachment> accessMap;
    public HashMap<String, ArrayList<String>>   groupMap;
    public Plugin                               plugin;
    public PersistenceModule                    persistenceModule;

    public AccessModule(Plugin parentPlugin){

        this.plugin            = parentPlugin;
        this.accessMap         = new HashMap<String, PermissionAttachment>();
        this.groupMap          = new HashMap<String, ArrayList<String>>();
        this.persistenceModule = new PersistenceModule((JavaPlugin) this.plugin, "accessConfig.yml");

        this.persistenceModule.saveDefaultConfig();

    }
    public void setUserPermNode(CommandSender sender, String playerName, String permNode, boolean set){

       PermissionAttachment attachment;

       if(!this.accessMap.containsKey(playerName)){

          attachment = new PermissionAttachment(this.plugin, Bukkit.getServer().getPlayerExact(playerName));
          this.accessMap.put(playerName,attachment);

       }else{

         attachment = this.accessMap.get(playerName);

       }

       attachment.setPermission(permNode, set);
       sender.sendMessage("\u00a7b" + playerName + " has been given " + permNode + ".");
    }
    public void setGroupPermNode(CommandSender sender, String groupName, String permNode, boolean set){

        PermissionAttachment attachment;
        ArrayList<String>    playerList;

        if(!this.groupMap.containsKey(groupName)){

            playerList = new ArrayList<String>();
            this.groupMap.put(groupName, playerList);

        }

        playerList = this.groupMap.get(groupName);

        for(String playerName : playerList){

            attachment = this.accessMap.get(playerName);
            attachment.setPermission(permNode, set);

        }

        sender.sendMessage("\u00a7bAll players in group " + groupName + " have been given " + permNode + ".");
    }
    public void removeAllAttachments()
    {
        Iterator it = this.accessMap.entrySet().iterator();

        while (it.hasNext()){

            Map.Entry currEntry = (Map.Entry)it.next();
            Player currPlayer   = this.plugin.getServer().getPlayerExact(currEntry.getKey().toString());

            currPlayer.removeAttachment((PermissionAttachment) currEntry.getValue());
            it.remove();
        }
    }
    public void setUserGroup(CommandSender sender, String userName, String GroupName)
    {
        if(!this.groupMap.containsKey(GroupName)){

            sender.sendMessage("Sorry, there is no such group.");

        }else{

           this.groupMap.get(GroupName).add(userName);

        }
    }
    public void setGroupUser(CommandSender sender, String groupName, String userName)
    {
        if(!this.groupMap.containsKey(groupName)){

            sender.sendMessage("This group has not been created yet. Type /qa create, and then try again.");
            return;

        }

        //TODO: What if the user is in another group? Transfer? Remove? Ignore?
        if(this.groupMap.get(groupName).contains(userName)){

            sender.sendMessage("The user already belongs to this group");

        }else{

            this.groupMap.get(groupName).add(userName);

        }
    }
    public void createGroup(CommandSender sender, String groupName)
    {
        if(!this.groupMap.containsKey(groupName)){

            ArrayList<String> playerList = new ArrayList<String>();
            this.groupMap.put(groupName,playerList);

            sender.sendMessage(groupName + " has been created.");

        }else{

            sender.sendMessage("This group has already been created.");

        }
    }
    public void printGroup(CommandSender sender, String groupName)
    {
       if(!this.groupMap.containsKey(groupName)){

           sender.sendMessage("This group does not exist.");
           return;

       }

       //All players in the group (for now) have the same perms.
       //TODO: Once the persistence layer is in, read this from the backend, since at the moment it deosn't cater for individual perms

       PermissionAttachment attachment;
       String playerName = this.groupMap.get(groupName).get(0);
       attachment        = this.accessMap.get(playerName);

       for(Map.Entry currEntry:attachment.getPermissions().entrySet()){

           String currPerm  = currEntry.getKey().toString();
           String currValue = currEntry.getValue().toString();

           if(currValue.equalsIgnoreCase("true"))
            sender.sendMessage("\u00a72" + currPerm + ": " + currValue);
           else
            sender.sendMessage("\u00a7c" + currPerm + ": " + currValue);

       }
    }
    public void printUser(CommandSender sender, String playerName)
    {
        if(!this.accessMap.containsKey(playerName)){

            sender.sendMessage("This player has not been created.");
            return;

        }

        PermissionAttachment attachment;
        attachment = this.accessMap.get(playerName);

        for(Map.Entry currEntry:attachment.getPermissions().entrySet()){

            String currPerm  = currEntry.getKey().toString();
            String currValue = currEntry.getValue().toString();

            if(currValue.equalsIgnoreCase("true"))
                sender.sendMessage("\u00a72" + currPerm + ": " + currValue);
            else
                sender.sendMessage("\u00a7c" + currPerm + ": " + currValue);
        }
    }

    public void saveAll(){

        this.persistenceModule.saveConfig();

    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent evt){

        PermissionAttachment attachment = evt.getPlayer().addAttachment(this.plugin);
        ArrayList<String> playerList    = new ArrayList<String>();

        this.accessMap.put(evt.getPlayer().getName(), attachment);

        playerList.add(evt.getPlayer().getName());
        this.groupMap.put("default",playerList);

        //TODO: Need to load existing perms for this user, once the persistence layer is coded...
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent evt){

        evt.getPlayer().removeAttachment(this.accessMap.get(evt.getPlayer().getName()));

    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent evt){

        evt.getPlayer().removeAttachment(this.accessMap.get(evt.getPlayer().getName()));

    }
}
