package pt.tecnico.grupo6.myDrive.domain;

import org.joda.time.LocalDate;
import pt.tecnico.grupo6.myDrive.exception.*;
import org.jdom2.Element;
import org.jdom2.Document;
public class File extends File_Base {
    
	protected File(){
		super();
	}

    protected void init(String name, User owner, Directory parent, LocalDate lastChange, Integer id) throws InvalidFileNameException{
        init(name, owner, parent);
        setId(id);
        setLastChange(lastChange);
    }

    protected void init(String name, User owner, Directory parent) throws InvalidFileNameException{
        
        if(!File.validFileName(name))
        {
            throw new InvalidFileNameException();
        }

        LocalDate myDate = new LocalDate();
        setName(name);
        setOwner(owner);
        setMask(owner.getMask());
        setLastChange(myDate);
        setParent(parent);
        parent.addFile(this);
        setId(MyDrive.getInstance().autoIncrement());
    }

    protected void init(String name, User owner) throws InvalidFileNameException{
        if(!File.validFileName(name))
        {
            throw new InvalidFileNameException();
        }
        LocalDate myDate = new LocalDate();
        setName(name);
        setOwner(owner);
        setMask(owner.getMask());
        setLastChange(myDate);
        setParent(null);
        setId(MyDrive.getInstance().autoIncrement());
    }

    public Element xmlExport(){
        //Abstract
        return new Element("error - this line should be unreachable!!! Please override method xmlExport on PlainFile, Application and Link");
    }

    protected void fillNode(Element node){

        node.setAttribute("id", getId().toString());
        node.setAttribute("lastChange", getLastChange().toString());
        
        Element name = new Element("name");
        name.addContent(getName());
        node.addContent(name);

        Element ownerUsername = new Element("owner");

        if(getParent().getName().equals("/") && getName().equals("home")){
             /* Dirty hack ;) */
            ownerUsername.addContent("root");
        }else{
            ownerUsername.addContent(getOwner().getUsername());   
        }
        
        node.addContent(ownerUsername);
    }

    protected void exportNode(Element node, Directory parent) throws InvalidFileNameException, UserDoesntExistException{
        
        String name = node.getChildText("name");
        String ownerUsername = node.getChildText("owner");
        Integer id = Integer.parseInt(node.getAttribute("id").getValue());
        String lastChange = node.getAttribute("lastChange").getValue();
        

        init(name, MyDrive.getInstance().getUserByUsername(ownerUsername), parent, new LocalDate(lastChange), id);
        
    }

    public void remove(){
        
        setParent(null);
        setOwner(null);
        deleteDomainObject();
    }

    public String getAbsolutePath(){

        if(getName().equals("/")){
            return "/";
        }
        
        String path = "/"+getName();
        Directory currentDir = getParent();

        while(!currentDir.getName().equals("/")){
            path = "/"+currentDir.getName()+path;
            currentDir = currentDir.getParent();
        }

        return path;

    }

    static boolean validFileName(String name){
        if(name.equals("") || name.length() > 1024){
            return false;
        }else{
            for(char c : name.toCharArray()){
                if(!Character.isLetter(c) && !Character.isDigit(c) && c != '.'){
                    return false;
                }
            }
            return true;
        }
    }
}