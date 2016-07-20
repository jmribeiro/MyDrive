package pt.tecnico.grupo6.myDrive.domain;

import pt.tecnico.grupo6.myDrive.exception.*;

import org.jdom2.Element;
import org.jdom2.Document;
import java.lang.reflect.*;

public class User extends User_Base {

/* ################## */
/*    Construction    */
/* ################## */
    
    protected User(){
        super();
    }

    public User(MyDrive myDrive, String username) throws InvalidUsernameException{
        super();
        init(myDrive, username);
    }

    public User(MyDrive myDrive, String username, String password){
        super();
        init(myDrive, username, password, username, "rwxd----");
    }

    public User(MyDrive myDrive, String username, String password, String name) throws InvalidUsernameException{
        super();
        init(myDrive, username, password, name);
    }

    public User(MyDrive myDrive, String username, String password, String name, String mask) throws InvalidUsernameException{
        super();
        init(myDrive, username, password, name, mask);
    }

    public User(MyDrive myDrive, Element xmlUser) throws InvalidUsernameException{
        super();

        String username = xmlUser.getChildText("username");
        String name = xmlUser.getChildText("name");
        String password = xmlUser.getChildText("password");
        String mask = xmlUser.getChildText("mask");
        
        init(myDrive, username, password, name, mask);
    }

    protected void init(MyDrive myDrive, String username) throws InvalidUsernameException {
        init(myDrive, username, username, username);
    }

    protected void init(MyDrive myDrive, String username, String password, String name) throws InvalidUsernameException {
        init(myDrive, username, password, name, "rwxd----");
    }

    protected void init(MyDrive myDrive, String username, String password, String name, String mask) throws InvalidUsernameException{ 
        
        if(!User.validUsername(username)){
            throw new InvalidUsernameException();
        }

        if(password.length() < 8 && !username.equals("root") && !username.equals("nobody")){
            throw new InvalidPasswordException();
        }

        if(myDrive.hasUser(username)){
            throw new UserAlreadyExistsException(username);
        }

        setMyDrive(myDrive);
        setUsername(username);
        setPassword(password);
        setName(name);
        setMask(mask);
    }

    @Override
    public void setUsername(String username){
        if(!validUsername(username)){
            throw new InvalidUsernameException();
        }else{
            super.setUsername(username);
        }
    }

/* ################## */
/*    Create Files    */
/* ################## */

    public Directory createHome(Directory home){
        Directory homeDirectory = null;

        homeDirectory = new Directory(getUsername(), this, home);
        setHome(homeDirectory);
        addFiles(home);

        return homeDirectory;
    }

    public void createDirectory(String name, Directory parent) throws FileAlreadyExistsException, PermissionDeniedException, InvalidFileNameException{
        if(parent.containsFile(name)){
            throw new FileAlreadyExistsException(name);
        }else if(!canWrite(parent)){
            throw new PermissionDeniedException(getUsername());
        }else{
            Directory directory = new Directory(name, this, parent);
            addFiles(directory);
        }
    }

    public void createPlainFile(String name, Directory parent) throws FileAlreadyExistsException, PermissionDeniedException, InvalidFileNameException{
        if(parent.containsFile(name)){
            throw new FileAlreadyExistsException(name);
        }else if(!canWrite(parent)){
            throw new PermissionDeniedException(getUsername());
        }else{
            PlainFile file = new PlainFile(name, this, parent);
            addFiles(file);
        }
    }

    public void createApplication(String name, Directory parent) throws FileAlreadyExistsException, PermissionDeniedException, InvalidFileNameException{
        if(parent.containsFile(name)){
            throw new FileAlreadyExistsException(name);
        }else if(!canWrite(parent)){
            throw new PermissionDeniedException(getUsername());
        }else{

            Application application = new Application(name, this, parent);
            addFiles(application);
        }
    }
    
    public void createLink(String name, String path, Directory parent) throws FileAlreadyExistsException, PermissionDeniedException, InvalidFileNameException, InvalidPathException{
        createLink(name, parent);
        Link link = (Link)parent.getFileByName(name);
        link.setPath(path);
    } 

    public void createLink(String name, Directory parent) throws FileAlreadyExistsException, PermissionDeniedException, InvalidFileNameException{
        if(parent.containsFile(name)){
            throw new FileAlreadyExistsException(name);
        }else if(!canWrite(parent)){
            throw new PermissionDeniedException(getUsername());
        }else{

            Link link = new Link(name, this, parent, "");
            addFiles(link);
        }
    }

    public void createLink(Directory reference, Directory parent) throws PermissionDeniedException, InvalidFileNameException{
        if(!canWrite(parent)){
            throw new PermissionDeniedException(getUsername());
        }else{
            try{
                String path = reference.getAbsolutePath();
                Link link = new Link(reference.getName(), this, parent, path);
                addFiles(link);
            }
            catch (FileDoesntExistException e){
                throw new PermissionDeniedException();
            }
        }
    }

/* ################## */
/*      Search        */
/* ################## */

