package pt.tecnico.grupo6.myDrive.service;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class DeleteFileService extends Service{
	
	Long _token;
	String _fileName;


	public DeleteFileService(Long token, String target){
		_token = token;
		_fileName = target;
	}
	
	public final void dispatch() throws MyDriveException{

		MyDrive md = MyDrive.getInstance();

		// se o token for invalido manda InvalidSessionException
		Login log = md.getLoginByToken(_token);
		// se o file nao existir manda FileDoesntExistException e PermissionDeniedException
		log.deleteFile(_fileName);
	}

}