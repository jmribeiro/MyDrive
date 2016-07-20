package pt.tecnico.grupo6.myDrive.exception;

public class WrongPasswordException extends MyDriveException{

	public WrongPasswordException(){
		super();
	}
	public WrongPasswordException(String message){
		super(message);
	}
}