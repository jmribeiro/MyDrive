package pt.tecnico.grupo6.myDrive.service;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class ExecuteAssociationService extends Service{
	
	private Long _token;
	private String _path;

	public ExecuteAssociationService(Long token, String path){
		_token = token;
		_path = path;
	}

	public final void dispatch() throws MyDriveException{
		//MOCKED EXAMPLE
	}
}