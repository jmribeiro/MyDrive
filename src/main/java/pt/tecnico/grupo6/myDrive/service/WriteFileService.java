package pt.tecnico.grupo6.myDrive.service;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class WriteFileService extends Service{
	
	Long _token;
	String _file;
	String _content;

	public WriteFileService(Long token, String fileName, String newContent){
		_token = token;
		_file = fileName;
		_content = newContent;
	}

	public final void dispatch() throws MyDriveException{

		MyDrive md = Service.getMyDrive();	
		Login l = md.getLoginByToken(_token);	
		_dto = new ContentsDto(l.writeFile(_file, _content));
	}

}