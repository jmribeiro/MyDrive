package pt.tecnico.grupo6.myDrive.exception;

public class IsNotDirectoryException extends MyDriveException{

	private final String _filename;

	public IsNotDirectoryException(String filename){
		_filename = filename;
	}

	@Override
	public String getMessage(){
		return "File '"+_filename+"' is not a directory. Please pick a different filename";
	}

}
