package pt.tecnico.grupo6.myDrive.presentation;

import pt.tecnico.grupo6.myDrive.service.ChangeDirectoryService;
import pt.tecnico.grupo6.myDrive.service.dto.ContentsDto;

public class ChangeWorkingDirectory extends MdCommand {

    public ChangeWorkingDirectory(MdShell shell) { 
    	super(shell, "cwd", "Change the Current Working Directory"); 
    }

    public void execute(String[] args) {
    	
    	Long token = shell().getCurrentToken();

		if(args.length == 1){
			ChangeDirectoryService service = new ChangeDirectoryService(token, args[0]);
			service.execute();
			String path = ((ContentsDto)service.result()).getContent();
			System.out.println(path);
		}else if(args.length == 0){
			ChangeDirectoryService service = new ChangeDirectoryService(token);
			service.execute();
			String path = ((ContentsDto)service.result()).getContent();
			System.out.println(path);
		}else{
			throw new RuntimeException("USAGE: "+name()+" [path]");
		}
    }
}