package pt.tecnico.grupo6.myDrive.domain;
import pt.tecnico.grupo6.myDrive.exception.*;
import org.jdom2.*;
import java.util.*;
import org.joda.time.LocalDate;


public class Directory extends Directory_Base {
    
/* ################## */
/*    Construction    */
/* ################## */

    protected Directory() {
        super();
    }

    public Directory(String name, User owner) throws InvalidFileNameException{
        init(name, owner);
    }

    public Directory(String name, User owner, Directory parent) throws InvalidFileNameException{
        init(name, owner, parent);
        parent.addFile(this);
    }
    
    public Directory(Element directoryNode, Directory parent) throws InvalidFileNameException, UserDoesntExistException{
        super();
        Element directoryFilesNode = directoryNode.getChild("directoryFiles");
        exportNode(directoryNode, parent);

        String ownerUsername = getOwner().getUsername();
        String name = getName();

        if(name.equals(ownerUsername)){ // Era a home
            MyDrive.getInstance().getUserByUsername(ownerUsername).setHome(this);
        }
        
        xmlFilesSetup(directoryFilesNode);
    }

/*################*/
/*       XML      */
/*################*/


    private void xmlFilesSetup(Element directoryFilesNode) throws InvalidFileNameException, UserDoesntExistException{
        List<Element> directories = directoryFilesNode.getChildren("directory");
        List<Element> plainFiles = directoryFilesNode.getChildren("plainFile");
        List<Element> applications = directoryFilesNode.getChildren("application");
        List<Element> links = directoryFilesNode.getChildren("linkFile");
        
        for(Element directory : directories){
            Directory d = new Directory(directory, this);
        }

        for(Element plainFile : plainFiles){
            PlainFile pf = new PlainFile(plainFile, this);
        }

        for(Element application : applications){
            Application app = new Application(application, this);
        }

        for(Element link : links){
            Link l = new Link(link, this);
        }
    }

    @Override
    public Element xmlExport() {
        
        Element directory = new Element("directory");

        fillNode(directory);

        Element directoryFiles = new Element("directoryFiles");

        for (File f : getDirectoryFilesSet()){
            directoryFiles.addContent(f.xmlExport());
        }

        directory.addContent(directoryFiles);
        
        return directory;
    }

/* ################## */
/*      Search        */
/* ################## */

    public boolean containsDirectory(String name){
        return containsFile(name) && (getFileByName(name) instanceof Directory);
    }

    public boolean containsApplication(String name){
        return containsFile(name) && (getFileByName(name) instanceof Application);
    }

    public boolean containsPlainFile(String name){
        return containsFile(name) && (getFileByName(name) instanceof PlainFile);
    }

    public boolean containsLink(String name){
        return containsFile(name) && (getFileByName(name) instanceof Link);
    }

    public boolean containsFile(String name){
        for(File l : getDirectoryFilesSet()){
            if(l.getName().equals(name)){
                return true;
            }
        }
        return false;      
    }

    public File getFileByName(String name) throws FileDoesntExistException{
        
        if(name.equals(".")){
            return this;
        }else if(name.equals("..")){
            return getParent();
        }else if(!containsFile(name)){
            throw new FileDoesntExistException(name);
        }else{
            File f = null;
            for(File l : getDirectoryFilesSet()){
                if(l.getName().equals(name)){
                    f = l;
                }
            }
            return f;
        }
    }

    public Directory getDirectoryByName(String name) throws  IsNotDirectoryException, FileDoesntExistException{

        if(name.equals(".")){
            return this;
        }else if(name.equals("..")){
            return getParent();
        }else if(containsDirectory(name)){
            return (Directory) getFileByName(name);
        }else if(containsFile(name)){
            throw new IsNotDirectoryException(name);
        }else{
            throw new FileDoesntExistException(name);
        }
    }

    public Directory getDirectoryByPath(String path) throws IsNotDirectoryException, FileDoesntExistException {
        File f = getFileByPath(path);
        if(f instanceof Directory){
            return (Directory) f;
        }else{
            throw new IsNotDirectoryException(path);
        }
    }

    public PlainFile getPlainFileByPath(String path) throws IsNotPlainFileException, InvalidPathException, FileDoesntExistException{
        if(getFileByPath(path) instanceof PlainFile){
            return (PlainFile) getFileByPath(path);
        }else{
            throw new IsNotPlainFileException(path);
        }
    }

    public Application getApplicationByPath(String path) throws IsNotApplicationException, InvalidPathException, FileDoesntExistException{
        if(getFileByPath(path) instanceof Application){
            return (Application) getFileByPath(path);
        }else{
            throw new IsNotApplicationException(path);
        }
    }

