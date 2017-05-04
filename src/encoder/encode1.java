//package csci576final;//TODO remove! package while loop args
package encoder;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
class DCTFrame {
	public double[][] Coef;
	public DCTFrame(double[][] Coef) {
		this.Coef = Coef;
	}
}

class ClusterP {
    Point center;
    int num = 0;
    ArrayList<Point> list;
    public ClusterP() {
        list = new ArrayList<>();
    }
    public ClusterP(int x, int y) {
        center = new Point(x, y);
        list = new ArrayList<>();
    }
}

public class encode1 {

	static JFrame frame;
	static JLabel lbIm1;
	static JLabel lbIm2;
 //	BufferedImage img;


	//static double[][] DCTCoefficients = new double[4][193];
	static String outputFile = "/Users/zongyangli/Desktop/output2.cmp";
	static FileWriter writer;

	static final double pi = Math.PI;
	static int lines = 0;

	static int[][] blockRed = new int[16][16];
	static int[][] blockBlue = new int[16][16];
	static int[][] blockGreen = new int[16][16];
	static double[][] cosMU = new double[8][8];
	static double[][] cosNV = new double[8][8];

	static int width = 960;
	static int height = 540;
	static int frameRate = 10;

	static int currentj = 0;
	static int currentk = 0;
	
