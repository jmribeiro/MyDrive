package pt.tecnico.grupo6.myDrive.domain;

public enum Permission{

	OWNER_READ(0), OWNER_WRITE(1), OWNER_EXECUTE(2), OWNER_DELETE(3),
	READ(4), WRITE(5), EXECUTE(6), DELETE(7);
	
	private int _index;

	private Permission(int index){
		_index = index;
	}
	
	public int getValue(){
		return _index;
	}

	private static boolean hasPermission(Permission p, String mask){
		return validMask(mask) && mask.charAt(p.getValue())!='-';
	}

	public static boolean hasPermission(Permission p, String mask1, String mask2){
		return validMask(mask1) && validMask(mask2) && hasPermission(p, mask1) && hasPermission(p, mask2);
	}

	public static boolean equals(String mask1, String mask2){
		return validMask(mask1) && validMask(mask2) && mask1.equals(mask2);
	}	

	@Deprecated
	public static boolean canRead(String userMask, String fileMask){
		return hasPermission(READ, userMask, fileMask);
	}
	
	@Deprecated
	public static boolean canWrite(String userMask, String fileMask){
		return hasPermission(WRITE, userMask, fileMask);
	}
	
	@Deprecated
	public static boolean canExecute(String userMask, String fileMask){
		return hasPermission(EXECUTE, userMask, fileMask);
	}
	
	@Deprecated
	public static boolean canDelete(String userMask, String fileMask){
		return hasPermission(DELETE, userMask, fileMask);
	}

	public static boolean validMask(String mask){

		if(mask.length() != 8){
			return false;
		}

		char owner_read, owner_write, owner_execute, owner_delete, read, write, execute, delete;
		
		owner_read = mask.charAt(OWNER_READ.getValue());
		owner_write = mask.charAt(OWNER_WRITE.getValue());
		owner_execute = mask.charAt(OWNER_EXECUTE.getValue());
		owner_delete = mask.charAt(OWNER_DELETE.getValue());
		read = mask.charAt(READ.getValue());
		write = mask.charAt(WRITE.getValue());
		execute = mask.charAt(EXECUTE.getValue());
		delete = mask.charAt(DELETE.getValue());
		
		if((owner_read!='r' && owner_read!='-') || (read!='r' && read!='-')){
			return false;
		}else if((owner_write!='w' && owner_write!='-') || (write!='w' && write!='-')){
			return false;
		}else if((owner_execute!='x' && owner_execute!='-') || (execute!='x' && execute!='-')){
			return false;
		}else if((owner_delete!='d' && owner_delete!='-') || (delete!='d' && delete!='-')){
			return false;
		}else{
			return true;
		}
	}


}