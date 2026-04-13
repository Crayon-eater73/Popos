/**
 * Tester for Chapter 01: Introduction
 * 
 */
import java.io.*;
import java.nio.file.*;
import java.lang.reflect.*;
@SuppressWarnings("unchecked")
public class Ch01Test{
	public static int testsFailed = 0;
	public static Class ch;
	public static ByteArrayOutputStream baos;
	public static PrintStream original = System.out, record;
	
	public static void main(String[]args){
		try{
			assert false;
			throw new Exception("Asserts not enabled");
		}catch(AssertionError ae){
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		
		try{
			testOutput("Spikey");
			testOutput("WellFormed");
			testOutput("MuchBetter");
			testOutput("StarFigures");

		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		System.out.println();

		String className = "TwelveDays";
		try{
			Class temp = Class.forName(className);
			try{
				testOutput(className);
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
		}catch(ClassNotFoundException cnfe){
			System.out.println("Could not find optional class "+className);
		}
		if(testsFailed == 0){
			System.out.println("All tests passed");
		}else{
			System.out.println(testsFailed+ " test(s) failed");
			System.exit(-1);
		}
	}
	public static void assertEquals(String expected, String actual){
		expected = expected.replaceAll("\r", "").trim();
		actual = actual.replaceAll("\r", "").trim();
		while(actual.contains(" \n")){
			actual = actual.replaceAll(" \n", "\n");
		}
		assert expected.equals(actual) : "Expected\n" + expected.toString() + "\nbut was \n" + actual.toString();
	}
	public static void start(){
		baos = new ByteArrayOutputStream();
		record = new PrintStream(baos);
		System.setOut(record);
	}
	public static String stop(){
		record.flush();
		String actual = baos.toString();
		record.close();
		System.setOut(original);
		return actual;
	}
	public static void testOutput(String className){
		String actual = "", expected = "";
		try{
			ch = Class.forName(className);
			Method main = null;
			Method[] methods = ch.getDeclaredMethods();
			for(Method m : methods){
				if(m.getName().equals("main")){
					main = m;
					break;
				}
			}
			if(main == null)throw new NoSuchMethodException();
			Path path = Path.of("output/"+className+".txt");
			expected = Files.readString(path);
			start();
			if(main.getParameterCount() == 1){
				Object[] args2 = new Object[1];
				args2[0] = new String[0];
				try{//static
					main.invoke(null, args2);
				}catch(InvocationTargetException | NullPointerException ite){//non-static
					Constructor con = ch.getDeclaredConstructor(new Class[0]);
					Object hw = ch.cast(con.newInstance());
					main.invoke(hw, args2);
				}
			}else{
				try{//static
					main.invoke(null, new Object[0]);
				}catch(InvocationTargetException | NullPointerException ite){//non-static
					Constructor con = ch.getDeclaredConstructor(new Class[0]);
					Object hw = ch.cast(con.newInstance());
					main.invoke(hw, new Object[0]);
				}
			}
			actual = stop();
			assertEquals(expected, actual);
			
		}catch(ClassNotFoundException cnfe){
			testsFailed++;
			System.out.println("Could not find class " + className);
			return;
		}catch(AssertionError ae){
			testsFailed++;
			actual = actual.replaceAll("\r", "").trim();
			expected = expected.replaceAll("\r", "").trim();
			while(actual.contains(" \n")){
				actual = actual.replaceAll(" \n", "\n");
			}
			int line = 1;
			for(int i = 0; i < actual.length() && i < expected.length();i++){
				char a = actual.charAt(i), e = expected.charAt(i);
				if(a != e){
					System.out.print("Expected <");
					print(e);
					System.out.print("> but was <");
					print(a);
					System.out.println("> on line " + line);
					break;
				}
				if(e == '\n') line++;
			}
			System.out.println(ae.getMessage());
			System.out.println(className + " test failed");
			return;
		}
		catch(Exception e){
			testsFailed++;
			System.out.println(e.getMessage());
			System.out.println(className + " test failed");
			return;
		}
		System.out.println(className + " test passed");
	}
	public static void print(char x){
		if(x == '\n' || x == '\r'){
			System.out.print("\\n");
		}else{
			System.out.print(x);
		}
	}
}
