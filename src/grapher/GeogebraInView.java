package grapher;

import geogebra.GeoGebraPanel;
import geogebra.plugin.GgbAPI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import javax.swing.WindowConstants;

import org.nlogo.api.*;

import ch.randelshofer.quaqua.ext.base64.Base64;


//import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
//import com.sun.org.apache.xml.internal.security.utils.Base64;

public class GeogebraInView extends DefaultClassManager {

	static GeoGebraPanel ggbPanel, galleryPanel;
	
	static GgbAPI theAPI, galleryAPI;
	// static BufferedImage blittedImage;
	static int ggWid = 0;
	static int ggHt = 0;
	static JFrame graphFrame;
	
	static JFrame galleryFrame;
	static boolean filtered = false;

	//static HashSet<String> studentFunctionObjectNames = new HashSet<String>();
	static Hashtable<String, String> studentFunctionLookup = new Hashtable<String, String>();
	
	static Hashtable<String, String> teacherFunctionLookup = new Hashtable<String, String>();
	

	@Override
	public void load(PrimitiveManager primManager) throws ExtensionException {
		
		
		//System.err.println(UIManager.getLookAndFeel().getClass().toString()); 
		//String s = UIManager.getLookAndFeel().getClass().toString(); 
			graphFrame = new JFrame(); 
			graphFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			graphFrame.setLayout(new BorderLayout() );
			graphFrame.setTitle( "GeoGebra Window" );
			//graphFrame.setTitle( s );
			
			ggbPanel = new GeoGebraPanel();
			ggbPanel.buildGUI();
			ggbPanel.openFile(GeogebraInView.class
				.getResource("res/functionactivity.ggb")); //$NON-NLS-1$
			ggbPanel.setMaxIconSize(32);
			ggbPanel.setDoubleBuffered(true);
			ggbPanel.getGeoGebraAPI().setErrorDialogsActive(false); //TODO: PUT THIS BACK
			//ggbPanel.buildGUI();
			graphFrame.add(ggbPanel, BorderLayout.CENTER);
			graphFrame.setPreferredSize(new Dimension(908, 800));
			graphFrame.pack();
			//graphFrame.setLocationRelativeTo(null);
			//ggbPanel.setShowAlgebraInput(true);
			
			graphFrame.setResizable(true);
			graphFrame.setVisible(true);
			
			//GALLERY INITIALIZATION
			galleryFrame = new JFrame(); 
			galleryFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			galleryFrame.setLayout(new BorderLayout() );
			galleryFrame.setTitle( "GeoGebra Gallery" );
			//graphFrame.setTitle( s );
			
			galleryPanel = new GeoGebraPanel();
			galleryPanel.buildGUI();
			galleryPanel.openFile(GeogebraInView.class
				.getResource("res/functionactivity.ggb")); //$NON-NLS-1$
			galleryPanel.setMaxIconSize(32);
			galleryPanel.setDoubleBuffered(true);
			galleryPanel.getGeoGebraAPI().setErrorDialogsActive(false); //TODO: PUT THIS BACK
			//ggbPanel.buildGUI();
			galleryFrame.add(galleryPanel, BorderLayout.CENTER);
			galleryFrame.setPreferredSize(new Dimension(908, 800));
			galleryFrame.pack();
			graphFrame.setLocationRelativeTo(null);
			ggbPanel.setShowAlgebraInput(true);
			
			galleryFrame.setResizable(true);
			galleryFrame.setVisible(true);
			
			addGalleryFunction("test","50+sin(x)", 0,0,0);
			SwingUtilities.invokeLater( new Runnable() {
					     public void run() {
					    	 
					    	 getGalleryAPI().deleteObject("test");
					     }
					 } );
			galleryFrame.setState ( Frame.ICONIFIED );
			
			

		primManager.addPrimitive("set-coordinates", new SetCoords()); 
		primManager.addPrimitive("add-student-function", new AddStudentFunction()); 
		primManager.addPrimitive("add-student-point", new AddStudentPoint()); 
		primManager.addPrimitive("graph-teacher-function", new GraphTeacherFunction()); 
		primManager.addPrimitive("graph-teacher-point", new GraphTeacherPoint()); 

		primManager.addPrimitive("color-student-object", new ColorStudentObject()); 
		primManager.addPrimitive("color-named-object", new ColorNamedObjectRGB()); 
		primManager.addPrimitive("restore-geogebra-frame", new RestoreGGB()); 
		primManager.addPrimitive("minimize-geogebra-frame", new MinimizeGGB()); 
		primManager.addPrimitive("functions-passing-through", new FunctionsThroughGivenPoint()); 
		primManager.addPrimitive("get-function-expression", new GetFunctionExpression()); 
		
		primManager.addPrimitive("set-student-object-visible", new SetStudentObjectVisible());
		
		primManager.addPrimitive( "delete-named-object", new DeleteNamedObject());
		primManager.addPrimitive("delete-student-function", new DeleteStudentFunction());
		
		primManager.addPrimitive("save-geogebra-file", new SaveGeogebraFile());
		
		primManager.addPrimitive("functions-with-color", new FunctionsWithGivenColor() );
		primManager.addPrimitive("color-all-functions", new ColorAllFunctions() );
		
		primManager.addPrimitive("add-function-to-gallery", new AddFunctionToGallery() );
		primManager.addPrimitive("clear-gallery", new ClearGallery());
		primManager.addPrimitive("clear-geogebra", new ClearGeoGebra());
		primManager.addPrimitive("save-gallery", new SaveGalleryAsDocument() );
		
		primManager.addPrimitive("restore-gallery", new RestoreGallery());
		primManager.addPrimitive("minimize-gallery", new MinimizeGallery());	
		
		
//		primManager.addPrimitive("get-path-for-file", new GetPath() );
		
	}
	
