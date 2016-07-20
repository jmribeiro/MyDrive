package pt.tecnico.grupo6.myDrive.domain;

import pt.tecnico.grupo6.myDrive.exception.*;

public class Root extends Root_Base {
    
    static Root getInstance() {
        Root root = MyDrive.getInstance().getRootUser();
        if(root != null){
            return root;
        }else{
            return new Root();
        }
    }
    
    private Root(){
        super();

        MyDrive filesystem = MyDrive.getInstance();
        
        init(filesystem, "root", "***", "Super User", "rwxdr-x-");

        filesystem.setRootUser(this);
        setFileSystemRoot(filesystem);
    }



    @Override
    public Directory createHome(Directory home){
        
        Directory homeDirectory = new Directory(getUsername(), this, home);
        setHome(homeDirectory);
        addFiles(home);

        return homeDirectory;
    }

    public boolean canRead(String absolutePath) throws InvalidPathException, FileDoesntExistException{
        File file = searchFile(absolutePath);
        return true;
    }

    public boolean canWrite(String absolutePath) throws InvalidPathException, FileDoesntExistException{
        File file = searchFile(absolutePath);
        return true;
    }

    public boolean canExecute(String absolutePath) throws InvalidPathException, FileDoesntExistException{
        File file = searchFile(absolutePath);
        return true;
    }

    public boolean canDelete(String absolutePath) throws InvalidPathException, FileDoesntExistException{
        File file = searchFile(absolutePath);
        return true;
    }

    @Override
    public boolean canRead(File file){
        return true;
    }

    @Override
    public boolean canWrite(File file){
        return true;
    }

    @Override
    public boolean canExecute(File file){
        return true;
    }

    @Override
    public boolean canDelete(File file){
        return true;
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


