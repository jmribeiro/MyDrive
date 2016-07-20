package pt.tecnico.grupo6.myDrive.service;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;
import java.lang.reflect.*;

public class ExecuteFileService extends Service{

	private Long _token;
	private String _path;
	private String[] _args;

	public ExecuteFileService(Long token, String path, String[] args){
		_token = token;
		_path = path;
		_args = args;
	}

	public ExecuteFileService(Long token, String path){
		_token = token;
		_path = path;
	}
	
	public final void dispatch() throws MyDriveException {		
		MyDrive md = Service.getMyDrive();
		Login login = md.getLoginByToken(_token);

		login.executeFile(_path, _args);
	}

}