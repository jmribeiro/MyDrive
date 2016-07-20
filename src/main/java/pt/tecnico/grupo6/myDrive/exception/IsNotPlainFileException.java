package pt.tecnico.grupo6.myDrive.exception;

public class IsNotPlainFileException extends MyDriveException{

	private final String _filename;

	public IsNotPlainFileException(String filename){
		_filename = filename;
	}

	@Override
	public String getMessage(){
		return "File '"+_filename+"' is not a plain file. Please pick a different filename";
	}

}
