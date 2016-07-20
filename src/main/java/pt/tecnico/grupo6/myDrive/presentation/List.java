package pt.tecnico.grupo6.myDrive.presentation;

import pt.tecnico.grupo6.myDrive.service.ListDirectoryService;
import pt.tecnico.grupo6.myDrive.service.dto.ContentsDto;

public class List extends MdCommand {

    public List(MdShell shell) { 
    	super(shell, "ls", "List the Current Working Directory"); 
    }

    public void execute(String[] args) {

    	Long token = shell().getCurrentToken();
    	
		if(args.length == 0){
			
			ListDirectoryService service = new ListDirectoryService(token);
			service.execute();

			String dirList = ((ContentsDto)service.result()).getContent();
			System.out.println(dirList);

			// Long token = ((TokenDto)service.result()).getToken();
			// ((MdShell)shell()).addUser(token, args[0]);
						

		}else if(args.length == 1){
			
			ListDirectoryService service = new ListDirectoryService(token, args[0]);
			service.execute();

			String dirList = ((ContentsDto)service.result()).getContent();
			System.out.println(dirList);
			
			

		}else{
			throw new RuntimeException("USAGE: "+name()+" [path]");
		}
    }
}