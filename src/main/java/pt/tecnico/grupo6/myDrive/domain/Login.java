package pt.tecnico.grupo6.myDrive.domain;
import java.util.Random;
import java.math.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import pt.tecnico.grupo6.myDrive.exception.*;
import org.joda.time.*;
import org.joda.time.format.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.output.*;

public class Login extends Login_Base {

/* ################## */
/*    CONSTRUCTION    */
/* ################## */

    private Login() {
        super();
    }
    
    public Login(MyDrive myDrive, String username, String password) throws EmptyLoginFieldsException, UserDoesntExistException, WrongPasswordException {
    	
        super();

        if(username.equals("") || (!username.equals("nobody") && password.equals(""))){
            throw new EmptyLoginFieldsException();
        }

        User user = myDrive.getUserByUsername(username);

        if(user.checkPassword(password)){
            
            setToken(newToken());
            setMyDriveLogin(myDrive);
    		setLoggedUser(user);
    		setCurrentWorkingDirectory(user.getHome());

            if(username.equals("root")){
                refresh(10);  
            }else{
                refresh();
            }

    	}else{
    	   throw new WrongPasswordException();
    	}

    }
    
    private Long newToken(){
        Long token = new BigInteger(64, new Random()).longValue();
        while(MyDrive.getInstance().tokenExists(token) || token == 0){
            token = new BigInteger(64, new Random()).longValue();
        }
        return token;
    }

/* ################# */
/*    DESTRUCTION    */
/* ################# */

    public void remove(){
        
        getLoggedUser().logout(this);
        setLoggedUser(null);

        getCurrentWorkingDirectory().logout();
        setCurrentWorkingDirectory(null);

        getMyDriveLogin().removeLogin(getToken());
        setMyDriveLogin(null);
        
        cleanEnvironmentVariables();

        deleteDomainObject();
    }

/* ######################## */
/*    SERVICE OPERATIONS    */
/* ######################## */

    /* ######################## */
    /* Change Directory Service */
    /* ######################## */
    public String changeCurrentWorkingDirectory(String directoryPath) 
        throws InvalidSessionException, PermissionDeniedException, InvalidPathException, IsNotDirectoryException, FileDoesntExistException
    {
        CHECKEXPIRED();

        Directory d = getCurrentWorkingDirectory().getDirectoryByPath(directoryPath);
        if(getLoggedUser().canExecute(d.getAbsolutePath())){
            setCurrentWorkingDirectory(d);
        }else{
            throw new PermissionDeniedException();
        }

        return d.getAbsolutePath();
    }
    public String changeCurrentWorkingDirectory() 
        throws InvalidSessionException, PermissionDeniedException, InvalidPathException, IsNotDirectoryException, FileDoesntExistException
    {
        CHECKEXPIRED();

        Directory d = getCurrentWorkingDirectory();
        if(getLoggedUser().canExecute(d.getAbsolutePath())){
            setCurrentWorkingDirectory(d);
        }else{
            throw new PermissionDeniedException();
        }

        return d.getAbsolutePath();
    }
    /* ################### */
    /* Change File Service */
    /* ################### */
    public void createFile(String fileName, FileType fileType, String content)
        throws InvalidSessionException, PermissionDeniedException, InvalidContentException, InvalidFileTypeException
    {  
        CHECKEXPIRED();

        Directory cwd = getCurrentWorkingDirectory();
        User user = getLoggedUser();

        switch(fileType){
            case PLAINFILE:
                user.createPlainFile(fileName, cwd);
                PlainFile newPlainFile = (PlainFile) cwd.getFileByName(fileName);
                newPlainFile.setContent(content);
                break;
            case APPLICATION:
                user.createApplication(fileName, cwd);
                Application newApplication = (Application) cwd.getFileByName(fileName);
                newApplication.setMethod(content);
                break;
            case LINK:  
                if (content.equals("")) {throw new InvalidContentException();}
                user.createLink(fileName, cwd);
                Link newLink = ( Link) cwd.getFileByName(fileName);
                newLink.setPath(content);
                break;
            case DIRECTORY:
                if (! content.equals("")) {throw new InvalidContentException();}
                user.createDirectory(fileName, cwd);
                break;
            default:
                throw new InvalidFileTypeException();
        }
    }


    /* ################### */
    /* Delete File Service */
    /* ################### */
    public void deleteFile(String fileName) 
        throws InvalidSessionException, PermissionDeniedException, InvalidPathException, IsNotDirectoryException, FileDoesntExistException
    {
        CHECKEXPIRED();

        User user = getLoggedUser();
        Directory d = getCurrentWorkingDirectory();

        File f = d.getFileByName(fileName);

        user.deleteFile(f.getAbsolutePath());
    }
    /* ###################### */
    /* List Directory Service */
    /* ###################### */
    public String listCurrentWorkingDirectory() 
        throws InvalidSessionException, PermissionDeniedException
    {
        CHECKEXPIRED();
        
        User user = getLoggedUser();
        Directory cwd = getCurrentWorkingDirectory();

        return user.listDirectory(cwd.getAbsolutePath());
    }

