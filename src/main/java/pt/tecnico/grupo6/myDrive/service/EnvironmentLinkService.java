package pt.tecnico.grupo6.myDrive.service;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class EnvironmentLinkService extends Service{
	
	private Long _token;
	private String _path;

	public EnvironmentLinkService(Long token, String path){
		_token = token;
		_path = path;
	}

	public final void dispatch() throws MyDriveException{
		//MOCKED EXAMPLE
	}
}