package pt.tecnico.grupo6.myDrive.exception;

public class InvalidPasswordException extends MyDriveException{

	public InvalidPasswordException(){

	}

	@Override
	public String getMessage(){
		return "Invalid password, must have atleast 8 characters , Please pick a different password";
	}

}