package com.qbcraft.QBProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.qbcraft.QBProtect.QBProtect;
/**
 * Command executor for the QBProtect access module
 * Author: SangWolf27
 */
public class AccessExecutor implements CommandExecutor{

    public  boolean           debugOn;
    public  QBProtect.context currentContext;
    public  String            currentObject;
    private QBProtect         qbProtect;

    public AccessExecutor(QBProtect qbProtect) {

        this.qbProtect      = qbProtect;
        this.debugOn        = false;
        this.currentContext = QBProtect.context.NONE;
        this.currentObject  = "";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String label, String[] args) {
        if(!(sender.hasPermission("qbprotect.access.manage"))){
            sender.sendMessage("You do not have permission to use that command.");
            return true;
        }
        if(args.length >= 1){
            if(args[0].equalsIgnoreCase("user")){
                if(args.length != 2){
                    sender.sendMessage("Usage: /qa user <username>");
                    return true;
                }else{
                    return qa_user(sender, args[1]);
                }
            }else if(args[0].equalsIgnoreCase("group")){
                if(args.length != 2){
                    sender.sendMessage("Usage: /qa group <groupname>");
                    return true;
                }else{
                    return qa_group(sender, args[1]);
                }
            }else if(args[0].equalsIgnoreCase("print")){
                if(args.length != 1){
                    sender.sendMessage("Usage: /qa print");
                    return true;
                }else{
                    return qa_print(sender);
                }
            }else if(args[0].equalsIgnoreCase("create")){
                if(args.length != 1){
                    sender.sendMessage("Usage: /qa create");
                    return true;
                }else{
                    return qa_create(sender);
                }
            }else if(args[0].equalsIgnoreCase("delete")){
                if(args.length != 1){
                    sender.sendMessage("Usage: /qa delete");
                    return true;
                }else{
                    return qa_delete(sender);
                }
            }else if(args[0].equalsIgnoreCase("set")){
                if(args.length != 2){
                    sender.sendMessage("Usage: \nGROUP context:/qa set <username> \nUSER context:/qa set <groupname>");
                    return true;
                }else{
                    return qa_set(sender, args[1]);
                }
            }else if(args[0].equalsIgnoreCase("+")){
                if(args.length != 2){
                    sender.sendMessage("Usage: /qa + <permission node>");
                    return true;
                }else{
                    return qa_add_perm(sender, args[1]);
                }
            }else if(args[0].equalsIgnoreCase("-")){
                if(args.length != 2){
                    sender.sendMessage("Usage: /qa - <permission node>");
                    return true;
                }else{
                    return qa_del_perm(sender, args[1]);
                }
            }else if(args[0].equalsIgnoreCase("done")){
                if(args.length != 1){
                    sender.sendMessage("Usage: /qa done");
                    return true;
                }else{
                    return qa_done(sender);
                }
            }else if(args[0].equalsIgnoreCase("reload")){
                if(args.length != 1){
                    sender.sendMessage("Usage: /qa reload");
                    return true;
                }else{
                    return qa_reload(sender);
                }
            }else if(args[0].equalsIgnoreCase("default")){
                if(args.length != 1){
                    sender.sendMessage("Usage:/qa default (GROUP CONTEXT ONLY)");
                    return true;
                }else{
                    if(this.currentContext != QBProtect.context.GROUP){
                        sender.sendMessage("You can only use this command in the group context.");
                        return true;
                    }
                    return qa_default(sender);
                }
            }else if(args[0].equalsIgnoreCase("inherit")){
                if(args.length != 2){
                    sender.sendMessage("Usage: /qa inherit <groupname> (GROUP CONTEXT ONLY)");
                    return true;
                }else{
                    if(this.currentContext != QBProtect.context.GROUP){
                        sender.sendMessage("You can only use this command in the group context.");
                        return true;
                    }
                    return qa_inherit(sender, args[1]);
                }
            }else if(args[0].equalsIgnoreCase("debug")){
                if(args.length != 2){
                    sender.sendMessage("Usage: /qa debug <on/off>");
                    return true;
                }else{
                    return qa_debug(sender, args[1]);
                }
            }
        }
        return false;
    }