	public static GgbAPI getGgbAPI()
	{
		if (theAPI == null) {
			theAPI = ggbPanel.getGeoGebraAPI();
		}
		return theAPI;
	}
	
	public static GgbAPI getGalleryAPI()
	{
		if (galleryAPI == null) {
			galleryAPI = galleryPanel.getGeoGebraAPI();
		}
		return galleryAPI;
	}
	
	
	

	public static boolean runCommand(String theCommand) {
		//TODO:  decide whether this has to be an invokeandwait.
		return getGgbAPI().evalCommand(theCommand);
	}
	
	public static boolean galleryCommand(String theCommand) {
		//TODO: decide whether this has to be an invokeandwait
		return getGalleryAPI().evalCommand(theCommand);
	}

	public static String casEvaluate(String theCommand) {
		
		return getGgbAPI(). 
		evalGeoGebraCAS(theCommand);   //Geogebra 4.0 call
		
		//evalMathPiper(theCommand);   //Geogebra 3.2.x call
	}

	public static String getStringValueOf(String theObjectName) {
		
		return getGgbAPI().getValueString(theObjectName);
	}

	public static double runReporter(String theCommand) {
	
		return getGgbAPI().getValue(theCommand);
	}

	public static void setCoords(LogoList llst) {
		double[] bounds = { -15.0, 15.0, -20.0, 20.0 };
		for (int i = 0; i < 4; i++) {
			bounds[i] = ((Double) llst.get(i)).doubleValue();
		}
		
		
		final double xmin = bounds[0];
		final double xmax = bounds[1];
		final double ymin = bounds[2];
		final double ymax = bounds[3];
		
		SwingUtilities.invokeLater( new Runnable() {
		     public void run() {
		    	 getGgbAPI().setCoordSystem(xmin, xmax, ymin, ymax);  
		    	 System.err.println("HELLOW!!");
		    	 getGalleryAPI().setCoordSystem(xmin, xmax, ymin, ymax);
		     }
		 } );
		
	}

