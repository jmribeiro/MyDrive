package pt.tecnico.grupo6.myDrive.presentation;
import pt.tecnico.grupo6.myDrive.service.LoginUserService;
import pt.tecnico.grupo6.myDrive.service.dto.TokenDto;

public class Login extends MdCommand {

    public Login(MdShell shell) { 
    	super(shell, "login", "login as an existing user"); 
    }

    public void execute(String[] args) {
		if(args.length == 1){
			
			LoginUserService service = new LoginUserService(args[0], "");
			service.execute();

			Long token = ((TokenDto)service.result()).getToken();
			((MdShell)shell()).addUser(token, args[0]);

		}else if(args.length == 2){
			
			LoginUserService service = new LoginUserService(args[0], args[1]);
			service.execute();
			
			Long token = ((TokenDto)service.result()).getToken();
			((MdShell)shell()).addUser(token, args[0]);

		}else{
			throw new RuntimeException("USAGE: "+name()+" <username> [password]");
		}
    }
}
