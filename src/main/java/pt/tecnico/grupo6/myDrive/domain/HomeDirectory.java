package pt.tecnico.grupo6.myDrive.domain;
import org.joda.time.LocalDate;
import pt.tecnico.grupo6.myDrive.exception.*;

public class HomeDirectory extends HomeDirectory_Base {
    
    private HomeDirectory() {
        super();
    }

    public HomeDirectory(MyDrive myDrive, Root owner){
        super();
        init("placeholder", owner, myDrive.getRootFolder());
        setMyDriveHome(myDrive);
        setName("home");
    }
    
    public void remove(){
        setMyDriveHome(null);
        super.remove();
    }
}


