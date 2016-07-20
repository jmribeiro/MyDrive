package pt.tecnico.grupo6.myDrive.exception;

public class InvalidUsernameException extends MyDriveException{

	public InvalidUsernameException(){
	}

	@Override
	public String getMessage(){
		return "Username cannot be empty and must contain only letters and decimal digits.";
	}
}