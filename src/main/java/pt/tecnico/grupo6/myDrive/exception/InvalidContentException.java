package pt.tecnico.grupo6.myDrive.exception;

public class InvalidContentException extends MyDriveException{

	public InvalidContentException(){
		super("Content not compatible with file type");
	}

	public InvalidContentException(String message){
		super(message);
	}
}
