package pt.tecnico.grupo6.myDrive.exception;

public class FileDoesntExistException extends MyDriveException{

	private final String _filename;

	public FileDoesntExistException(String filename){
		_filename = filename;
	}

	@Override
	public String getMessage(){
		return "File '"+_filename+"' doesn't exist. Please pick a different filename";
	}

}