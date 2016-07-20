package pt.tecnico.grupo6.myDrive.service;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class LoginUserService extends Service{
	
	private String _username, _password;

	public LoginUserService(String username, String password){
		_username = username;
		_password = password;
	}

	public final void dispatch() throws MyDriveException{
		MyDrive md = Service.getMyDrive();
		_dto = new TokenDto(md.login(_username, _password));
	}

}