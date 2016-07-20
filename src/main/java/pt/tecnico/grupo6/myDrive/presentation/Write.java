package pt.tecnico.grupo6.myDrive.presentation;

import pt.tecnico.grupo6.myDrive.service.WriteFileService;

public class Write extends MdCommand {

    public Write(MdShell shell) { 
    	super(shell, "update", "Change the content of a file to a input text"); 
    }

    public void execute(String[] args) {

    	Long token = shell().getCurrentToken();

    	if(args.length == 2){
			WriteFileService service = new WriteFileService(token, args[0], args[1]);
			service.execute();
		}else{
			throw new RuntimeException("USAGE: "+name()+" path text");
		}
    }
}