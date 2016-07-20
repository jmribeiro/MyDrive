package pt.tecnico.grupo6.myDrive.presentation;

import pt.tecnico.grupo6.myDrive.service.*;
import pt.tecnico.grupo6.myDrive.service.dto.EnvironmentVariablesListDto;

import java.util.*;

public class Environment extends MdCommand {

    public Environment(MdShell shell) { 
    	super(shell, "env", "Create, change or view an Environment Variable"); 
    }

    public void execute(String[] args) {
    	
    	Long token = shell().getCurrentToken();

		if(args.length == 0){//Imprime todas as vars ambiente
			
			ListVariablesService service = new ListVariablesService(token);
			service.execute();

			Map<String, String> variables = ((EnvironmentVariablesListDto)service.result()).getEnvironmentVariables();
			printVariables(variables);

		}else if(args.length == 1){//Imprime o value da var dada
			ListVariablesService service = new ListVariablesService(token, args[0]);
			service.execute();

			String value = ((EnvironmentVariablesListDto)service.result()).getValue();
			System.out.println(args[0]+"="+value);
		}else if(args.length == 2){
			
			AddVariableService service = new AddVariableService(token, args[0], args[1]);
			service.execute();
			
			Map<String, String> variables = ((EnvironmentVariablesListDto)service.result()).getEnvironmentVariables();
			
			printVariables(variables);
		}else{
			throw new RuntimeException("USAGE: "+name()+" [name [value]]");
		}
    }

    private static void printVariables(Map<String, String> variables){
    	Iterator it = variables.keySet().iterator();
    	while(it.hasNext()){
    		String currentName = (String)it.next();
    		System.out.println(currentName+"="+variables.get(currentName));
    	}
    }

}