	public static void main(String[] args) {
		// input
		String fileName = "/Users/zongyangli/Desktop/two_people_moving_background.rgb";

/*
		int width = 960;
		int height = 540;
		int frameRate = 10;
*/

		ArrayList<BufferedImage> inputVideo = new ArrayList<BufferedImage>();
		inputVideo = readInputVideo(fileName, width, height, frameRate);
		try {
			writer = new FileWriter(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int u = 0;u < 8;u++){//row
			for(int v = 0;v < 8;v++){//col
				for(int m = 0;m < 8;m++){
					for(int n = 0;n < 8;n++){
						cosMU[m][u] = Math.cos((2*(m)+1)*u*pi/16.0);
						cosNV[n][v] = Math.cos((2*(n)+1)*v*pi/16.0);
					}
				}
			}
		}

   		calMotionVectors(inputVideo, width, height);
   		System.out.println("finished calculation");
   		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	private static void calMotionVectors(ArrayList<BufferedImage> inputVideo,int width, int height) {
		System.out.println("start cal motion vector");
		BufferedImage referenceImage = inputVideo.get(0);//TODO 如果没有帧
		if (width % 16 != 0) {
			width = ((width /16) + 1) * 16;
		}
		if (height % 16 != 0) {
			height = ((height / 16) + 1) * 16;
		}

		for(int i = 0; i < inputVideo.size(); i++){//第i帧
			ArrayList<DCTFrame> dct = new ArrayList<>();
			ArrayList<Point> motionV = new ArrayList<>();
			int totalx = 0;
			int totaly = 0;
			System.out.println("Frame " + i + ":");
			long startTime = System.currentTimeMillis();   //获取开始时间
//			for(int i=1;i<2;i++){//第i帧
			BufferedImage targetImage = inputVideo.get(i);
			for(int j = 0;j < width; j = j + 16){//TODO fix
				for(int k = 0;k < height; k = k + 16){
//					System.out.println(j+" "+k);
//					long minSum=Long.MAX_VALUE;	 TODO IMPORTANT
					int minSum = Integer.MAX_VALUE;
					int vectorx = 0;
					int vectory = 0;
					double[][] referenceY = new double[16][16];

					//get Y
					for(int p = 0;p < 16;p++){
						for(int q = 0;q < 16;q++){
							int targetRGB = targetImage.getRGB(j+p, k+q);
							Color c2 = new Color(targetRGB);
							blockRed[p][q] = c2.getRed();
							blockGreen[p][q] = c2.getGreen();
							blockBlue[p][q] = c2.getBlue();
							double y2 = 0.299*c2.getRed()+0.587*c2.getGreen()+0.114*c2.getBlue();//先double用于比较
							referenceY[p][q] = y2;
						}
					}
					if(i == 0) {
						calDCT(1, dct);
						continue;
					}
					
					int searchStep = 4;
					int range1 = Math.max(j - searchStep, 0);
					int range2 = Math.min(j + searchStep, width - 16);
					int range3 = Math.max(k - searchStep, 0);
					int range4 = Math.min(k + searchStep, height - 16);
					
					
					for(int m = range1;m < range2;m++){//TODO fix
						for(int n = range3;n < range4;n++){//cal sd
							int sum = 0;
							for(int p = 0;p < 16;p++){
								for(int q = 0;q < 16;q++){
									int referenceRGB = referenceImage.getRGB(m + p, n + q);
									Color c1 = new Color(referenceRGB);
									double y1 = 0.299*c1.getRed()+0.587*c1.getGreen()+0.114*c1.getBlue();//先double用于比较
									double y2 = referenceY[p][q];
									sum += Math.abs(y1-y2);
	//								System.out.println(p+" "+q+" "+Math.abs(y1-y2)+" "+sum+" ");
								}
							}
							if(sum < minSum){
	//							System.out.println("reference "+m+" "+n+" "+sum);
								minSum = sum;
								vectorx = m - j;
								vectory = n - k;
							}
						}
					}
					//logSearch(searchStep, j, k, referenceImage,referenceY);
					//vectorx = currentj - j;
					//vectory = currentk - k;
					
					totalx += Math.abs(vectorx);
					totaly += Math.abs(vectory);
					//System.out.println("("+vectorx+","+vectory+")");
					motionV.add(new Point(vectorx, vectory));
					if((Math.abs(vectorx)+Math.abs(vectory)) < 3){
						calDCT(0, dct);
					}else{
						calDCT(1, dct);
					}

				}
			}
			int avgx = totalx / dct.size();
			int avgy = totaly / dct.size();
			System.out.println("(" +  avgx + "," + avgy + ")");
			writeToCmp(dct, motionV, avgx, avgy);
			referenceImage=targetImage;
			System.out.println(lines);
			long endTime=System.currentTimeMillis(); //获取结束时间
			System.out.println("程序运行时间： "+(endTime-startTime)+"ms");   
		}
	}
	
	private static void logSearch(int searchStep2, int j, int k, BufferedImage targetImage, double[][] referenceY) {
//		System.out.println(j+" "+k);
		int newj=j;
		int newk=k;	
		int[] arr=new int[9];
		for(int searchStep=16;searchStep>0;searchStep=searchStep/2){
//			System.out.println("182");
		
			int sum=Integer.MAX_VALUE;
			arr[0]=blockDif(newj-searchStep,newk-searchStep,targetImage,referenceY);
			arr[1]=blockDif(newj,newk-searchStep,targetImage,referenceY);
			arr[2]=blockDif(newj+searchStep,newk-searchStep,targetImage,referenceY);
			arr[3]=blockDif(newj-searchStep,newk,targetImage,referenceY);
			arr[4]=blockDif(newj,newk,targetImage,referenceY);
			arr[5]=blockDif(newj+searchStep,newk,targetImage,referenceY);
			arr[6]=blockDif(newj-searchStep,newk+searchStep,targetImage,referenceY);
			arr[7]=blockDif(newj,newk+searchStep,targetImage,referenceY);
			arr[8]=blockDif(newj+searchStep,newk+searchStep,targetImage,referenceY);			
			int index=0;
			for(int i=0;i<9;i++){
				if(arr[i]<sum){
					sum=arr[i];
					index=i;
				}
			}
			 switch (index) {
	         case 0:  newj =newj-searchStep; newk =newk-searchStep;
	                  break;
	         case 1: newj =newj; newk =newk-searchStep;
	                  break;
	         case 2: newj =newj+searchStep; newk =newk-searchStep;
	                  break;
	         case 3: newj =newj-searchStep; newk =newk;
	                  break;
	         case 4:  newj =newj; newk =newk;
             			break;
	         case 5:  newj =newj+searchStep; newk =newk;
	                  break;
	         case 6: newj =newj-searchStep; newk =newk+searchStep;
	                  break;
	         case 7: newj =newj; newk =newk+searchStep;
	                  break;
	         case 8: newj =newj+searchStep; newk =newk+searchStep;
	                  break;       
	         default: newj =newj; newk =newk;
	                  break;
			 }	
		
		}
		
		currentj=newj;currentk=newk;
	
// 		logSearch(searchStep, newj,newk,targetImage,referenceY );
		
	}

	private static int blockDif(int m, int n, BufferedImage referenceImage, double[][] referenceY) {
		if(m < 0|| m >= width - 16 || n<0 || n >= height-16){
			return Integer.MAX_VALUE;
		}
		int sum = 0;
		for(int p = 0;p < 16;p++){
			for(int q = 0;q < 16;q++){
					int referenceRGB = referenceImage.getRGB(m + p, n + q);
					Color c1 = new Color(referenceRGB);
					double y1 = 0.299*c1.getRed()+0.587*c1.getGreen()+0.114*c1.getBlue();//先double用于比较
					double y2 = referenceY[p][q];
					sum += Math.abs(y1-y2);
			}
		}
		return sum;
	}

	

	private static void calDCT(int blockType, ArrayList<DCTFrame> dct) {
		double[][] DCTCoefficients = new double[4][193];
		for(int u = 0; u < 8; u++){//row
			double ci = ((u == 0)? 1.0 / Math.sqrt(2.0):1.0);
			for(int v = 0; v < 8; v++){//col
				double cj = ((v == 0)?1.0 / Math.sqrt(2.0):1.0);
				double FuvSumR1 = 0;
				double FuvSumG1 = 0;
				double FuvSumB1 = 0;
				double FuvSumR2 = 0;
				double FuvSumG2 = 0;
				double FuvSumB2 = 0;
				double FuvSumR3 = 0;
				double FuvSumG3 = 0;
				double FuvSumB3 = 0;
				double FuvSumR4 = 0;
				double FuvSumG4 = 0;
				double FuvSumB4 = 0;
				for(int m = 0; m < 8; m++){
					for(int n = 0; n < 8; n++){
						double cos = cosMU[m][u]*cosNV[n][v];
						int r1 = blockRed[m][n];
						int g1 = blockGreen[m][n];
						int b1 = blockBlue[m][n];
						FuvSumR1 += r1*cos;
						FuvSumG1 += g1*cos;
						FuvSumB1 += b1*cos;
						int r2 = blockRed[m+8][n];
						int g2 = blockGreen[m+8][n];
						int b2 = blockBlue[m+8][n];
						FuvSumR2 += r2*cos;
						FuvSumG2 += g2*cos;
						FuvSumB2 += b2*cos;
						int r3 = blockRed[m][n+8];
						int g3 = blockGreen[m][n+8];
						int b3 = blockBlue[m][n+8];
						FuvSumR3 += r3*cos;
						FuvSumG3 += g3*cos;
						FuvSumB3 += b3*cos;
						int r4 = blockRed[m+8][n+8];
						int g4 = blockGreen[m+8][n+8];
						int b4 = blockBlue[m+8][n+8];
						FuvSumR4 += r4*cos;
						FuvSumG4 += g4*cos;
						FuvSumB4 += b4*cos;
					}
				}
				DCTCoefficients[0][u*8+v+1] = ci * cj * 0.25*FuvSumR1;
				DCTCoefficients[0][u*8+v+65] = ci * cj * 0.25*FuvSumG1;
				DCTCoefficients[0][u*8+v+129] = ci * cj * 0.25*FuvSumB1;
				DCTCoefficients[1][u*8+v+1] = ci * cj * 0.25*FuvSumR2;
				DCTCoefficients[1][u*8+v+65] = ci * cj * 0.25*FuvSumG2;
				DCTCoefficients[1][u*8+v+129] = ci * cj * 0.25*FuvSumB2;
				DCTCoefficients[2][u*8+v+1] = ci * cj * 0.25*FuvSumR3;
				DCTCoefficients[2][u*8+v+65] = ci * cj * 0.25*FuvSumG3;
				DCTCoefficients[2][u*8+v+129] = ci * cj * 0.25*FuvSumB3;
				DCTCoefficients[3][u*8+v+1] = ci * cj * 0.25*FuvSumR4;
				DCTCoefficients[3][u*8+v+65] = ci * cj * 0.25*FuvSumG4;
				DCTCoefficients[3][u*8+v+129] = ci * cj * 0.25*FuvSumB4;

			}
		}
		DCTCoefficients[0][0] = blockType;
		DCTCoefficients[1][0] = blockType;
		DCTCoefficients[2][0] = blockType;
		DCTCoefficients[3][0] = blockType;
		dct.add(new DCTFrame(DCTCoefficients));
	}

	private static void writeToCmp(ArrayList<DCTFrame> dct, ArrayList<Point> motionV, int avgx, int avgy) {
		System.out.println("# of blocks per frame = " + dct.size());
		StringBuilder sb=new StringBuilder();
		if (avgx + avgy < 3 || motionV.size() == 0) {
			for (int k = 0; k < dct.size(); k++) {
				double[][] DCTCoefficients = dct.get(k).Coef;
				for(int i=0;i<4;i++){
					sb.append((int)DCTCoefficients[i][0]+" ");
		//			writer.write(((int)DCTCoefficients[i][0])+" ");
					for(int j=1;j<193;j++){
						sb.append(DCTCoefficients[i][j]+" ");
		//					writer.write(String.valueOf(DCTCoefficients[i][j])+" ");
		//					System.out.println(j+" "+DCTCoefficients[i][j]);
					}
					sb.append("\n");
		//			writer.write("\n");
		
				}
			}
		} else {
			ArrayList<Integer> list = func(motionV);
			for (int k = 0; k < dct.size(); k++) {
				double[][] DCTCoefficients = dct.get(k).Coef;
				for(int i=0;i<4;i++){
/*
					if ((Math.abs(motionV.get(k).x) + Math.abs(motionV.get(k).y)) < 4){
						sb.append(0 + " ");
					} else {
						sb.append(1 + " ");
					}
*/					
					sb.append(list.get(k) + " ");
		//			writer.write(((int)DCTCoefficients[i][0])+" ");
					for(int j=1;j<193;j++){
						sb.append(DCTCoefficients[i][j]+" ");
		//					writer.write(String.valueOf(DCTCoefficients[i][j])+" ");
		//					System.out.println(j+" "+DCTCoefficients[i][j]);
					}
					sb.append("\n");
		//			writer.write("\n");
		
				}
			}
		}
		try {
			writer.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

/*
	public static void showVideos(){

	}
*/

	public static ArrayList<BufferedImage> readInputVideo(String fileName, int width, int height, int frameRate) {
		System.out.println("Start read input file");
		ArrayList<BufferedImage> res=new ArrayList<BufferedImage>();
   		try {
   			File file = new File(fileName);
   			InputStream is = new FileInputStream(file);

		    long len = file.length();
		    byte[] bytes = new byte[(int)len];

		    int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }

		    int ind = 0;
		    int actualWidth = width;
		    int actualHeight = height;
		    if (width % 16 != 0) {
			    actualWidth = ((width / 16) + 1) * 16;
		    }
		    if (height % 16 != 0) {
			    actualHeight = ((height /16) + 1) * 16;
		    }

	    	while(ind+height*width*2 < len) {
	    		BufferedImage img = new BufferedImage(actualWidth, actualHeight, BufferedImage.TYPE_INT_RGB);
				for(int y = 0; y < actualHeight; y++){
					for(int x = 0; x < actualWidth; x++){
						if (x >= width || y >= height) {
							int pix = 0xff000000 | ((0 & 0xff) << 16) | ((0 & 0xff) << 8) | (0 & 0xff);
							img.setRGB(x,y,pix);
						} else {
//							byte a = 0;
							byte r = bytes[ind];
							byte g = bytes[ind+height*width];
							byte b = bytes[ind+height*width*2];
							ind++;
							int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
							img.setRGB(x,y,pix);
						}
					}
				}
				ind += height*width*2;
				res.add(img);
			}
	    	System.out.println("finish read video");
   		} catch (FileNotFoundException e) {
   			e.printStackTrace();
   		} catch (IOException e) {
   			e.printStackTrace();
   		}
   		return  res;
	}
	
	
	private static ArrayList<Integer> func(ArrayList<Point> points) {
        int num_cluster = 2;
        ClusterP[] cluster = new ClusterP[num_cluster];
        ArrayList<Integer> res = new ArrayList<>();
        initialClusterPoints(cluster);

        Point[] old = new Point[cluster.length];
        for (int i = 0; i < cluster.length; i++) {
            old[i] = new Point();
            old[i].setLocation(cluster[i].center);
        }
        int count = 0;
        while (count < 500) {
            for (int i = 0; i < cluster.length; i++) {
                cluster[i].num = 0;
                cluster[i].list = new ArrayList<>();
            }
            for (int i = 0; i < points.size(); i++){
                    addcluster(cluster, points.get(i));
            }
            Point[] tmp = new Point[cluster.length];
            for(int i = 0; i < tmp.length; i++) {
                tmp[i] = new Point(cluster[i].center);
            }
            updateCenter(cluster);
            if (isunchange(tmp, cluster, num_cluster)) {
                break;
            }
            count++;
        }
        //int a = Math.abs(cluster[0].center.x) + Math.abs(cluster[0].center.y);
        //int b = Math.abs(cluster[1].center.x) + Math.abs(cluster[1].center.y);
        int a = cluster[0].list.size();
        int b = cluster[1].list.size();
        Point background;
        if (a > b) {
          background = cluster[0].center;
        } else {
          background = cluster[1].center;
        }
        for (int i = 0; i < points.size(); i++){
            Point p = findcluster(cluster, points.get(i));
            if (p.equals(background)) {
              res.add(0);
            } else {
              res.add(1);
            }
        }
        return res;

    }

    private static boolean isunchange(Point[] tmp, ClusterP[] cluster, int n) {
        boolean res = true;
        for (int i = 0; i < cluster.length; i++) {
            if (!cluster[i].center.equals(tmp[i])) {
                res = false;
                break;
            }
        }
        return res;
    }

    private static void updateCenter(ClusterP[] cluster) {
        for (int i = 0; i < cluster.length; i++) {
            int sumx = 0;
            int sumy = 0;
            int count = 0;
            for (Point p : cluster[i].list) {
                sumx += p.x;
                sumy += p.y;
                count++;
            }
            int x = 0;
            int y = 0;
            if (count != 0) {
                x = sumx / count;
                y = sumy / count;
            }
            cluster[i].center.setLocation(x, y);
        }
    }

    private static void addcluster(ClusterP[] cluster, Point p) {
        Point center = findcluster(cluster, p);
        for(int i = 0; i < cluster.length; i++){
            if(cluster[i].center.equals(center)){
                Point tmp = new Point(p);
                cluster[i].list.add(tmp);
                cluster[i].num++;
                break;
            }
        }
    }

    private static Point findcluster(ClusterP[] cluster, Point p) {
        double min = Double.MAX_VALUE;
        Point res = new Point();
        for (int i = 0; i < cluster.length; i++) {
            double dis = Math.abs(p.distance(cluster[i].center));
            if (dis < min) {
                min = dis;
                res = new Point(cluster[i].center);
            }
        }
        return res;
    }

     private static void initialClusterPoints(ClusterP[] cluster) {
          cluster[0] = new ClusterP();
          cluster[0].center = new Point(1, 1);
          cluster[1] = new ClusterP();
          cluster[1].center = new Point(10, 10);
     }
	

}
