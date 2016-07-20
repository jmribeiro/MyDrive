package pt.tecnico.grupo6.myDrive.domain;
import org.joda.time.LocalDate;
import pt.tecnico.grupo6.myDrive.exception.*;

public class RootDirectory extends RootDirectory_Base {
    
    private RootDirectory() {
        super();
    }

    public RootDirectory(MyDrive myDrive, Root owner){
        super();
        try{
            init("placeholder", owner);
            setMyDriveRoot(myDrive);
            setName("/");
        }catch(InvalidFileNameException e){
            //Unreachable
        
        }
    }

    @Override
    public File getFileByPath(String path) throws InvalidPathException, FileDoesntExistException{
        
        if(path.equals("..")){
            return this;
        }
        else{
            return super.getFileByPath(path);
        }      
    }
    public void remove(){
        setMyDriveRoot(null);
        super.remove();
    }
}
