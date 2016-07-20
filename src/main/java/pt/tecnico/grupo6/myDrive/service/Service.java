package pt.tecnico.grupo6.myDrive.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.ist.fenixframework.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.exception.*;

import pt.tecnico.grupo6.myDrive.service.dto.*;

public abstract class Service{
	
	protected static final Logger log = LogManager.getRootLogger();
	
	protected Dto _dto;

	@Atomic
	public final void execute() throws MyDriveException{
		dispatch();
	}

	protected abstract void dispatch() throws MyDriveException;

	@Atomic
	static MyDrive getMyDrive(){
		return MyDrive.getInstance();
	}

	public Dto result(){
		return _dto;
	}
}