	// public static void refreshGGB( int width, int height)
	// {
	// if ( ggWid == 0 || ggHt == 0)
	// {
	// blittedImage =
	// theAPI.getApplication().getEuclidianView().getExportImage(1.0);
	// ggHt = blittedImage.getHeight();
	// ggWid = blittedImage.getWidth();
	// }
	// double scaleW = (double)width / (double)ggWid;
	// double scaleH = (double)height / (double)ggHt;
	//		
	// double scale = scaleW;
	// if (scale > scaleH) { scale = scaleH; }
	//		
	// blittedImage =
	// theAPI.getApplication().getEuclidianView().getExportImage(scale);
	// if ( filtered )
	// {
	// short[] invert = new short[256];
	// for (int i = 0; i < 256; i++)
	// invert[i] = (short)(255 - i);
	// BufferedImageOp invertOp = new LookupOp(
	// new ShortLookupTable(0, invert), null);
	// BufferedImage temp2 = blittedImage;
	// BufferedImage temp = invertOp.filter( blittedImage , temp2);
	// blittedImage = temp2;
	// }
	// }

	public static class MinimizeGGB extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] {});
		}

		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {
			graphFrame.setState(Frame.ICONIFIED);

		}
	}

	
	public static class MinimizeGallery extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] {});
		}

		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {
			galleryFrame.setState(Frame.ICONIFIED);

		}
	}
	
	public static class RestoreGGB extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] {});
		}

		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {
			graphFrame.setVisible(true);
			graphFrame.setState(Frame.NORMAL);
		}
	}
	
	
	public static class RestoreGallery extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] {});
		}

		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {
			galleryFrame.setVisible(true);
			galleryFrame.setState(Frame.NORMAL);
		}
	}

	public static class AddStudentFunction extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType(),
					Syntax.StringType(), 
					Syntax.StringType(),
					Syntax.NumberType(), Syntax.NumberType(), Syntax.NumberType()
					});
		}

		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {
			
			
			String username = arg[0].getString();
			String functiontag = arg[1].getString();
			final String expression = arg[2].getString();
			final int rvalue = arg[3].getIntValue();
			final int gvalue = arg[4].getIntValue();
			final int bvalue = arg[5].getIntValue();
			
			final String functionname = functiontag + "_{" + username + "}"; 
			
			SwingUtilities.invokeLater( new Runnable() {
			     public void run() {
			    	 addStudentFunction( functionname, expression, rvalue, gvalue, bvalue);	    	 
			     }
			 } );
			
		}
	}

	
	public static void addStudentFunction(final String functionname, final String expression, final int rval, final int gval, final int bval ) {
		
		String functiondescriptor = functionname + "(x)"; 
		String command = functiondescriptor + "=" + expression; 

		System.out.print( "About to ask GGB to validate: " + command + " ...... "); // Debug line
		boolean resultVal = runCommand(command);

		if (resultVal) {
			String type = getGgbAPI().getObjectType(functionname);
			System.out.println( "Passed! GGB recognizes it as a: " + type + ". Will add it as a student function." ); // Debug line
			// System.err.println( "Should be (student) Function:" + type);
			if (type.equals("function")) //$NON-NLS-1$
			{
				
				SwingUtilities.invokeLater( new Runnable() {
				     public void run() {
				    	GgbAPI ggbapi = getGgbAPI(); 
				    	ggbapi.setColor(functionname, rval, gval, bval);
						ggbapi.setLineThickness(functionname, 5);
						ggbapi.setLabelMode(functionname, false);	    	 
				     }
				 } );
				
				//studentFunctionObjectNames.add(functionname);  //changed to hashtable to keep the function definition around
				studentFunctionLookup.put( functionname,  expression );
				
			}
		} else { // the else block is just for debugging purpose
			System.out.println( "Failed! GGB can not recognize: " + command + ". No further action done on it." ); // Debug line
		}
	}
	
	
	public static class AddFunctionToGallery extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType(),
					Syntax.StringType(), Syntax.NumberType(), Syntax.NumberType(), Syntax.NumberType() });
		}

		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {
			final String functiontag = arg[0].getString();
			final String expression = arg[1].getString();
			final int r = arg[2].getIntValue();
			final int g = arg[3].getIntValue();
			final int b = arg[4].getIntValue();
					
			try {
				SwingUtilities.invokeLater( new Runnable() {
				     public void run() {
				    	addGalleryFunction( functiontag, expression, r, g, b );	
						galleryPanel.repaint();	    	 
				     }
				 } );
				
			}
			catch (Exception e)
			{
				System.err.println("Caught an exception: ");
				e.printStackTrace();
			}
		}
	}

	
	public static void addGalleryFunction(String functionname, String expression, int r, int g, int b ) {
		
		String functiondescriptor = functionname + "(x)"; 
		String command = functiondescriptor + "=" + expression; 

		System.out.print( "About to ask GGB to validate: " + command + " ...... "); // Debug line
		boolean resultVal = galleryCommand(command);

		if (resultVal) {
			GgbAPI gallapi = getGalleryAPI();
			String type = gallapi.getObjectType(functionname);
			System.out.println( "Passed! GGB recognizes it as a: " + type + ". Will add it as a student function." ); // Debug line
			// System.err.println( "Should be (student) Function:" + type);
			if (type.equals("function")) 
			{
				gallapi.setColor(functionname, r, g, b);				
				gallapi.setLineThickness(functionname, 5);
				gallapi.setLabelMode(functionname, false);
			}
		} else { // the else block is just for debugging purpose
			System.out.println( "Failed! GGB can not recognize: " + command + ". No further action done on it." ); // Debug line
		}
		//galleryFrame.setState( Frame.NORMAL );
	}
	
	
	public static class ClearGeoGebra extends DefaultCommand {
		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {

			
			SwingUtilities.invokeLater( new Runnable() {
			     public void run() {
			    	 GgbAPI ggbapi = getGgbAPI();
			    	 String[] grapheditems  = ggbapi.getObjNames();
						for (String item : grapheditems )
						{
							ggbapi.deleteObject(item);
						}
						ggbPanel.repaint();    	 
			     }
			 } );
			
			studentFunctionLookup.clear();
			teacherFunctionLookup.clear();
		}
	}
	
	public static class ClearGallery extends DefaultCommand {
		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {

			
			SwingUtilities.invokeLater( new Runnable() {
			     public void run() {
			    	 GgbAPI gallapi = getGalleryAPI();
			    	 String[] galleryitems  = gallapi.getObjNames();
						for (String item : galleryitems )
						{
							gallapi.deleteObject(item);
						}
						galleryPanel.repaint();    	 
			     }
			 } );
		}
	}
	
	public static class SaveGalleryAsDocument extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType() } );
		}
		
		public void perform(Argument[] arg, Context ctxt)
		throws ExtensionException, LogoException {

			String filename = arg[0].getString();
			GgbAPI api = galleryPanel.getGeoGebraAPI();
			try {
				byte[] data = Base64.decode(api.getBase64(true));
				String thepath;
				
				try {
					thepath = ctxt.attachModelDir(filename);
				} catch (MalformedURLException e) {  //if path is bad somehow, we'll save to the NL app home.
					thepath = filename;
				}
				OutputStream out = new FileOutputStream( thepath);
				out.write(data);
				out.close();
			}
			catch (IOException ioe )
			{
				System.err.println("UNABLE TO WRTIE");
			}
		}
		
	}
	
	
	public static class ColorStudentObject extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType(),
					Syntax.StringType(), Syntax.NumberType(), Syntax.NumberType(),
					Syntax.NumberType() });
		}

		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {

			String username = arg[0].getString();
			String prefix = arg[1].getString();

			final int rvalue = arg[2].getIntValue();
			final int gvalue = arg[3].getIntValue();
			final int bvalue = arg[4].getIntValue();

			final String objectname = prefix + "_{" + username + "}"; //$NON-NLS-1$ //$NON-NLS-2$

			
			SwingUtilities.invokeLater( new Runnable() {
			     public void run() {
			    	 getGgbAPI().setColor(objectname, rvalue, gvalue, bvalue);
			     }
			 } );
			
			
		}
	}
	
	public static class SetStudentObjectVisible extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType(),  Syntax.StringType(), Syntax.BooleanType() } );
		}
		
		public void perform(Argument[] arg, Context ctxt)
		throws ExtensionException, LogoException {

			String username = arg[0].getString();
			String prefix = arg[1].getString();
			
			final boolean onoff = arg[2].getBooleanValue();
			final String objectname = prefix + "_{" + username + "}"; 

			SwingUtilities.invokeLater( new Runnable() {
			     public void run() {
			    	 getGgbAPI().setVisible(objectname, onoff);
			     }
			 } );
		}
		
	}
	
	public static class SaveGeogebraFile extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType() } );
		}
		
		public void perform(Argument[] arg, Context ctxt)
		throws ExtensionException, LogoException {

			String filename = arg[0].getString();
			GgbAPI api = ggbPanel.getGeoGebraAPI();
			try {
				byte[] data = Base64.decode(api.getBase64(true));
				
				String thepath;
				
				try {
					thepath = ctxt.attachModelDir(filename);
				} catch (MalformedURLException e) {  //if path is bad somehow, we'll save to the NL app home.
					thepath = filename;
				}
				OutputStream out = new FileOutputStream( thepath);
				out.write(data);
				out.close();
			}
			catch (IOException ioe )
			{
				System.err.println("UNABLE TO WRTIE");
			}
		}
		
	}
	
		

	public static class ColorNamedObjectRGB extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax
					.commandSyntax(new int[] { Syntax.StringType(),
							Syntax.NumberType(), Syntax.NumberType(),
							Syntax.NumberType() });
		}

		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {

			
			final String objectname = arg[0].getString();
			final int rvalue = arg[1].getIntValue();
			final int gvalue = arg[2].getIntValue();
			final int bvalue = arg[3].getIntValue();

			SwingUtilities.invokeLater( new Runnable() {
			     public void run() {
			    	 getGgbAPI().setColor(objectname, rvalue, gvalue, bvalue);
			     }
			 } );
		}
	}

	public static class AddStudentPoint extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType(),
					Syntax.NumberType(), Syntax.NumberType() });
		}

		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {

			String username = arg[0].getString();
			double xval = arg[1].getDoubleValue();
			double yval = arg[2].getDoubleValue();
			
			
			
			final String pointname = "P_{" + username + "}"; 
			final String command = pointname + "=" + "(" + xval + "," + yval + ")"; 
			
			SwingUtilities.invokeLater( new Runnable() {
			     public void run() {
			    	 boolean result = runCommand(command);
						if (result) {
							GgbAPI ggbapi = getGgbAPI();
							String type = ggbapi.getObjectType(pointname);
							
							if (type.equals("point")) 
							{
								ggbapi.setPointSize(pointname, 8);
								ggbapi.setLabelMode(pointname, false);
							}
						} else { // the else block is just for debugging purposes
						    System.out.println( "Failed! GGB does not recognize " + command + ". No further action is done to it." );
						}
			     }
			 } );
			
		}
	}

	public static class GraphTeacherFunction extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType(),
					Syntax.StringType(), 
					Syntax.NumberType(), 
					Syntax.NumberType(), 
					Syntax.NumberType() });
		}

		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {

			String functiontag = arg[0].getString();
			final String functionname = functiontag
					+ Messages.getString("teacher.moniker"); 
			String lhs = functionname + "(x)"; 
			final String expression = arg[1].getString();
			final String command = lhs + "=" + expression;
			
			
			final int rval = arg[2].getIntValue();
			final int gval = arg[3].getIntValue();
			final int bval = arg[4].getIntValue();

			System.out.print( "About to ask GGB to validate: " + command + " ...... " ); // Debug line
			

			SwingUtilities.invokeLater( new Runnable() {
			     public void run() {
					boolean result = runCommand(command);
					if (result) {
						GgbAPI ggbapi = getGgbAPI();
						String type = ggbapi.getObjectType(functionname);
						System.out.println( "Passed! GGB recognizes it as a: " + type + ". Adding it as a Teacher function." ); // Debug line
						// System.err.println( "Should be (teacher) Fuction:" + type);
						if (type.equals("function")) 
						{
							ggbapi.setColor(functionname, rval, gval, bval);
							ggbapi.setLineThickness(functionname, 8);
							ggbapi.setLabelMode(functionname, true);
							
							teacherFunctionLookup.put( functionname,  expression );
						}
					} else { // the else block is just for debugging purpose only
					    System.out.println( "Failed! GGB does not recognize " + command + ". No further action is done to it." );
					}
			     }
			 } );
		}
	}

	public static class GraphTeacherPoint extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType(),
					Syntax.NumberType(), Syntax.NumberType(), Syntax.NumberType(), Syntax.NumberType(), Syntax.NumberType() });
		}

		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {

			String tag = arg[0].getString();
			int xval = arg[1].getIntValue();
			int yval = arg[2].getIntValue();
			
			final int rval = arg[3].getIntValue();
			final int gval = arg[4].getIntValue();
			final int bval = arg[5].getIntValue();
			
			
			
			final String pointname = Messages.getString("teacher.initial") + tag + "}"; 
			final String command = pointname + "=" + "(" + xval + "," + yval + ")"; 
			
			SwingUtilities.invokeLater( new Runnable() {
			     public void run() {
			    	 boolean result = runCommand(command);
						if (result) {
							GgbAPI ggbapi = getGgbAPI();
							String type = ggbapi.getObjectType(pointname);
							System.out.println( "Passed! GGB recognizes it as a: " + type + ". Adding it as a Teacher point." ); // Debug line
							// System.err.println( "Should be (teacher) Point:" + type);
							if (type.equals("point")) 
							{
								ggbapi.setColor(pointname, rval, gval, bval);
								ggbapi.setPointSize(pointname, 8);
								ggbapi.setLabelMode(pointname, true);
							}
						} else { // the else block is just for debugging purpose only
						    System.out.println( "Failed! GGB does not recognize: " + command + ". No further action is done to it." );
						}
			     }
			 } );
		}
	}

	public static class GetFunctionExpression extends DefaultReporter {
		public Syntax getSyntax() {
			return Syntax.reporterSyntax(new int[] { Syntax.StringType() },
					Syntax.StringType());
		}

		public Object report(Argument[] arg, Context arg1)
				throws ExtensionException, LogoException {

			String name = arg[0].getString();
			if ( studentFunctionLookup.containsKey( name ) )
			{
				return studentFunctionLookup.get(name);
			}
			else if ( teacherFunctionLookup.containsKey( name ) )
			{
				return teacherFunctionLookup.get(name); 
			}
			else
			{
				return "No Function";
			}
			//return theAPI.getValueString(arg[0].getString());
		}

	}
	
	
	public static class GetPath extends DefaultReporter {
		public Syntax getSyntax() {
			return Syntax.reporterSyntax(new int[] { Syntax.StringType() },
					Syntax.StringType());
		}

		public Object report(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {

			String maybeworks;
			String filenm = arg[0].getString();
			
			try {
				maybeworks = ctxt.attachModelDir(filenm);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				maybeworks = "OOPS";
			}
			return maybeworks;
		}

	}
	
	public static class DeleteNamedObject extends DefaultCommand {
		
		public Syntax getSyntax() {
			return Syntax
					.commandSyntax(new int[] { Syntax.StringType()  });
		}
		
		public void perform(Argument[] arg, Context ctxt) throws ExtensionException, LogoException {
			
			final String objectname = arg[0].getString();
			
			SwingUtilities.invokeLater( new Runnable() {
			     public void run() {
			    	 getGgbAPI().deleteObject(objectname);
			     }
			 } );
		}
	}
	
	
	public static class DeleteStudentFunction extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType(),
					Syntax.StringType()});
		}

		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {
			String username = arg[0].getString();
			String functiontag = arg[1].getString();
			
			
			final String functionname = functiontag + "_{" + username + "}";
			
			SwingUtilities.invokeLater( new Runnable() {
			     public void run() {
			    	 getGgbAPI().deleteObject(functionname);
			     }
			 } );
		}
	}

