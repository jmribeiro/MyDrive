package pt.tecnico.grupo6.myDrive.exception;

public class FileIsNotReadableException extends MyDriveException{

	private final String _filename;

	public FileIsNotReadableException(String filename){
		_filename = filename;
	}

	@Override
	public String getMessage(){
		return "File '"+_filename+"' is not readable. Please pick a text file";
	}
}