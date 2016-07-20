package pt.tecnico.grupo6.myDrive.presentation;

import pt.tecnico.grupo6.myDrive.domain.*;

public class Key extends MdCommand {

    public Key(MdShell shell) { 
    	super(shell, "token", "Change between active sessions"); 
    }

    public void execute(String[] args) {
		Long token = shell().getCurrentToken();

		if(args.length == 0){
			System.out.println("Token: "+ token +" Username: "+ ((MdShell)shell()).getUsernameByToken(token));
		}else if(args.length == 1){
			Long tk = ((MdShell)shell()).getToken(args[0]);
			((MdShell)shell()).setCurrent(tk, args[0]);
			System.out.println("Token: "+ tk);
		}else{
			throw new RuntimeException("USAGE: "+name()+" [path]");
		}
    }
}