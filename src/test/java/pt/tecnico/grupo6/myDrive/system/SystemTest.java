package pt.tecnico.grupo6.myDrive.system;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.grupo6.myDrive.service.ServiceTest ;
import pt.tecnico.grupo6.myDrive.presentation.*;


public class SystemTest extends ServiceTest {

    private MdShell md;

    protected void populate() {
        md = new MdShell("drive.xml");
    }

    @Test
    public void success() {
        
        new Login(md).execute(new String[] { "root", "***" } );
        new ChangeWorkingDirectory(md).execute(new String[] { });
        new List(md).execute(new String[] {  } );
        new ChangeWorkingDirectory(md).execute(new String[] { ".." });
        new List(md).execute(new String[] { "/" } );
        new Environment(md).execute(new String[] { "OLA", "ADEUS" } );
        
        new Key(md).execute(new String[] { "nobody" } );
        new Environment(md).execute(new String[] { "VAR1", "VALOR1" } );
        new Environment(md).execute(new String[] { "VAR1" } );

        new Key(md).execute(new String[] { "root" } );
        new Key(md).execute(new String[] {  } );
        new Environment(md).execute(new String[] { "OLA" } );
        new Environment(md).execute(new String[] { } );
        new ExportXmlFile(md).execute(new String[] {  } );        
    }
}

