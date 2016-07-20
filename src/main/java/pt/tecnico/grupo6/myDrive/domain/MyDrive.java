package pt.tecnico.grupo6.myDrive.domain;
import pt.tecnico.grupo6.myDrive.exception.*;

import pt.ist.fenixframework.*;

import org.apache.logging.log4j.*;

import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.output.*;

import java.io.*;
import java.util.*;

public class MyDrive extends MyDrive_Base {

/* ################## */
/*    SINGLETON       */
/* ################## */
    
    public static MyDrive getInstance(){

        MyDrive md = FenixFramework.getDomainRoot().getApplication();
        if (md != null){
            return md;
        }
        return new MyDrive();
    }

/* ################## */
/*    Construction    */
/* ################## */

    private MyDrive(){
        super();
        reset();
    }

    public void reset(){
        clean();
        setup();
    }

    private void setup(){
        setDomainRoot(FenixFramework.getDomainRoot());
        createStaticUsers();
        createStaticDirectories();
    }

    private void createStaticUsers(){
        setRootUser(Root.getInstance());
        setGuestUser(Guest.getInstance());

        getUsersSet().add(getRootUser());
        getUsersSet().add(getGuestUser());
    }

    private void createStaticDirectories(){
        setRootFolder(new RootDirectory(this, getRootUser()));
        setHomeFolder(new HomeDirectory(this, getRootUser()));

        createStaticHomes();
    }

    private void createStaticHomes(){
        getRootUser().createHome(getHomeFolder());
        getGuestUser().createHome(getHomeFolder());
    }

    Integer autoIncrement(){
        Integer value = getAutoIncrement();
        value++;
        setAutoIncrement(value);

        return new Integer(value-1);
    }

/* ##################*/
/*    Destruction    */
/* ##################*/
    
    private void clean(){
        setAutoIncrement(0);
        cleanUsers();
        cleanLogins();
    }
    
    private void cleanUsers(){

        for(User u : getUsersSet()){

            if(u.getUsername().equals("root") || u.getUsername().equals("nobody")){
                continue;
            }
            removeUsers(u);
            u.remove();  
        }

        removeStaticUsers();
    }

    private void cleanLogins(){
        for(Login l : getLoginsSet()){
            l.remove();
        }
    }

    private void removeStaticUsers(){
        
        Root r = getRootUser();
        Guest g = getGuestUser();
        
        if(r == null || g == null){
            return;
        }else{
            r.setMyDrive(null);
            r.setHome(null);

            r.removeFiles(getRootFolder());
            setRootUser(null);

            g.setMyDrive(null);
            g.setHome(null);
            setGuestUser(null);

            setHomeFolder(null);
            setRootFolder(null);
        }

    }

/* ####################### */
/*    User Manipulation    */
/* ####################### */

    public User getUserByUsername(String username) throws UserDoesntExistException{
        for(User u : getUsersSet()){
            if(u.getUsername().equals(username)){
                return u;
            }
        }
        throw new UserDoesntExistException(username);
    }

    public boolean hasUser(String username){
        try{
            getUserByUsername(username);
            return true;
        }catch(UserDoesntExistException e){
            return false;
        }
    }

    public void newUser(String username, String password, String name, String homeLocationPath) throws InvalidUsernameException, UserAlreadyExistsException, FileDoesntExistException, IsNotDirectoryException{
        if(homeLocationPath.equals("")){
            createUser(username, password, name, getHomeFolder());
        }else{
            Directory homeLocation = getRootFolder().getDirectoryByPath(homeLocationPath);
            createUser(username, password, name, homeLocation);
        }
    }

    private void createUser(String username, String password, String name, Directory homeLocation) throws InvalidUsernameException, UserAlreadyExistsException{

        if(password.equals("")){
            password = username;
        }

        if(username.length() < 8){
            password = "12345678";
        }

        if(name.equals("")){
            name = username;
        }

        User u;
        try{
            u = getUserByUsername(username);
        }catch(UserDoesntExistException e){
            u = new User(this, username, password, name);
            addUsers(u);
            u.createHome(homeLocation);
        }

    }

/* ######### */
/*    XML    */
/* ######### */

    public Document xmlExport() {

        Element rootNode = initializeRootNode();

        Document doc = new Document(rootNode);
        
        Element files = saveFiles();
        Element users = saveUsers();

        rootNode.addContent(files);
        rootNode.addContent(users);

        return doc;
    }  

    public void xmlScan(java.io.File file) throws InvalidXmlFileException {
        try{
            reset();
            SAXBuilder builder = new SAXBuilder();
            Document document = (Document)builder.build(file);
            xmlImport(document.getRootElement());
        }catch(JDOMException | IOException e){
            throw new InvalidXmlFileException();
        }
    }

