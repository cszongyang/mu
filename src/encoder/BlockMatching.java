package encoder;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.*;

//import javax.media.jai.iterator.RandomIter;
//import javax.swing.JCheckBoxMenuItem;
//import javax.media.jai.iterator.RandomIter;
//import javax.media.jai.iterator.RandomIterFactory;

import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.awt.Image;

public class BlockMatching extends JPanel implements MouseListener   
{
	//Dimension size;
	BufferedImage img;
	int baseSize;
	private JLabel label;
	JTextArea textArea;
	static final String NEWLINE = System.getProperty("line.separator");
	static String []ImageNames = new String [2] ;
	JLabel leftLabel;
	JLabel rightLabel;
	int xCoordinate;
	int yCoordinate;
	int xRealcoordinate;
	int yRealcoordinate;
	private final int ITEM_PLAIN	=	0;

	private	JMenuBar  menuBar;
	private	JMenu	  menuFile;
	private	JMenu	  menuHelp;
	private	JMenu	  menuProperty;
	private	JMenuItem menuPropertyFull;
	private	JMenuItem menuPropertyThree;
	private	JMenuItem menuPropertyBinary;
	private	JMenuItem menuFileNew;
	private	JMenuItem menuFileOpenRefImg;
	private	JMenuItem menuFileOpenImg;
	private	JMenuItem menuFileExit;

	static JFrame frame;

