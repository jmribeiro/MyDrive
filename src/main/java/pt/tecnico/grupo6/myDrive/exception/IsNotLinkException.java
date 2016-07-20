package pt.tecnico.grupo6.myDrive.exception;

public class IsNotLinkException extends MyDriveException{

	private final String _filename;

	public IsNotLinkException(String filename){
		_filename = filename;
	}

	@Override
	public String getMessage(){
		return "File '"+_filename+"' is not a link. Please pick a different filename";
	}

}