//	// not exposed
//	public static class GgbCommand extends DefaultCommand {
//		@Override
//		public Syntax getSyntax() {
//			return Syntax.commandSyntax(new int[] { Syntax.StringType() });
//		}
//
//		public void perform(Argument[] arg, Context ctxt)
//				throws ExtensionException, LogoException {
//			// TODO Auto-generated method stub
//		        System.out.println( "this line is from the GGBCommand.perform() block. Ever executed??" ); // Debug line
//			runCommand(arg[0].getString());
//			// BufferedImage bi = ctxt.getDrawing();
//			// int wid = bi.getWidth();
//			// int ht = bi.getHeight();
//			// refreshGGB( wid, ht );
//			// Graphics g = bi.getGraphics();
//			// g.drawImage( blittedImage, 0, 0, null);
//		}
//	}
//
	// not exposed
	public static class SetCoords extends DefaultCommand {
		@Override
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.ListType() });
		}

		public void perform(Argument[] arg, Context ctxt)
				throws ExtensionException, LogoException {
			// TODO Auto-generated method stub

			setCoords(arg[0].getList());
			// BufferedImage bi = ctxt.getDrawing();
			// int wid = bi.getWidth();
			// int ht = bi.getHeight();
			// refreshGGB( wid, ht );
			// Graphics g = bi.getGraphics();
			// g.drawImage( blittedImage, 0, 0, null);
		}
	}
