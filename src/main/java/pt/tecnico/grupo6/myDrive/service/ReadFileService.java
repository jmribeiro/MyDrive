package pt.tecnico.grupo6.myDrive.service;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class ReadFileService extends Service{
	
	private Long _token;
	String _fileName;

	public ReadFileService(Long token, String fileName){
		_token    = token;
		_fileName = fileName;
	}

	public final void dispatch()throws MyDriveException{
		
		MyDrive md = Service.getMyDrive();
		
		Login log = md.getLoginByToken(_token);
		_dto = new ContentsDto(log.readFile(_fileName));
			
	}

}