    public File getFileByPath(String path) throws InvalidPathException, FileDoesntExistException{
        
        if(path.length() > 1024){
            throw new InvalidPathException();
        }else if(path.equals("..")){
            return getParent();
        }else if(path.equals(".")){
            return this;
        }else if(path.startsWith("..")){
            String parentPath = path.split("..")[1];
            return getParent().getFileByPath(parentPath);
        }else if(path.equals("/")){
            return MyDrive.getInstance().getRootFolder();
        }else{
            return searchFile(path);

        }      
    }

    private File searchFile(String path) throws FileDoesntExistException{
            
        String[] parts = path.split("/", 2);
        
        if(parts.length == 1){
            return searchBottom(path);
        }else{
            return recursiveSearch(parts[0], parts[1]);
            
        }
    }

    private File recursiveSearch(String subDirectory, String subPath) throws FileDoesntExistException{
 
        if(subDirectory.equals("")){
            // Absolute Path
            return MyDrive.getInstance().getRootFolder().getFileByPath(subPath);
        }else{
            // Relative Path
            File current = getFileByName(subDirectory);
            Directory currentDirectory;

            if(current instanceof Link){
                currentDirectory = (Directory) getFileByPath( ((Link)current).getPath() );
            }else{
                currentDirectory = (Directory) current;
            }

            return currentDirectory.getFileByPath(subPath);
        }
    }

    private File searchBottom(String fileName) throws FileDoesntExistException{
    
        File target = getFileByName(fileName);
        if(target instanceof Link){
            return getFileByPath( ((Link)target).getPath() );
        }else{
            return target;
        }
    }

    /* PURE LINKS */
    public Link getLinkByPath(String path) throws IsNotLinkException, InvalidPathException, FileDoesntExistException{

        if(path.length() > 1024){
            throw new InvalidPathException();
        }else if(path.startsWith("..")){
            String parentPath = path.split("..")[1];
            return getParent().getLinkByPath(parentPath);
        }else if(path.equals("/")){
            throw new InvalidPathException();
        }else{
            return searchLink(path);
        } 
    }

    private Link searchLink(String path) throws IsNotLinkException, FileDoesntExistException{
            
        String[] parts = path.split("/", 2);
        
        if(parts.length == 1){
            return searchLinkBottom(path);
        }else{
            return recursiveLinkSearch(parts[0], parts[1]);
            
        }
    }
    
    private Link recursiveLinkSearch(String subDirectory, String subPath) throws FileDoesntExistException{
 
        if(subDirectory.equals("")){
            // Absolute Path
            return MyDrive.getInstance().getRootFolder().getLinkByPath(subPath);
        }else{
            // Relative Path
            File current = getFileByName(subDirectory);
            Directory currentDirectory;

            if(current instanceof Link){
                currentDirectory = (Directory) getFileByPath( ((Link)current).getPath() );
            }else{
                currentDirectory = (Directory) current;
            }

            return currentDirectory.getLinkByPath(subPath);
        }
    }
    
    private Link searchLinkBottom(String fileName) throws IsNotLinkException, FileDoesntExistException{
        File target = getFileByName(fileName);
        if(target instanceof Link){
            return (Link) target;
        }else{
            throw new IsNotLinkException(fileName);
        }
    }

/* ######################### */
/*      File Handling        */
/* ######################### */

    public void addFile(File file){
        addDirectoryFiles(file);
    }

    public void removeFile(String fileName) throws FileDoesntExistException{
        File f = getFileByName(fileName);
        removeDirectoryFiles(f);
        f.remove();
    }

    public String list(){
        String separator = "  ";
        String ls = "."+separator+".."+separator;
        for(File c : getDirectoryFilesSet()){
            ls = ls.concat(c.getName()+separator);
        } 
        return ls+"\n";
    }

    public int getSize(){
        
        int size = 0;

        if(getName().equals(getParent().getName())){
            size = 1;
        }else{
            size = 2;
        }
        return size + getDirectoryFilesCount();
    }

/*############################*/
/*     Session Management     */
/*############################*/

    public void logout(){
        removeLoginSession(null);
    }

/*#################### */
/*     Destruction     */
/*#################### */
    
    public void remove(){
        for(File f : getDirectoryFilesSet()){
            removeDirectoryFiles(f);
            f.remove();
        }

        for(Login l : getLoginSessionSet()){
            removeLoginSession(l);
        }

        setUser(null);
        super.remove();
    }

}

