package pt.tecnico.grupo6.myDrive.service;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class AddVariableService extends Service{
	
	private Long _token;
	private String _name, _value;

	public AddVariableService(Long token, String name, String value){
		_token = token;
		_name = name;
		_value = value;
	}
	
	public final void dispatch() throws MyDriveException{
		MyDrive myDrive = Service.getMyDrive();
		Login login = myDrive.getLoginByToken(_token); // throws InvalidSessionException
		_dto = new EnvironmentVariablesListDto(login.addVariable(_name, _value));
	}

}