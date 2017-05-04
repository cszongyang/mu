package decoder;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class decode5 {
	static JFrame frame;
	static JLabel lbIm1;
	static JLabel lbIm2;
	static BufferedImage img;
	final static   double pi=Math.PI;
	 static  int q1=1000;
	 static int q2=10;
	 static BufferedImage original;
	 
     public static List<BufferedImage> outputVideo=new ArrayList<BufferedImage>();
     public static List<BufferedImage> originalVideo=new ArrayList<BufferedImage>();
	 
	 static double[][] blockXU=new double[8][8];
	 static double[][] blockYV=new double[8][8];
//	public static void main(String[] args){
		
		//getVideo();
		
//		String filename= "output.cmp";		
////		String filename= "output.cmp";		
//        String line="";
//        
//        int frameRate=15;
//      
//        //预先计算idct公式里重复的部分
//        for(int x=0;x<8;x++){
//        	for(int y=0;y<8;y++){
//        		 for(int u=0;u<8;u++){
//        	        	for(int v=0;v<8;v++){
//        	        		blockXU[x][u]=Math.cos((2*x+1)*u*pi/16.0);
//        	        		blockYV[y][v]=Math.cos((2*y+1)*v*pi/16.0);
//        	        	}
//        	        }
//        	}
//        }
//        
//        List<BufferedImage> outputVideo=new ArrayList<BufferedImage>();
//        img = new BufferedImage(960, 540, BufferedImage.TYPE_INT_RGB);	
//        //一帧8160行
//	      try
//	       {
//              BufferedReader in=new BufferedReader(new FileReader(filename));
//               line=in.readLine();
//                int blockCount=0;
//            	long startTime=System.currentTimeMillis();   //获取开始时间  
//                while (line!=null)
//               {               	              	               
//                      idct(line,blockCount);
//                	  blockCount++;
//                	  if(blockCount==8160){
//                		  long endTime=System.currentTimeMillis(); //获取结束时间  
//                          System.out.println("程序运行时间： "+(endTime-startTime)+"ms");   
//                		  outputVideo.add(img);
//                		  blockCount=0;
//                		  img = new BufferedImage(960, 540, BufferedImage.TYPE_INT_RGB);
//                			 startTime=System.currentTimeMillis();   //获取开始时间  
//                	  }else{
//                		  
//                	  }
//                	  line=in.readLine();       		    
//                     
//               }
//              
//                in.close();
//	        } catch (IOException e)
//	         {
//	               e.printStackTrace();
//	        }
//	      
//	      
//	 
//		    JFrame frame = new JFrame();
//		    GridBagLayout gLayout = new GridBagLayout();
//		    frame.getContentPane().setLayout(gLayout);
//		    
//		    JLabel lbText1 = new JLabel("Video height: ");
//		    lbText1.setHorizontalAlignment(SwingConstants.CENTER);
//			
//		    lbIm1 = new JLabel(new ImageIcon(img));
//			GridBagConstraints c = new GridBagConstraints();
//			c.fill = GridBagConstraints.HORIZONTAL;
//			c.anchor = GridBagConstraints.CENTER;
//			c.weightx = 0.5;
//			c.gridx = 0;
//			c.gridy = 0;
//			frame.getContentPane().add(lbText1, c);		
//			c.fill = GridBagConstraints.HORIZONTAL;
//			c.gridx = 0;
//			c.gridy = 1;
//			frame.getContentPane().add(lbIm1, c);
//			
//			frame.pack();
//			frame.setVisible(true);
//		
//			for(int i=1; i < outputVideo.size(); i++) {
//		    	lbIm1.setIcon(new ImageIcon(outputVideo.get(i)));
//		    	try {
//		    		Thread.sleep(1000/frameRate);
//		    	} catch(InterruptedException e) {
//		    		e.printStackTrace();
//		    	}
//		    }
//			while(true){
//			 for(int i=0; i < outputVideo.size(); i++) {
//			    	lbIm1.setIcon(new ImageIcon(outputVideo.get(i)));
//			    	try {
//			    		Thread.sleep(1000/frameRate);
//			    	} catch(InterruptedException e) {
//			    		e.printStackTrace();
//			    	}
//			    }
//			}
		    
		    
//	}
	
	public static void getVideo(String filename) {
		
//		String filename= "output.cmp";		
        String line="";
        
        int frameRate = 30;
      
        //预先计算idct公式里重复的部分
        for(int x=0;x<8;x++){
        	for(int y=0;y<8;y++){
        		 for(int u=0;u<8;u++){
        	        	for(int v=0;v<8;v++){
        	        		blockXU[x][u]=Math.cos((2*x+1)*u*pi/16.0);
        	        		blockYV[y][v]=Math.cos((2*y+1)*v*pi/16.0);
        	        	}
        	        }
        	}
        }
        
        outputVideo=new ArrayList<BufferedImage>();
        originalVideo=new ArrayList<BufferedImage>();
        
        img = new BufferedImage(960, 540, BufferedImage.TYPE_INT_RGB);
        original = new BufferedImage(960, 540, BufferedImage.TYPE_INT_RGB);	
        //一帧8160行
	      try
	       {
              BufferedReader in=new BufferedReader(new FileReader(filename));
               line=in.readLine();
                int blockCount=0;
            	long startTime=System.currentTimeMillis();   //获取开始时间  
                while (line!=null)
               {               	              	               
                      idct(line,blockCount);
                	  blockCount++;
                	  if(blockCount == 8160){
                		  long endTime = System.currentTimeMillis(); //获取结束时间  
                          System.out.println("程序运行时间： "+(endTime-startTime)+"ms");

                		  outputVideo.add(img);
                		  originalVideo.add(original);
                		  blockCount=0;
                		  img = new BufferedImage(960, 540, BufferedImage.TYPE_INT_RGB);
                		  original = new BufferedImage(960, 540, BufferedImage.TYPE_INT_RGB);
                			 startTime=System.currentTimeMillis();   //获取开始时间  
                	  }else{
                		  
                	  }
                	  line=in.readLine();       		    
                     
               }
              
               
                in.close();
	        } catch (IOException e)
	         {
	               e.printStackTrace();
	        }
	      
//	      
//	 
//		    JFrame frame = new JFrame();
//		    GridBagLayout gLayout = new GridBagLayout();
//		    frame.getContentPane().setLayout(gLayout);
//		    
//		    JLabel lbText1 = new JLabel("Video height: ");
//		    lbText1.setHorizontalAlignment(SwingConstants.CENTER);
//			
//		    lbIm1 = new JLabel(new ImageIcon(img));
//			GridBagConstraints c = new GridBagConstraints();
//			c.fill = GridBagConstraints.HORIZONTAL;
//			c.anchor = GridBagConstraints.CENTER;
//			c.weightx = 0.5;
//			c.gridx = 0;
//			c.gridy = 0;
//			frame.getContentPane().add(lbText1, c);		
//			c.fill = GridBagConstraints.HORIZONTAL;
//			c.gridx = 0;
//			c.gridy = 1;
//			frame.getContentPane().add(lbIm1, c);
//			
//			frame.pack();
//			frame.setVisible(true);
//		
//			for(int i=1; i < outputVideo.size(); i++) {
//		    	lbIm1.setIcon(new ImageIcon(outputVideo.get(i)));
//		    	try {
//		    		Thread.sleep(1000/frameRate);
//		    	} catch(InterruptedException e) {
//		    		e.printStackTrace();
//		    	}
//		    }
//			while(true){
//			 for(int i=0; i < outputVideo.size(); i++) {
//			    	lbIm1.setIcon(new ImageIcon(outputVideo.get(i)));
//			    	try {
//			    		Thread.sleep(1000/frameRate);
//			    	} catch(InterruptedException e) {
//			    		e.printStackTrace();
//			    	}
//			    }
//			}
			
		    
	}
	
	private static void idct(String line, int blockCount){
//		System.out.println(blockCount);
		int blockth=blockCount%4;
//		 System.out.println("th block "+blockth);
		int offsetx=0;
		int offsety=0;
		offsetx=blockCount/4/34;//TODO FIXME 
		offsety=blockCount/4%34;
		int actualx=offsetx*16;
		int actualy=offsety*16;
//		System.out.println(actualx+" "+actualy);
        String[] coe=line.split(" ");
        int blockType=Integer.valueOf(coe[0]);
        int[] qcoe=new int[193];
        if(blockType==0){
        	 for(int i=0;i<coe.length;i++){
             	qcoe[i]=(int)((Double.valueOf(coe[i])/q1))*q1;
             }
        }else{
        	 for(int i=0;i<coe.length;i++){
              	qcoe[i]=(int)((Double.valueOf(coe[i])/q2))*q2;
              }
        }      
//        if(blockType==1){
//        	System.out.println("fore!!! "+blockCount);
//        }
//        System.out.println(blockType);        
     
        for(int i=1;i<65;i++){
       	 double fxyr=0;double fxyg=0;double fxyb=0;
       	 int x=(i-1)/8;int y=(i-1)%8;
//       	 System.out.println("x  y "+x+" "+y);
       	 for(int u=0;u<8;u++){
       		 double ci = ((u==0)?1.0/Math.sqrt(2.0):1.0);	
       		 for(int v=0;v<8;v++){       			
                	double cj = ((v==0)?1.0/Math.sqrt(2.0):1.0);
                	double cc=ci*cj;
                	double cos=blockXU[x][u]*blockXU[y][v];
                	if(blockType==0){
                		 fxyr+=cc* qcoe[u*8+v+1]*cos;
               			 fxyg+=cc* qcoe[u*8+v+65]*cos;
               			 fxyb+=cc* qcoe[u*8+v+129]*cos;
                	}else{
                		 fxyr+=cc*qcoe[u*8+v+1]*cos;
               			 fxyg+=cc* qcoe[u*8+v+65]*cos;
               			 fxyb+=cc*qcoe[u*8+v+129]*cos;
                	}
//               
       		 }
       	 }
//       	 System.out.println("fxy "+fxyr);
       	 int r=(int) (0.25*fxyr);
       	 int g=(int) (0.25*fxyg);
       	 int b=(int) (0.25*fxyb);
//       	 System.out.println(r+" "+g+" "+b);
       	 int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
//       	 img.setRGB(x, y, pix);
//    	 img.setRGB(x+8, y, pix);
//    	 img.setRGB(x, y+8, pix);
//    	 img.setRGB(x+8, y+8, pix);
//       	 System.out.println((actualx+x)+" "+(actualy+y));
       	
       	 switch (blockth){//TODO FIXME
       	 	case 0:
       	 		if((actualx+x<960)&&(actualy+y)<540){
       	 			img.setRGB(actualx+x, actualy+y, pix);
       	 		}
       	 		break;
       	 	case 1:
       	 		if((actualx+x+8<960)&&(actualy+y)<540){
       	 			img.setRGB(actualx+x+8, actualy+y, pix);
       	 		}
       	 		break;
       	 	case 2: 
	       	 	if((actualx+x<960)&&(actualy+y+8)<540){
	       	 		img.setRGB(actualx+x, actualy+y+8, pix);
	   	 		}
	       	 	break;    	 	
       	 	case 3: 
	       	 	if((actualx+x+8<960)&&(actualy+y+8)<540){
	       	 	img.setRGB(actualx+x+8, actualy+y+8, pix);
	   	 		}
       	 		break;
       	 }  	
    	
        }
	}
}
