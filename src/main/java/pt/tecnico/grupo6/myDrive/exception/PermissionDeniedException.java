package pt.tecnico.grupo6.myDrive.exception;

public class PermissionDeniedException extends MyDriveException {

	private final String _message;

	public PermissionDeniedException(String username) {
		
		_message = "User '" + username + "' doesn't have permission to access this file.";
	}

	public PermissionDeniedException() {
		_message = "you dont have permission to do that";
	}



	@Override
	public String getMessage() {
		return _message;
	}
}