    protected PlainFile searchPlainFile(String absolutePath) throws InvalidPathException, FileDoesntExistException, IsNotPlainFileException{
        if(!absolutePath.startsWith("/")){
            throw new InvalidPathException("Path must be absolute");
        }else{
            return getHome().getPlainFileByPath(absolutePath);
        }
    }

    protected Link searchLink(String absolutePath) throws InvalidPathException, FileDoesntExistException, IsNotLinkException{
        if(!absolutePath.startsWith("/")){
            throw new InvalidPathException("Path must be absolute");
        }else{
            return getHome().getLinkByPath(absolutePath);
        }
    }

    protected Application searchApplication(String absolutePath) throws InvalidPathException, FileDoesntExistException, IsNotApplicationException{
        
        if(!absolutePath.startsWith("/")){
           
            throw new InvalidPathException("Path must be absolute");
        }else{
            return getHome().getApplicationByPath(absolutePath);
        }
    }

    protected File searchFile(String absolutePath) throws InvalidPathException, FileDoesntExistException{
        
        if(!absolutePath.startsWith("/")){
            throw new InvalidPathException("Path must be absolute");
        }else{
            return getHome().getFileByPath(absolutePath);
        } 
    }

    protected Directory searchDirectory(String absolutePath) throws InvalidPathException, IsNotDirectoryException, FileDoesntExistException{
        if(!absolutePath.startsWith("/")){
            throw new InvalidPathException("Path must be absolute");
        }else{
            return getHome().getDirectoryByPath(absolutePath);
        } 
    }

/* ######################### */
/*      File Handling        */
/* ######################### */

    public String printFile(String absolutePath) throws PermissionDeniedException, InvalidPathException, IsNotPlainFileException{
        
        PlainFile file = searchPlainFile(absolutePath);

        if(canRead(file)){
            return file.print();
        }else{
            throw new PermissionDeniedException(getUsername());
        }
    }

    public void editPlainFile(String absolutePath, String newContent) throws PermissionDeniedException, InvalidPathException, FileDoesntExistException{
        
        PlainFile file = searchPlainFile(absolutePath);

        if(canWrite(file)){
            file.setContent(newContent);
        }else{
            throw new PermissionDeniedException(getUsername());
        }
    }

    public void executeFile(String absolutePath, String[] args) 
        throws InvalidPathException, PermissionDeniedException, FileDoesntExistException,  
            ClassNotFoundException, InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{

        File file = searchFile(absolutePath);
        
        if(file instanceof Application){
            executeApplication(file.getAbsolutePath(), args);
        }else if(file instanceof PlainFile){
            if(args != null){
                throw new InvalidExecutionException();
            }
            executePlainFile(file.getAbsolutePath());
        }else{
            throw new FileDoesntExistException(absolutePath);
        }
    }

    public void executeApplication(String appPath, String[] args) 
        throws InvalidPathException, PermissionDeniedException, FileDoesntExistException, 
            ClassNotFoundException, InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{

        Application app = searchApplication(appPath);

        if(canExecute(app)){
            System.out.println("A executar app:");
            app.execute(args);
        }else{
            throw new PermissionDeniedException(getUsername());
        }
    }

    public void executePlainFile(String plainFilePath) 
        throws InvalidPathException, PermissionDeniedException, FileDoesntExistException, 
            ClassNotFoundException, InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{

        PlainFile file = searchPlainFile(plainFilePath);

        if(!canRead(file)){
            throw new PermissionDeniedException(getUsername());
        }

        String[] lines = file.getContent().split("\n");

        for(String s : lines){            
            executeLine(s);
        }


    }

    private void executeLine(String line) 
        throws InvalidPathException, PermissionDeniedException, FileDoesntExistException, 
            ClassNotFoundException, InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{

        String[] parts = line.split(" ");
        
        String appPath = parts[0];
        String[] args = new String[parts.length-1];

        for(int i = 1; i < parts.length; i++){
            args[i-1] = parts[i];
        }

        executeApplication(appPath, args);

    }

    public void deleteFile(String absolutePath) throws PermissionDeniedException, FileDoesntExistException, InvalidPathException{
        File file = searchFile(absolutePath);
        if(canDelete(file)){
            file.getParent().removeFile(file.getName());
        }else{
            throw new PermissionDeniedException(getUsername());
        }
    }

    public String listDirectory(String absolutePath) throws PermissionDeniedException, IsNotDirectoryException, FileDoesntExistException{
        
        Directory directory = searchDirectory(absolutePath);

        if(!canRead(directory)){
            throw new PermissionDeniedException(getUsername());
        }else{
            return directory.list();
        }
    }

    public void readFile(String filePath) throws PermissionDeniedException, InvalidPathException, FileDoesntExistException, IsNotPlainFileException{

        printFile(filePath);

    }

/*################*/
/*       XML      */
/*################*/

    public Element xmlExport() {
        
        Element user = new Element("user");

        Element username = new Element("username");
        username.addContent(getUsername());
        user.addContent(username);

        Element name = new Element("name");
        name.addContent(getName());
        user.addContent(name);

        Element password = new Element("password");
        password.addContent(getPassword());
        user.addContent(password);

        Element mask = new Element("mask");
        mask.addContent(getMask());
        user.addContent(mask);

        return user;
    }

/*################*/
/*     ACCESS     */
/*################*/

