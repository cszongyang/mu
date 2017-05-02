package player;
import java.util.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
//import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import decoder.decode5;



public class AVPlayer extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private final Object pauseLock = new Object();
	
	ReadImage imageReader = new ReadImage();
	
	private Thread imageThread;
//	private Thread audioThread;
	
	private PlayingTimer timer;
	
	private boolean isPlaying = false;
	private boolean isPause = false;
	
	private String imageFileName;
	private String imageLastOpenPath;
	
	private String audioFileName;
	private String audioLastOpenPath;
	
	private JLabel labelFileNameImage = new JLabel("Image File:");
	//private JLabel labelFileNameAudio = new JLabel("Audio File:");
	private JLabel labelTimeCounter = new JLabel("00:00:00");
	private JLabel labelDuration = new JLabel("00:00:00");
	
	private JButton buttonOpenImage = new JButton("Open Image");
//	private JButton buttonOpenAudio = new JButton("Open Audio");
	private JButton buttonPlay = new JButton("Play");
	private JButton buttonPause = new JButton("Pause");
	
	private JSlider sliderTime = new JSlider();
	
	// Icons used for buttons
	private ImageIcon iconOpen = new ImageIcon(getClass().getResource("/images/Open.png"));
	private ImageIcon iconPlay = new ImageIcon(getClass().getResource("/images/Play.gif"));
	private ImageIcon iconStop = new ImageIcon(getClass().getResource("/images/Stop.gif"));
	private ImageIcon iconPause = new ImageIcon(getClass().getResource("/images/Pause.png"));
	
	ImageReaderComponent component = new ImageReaderComponent();

	private final int width = 960;
	private final int height = 540;
	private final double fps = 15; // Frames Per Second
	private BufferedImage img;
	
	/**
	 * Design the Java Swing UI with JFrame, buttons.
	 */
	public AVPlayer() {		
		super("CSCI 576 Project Player");
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.anchor = GridBagConstraints.WEST;
		
		buttonOpenImage.setFont(new Font("Sans", Font.BOLD, 14));
		buttonOpenImage.setIcon(iconOpen);
//		buttonOpenAudio.setFont(new Font("Sans", Font.BOLD, 14));
//		buttonOpenAudio.setIcon(iconOpen);
		
		buttonPlay.setFont(new Font("Sans", Font.BOLD, 14));
		buttonPlay.setIcon(iconPlay);
		buttonPlay.setEnabled(false);
		
		buttonPause.setFont(new Font("Sans", Font.BOLD, 14));
		buttonPause.setIcon(iconPause);
		//buttonPause.setEnabled(false);
		
		labelTimeCounter.setFont(new Font("Sans", Font.BOLD, 12));
		labelDuration.setFont(new Font("Sans", Font.BOLD, 12));
		
		sliderTime.setPreferredSize(new Dimension(500, 20));
		sliderTime.setEnabled(false);
		sliderTime.setValue(0);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		//add(labelFileNameAudio, constraints);
		
		constraints.gridy = 1;
		add(labelFileNameImage, constraints);
		
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		add(labelTimeCounter, constraints);
		
		constraints.gridx = 1;
		add(sliderTime, constraints);
		
		constraints.gridx = 2;
		add(labelDuration, constraints);
		
		JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
		panelButtons.add(buttonOpenImage);
//		panelButtons.add(buttonOpenAudio);
		panelButtons.add(buttonPlay);
		panelButtons.add(buttonPause);
		
		constraints.gridwidth = 3;
		constraints.gridx = 0;
		constraints.gridy = 3;
		add(panelButtons, constraints);
		
		
		component.setPreferredSize(new Dimension(1000, 600));
		constraints.gridheight = 3;
		constraints.gridwidth = 12;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 12;		
		add(component, constraints);
		
		buttonOpenImage.addActionListener(this);
//		buttonOpenAudio.addActionListener(this);
		buttonPlay.addActionListener(this);
		buttonPause.addActionListener(this);
		
		pack();
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		PlayerBoardListener mouseListener = new PlayerBoardListener();
		component.addMouseMotionListener(mouseListener);

	}
	
	/** Listener responsible for handling mouse events on player board */
	private class PlayerBoardListener extends MouseAdapter {

	  @Override 
	  public void mouseMoved(MouseEvent event) {
	    //TODO
	    System.out.println("Mouse movement detected! Actual mouse position is: " + event.getX()+ "," + event.getY() + ".");
	  }   
 
	}
	
	/**
	 * Handle click events on the buttons.
	 * Open : Choose Audio, Image files
	 * Play : Play Video
	 * Stop : Stop Video and restart both threads
	 * Pause : Interrupt both threads and resume them on play
	 */
	public void actionPerformed(ActionEvent event) {
		
		Object source = event.getSource();
		if (source instanceof JButton) {
			JButton button = (JButton) source;
			if (button == buttonOpenImage) {
				openFileImage();
			}else if (button == buttonPlay) {
				if (!isPlaying) {
					playBack();
				} else {
					stopPlaying();
				}
			} else if (button == buttonPause) {
				
				if (!isPause) {
					pausePlaying();
				} else {
					resumePlaying();
				}
			} else;
		}
	}
	
	/**
	 * Using JFileChooser select Image and Audio Files
	 */
	private void openFileImage() {
		JFileChooser fileChooser = null;
		
		if (imageLastOpenPath != null && !imageLastOpenPath.equals("")) {
			fileChooser = new JFileChooser(imageLastOpenPath);
		} else {
			fileChooser = new JFileChooser();
		}
		
		FileFilter rgbFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "Image file (*.RGB)";
			}

			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					return file.getName().toLowerCase().endsWith(".cmp");
				}
			}
		};

		
		fileChooser.setFileFilter(rgbFilter);
		fileChooser.setDialogTitle("Open Image File");
		fileChooser.setAcceptAllFileFilterUsed(false);

		int userChoice = fileChooser.showOpenDialog(this);
		if (userChoice == JFileChooser.APPROVE_OPTION) {
			imageFileName = fileChooser.getSelectedFile().getAbsolutePath();
			imageLastOpenPath = fileChooser.getSelectedFile().getParent();
			
			if (isPlaying || isPause) {
				stopPlaying();
				
			}
			
		}
		
		System.out.println(imageLastOpenPath + imageFileName);
		
