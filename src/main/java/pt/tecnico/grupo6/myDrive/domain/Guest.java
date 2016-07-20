package pt.tecnico.grupo6.myDrive.domain;
import pt.tecnico.grupo6.myDrive.exception.*;

public class Guest extends Guest_Base {
    
	static Guest getInstance() {
		Guest guest = MyDrive.getInstance().getGuestUser();
		if(guest != null){
			return guest;
		}else{
			return new Guest();
		}
	}
    
    private Guest(){
        super();

        MyDrive filesystem = MyDrive.getInstance();
        
        init(filesystem, "nobody", "", "Guest", "rwxdr-x-");
        
    	filesystem.setGuestUser(this);
    	setFileSystemGuest(filesystem);
    }

    @Override
    public Directory createHome(Directory home){

        Directory homeDirectory = new Directory(getUsername(), this, home);
        setHome(homeDirectory);
        addFiles(home);
        return homeDirectory;
    }


    public boolean canDelete(String absolutePath) throws InvalidPathException, FileDoesntExistException{
        File file = searchFile(absolutePath);
        return false;
    }

    @Override
    public boolean canDelete(File file){
        return false;
    }

    @Override
    public boolean checkPassword(String password){
        return password.equals(getPassword());
    }
    
    @Override
    public void remove() throws PermissionDeniedException{
        throw new PermissionDeniedException();
    }

    void removeFromDatabase(){
        deleteDomainObject();
    }

}


