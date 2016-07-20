package pt.tecnico.grupo6.myDrive.exception;

public class InvalidXmlFileException extends MyDriveException{

	@Override
	public String getMessage(){
		return "The XML source file was manually tempered";
	}
}