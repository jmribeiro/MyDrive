package pt.tecnico.grupo6.myDrive.exception;

public class InvalidFileTypeException extends MyDriveException{

	public InvalidFileTypeException(){
	}

	@Override
	public String getMessage(){
		return "File type is invalid.";
	}
}