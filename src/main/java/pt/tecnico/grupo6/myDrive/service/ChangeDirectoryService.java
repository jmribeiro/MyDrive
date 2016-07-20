package pt.tecnico.grupo6.myDrive.service;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class ChangeDirectoryService extends Service{
	
	private Long _token;
	private String _path = null;

	public Dto _dto2;

	public ChangeDirectoryService(Long token, String path){
		_token = token;
		_path = path;
	}

	public ChangeDirectoryService(Long token){
		_token = token;
	}

	public final void dispatch() throws MyDriveException{
		MyDrive md = Service.getMyDrive();
		Login l = md.getLoginByToken(_token); // Invalid Token / Expired => InvalidSessionException
		
		if(_path != null){
			_dto = new ContentsDto(l.changeCurrentWorkingDirectory(_path));
		}else{
			_dto = new ContentsDto(l.changeCurrentWorkingDirectory());
		}
		
	}
}