    public void xmlImport(Element rootNode) throws InvalidXmlFileException{
        
        reloadRoot();
        loadState(rootNode);

        try{
            loadUsers(rootNode);
            loadFiles(rootNode);
        }
        catch (InvalidFileNameException e){
            throw new InvalidXmlFileException();
        }catch (UserDoesntExistException e){
            throw new InvalidXmlFileException();
        }
    }

    private Element initializeRootNode(){
        Element rootNode = new Element("myDrive");
        rootNode.setAttribute("id", getRootFolder().getId().toString());
        rootNode.setAttribute("autoIncrement", getAutoIncrement().toString());
        return rootNode;
    }

    private Element saveFiles(){
        Element files = new Element("filesystem");
        for(File f : getRootFolder().getDirectoryFilesSet()){
            if(!f.getName().equals("/")){
                files.addContent(f.xmlExport());
            }
        }
        return files;
    }

    private Element saveUsers(){
        Element users = new Element("users");
        for(User u : getUsersSet()){
            users.addContent(u.xmlExport());
        }
        return users;
    }

    private void reloadRoot(){
        setRootUser(Root.getInstance());
        setRootFolder(new RootDirectory(this, getRootUser()));
        getUsersSet().add(getRootUser());
    }

    private void loadUsers(Element rootNode) throws InvalidXmlFileException{
        Element usersNode = rootNode.getChild("users");
        List<Element> users = usersNode.getChildren("user");
        xmlUsersSetup(users);
    }

    private void loadFiles(Element rootNode) throws InvalidXmlFileException{
        Element fileSystemNode = rootNode.getChild("filesystem");
        xmlFilesSetup(fileSystemNode);
    }

    private void loadState(Element rootNode){
        setAutoIncrement(new Integer(Integer.parseInt(rootNode.getAttributeValue("autoIncrement"))));
    }

    private void xmlUsersSetup(List<Element> users) throws InvalidXmlFileException{
        for(Element user : users){
            
            if(user.getChildText("username").equals("root") || user.getChildText("username").equals("nobody")){
                continue; // Root é sempre criada com o filesystem, skip ahead
            }

            try{
                User u = new User(this, user);
                addUsers(u);
            }catch(InvalidUsernameException e){
                throw new InvalidXmlFileException();
            }
        }
    }

    private void xmlFilesSetup(Element fileSystemNode) throws InvalidFileNameException, UserDoesntExistException{
        
        List<Element> directories = fileSystemNode.getChildren("directory");
        List<Element> plainFiles = fileSystemNode.getChildren("plainFile");
        List<Element> applications = fileSystemNode.getChildren("application");
        List<Element> links = fileSystemNode.getChildren("linkFile");
        
        for(Element directory : directories){
            Directory d = new Directory(directory, getRootFolder());
        }

        for(Element plainFile : plainFiles){
            PlainFile pf = new PlainFile(plainFile, getRootFolder());
        }

        for(Element application : applications){
            Application app = new Application(application, getRootFolder());
        }

        for(Element link : links){
            Link l = new Link(link, getRootFolder());
        }
    }

/* #########################*/
/*    Login Manipulation    */
/* #########################*/

    public Long login(String username, String password) throws InvalidSessionException, UserDoesntExistException, WrongPasswordException, EmptyLoginFieldsException{
        
        Login login = new Login(this, username, password);
        addLogins(login);
        return login.getToken();
    }

    public void logout(Long token){
        
        Login login = getLoginByToken(token);
        User loggedUser = login.getLoggedUser();
        
        loggedUser.logout(login);

        removeLogin(token);
        
    }

    public void removeLogin(Long token){
        Login target = getLoginByToken(token);
        removeLogins(target);
    }

    public boolean tokenExists(Long token){
        try{
            getLoginByToken(token);
            return true;
        }catch(InvalidSessionException e){
            return false;
        }
    }

    public Login getLoginByToken(Long token) throws InvalidSessionException{
        
        for(Login l : getLoginsSet()){
            
            if(l.getToken().equals(token)){
                if(l.isExpired()){
                    removeLogins(l);
                    l.remove();
                    throw new InvalidSessionException("Login has timed out...");
                }
                l.refresh();
                return l;
            }
        }
        throw new InvalidSessionException("Login doesn't exist");
    }

/* #####################################*/
/*    Deprecated - View Alternatives    */
/* #####################################*/

    @Deprecated
    public void newUser(String username) throws InvalidUsernameException, UserAlreadyExistsException, FileDoesntExistException, IsNotDirectoryException{
        newUser(username, "", "", "");
    }
}