//		Load Image Reader 
		imageReader.loadFromDecoder("/Users/zongyangli/Desktop/output.cmp");
		
		if(!imageLastOpenPath.isEmpty()){
			buttonPlay.setEnabled(true);
		}	
	}
	
	
	/**
	 * Stop, Pause, Resume audio and video
	 */
	private void stopPlaying() {
		isPause = false;
		buttonPause.setText("Pause");
		buttonPause.setEnabled(false);
		timer.reset();
		timer.interrupt();	
		imageReader.resetInputStream(imageFileName);
		imageThread.interrupt();
				
	}
	
	private void pausePlaying() {
		buttonPause.setText("Resume");
		isPause = true;
		timer.pauseTimer();
		
		//imageThread.interrupt();


	}
	
	private void resumePlaying() {
		buttonPause.setText("Pause");
		isPause = false;
		timer.resumeTimer();
		
		
		synchronized (pauseLock) {
			pauseLock.notify();
		}
		//imageThread.interrupt();
			
	}
	
	private void resetControls() {
		timer.reset();
		timer.interrupt();

		buttonPlay.setText("Play");
		buttonPlay.setIcon(iconPlay);
		
		buttonPause.setEnabled(false);
		
		isPlaying = false;		
	}
	

	/**
	 * Start playing sound and images in sync
	 */
	private void playBack() {
		timer = new PlayingTimer(labelTimeCounter, sliderTime);
		timer.start();
		isPlaying = true;
		
		System.out.println("--------in playback------");
		
		try {
			//Load Audio Player Controls
			//audioPlayer.load(audioFileName);		
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(AVPlayer.this,  
					"Error", "Error", JOptionPane.ERROR_MESSAGE);
			resetControls();
			e1.printStackTrace();
		} 
		
	
		List<BufferedImage> video = imageReader.getVideo();
		//List<BufferedImage> video = decode5.getVideo();
		imageThread = new Thread(new Runnable() {
			public void run() {						
				
				labelFileNameImage.setText("Image File: " + imageFileName);
				
				System.out.println("# image run...");
				long length = width*height*3;
				//long numFrames = imageReader.getImageFileLength()/length;
				
			

				for(int i = 0; i < video.size(); i++) {
				
					img = video.get(i);
					//img = imageReader.readBytes();
					component.setImg(img);
					repaint();	
					
					try {
						Thread.sleep(33);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					
					synchronized (pauseLock) {
						if (isPause) {
							try {
								pauseLock.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
					System.gc();
				}
				
				
			}
		});

		System.out.println("--------start image thread-----");
		imageThread.start();
		
	}
	
	//Driver for loading the player
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new AVPlayer().setVisible(true);
			}
		});
	}

}