package pt.tecnico.grupo6.myDrive.exception;

public class UserAlreadyExistsException extends MyDriveException{
	
	private String _username;

	public UserAlreadyExistsException(String username){
		_username = username;
	}

	@Override
	public String getMessage(){
		return "User '"+_username+"' already exists. Please pick a different username";
	}
}