package pt.tecnico.grupo6.myDrive.service;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class ListVariablesService extends Service{
	
	private Long _token;
	private String _name;

	public ListVariablesService(Long token, String name){
		_token = token;
		_name = name;
	}

	public ListVariablesService(Long token){
		_token = token;
	}

	public final void dispatch() throws MyDriveException{
		MyDrive myDrive = Service.getMyDrive();
		Login login = myDrive.getLoginByToken(_token); // throws InvalidSessionException

		if(_name == null){
			_dto = new EnvironmentVariablesListDto(login.listVariables());
		}else{
			_dto = new EnvironmentVariablesListDto(login.listVariable(_name));
		}
		
	}

}