    private boolean qa_user(CommandSender sender, String userName){

        this.currentContext = QBProtect.context.USER;
        this.currentObject  = userName;

        sender.sendMessage("\u00a7bUSER CONTEXT set. Now managing user " + userName);

        return true;
    }

    private boolean qa_group(CommandSender sender, String groupName){

        this.currentContext = QBProtect.context.GROUP;
        this.currentObject  = groupName;

        sender.sendMessage("\u00a7bGROUP CONTEXT set. Now managing group " + groupName);
        return true;
    }

    private boolean qa_print(CommandSender sender){

        if(this.currentContext == QBProtect.context.USER){

            sender.sendMessage("\u00a7bRetrieving perms for user " + this.currentObject);
            this.qbProtect.accessModule.printUser(sender,this.currentObject);

        }else if(this.currentContext == QBProtect.context.GROUP){

            sender.sendMessage("\u00a7bRetrieving perms for group " + this.currentObject);
            this.qbProtect.accessModule.printGroup(sender, this.currentObject);

        }else{

            sender.sendMessage("\u00a7bYou must set a context first. Try /qa user <username> or /qa group <groupname>");

        }
        return true;
    }

    private boolean qa_create(CommandSender sender){

        sender.sendMessage("\u00a7bWill create the applicable object...");
        this.qbProtect.accessModule.createGroup(sender, this.currentObject);
        return true;
    }

    private boolean qa_delete(CommandSender sender){

        sender.sendMessage("\u00a7bWill delete the applicable object...");
        return true;
    }

    private boolean qa_set(CommandSender sender, String setTo){

        sender.sendMessage("\u00a7bDepending on context, will set to " + setTo);
        if(this.currentContext == QBProtect.context.USER){

           this.qbProtect.accessModule.setUserGroup(sender, this.currentObject, setTo);

        }else if(this.currentContext == QBProtect.context.GROUP){

            this.qbProtect.accessModule.setGroupUser(sender, this.currentObject, setTo);

        }else{

            sender.sendMessage("\u00a7bYou must set a context first. Try /qa user <username> or /qa group <groupname>");
        }
        return true;
    }

    private boolean qa_add_perm(CommandSender sender, String permNode){

        sender.sendMessage("\u00a7bAdding perm node " + permNode + " to applicable object...");

        if(this.currentContext == QBProtect.context.USER){

            this.qbProtect.accessModule.setUserPermNode(sender, this.currentObject,permNode, true);

        }else if(this.currentContext == QBProtect.context.GROUP){

             this.qbProtect.accessModule.setGroupPermNode(sender, this.currentObject, permNode, true);

        }else{

            sender.sendMessage("\u00a7bYou must set a context first. Try /qa user <username> or /qa group <groupname>");
        }
        return true;
    }

    private boolean qa_del_perm(CommandSender sender, String permNode){

        sender.sendMessage("\u00a7bRemoving perm node " + permNode + " from applicable object...");

        if(this.currentContext == QBProtect.context.USER){

            this.qbProtect.accessModule.setUserPermNode(sender, this.currentObject,permNode, false);

        }else if(this.currentContext == QBProtect.context.GROUP){

            this.qbProtect.accessModule.setGroupPermNode(sender, this.currentObject, permNode, false);

        }else{

            sender.sendMessage("\u00a7bYou must set a context first. Try /qa user <username> or /qa group <groupname>");
        }
        return true;
    }

    private boolean qa_done(CommandSender sender){

        this.currentContext = QBProtect.context.NONE;
        this.currentObject  = "";

        this.qbProtect.accessModule.saveAll();
        sender.sendMessage("\u00a7bContext reset, perms saved.");

        return true;
    }

    private boolean qa_reload(CommandSender sender){

        sender.sendMessage("\u00a7bWill reload the config... ");
        return true;
    }

    private boolean qa_debug(CommandSender sender, String state){

        sender.sendMessage("\u00a7bWill turn the debug mode " + state);
        return true;
    }

    private boolean qa_default(CommandSender sender){

        sender.sendMessage("\u00a7bCheck GROUP CONTEXT only, and set current group to default.");
        return true;
    }

    private boolean qa_inherit(CommandSender sender, String inheritFrom){

        sender.sendMessage("\u00a7bCheck GROUP CONTEXT only, and set current group to inherit from " + inheritFrom);
        return true;
    }
}
