package pt.tecnico.grupo6.myDrive.service.dto;

public class ContentsDto extends Dto {
	
	private String _content;

	public ContentsDto(String string){
		_content = string;
	}

	public String getContent(){
		return new String(_content);
	}
}

