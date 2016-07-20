package pt.tecnico.grupo6.myDrive.exception;

public class IsNotApplicationException extends MyDriveException{

	private final String _filename;

	public IsNotApplicationException(String filename){
		_filename = filename;
	}

	@Override
	public String getMessage(){
		return "File '"+_filename+"' is not an application. Please pick a different filename";
	}

}
