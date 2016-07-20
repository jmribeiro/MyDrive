package pt.tecnico.grupo6.myDrive.exception;

public class FileAlreadyExistsException extends MyDriveException{

	private final String _filename;

	public FileAlreadyExistsException(String filename){
		_filename = filename;
	}

	@Override
	public String getMessage(){
		return "File '"+_filename+"' already exists. Please pick a different filename";
	}
}