    /* ################# */
    /* Read File Service */
    /* ################# */
    public String readFile(String fileName) 
        throws InvalidSessionException, InvalidPathException, FileDoesntExistException
    {
        CHECKEXPIRED();

        User user = getLoggedUser();
        Directory d = getCurrentWorkingDirectory();

        File f = d.getFileByName(fileName);
        
        return user.printFile(f.getAbsolutePath());
    }

    /* ################## */
    /* Write File Service */
    /* ################## */
    public String writeFile(String fileName, String newContent) 
        throws InvalidSessionException, InvalidFileNameException, IsNotPlainFileException, FileDoesntExistException, PermissionDeniedException
    {
        CHECKEXPIRED();

        String absPath = "";
        
        if(fileName.startsWith("/")){
            absPath = fileName;

        }else{
            Directory d = getCurrentWorkingDirectory();
            File file = d.getFileByName(fileName);
            absPath = file.getAbsolutePath();
        }

        User u = getLoggedUser();

        u.editPlainFile(absPath, newContent);

        return getCurrentWorkingDirectory().getPlainFileByPath(absPath).getContent();
    }

    /* #################### */
    /* Add Variable Service */
    /* #################### */
    public Set<EnvironmentVariable> addVariable(String name, String value){

        CHECKEXPIRED();

        for(EnvironmentVariable s : getSessionVariablesSet()){
            if(s.getName().equals(name))
                s.changeValue(value);
        }
        EnvironmentVariable ev = new EnvironmentVariable(this, name, value);
        addSessionVariables(ev);
        return getSessionVariablesSet();
    }

    /* #################### */
    /* Execute File Service */
    /* #################### */
    public void executeFile(String path, String[] args)         
        throws InvalidPathException, PermissionDeniedException, 
        FileDoesntExistException, ExecutionErrorException{

        // DEVE SER REFACTORIZADO


        CHECKEXPIRED();
        
        try{

            getLoggedUser().executeFile(path, args);


        }catch(ClassNotFoundException | InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new ExecutionErrorException(e.getMessage());
        }
        
    }
    
    /* ################## */
    /* Export XML Service */
    /* ################## */
    public void exportXml(String filename){
        CHECKEXPIRED();
        try{
            Document doc = MyDrive.getInstance().xmlExport();
            XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter(filename));
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("Something went wrong with exportXml");
        }
    }

    /* ###################### */
    /* List Variables Service */
    /* ###################### */
    public Set<EnvironmentVariable> listVariables(){
        CHECKEXPIRED();
        return getSessionVariablesSet();
    }


    /* ###################### */
    /* List Variables Service */
    /* ###################### */
    public EnvironmentVariable listVariable(String name) throws InvalidEnvironmentVariableException{
        
        CHECKEXPIRED();

        EnvironmentVariable target = null;

        for(EnvironmentVariable s : getSessionVariablesSet()){
            if(s.getName().equals(name))
                target = s;
        }

        if(target == null){
            throw new InvalidEnvironmentVariableException(name+" doesn't exist");
        }else{
            return target;
        }
    }

/* ####################### */
/*    Time To Live Mgmt    */
/* ####################### */

    public boolean isExpired(){
        return !(new DateTime().isBefore(getExpirationDate()));
    }

    private void CHECKEXPIRED() throws InvalidSessionException{
        if(isExpired()){
            remove();
            throw new InvalidSessionException();
        }else{
            refresh();
        }
    }

    public String expirationDate(){
        return getExpirationDate().toString();
    }

    public void refresh(){
        refreshHours(2);
    }

    private void refreshHours(int hours){
        refresh(hours*60);
    }

    private void refresh(int minutes){
        setExpirationDate(new DateTime());
        setExpirationDate(getExpirationDate().plusMinutes(minutes));

    }

    public String remainingTime(){

        DateTime expDate = getExpirationDate();
        DateTime now = new DateTime();

        Period period = new Period(now, expDate, PeriodType.dayTime());


        PeriodFormatter formatter = new PeriodFormatterBuilder()
        .printZeroIfSupported().minimumPrintedDigits(2)
        .appendDays().appendSuffix("d:").printZeroIfSupported().minimumPrintedDigits(2)
        .appendHours().appendSuffix("h:").printZeroIfSupported().minimumPrintedDigits(2)
        .appendMinutes().appendSuffix("m:").printZeroIfSupported().minimumPrintedDigits(2)
        .appendSeconds().minimumPrintedDigits(2).appendSuffix("s")
        .toFormatter();

        return formatter.print(period);
    }


/* ########################### */
/*    Environment Variables    */
/* ########################### */

    public void addEnvironmentVariable(String name, String value){
        EnvironmentVariable var = new EnvironmentVariable(this, name, value);
        addSessionVariables(var);
    }

    private void cleanEnvironmentVariables(){
        Iterator<EnvironmentVariable> it = getSessionVariablesSet().iterator();
        EnvironmentVariable current;
        while(it.hasNext()){
            current = it.next();
            current.remove();
        }
    }
}