	JTextArea log;
	JFileChooser fc;
	JPanel field;
	BufferedImage RealLeftImage;
	BufferedImage RealRightImage;
	static BufferedImage left;
	static BufferedImage right;
	static int SType;
	;
	// Constructor 
	public BlockMatching(String IMG1, String IMG2, int SearchType)
	{
		setLayout(new BorderLayout());
		//setLayout( new FlowLayout() );
		baseSize = 600;
		fc = new JFileChooser();
		Dimension size = new Dimension(1780,1180);
		ImageNames[0] = IMG1 ;
		ImageNames[1] = IMG2 ;
		SType = SearchType;
		//final JFileChooser fc = new JFileChooser();
		//int returnVal = fc.showOpenDialog(aComponent);
		left = createImage(0,true); // to do resize control
		right = createImage(1,true);

		RealLeftImage= createImage(0,false);
		RealRightImage = createImage(1,false);

		textArea = new JTextArea(3,3);
		menuBar = new JMenuBar();

		leftLabel = new JLabel(new ImageIcon(left));
		rightLabel = new JLabel(new ImageIcon(right));
		field = new JPanel();

		add(menuBar, BorderLayout.NORTH);
		add(leftLabel, BorderLayout.WEST);
		add(rightLabel, BorderLayout.EAST);
		add(field, BorderLayout.CENTER);

		field.add(textArea);
		menuProperty = new JMenu( "Properties" );
		menuProperty.setMnemonic( 'P' );

		// Create property items
		menuPropertyFull = CreateMenuItem( menuProperty, ITEM_PLAIN,
				"Full Search..", null, 'S', null );
		menuPropertyThree = CreateMenuItem( menuProperty, ITEM_PLAIN,
				"Three Step Search..", null, 'E', null );
		menuPropertyBinary = CreateMenuItem( menuProperty, ITEM_PLAIN,
				"Binary Search..", null, 'D', null );

		menuPropertyFull.addMouseListener(this);

		// Create the file menu
		menuFile = new JMenu( "File" );
		menuFile.setMnemonic( 'F' );
		menuBar.add( menuFile );

		// Create the file menu
		// Build a file menu items
		menuFileNew = CreateMenuItem( menuFile, ITEM_PLAIN,
				"New", null, 'N', null );
		menuFileOpenRefImg = CreateMenuItem( menuFile, ITEM_PLAIN, "Open Reference Frame..",
				new ImageIcon( "open.gif" ), 'O',
				"Open a new file" );

		menuFileOpenImg = CreateMenuItem( menuFile, ITEM_PLAIN, "Open Image..",
				new ImageIcon( "open.gif" ), 'O',
				"Open a new file" );

		menuFileOpenRefImg.addMouseListener(this);
		menuFileOpenImg.addMouseListener(this);

		// Add the property menu
		menuFile.addSeparator();
		menuFile.add( menuProperty );	
		menuFile.addSeparator();
		menuFileExit = CreateMenuItem( menuFile, ITEM_PLAIN,
				"Exit", null, 'x',
				"Exit the program" );
		// Create the file menu
		menuHelp = new JMenu( "Help" );
		menuHelp.setMnemonic( 'E' );
		menuBar.add( menuHelp );
		leftLabel.addMouseListener(this);
		rightLabel.addMouseListener(this);
		addMouseListener(this);

		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane,BorderLayout.SOUTH);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
	}

	private static void createAndShowUI(String IMG1, String IMG2, int SearchType)
	{

		frame.dispose();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new BlockMatching(IMG1, IMG2, SearchType) );
		frame.setSize(2100, 2100);
		frame.setLocationByPlatform( true );
		frame.setVisible( true );
	}

	public static int[] fullsearch (int x , int y){

		int[] t = new int[2]; 

		int xCenter = x;
		int yCenter = y;

		int MovingX;
		int MovingY;

		int[][][] pixelArrayLeft = new int[30][30][3];
		int[][][] pixelArrayRight = new int[30][30][3];

		double mseExtracted;
		double mseMin = 100000;
		//System.out.println("extract real");

		pixelArrayLeft = extractPixelArray(x,y, left);
		MovingX = xCenter - 150;
		MovingY = yCenter - 50;

		//System.out.println("Before: moving x = " + MovingX+" and moving y = "+ MovingY );

		for (int i=0 ; i < 300; i++){

			MovingY = y - 50;
			MovingX++ ;
			//System.out.println("moving x = " + MovingX+" and moving y = "+ MovingY + " i " + i);
			if (MovingX <= 0){
				//System.out.println(" x < 0");
				continue;
			}
			if (MovingX >= 370){
				//System.out.println(" x > 392");
				continue;
			}

			for(int j = 0; j < 100; j++){


				MovingY++;
				//System.out.println("moving x = " + MovingX+" and moving y = "+ MovingY );
				if (MovingY <= 0){

					continue;
				}
				if (MovingY >= 570){
					continue;
				}


				pixelArrayRight = extractPixelArray(MovingX,MovingY, right);
				mseExtracted = MSE(pixelArrayLeft,pixelArrayRight);

				if (mseExtracted < mseMin){
					mseMin = mseExtracted ;
					//System.out.println("min changed at " +MovingX+","+MovingY +" and min is "+ mseMin );
					xCenter = MovingX;
					yCenter = MovingY;
				}

			}
		}
		System.out.println("Final matching coordinates at (" +xCenter+","+yCenter +") and min value is "+ mseMin );
		t[0] = xCenter;
		t[1] = yCenter;
		return t;

	}
	// Three step search 
	public static int[] ThreeStepSearch (int x, int y){
		int[] t = new int[2]; 

		int xCenter = x;
		int yCenter = y;
		//Coordinates of the moving orgin
		int MovingX;
		int MovingY;
		// Creating pixel array with size macroblock(30X30) 30X30X3(RGB)
		int[][][] pixelArrayLeft = new int[30][30][3];
		int[][][] pixelArrayRight = new int[30][30][3];

		double mseExtracted;
		// Initializing Meansquare Error
		double mseMin = 100000;
		//System.out.println("extract real");

		// Extracted Array extraction
		pixelArrayLeft = extractPixelArray(x,y, left);
		// Start of three step search with init step 64 and a convergence rate of 1/2
		for (int step= 64 ; step > 1; step = step/2){

			MovingX = xCenter - step;
			MovingY = yCenter - step;

			//System.out.println ("STEP" + step);
			for(int i = 1 ; i <=3; i++){
				//System.out.println ("coordinate " + i + "," + j);
				MovingY = y - step;
				if (MovingX < 0){
					MovingX = MovingX + step;
					continue;
				}
				// Limit check
				if (MovingX >= 370){
					continue;
				}
				for(int j = 1; j <=3 ; j++){
					//System.out.println ("coordinate " + i + "," + j);
					if (MovingY < 0){
						MovingY = MovingY + step;
						continue;
					}
					if (MovingY >= 570){
						continue;
					}

					// Compare
					//System.out.println("moving x = " + MovingX+" and moving y = "+ MovingY );
					pixelArrayRight = extractPixelArray(MovingX,MovingY, right);
					mseExtracted = MSE(pixelArrayLeft,pixelArrayRight);

					// Checking if current MSE is less that the current minMSE and updating if yes  
					if (mseExtracted < mseMin){
						mseMin = mseExtracted ;
						xCenter = MovingX;
						yCenter = MovingY;
					}
					//Changing step in Y-axis
					MovingY = MovingY + step;
				}
				//Changing step in X-axis
				MovingX = MovingX + step;
			}
		}
		//System.out.println("Final min at " +xCenter+","+yCenter +" and min is "+ mseMin );
		t[0] = xCenter;
		t[1] = yCenter;
		return t;
	}

	// Creating Image 
	public static BufferedImage createImage(int x, boolean resize)
	{
		Image img = null;
		BufferedImage imgBuffered = null;
		try {
			img = ImageIO.read(new File(ImageNames[x]));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (resize == true){
			img = resizeImage(img, 600,600, true);
		}

		imgBuffered = toBufferedImage(img);
		return imgBuffered;
	}

	public static Image resizeImage(Image image, int width, int height, boolean max) {

		if (width < 0 && height > 0) {
			return resizeImageBy(image, height, false);
		} else if (width > 0 && height < 0) {
			return resizeImageBy(image, width, true);
		} else if (width < 0 && height < 0) {

			return image;

		}
		int currentHeight = image.getHeight(null);
		int currentWidth = image.getWidth(null);
		int expectedWidth = (height * currentWidth) / currentHeight;
		int size = height;
		if (max && expectedWidth > width) {
			size = width;
		} else if (!max && expectedWidth < width) {
			size = width;
		}
		return resizeImageBy(image, size, (size == width));
	}

	public static Image resizeImageBy(Image image, int size, boolean setWidth) {
		if (setWidth) {
			return image.getScaledInstance(size, -1, Image.SCALE_SMOOTH);
		} else {
			return image.getScaledInstance(-1, size, Image.SCALE_SMOOTH);
		}
	}

	public static BufferedImage toBufferedImage(Image img)
	{
		if (img instanceof BufferedImage)
		{
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	public static Image getImageFromArray(int[] pixels, int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = (WritableRaster) image.getData();
		raster.setPixels(0,0,width,height,pixels);
		return image;
	}

	// Extracting pixels of a given coordinates in a buffered image
	public static int[][][] extractPixelArray (int x, int y, BufferedImage image){

		int pixelNum = 30;
		int[][][] RGB =  new int [30][30][3];
		int bufferINT ; 
		Color temp; 

		for (int i = 0; i< pixelNum; i++){
			//System.out.println("inside 1");
			for (int j = 0; j< pixelNum; j++ ){
				//System.out.println("pixel inside 2 ==> " + i +","+ j);

				bufferINT = image.getRGB(x+i,y+j);
				temp = new Color(bufferINT);

				for (int k=0; k< 3 ; k++){
					//System.out.println("inside 3" + k);
					switch (k) {
					case 0: 
						RGB[i][j][k] = temp.getRed();
						break;
					case 1:
						RGB[i][j][k] = temp.getGreen();
						break;
					case 2: 
						RGB[i][j][k] = temp.getBlue();
						break;
					}
				}
			}
		}
		//System.out.println("extract Done");
		return RGB;

	}	

	// Calculating MSE between two macro-blocks 
	public static double MSE (int [][][]sourceImage,int [][][] ComparedImage) {

		int sum_sq = 0;
		//double []mse = new double[3];

		// 8X8 block
		int h= 30;
		int w = 30;
		int RGB = 3;

		for (int k = 0 ; k<RGB ; ++k){

			for (int i = 0; i < h; ++i)
			{
				for (int j = 0; j < w; ++j)
				{
					int p1 = sourceImage[i][j][k];
					int p2 = ComparedImage[i][j][k];
					int err = p2 - p1;
					sum_sq += (err * err);
				}
			}
			//mse[k] = (double)sum_sq / (h * w);
		}
		//double tmse = Math.pow((mse[0]*mse[0]),2) + Math.pow((mse[1]*mse[1]),2) + Math.pow((mse[2]*mse[2]),2);
		//return (Math.sqrt(tmse));
		//System.out.println("MSE is " + ((double)sum_sq / (h * w)));
		return ((double)sum_sq / (h * w));
	}

	// EnhancedFullSearch ()
	//@ToDo: Solve dynamic block size
	public static int[] compareBestMSE (int x1, int y1, int x2, int y2, int x3, int y3, double refMSE1, double refMSE2, double refMSE3){

		int[][] a = new int[3][2];
		int[][] b = new int[3][2];
		a[0][1] = 0;
		a[1][1] = 0;
		a[2][1] = 0;

		a[0][0] = x1;
		a[1][0] = x2;
		a[2][0] = x3;

		b[0][1] = 0;
		b[1][1] = 0;
		b[2][1] = 0;

		b[0][0] = y1;
		b[1][0] = y2;
		b[2][0] = y3;

		double MSE1;
		double MSE2;
		double MSE3;

		//System.out.println("ALL Before getting range "+x1+","+y1+" and "+x2+","+y2+" and " +x3+","+ y3);

		int[] t = new int[2];
		// first check the best MSE
		int minX;
		int minY;

		boolean negativeFlagX = false;
		boolean negativeFlagY = false;

		int[][][] option1;
		int[][][] option2;
		int[][][] option3;

		int[][][] ref1;
		int[][][] ref2;
		int[][][] ref3;

		for (int i=0 ; i < 3; i++){
			if (a[i][0] >= 200){
				a[i][0] = 400  - a[i][0];
				a[i][1] = 1;
			}
			if (b[i][0] >= 300){
				b[i][0] = 600 - b[i][0] ;
				b[i][1] = 1;
			}
		}
		minX = a[0][0];
		if (a[0][1] == 1){
			negativeFlagX = true;}
		minY = b[0][0];
		if (b[0][1] == 1){
			negativeFlagY = true;}

		for (int i=1 ; i < 3; i++){
			//System.out.println("As "+a[0][0]+","+b[0][0]+" and "+a[1][0]+","+b[1][0]+" and " +a[2][0]+","+ b[2][0]);
			//System.out.println("Flag As "+a[0][1]+","+b[0][1]+" and "+a[1][1]+","+b[1][1]+" and " +a[2][1]+","+ b[2][1]);

			if (a[i][0] < minX){
				//System.out.println("As "+a[i][0] + "FLAG is" + a[i][1]);
				minX= a[i][0];
				if (a[i][1] == 1){
					negativeFlagX = true;
				} else {
					negativeFlagX = false;
				}
			}

			if (b[i][0] < minY){
				minY= b[i][0];
				if (b[i][1] == 1){
					negativeFlagY = true;
				} else {
					negativeFlagY = false;
				}
			}		
		}

		if (negativeFlagX){
			minX = -1 * minX;
		}
		if (negativeFlagY){
			minY = -1 * minY;
		}
		//System.out.println("AFTER "+minX+","+minY);
		option1 = extractPixelArray(x1 + minX, y1+ minY, right);
		ref1 = extractPixelArray(x1 + minX, y1+ minY, left);
		MSE1 = MSE(option1,ref1);

		option2 = extractPixelArray(x2 + minX, y2+ minY, right);
		ref2 = extractPixelArray(x2 + minX, y2+ minY, left);
		MSE2 = MSE(option2,ref2);

		option3 = extractPixelArray(x3 + minX, y3+ minY, right);
		ref3 = extractPixelArray(x3 + minX, y3+ minY, left);
		MSE3 = MSE(option3,ref3);

		MSE1 = MSE1 + 1.2*refMSE1;
		MSE2 = MSE2 + 1.2*refMSE2;
		MSE3 = MSE3 + 1.2*refMSE3;

		if (MSE1 < MSE2 && MSE1 < MSE3){
			t[0] = x1;
			t[1] = y1;
		} else if (MSE2 < MSE1 && MSE2 < MSE3){
			t[0] = x2;
			t[1] = y2;
		} else if (MSE3 < MSE1 && MSE3 < MSE2){
			t[0] = x3;
			t[1] = y3;
		}

		return t;

	}

	public static int[] fullsearchMODIFIED (int x , int y){

		int[] t = new int[2]; 

		int xCenter = x;
		int yCenter = y;

		int[][] bestThree = new int[3][2];

		int MovingX;
		int MovingY;
		double temp;
		int tempx;
		int tempy;
		int[][][] pixelArrayLeft = new int[30][30][3];
		int[][][] pixelArrayRight = new int[30][30][3];

		double mseExtracted;
		double mseMin1 = 100000;
		double mseMin2 = 100000;
		double mseMin3 = 100000;
		//System.out.println("extract real");

		pixelArrayLeft = extractPixelArray(x,y, left);
		MovingX = xCenter - 150;
		MovingY = yCenter - 50;

		//System.out.println("Before: moving x = " + MovingX+" and moving y = "+ MovingY );

		for (int i=0 ; i < 300; i++){

			MovingY = y - 50;
			MovingX++ ;
			//System.out.println("moving x = " + MovingX+" and moving y = "+ MovingY + " i " + i);
			// Checking Frame limits
			if (MovingX <= 0){
				System.out.println(" x < 0");
				continue;
			}
			if (MovingX >= 380){
				System.out.println(" x > 392");
				continue;
			}

			for(int j = 0; j < 100; j++){


				MovingY++;
				//System.out.println("moving x = " + MovingX+" and moving y = "+ MovingY );
				if (MovingY <= 0){

					continue;
				}
				if (MovingY >= 580){
					continue;
				}


				pixelArrayRight = extractPixelArray(MovingX,MovingY, right);
				mseExtracted = MSE(pixelArrayLeft,pixelArrayRight);

				if (mseExtracted < mseMin3){
					mseMin3 = mseExtracted ;

					bestThree[2][0]= MovingX;
					bestThree[2][1]= MovingY;

					if (mseMin3 < mseMin2){
						temp = mseMin2; 
						mseMin2 = mseMin3;
						mseMin3 = temp;
						tempx = bestThree[1][0];
						tempy = bestThree[1][1];
						bestThree[1][0]= bestThree[2][0];
						bestThree[1][1]= bestThree[2][1];
						bestThree[2][0] = tempx;
						bestThree[2][1] = tempy;

					} 

					if (mseMin2 < mseMin1){
						temp = mseMin1; 
						mseMin1 = mseMin2;
						mseMin2 = temp;
						tempx = bestThree[0][0];
						tempy = bestThree[0][1];
						bestThree[0][0]= bestThree[1][0];
						bestThree[0][1]= bestThree[1][1];
						bestThree[1][0] = tempx;
						bestThree[1][1] = tempy;
					} 
				}

			}
		}
		t = compareBestMSE(bestThree[0][0],bestThree[0][1],bestThree[1][0],bestThree[1][1],bestThree[2][0], bestThree[2][1], mseMin1, mseMin2,mseMin3);
		//System.out.println("Three points " +bestThree[0][0]+","+bestThree[0][1] +" and "+ +bestThree[1][0]+","+bestThree[1][1]+" and "+ +bestThree[2][0]+","+bestThree[2][1]+" and min is "+ mseMin1 +","+mseMin2+","+mseMin3);
		return t;

	}

	// MouseListener 
	public void mousePressed(MouseEvent e) {
		//eventOutput("Mouse pressed; # of clicks: " + e.getClickCount(), e);
		File file = new File("mousePressed.txt");

		if (e.getSource() == menuFileOpenRefImg) {
			int returnVal = fc.showOpenDialog(BlockMatching.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				//This is where a real application would open the file.
				textArea.append("Opening: " + file.getName() + "." + NEWLINE);
			} else {
				textArea.append("Open command cancelled by user." + NEWLINE);
			}
			//textArea.setCaretPosition(textArea.getDocument().getLength());
			ImageNames[1] = file.getName();
			//frame.dispose();
			//frame.add( new BlockMatching(ImageNames[0], ImageNames[1], SType) );
			//frame.setVisible(true);
			createAndShowUI(ImageNames[0],ImageNames[1], SType);

		} else if (e.getSource() == menuFileOpenImg) {
			int returnVal = fc.showOpenDialog(BlockMatching.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				//This is where a real application would open the file.
				textArea.append("Opening: " + file.getName() + "." + NEWLINE);
			} else {
				textArea.append("Open command cancelled by user." + NEWLINE);
			}
			//textArea.setCaretPosition(textArea.getDocument().getLength());
			ImageNames[0] = file.getName();	
			//frame.dispose();
			//frame.add( new BlockMatching(ImageNames[0], ImageNames[1], SType) );
			//frame.setVisible(true);
			createAndShowUI(ImageNames[0],ImageNames[1], SType);
		}	
	}

	void eventOutput(String eventDescription, MouseEvent e) {
		textArea.append(eventDescription + " detected on "
				+ e.getComponent().getClass().getName()
				+ "." + NEWLINE);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	public void mouseReleased(MouseEvent e) {
		//eventOutput("Mouse released (# of clicks: "
		//+ e.getClickCount() + ")", e);
	}

	public void mouseEntered(MouseEvent e) {
		//eventOutput("Mouse entered", e);
	}

	public void mouseExited(MouseEvent e) {
		//eventOutput("Mouse exited", e);
	}

	public void mouseClicked(MouseEvent e) {
		//Action
		xCoordinate = e.getX();
		yCoordinate = e.getY();


		int[] rightco = new int[2];
		// Extracting RGB from action
		int dataBuffInt2 = left.getRGB(xCoordinate, yCoordinate); 
		Color c2 = new Color(dataBuffInt2);

		textArea.append("Coordinate of point clicked is ("+ xCoordinate + "," + yCoordinate + ")" + NEWLINE);

		// Choosing type of search..

		if (SType == 1){
			rightco = fullsearch(xCoordinate,yCoordinate);
		} else if (SType == 2) {
			rightco = ThreeStepSearch(xCoordinate,yCoordinate);
		}
		//


		//rightco = fullsearchMODIFIED(xCoordinate,yCoordinate);

		//System.out.println("Right image equal = " + rightco[0] + "," + rightco[1]);

		//// Markers on clicked points and its matched block
		Graphics2D graphLeft = left.createGraphics();
		graphLeft.setColor(Color.BLACK);
		graphLeft.fill(new Rectangle(xCoordinate, yCoordinate, 9, 9));

		Graphics2D graphRight = right.createGraphics();
		graphRight.setColor(Color.BLACK);
		graphRight.fill(new Rectangle(rightco[0], rightco[1], 9, 9));


		rightLabel.validate();
		leftLabel.validate();

		frame.setVisible( false );
		frame.setVisible( true );

		textArea.append("RGB values of clicked point is:");
		textArea.append(" Red  = " + c2.getRed());
		textArea.append(" Blue  = " + c2.getGreen());
		textArea.append(" Green = " + c2.getBlue());
		textArea.append(". Best match is at (" + rightco[0] + "," + rightco[1] +")" + NEWLINE);

	}

	// Creating Menu
	public JMenuItem CreateMenuItem( JMenu menu, int iType, String sText,
			ImageIcon image, int acceleratorKey,
			String sToolTip )
	{
		// Create the item
		JMenuItem menuItem;

		switch( iType )
		{
		//		case ITEM_RADIO:
		//			menuItem = new JRadioButtonMenuItem();
		//			break;
		//
		//		case ITEM_CHECK:
		//			menuItem = new JCheckBoxMenuItem();
		//			break;

		default:
			menuItem = new JMenuItem();
			break;
		}

		// Add the item test
		menuItem.setText( sText );

		// Add the optional icon
		if( image != null )
			menuItem.setIcon( image );

		// Add the accelerator key
		if( acceleratorKey > 0 )
			menuItem.setMnemonic( acceleratorKey );

		// Add the optional tool tip text
		if( sToolTip != null )
			menuItem.setToolTipText( sToolTip );

		// Add an action handler to this menu item
		menuItem.addMouseListener( this );

		menu.add( menuItem );

		return menuItem;
	}

	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				// Calling the Main Frame
				frame = new JFrame("Frame Jaeger (General Interfaces GmbH Copyright)");
				//Creating and Showing the User Interface 
				createAndShowUI("TestIMG.JPG","TestIMG.JPG",2);
				// 1 for full search, 2 for three step search
			}
		});
	}

}
