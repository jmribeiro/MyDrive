package pt.tecnico.grupo6.myDrive.exception;

public class UserDoesntExistException extends MyDriveException{

	private final String _username;

	public UserDoesntExistException(String username){
		_username = username;
	}

	@Override
	public String getMessage(){
		return "User '"+_username+"' doesn't exists. Please pick a different username";
	}

}