    public boolean canRead(String absolutePath) throws InvalidPathException, FileDoesntExistException{
        File file = searchFile(absolutePath);
        return (hasOwnerPermission(Permission.OWNER_READ, file) || Permission.hasPermission(Permission.READ, getMask(), file.getMask()));
    }

    public boolean canWrite(String absolutePath) throws InvalidPathException, FileDoesntExistException{
        File file = searchFile(absolutePath);
        return (hasOwnerPermission(Permission.OWNER_WRITE, file) || Permission.hasPermission(Permission.WRITE, getMask(), file.getMask()));
    }

    public boolean canExecute(String absolutePath) throws InvalidPathException, FileDoesntExistException{
        File file = searchFile(absolutePath);
        return (hasOwnerPermission(Permission.OWNER_EXECUTE, file) || Permission.hasPermission(Permission.EXECUTE, getMask(), file.getMask()));
    }

    public boolean canDelete(String absolutePath) throws InvalidPathException, FileDoesntExistException{
        File file = searchFile(absolutePath);
        return (hasOwnerPermission(Permission.OWNER_DELETE, file) || Permission.hasPermission(Permission.DELETE, getMask(), file.getMask()));
    }
    
    public boolean ownsFile(String absolutePath){
        // Assumindo que username é unico, nao e necessario equals no User
        try{
            File file = searchFile(absolutePath);
            return getUsername().equals(file.getOwner().getUsername());
        }catch(FileDoesntExistException e){
            return false;
        }
    }

    protected boolean hasOwnerPermission(Permission p, File file){
        return (ownsFile(file) && Permission.hasPermission(p, getMask(), file.getMask()));
    }

/*################*/
/*     Static     */
/*################*/

    static boolean validUsername(String username){
        if(username.equals("") || username.length()<3){
            return false;
        }
        for(char c : username.toCharArray()){
            if(!Character.isLetter(c) && !Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }

/*############################*/
/*     Session Management     */
/*############################*/

    public boolean checkPassword(String password){
        return password.equals(getPassword()) && getPassword().length()>=8;
    }

    public void logout(Login session){
        removeSession(session);
    }

    public boolean isLogged(){
        return getSession()!=null;
    }

/*#####################*/
/*     Destruction     */
/*#####################*/

    public void remove() throws PermissionDeniedException{

        setMyDrive(null);
        setHome(null);

        for(File f : getFilesSet()){
            removeFiles(f);
            f.remove();
        }

        deleteDomainObject();
    }

/*#################################*/
/*           Deprecated            */
/*   (View alternatives above)     */
/*#################################*/

    @Deprecated
    public void printFile(PlainFile file) throws PermissionDeniedException{
        if(canRead(file)){
            file.print();
        }else{
            throw new PermissionDeniedException(getUsername());
        }
    }

    @Deprecated
    public void editPlainFile(PlainFile file, String newContent) throws PermissionDeniedException{
        if(canWrite(file)){
            file.setContent(newContent);
        }else{
            throw new PermissionDeniedException(getUsername());
        }
    }

    @Deprecated
    public void deleteFile(File file) throws PermissionDeniedException, FileDoesntExistException{
        if(canDelete(file)){
            Directory parent = file.getParent();
            parent.removeFile(file.getName());
        }else{
            throw new PermissionDeniedException(getUsername());
        }
    }
    
    @Deprecated
    public String listDirectory(Directory directory) throws PermissionDeniedException{
        if(!canRead(directory)){
            throw new PermissionDeniedException(getUsername());
        }else{
            return directory.list();
        }
    }

    @Deprecated
    public boolean canRead(File file){
        return (hasOwnerPermission(Permission.OWNER_READ, file) || Permission.hasPermission(Permission.READ, getMask(), file.getMask()));
    }

    @Deprecated
    public boolean canWrite(File file){
        return (hasOwnerPermission(Permission.OWNER_WRITE, file) || Permission.hasPermission(Permission.WRITE, getMask(), file.getMask()));
    }

    @Deprecated
    public boolean canExecute(File file){
        return (hasOwnerPermission(Permission.OWNER_EXECUTE, file) || Permission.hasPermission(Permission.EXECUTE, getMask(), file.getMask()));
    }

    @Deprecated
    public boolean canDelete(File file){
        return (hasOwnerPermission(Permission.OWNER_DELETE, file) || Permission.hasPermission(Permission.DELETE, getMask(), file.getMask()));
    }

    @Deprecated
    protected boolean ownsFile(File file){
        // Assumindo que username é unico, nao e necessario equals no User
        return getUsername().equals(file.getOwner().getUsername());
    }

    @Deprecated
    public void executePlainFile(PlainFile file) 
        throws InvalidPathException, PermissionDeniedException, FileDoesntExistException, 
            ClassNotFoundException, InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{

        String[] lines = file.getContent().split("\n");

        for(String s : lines){            
            executeLine(s);
        }
    }
}