//
//	// not exposed
//	public static class ShowWindow extends DefaultCommand {
//		public void perform(Argument[] arg, Context ctxt)
//				throws ExtensionException, LogoException {
//			// TODO Auto-generated method stub
//
//			graphFrame.setVisible(true);
//		}
//	}
//
//	// not exposed
//	public static class HideWindow extends DefaultCommand {
//		public void perform(Argument[] arg, Context ctxt)
//				throws ExtensionException, LogoException {
//			// TODO Auto-generated method stub
//
//			graphFrame.setVisible(false);
//		}
//	}
//
//	// not exposed
//	public static class EvalFn extends DefaultReporter {
//
//		@Override
//		public Syntax getSyntax() {
//			return Syntax.reporterSyntax(new int[] { Syntax.StringType() },
//					Syntax.NumberType());
//		}
//
//		public Object report(Argument[] arg, Context arg1)
//				throws ExtensionException, LogoException {
//			// TODO Auto-generated method stub
//			double retn = runReporter(arg[0].getString());
//			return new Double(retn);
//		}
//	}
//
//	// not exposed
//	public static class EvalCasCommand extends DefaultReporter {
//		public Syntax getSyntax() {
//			return Syntax.reporterSyntax(new int[] { Syntax.StringType() },
//					Syntax.StringType());
//		}
//
//		public Object report(Argument[] arg, Context arg1)
//				throws ExtensionException, LogoException {
//			// TODO Auto-generated method stub
//			return casEvaluate(arg[0].getString());
//		}
//	}

	public static class FunctionsThroughGivenPoint extends DefaultReporter {
		public Syntax getSyntax() {
			return Syntax.reporterSyntax(new int[] { Syntax.NumberType(),
					Syntax.NumberType() }, Syntax.ListType());
		}

		public Object report(Argument[] arg, Context arg1)
				throws ExtensionException, LogoException {

			Vector<String> hitVector = new Vector<String>();
			//Iterator<String> iter = studentFunctionObjectNames.iterator();
			
			Iterator<String> iter = studentFunctionLookup.keySet().iterator();
			Iterator<String> teachiter = teacherFunctionLookup.keySet().iterator();
			
			double xVal = arg[0].getDoubleValue();
			double yVal = arg[1].getDoubleValue();
			while (iter.hasNext()) {
				String fname = iter.next();
				String studentFunction = studentFunctionLookup.get(fname);
				String test = "Substitute["+studentFunction+",x,"+xVal+"]";
				String result = getGgbAPI().evalGeoGebraCAS(test);
				
				try {
					Double diff = Double.valueOf(result) - yVal;
					if ( Math.abs(diff) < .0001 ) {  
						hitVector.add(fname); }
					else {
						//nothing	
					}
				}
				catch ( NumberFormatException nfe )
				{
					//this happens when the function is not defined.
					//hitVector.add("THERE WAS A NUMBER FORMAT EXCEPTION ON "+result);
				}
			}//student functions
			
			while (teachiter.hasNext() )
			{
				String tfname = teachiter.next();
				String teacherFunction = teacherFunctionLookup.get(tfname);
				String test = "Substitute["+teacherFunction+",x,"+xVal+"]";
				String result = getGgbAPI().evalGeoGebraCAS(test);
				
				try {
					Double diff = Double.valueOf(result) - yVal;
					if ( Math.abs(diff) < .0001 ) {  
						hitVector.add(tfname); }
					else {
						//nothing
					}
				}
				catch ( NumberFormatException nfe )
				{
					//this happens when the function is not defined.
					//hitVector.add("THERE WAS A NUMBER FORMAT EXCEPTION ON "+result);
				}
			}//teacher functions 
		
			LogoListBuilder nllb = new LogoListBuilder();
			nllb.addAll(hitVector);
			return nllb.toLogoList();
		}

	}
	
	
	public static class FunctionsWithGivenColor extends DefaultReporter {
		public Syntax getSyntax() {
			return Syntax.reporterSyntax(new int[] { Syntax.NumberType(),
					Syntax.NumberType(), Syntax.NumberType() }, Syntax.ListType());
		}

		public Object report(Argument[] arg, Context arg1)
				throws ExtensionException, LogoException {
			
			
			int rvalue = arg[0].getIntValue();
			int gvalue = arg[1].getIntValue();
			int bvalue = arg[2].getIntValue();
			java.awt.Color testColor = new java.awt.Color(rvalue, gvalue, bvalue);
			String testColorString = "#" + geogebra.util.Util.toHexString(testColor);		

			
			Vector<String> hitVector = new Vector<String>();
			//Iterator<String> iter = studentFunctionObjectNames.iterator();
			Iterator<String> iter = studentFunctionLookup.keySet().iterator();
			Iterator<String> teachiter = teacherFunctionLookup.keySet().iterator();
			
			while (iter.hasNext()) {
				String fname = iter.next();
				String thecolor = getGgbAPI().getColor(fname);
				if (thecolor.equals(testColorString))
					hitVector.add(fname);
			}

			while (teachiter.hasNext()) {
				String tfname = iter.next();
				String tcolor = getGgbAPI().getColor(tfname);
				if (tcolor.equals(testColorString))
					hitVector.add(tfname);
			}

			//new in 5.0 -- it appears creation of LogoLists is now 'facilitated' by this builder class.
			LogoListBuilder nllb = new LogoListBuilder();
			nllb.addAll(hitVector);
			return nllb.toLogoList();
		}

	}
	
	
	public static class ColorAllFunctions extends DefaultCommand {
		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.NumberType(),
					Syntax.NumberType(), Syntax.NumberType() });
		}

		public void perform(Argument[] arg, Context arg1)
				throws ExtensionException, LogoException {
			
			
			final int rvalue = arg[0].getIntValue();
			final int gvalue = arg[1].getIntValue();
			final int bvalue = arg[2].getIntValue();
			
			SwingUtilities.invokeLater( new Runnable() {
			     public void run() {
			    	 String[] theNames = getGgbAPI().getAllObjectNames();
						for (String name : theNames)
						{
							String type = getGgbAPI().getObjectType(name);
							if (type.equals ("function"))
							{
								getGgbAPI().setColor(name, rvalue, gvalue, bvalue);
							}
						}
			     }
			 } );
			
		}

	}
	

}