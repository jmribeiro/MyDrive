package pt.tecnico.grupo6.myDrive.presentation;

import pt.tecnico.grupo6.myDrive.service.ExportXmlFileService;

public class ExportXmlFile extends MdCommand {

    public ExportXmlFile(MdShell shell) { 
    	super(shell, "export", "Export"); 
    }

    public void execute(String[] args) {

        Long token = shell().getCurrentToken();
        if(args.length == 0){

            ExportXmlFileService service = new ExportXmlFileService(token);
            service.execute();

        } else if(args.length == 1){ 

            ExportXmlFileService service = new ExportXmlFileService(token, args[0]);
            service.execute();
            
        }else{
            throw new RuntimeException("USAGE: "+name()+" [filename] ");
        }
    }
}