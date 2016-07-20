package pt.tecnico.grupo6.myDrive.service.dto;
import pt.tecnico.grupo6.myDrive.domain.EnvironmentVariable;
import java.util.*;

public class EnvironmentVariablesListDto extends Dto {
	
	private Map<String, String> _vars;
	private String _name;
	private String _value;

	public EnvironmentVariablesListDto(Set<EnvironmentVariable> variablesSet){
		
		_vars = new TreeMap<String, String>();

		for(EnvironmentVariable var : variablesSet){
			_vars.put(new String(var.getName()), new String(var.getValue()));
		}
	}

	public EnvironmentVariablesListDto(EnvironmentVariable var){
		_name = new String(var.getName());
		_value = new String(var.getValue());
	}

	public Map<String, String> getEnvironmentVariables(){
		return _vars;
	}

	public String getValue(){
		return _value;
	}
}