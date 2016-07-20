package pt.tecnico.grupo6.myDrive.exception;

public class EmptyLoginFieldsException extends MyDriveException{

	public EmptyLoginFieldsException(){

	}

	public EmptyLoginFieldsException(String message){
		super(message);
	}

}