package pt.tecnico.grupo6.myDrive.presentation;

import pt.tecnico.grupo6.myDrive.service.ExecuteFileService;
import java.util.Arrays;

public class Execute extends MdCommand {

	Long token;
	ExecuteFileService service;

    public Execute(MdShell shell) { 
    	super(shell, "do", "Execute a file with input arguments"); 
    }

    public void execute(String[] args) {

		token = shell().getCurrentToken();

		if(args.length == 1){ // PATH
			service = new ExecuteFileService(token, args[0]);
			service.execute();

		}else if(args.length > 1){ // PATH + args
			service = new ExecuteFileService(token, args[0], Arrays.copyOfRange(args, 1, args.length));
			service.execute();

		}else{
			throw new RuntimeException("USAGE: "+name()+" path [args]");
		}
    }
}

