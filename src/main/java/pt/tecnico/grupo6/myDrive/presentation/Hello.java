package pt.tecnico.grupo6.myDrive.presentation;

public class Hello {

    public static void main(String[] args) {
        System.out.println("\nHello MyDrive!\n");
    }
    public static void bye(String[] args) {
        System.out.println("\nGodbye MyDrive!\n");
    }
    public static void greet(String[] args) {
        System.out.println("\nHello "+args[0] +"\n");
    }
    public static void execute(String[] args) {
	for (String s: args)
	    System.out.println("\nExecute "+args[0]+"?\n");
    }
    public static void sum(String[] args) {
	int sum = 0;
	for (String s: args) sum += Integer.parseInt(s);
	System.out.println("\nsum="+sum+"\n");
    }
}


