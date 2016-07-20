package pt.tecnico.grupo6.myDrive.service.dto;

public class TokenDto extends Dto {
	
	private Long _token;

	public TokenDto(Long token){
		_token = token;
	}

	public Long getToken(){
		return new Long(_token.longValue());
	}
}

