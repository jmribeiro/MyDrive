package pt.tecnico.grupo6.myDrive.service;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

public class ListDirectoryService extends Service{
	
	private Long tk;
	private String dirToList;

	public ListDirectoryService(Long token){
		tk = token;
		dirToList = "";
	}

	public ListDirectoryService(Long token, String path){
		tk = token;
		dirToList = path;
	}

	public final void dispatch() throws MyDriveException{

		MyDrive md = MyDrive.getInstance();
		Login log = md.getLoginByToken(tk);

		if(dirToList == ""){
			_dto = new ContentsDto(log.listCurrentWorkingDirectory());

		}else{
			log.changeCurrentWorkingDirectory(dirToList);
			_dto = new ContentsDto(log.listCurrentWorkingDirectory());
		}

		

	}

}