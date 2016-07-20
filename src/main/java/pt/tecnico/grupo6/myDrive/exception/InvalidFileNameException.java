package pt.tecnico.grupo6.myDrive.exception;

public class InvalidFileNameException extends MyDriveException{

	public InvalidFileNameException(){
	}

	@Override
	public String getMessage(){
		return "File name cannot be empty and must contain only letters and decimal digits.";
	}
}