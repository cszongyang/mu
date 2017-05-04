package encoder;

import java.awt.image.BufferedImage;
import java.awt.Color;



public class ThreeStepSearcher {
	
	
	// Three step search 
		public static int[] ThreeStepSearch (int x, int y, BufferedImage left, BufferedImage right){
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
}
