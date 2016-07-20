package pt.tecnico.grupo6.myDrive;

import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.exception.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.output.*;

import pt.ist.fenixframework.*;

import java.io.*;

public class Main {
	
	static final Logger log = LogManager.getRootLogger();

	public static void main(String[] args){
		
		System.out.println("\n\n**** Welcome to the MyDrive FileSystem! ****\n\n");
		
		try {
			/* Codigo de teste temporario */
			if(args.length==1){
				importFileSystem(new java.io.File(args[0]));
			}else{
				newFileSystem();
			}
		}catch(InvalidXmlFileException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		} finally { 
			FenixFramework.shutdown(); 
		}
	}

	@Atomic
	public static void init(){
		newFileSystem();
	}

	@Atomic
	public static void newFileSystem(){
		MyDrive.getInstance().reset();
	}

	@Atomic
	public static void importFileSystem(java.io.File xmlFile) throws InvalidXmlFileException{

		MyDrive.getInstance().reset();
		System.out.println("Importing from "+xmlFile);
		MyDrive.getInstance().xmlScan(xmlFile);

	}

	@Atomic
	public static void printXmlExport() throws IOException{

		Document doc = MyDrive.getInstance().xmlExport();
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());

		xmlOutput.output(doc, new PrintStream(System.out));

	}

	@Atomic
	public static void createReadme() throws FileAlreadyExistsException, PermissionDeniedException, InvalidFileNameException, FileDoesntExistException, UserDoesntExistException{ 
        User u = MyDrive.getInstance().getRootUser();
        Directory home = MyDrive.getInstance().getHomeFolder();
        u.createPlainFile("README", home);

        PlainFile readme = (PlainFile) home.getFileByName("README");

        for(User usr : MyDrive.getInstance().getUsersSet()){
            readme.addLine(usr.getUsername());
        }
    }

    @Atomic
    public static void createUsrLocalBin() throws InvalidFileNameException, IsNotDirectoryException, FileAlreadyExistsException, FileDoesntExistException, PermissionDeniedException, FileDoesntExistException, UserDoesntExistException{
    	Root r = MyDrive.getInstance().getRootUser();
        Directory rf = MyDrive.getInstance().getRootFolder();

        r.createDirectory("usr", rf);
        r.createDirectory("local", rf.getDirectoryByName("usr"));
        r.createDirectory("bin", rf.getDirectoryByName("usr").getDirectoryByName("local"));
    	
    }

    @Atomic
    public static void printReadme() throws FileDoesntExistException{ 

        User u = MyDrive.getInstance().getRootUser();
        Directory home = MyDrive.getInstance().getHomeFolder();
        PlainFile readme = (PlainFile) home.getFileByName("README");

        System.out.print(readme.print());

    }

    //Liça
    @Atomic
    public static void removeUsrLocalBin() throws FileDoesntExistException,IsNotDirectoryException, FileDoesntExistException, PermissionDeniedException{

       	User u = MyDrive.getInstance().getRootUser();
        
        Directory home = MyDrive.getInstance().getRootFolder();
        Directory usr = home.getDirectoryByName("usr");
        Directory local = usr.getDirectoryByName("local");

        u.deleteFile(local.getDirectoryByName("bin"));    
    }

    @Atomic
    public static void listHomeDirectory() throws FileDoesntExistException, PermissionDeniedException{
        User u = MyDrive.getInstance().getRootUser();
        Directory home = MyDrive.getInstance().getHomeFolder();
        System.out.print(u.listDirectory(home));
    }

	@Atomic
	public void deleteReame() throws FileDoesntExistException, PermissionDeniedException {
		User u = MyDrive.getInstance().getRootUser();
		Directory home = MyDrive.getInstance().getHomeFolder();
		pt.tecnico.grupo6.myDrive.domain.File readme = (pt.tecnico.grupo6.myDrive.domain.File) home.getFileByName("README");
		u.deleteFile(readme);
	}

}

