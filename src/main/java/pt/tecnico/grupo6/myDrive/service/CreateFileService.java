package pt.tecnico.grupo6.myDrive.service;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class CreateFileService extends Service{
	
	private Long _token;
	private String _fileName, _content;
	private FileType _fileType;

	public CreateFileService(Long token, String fileName, FileType fileType, String content){
		_token = token;
		_fileName = fileName;
		_fileType = fileType;
		_content = content;
	}

	public final void dispatch() throws MyDriveException{

		MyDrive myDrive = Service.getMyDrive();
		Login login = myDrive.getLoginByToken(_token); // throws InvalidSessionException
		login.createFile(_fileName, _fileType, _content);

	}

}