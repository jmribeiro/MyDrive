package pt.tecnico.grupo6.myDrive.exception;

public class InvalidPathException extends MyDriveException{

	public InvalidPathException(){
		super("Path given is invalid.");
	}

	public InvalidPathException(String message){
		super(message);
	}
}
