package tuckerdatalab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.ProcessingTarget;
import org.gephi.preview.api.RenderTarget;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import processing.core.PApplet;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;


public class AdjacencyMatrix {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
        //Create and set up the window.
        final JFrame frame = new JFrame("AdjacencyMatrix");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("                ");
        frame.add(label);



        
        //Add instructions
        JLabel instructions = new JLabel("Please enter text to generate an adjacency matrix for: ");
        frame.add(instructions, BorderLayout.NORTH);
        
        
        // the GUI as seen by the user (without frame)
        JPanel gui = new JPanel(new BorderLayout());
        gui.setBorder(new EmptyBorder(2,3,2,3));

        // adjust numbers for a bigger default area
        final JTextArea editArea = new JTextArea(5,40);
        // adjust the font to a monospaced font.
        Font font = new Font(
                Font.MONOSPACED, 
                Font.PLAIN, 
                editArea.getFont().getSize());
        editArea.setFont(font);
        //Use PromptSupport to add some dummy text

        
        gui.add(new JScrollPane(editArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
//        PromptSupport.setPrompt("Please enter text to generate an adjacency matrix for: ", editArea);
	    frame.add(gui);
	    
	    


	  //Create the combo box, select item at index 4.
	  //Indices start at 0, so 4 specifies the pig.

//	    frame.add(windowSizeList, BorderLayout.EAST);
	    
	    //Create a group of checkboxes
	    JPanel panel = new JPanel(new GridLayout(0, 1));
	    final JCheckBox nodeCheckBox = new JCheckBox("Show node labels", true);
	    panel.add(nodeCheckBox);
	    final JCheckBox curvedEdgeCheckBox = new JCheckBox("Curved edges");
	    panel.add(curvedEdgeCheckBox);
	    
	    //Add a title for our window
	    final JLabel windowTitle = new JLabel("Window size");
	    panel.add(windowTitle);
	    
	    
	    //Add a combo box (dropdown) for window size 
	    final JComboBox<Integer> windowSizeList = new JComboBox<Integer>(new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
	    windowSizeList.setSelectedIndex(4);
	    panel.add(windowSizeList);
	    
	    
	    //Add a title for our color dropdown
	    final JLabel colorTitle = new JLabel("Background color");
	    panel.add(colorTitle);
	    
	    //Add a combo box (dropdown) for color
	    final JComboBox<String> colorBox = new JComboBox<String>(new String[]{"Black", "Dark Gray", "White"});
	    panel.add(colorBox);
	  
	    frame.add(panel, BorderLayout.WEST);
	    
	    //add a button to generate adjacency matrix
	    JButton button = new JButton("Generate adjacency matrix");
	    button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				 //Process our common words to root out of our lecture text file 
				final String common_words_file = "src/common_words.txt";
				
					Set<String> C = new HashSet<String>();//words do not require maintained order
				    try (BufferedReader reader = new BufferedReader(new FileReader(common_words_file)))
				    {
				        String line = null;
				        while ((line = reader.readLine()) != null) 
				        	C.add(line.toLowerCase());
				        
				    } catch (IOException e1) {
						System.out.println(e1.toString());
						}
				    
				    
				    
					Pattern p = Pattern.compile("\\w+");
					Matcher m = null;
					Set<String> R = new LinkedHashSet<String>();
					ArrayList<String> word_list = new ArrayList<String>();
					String text = editArea.getText();
		        	m = p.matcher(text); 
		        	while (m.find())
		        	{
		        		R.add(m.group().toLowerCase());
		        		word_list.add(m.group().toLowerCase());
		        	}
		        	System.out.println();

				    
				    
				    
				    
				    //eliminate common words
				    Set<String> word_set = new LinkedHashSet<String>(R);
				    word_set.removeAll(C);
//				    System.out.println("Size after removing common words is " + word_set.size());
				
				    final int window_size = windowSizeList.getSelectedIndex();
				    Table<String, String, Integer> A = HashBasedTable.create();//Guava implementation of dictionary
				    for (String w1 : word_set)
				    {
				    	for (String w2 : word_set )
				    	{
				    		A.put(w1, w2, 0);
				    	}
				    }
				    
				    System.out.println(Arrays.asList(word_set));
//				    System.out.println("Word list size is " + word_list.size());
				    for (int i = 0; i < word_list.size(); i++)
				    {
				    	String word = word_list.get(i);//lower called when populating list
				    	if (word_set.contains(word))
				    	{
				    		for (int j = i; j < Math.min(i + window_size + 1,  word_list.size()); j++)
				    		{
					    			String adjacent_word = word_list.get(j);//lower called when pop list
					    			if (word_set.contains(adjacent_word))
					    			{
					    				int currentCount = A.get(word, adjacent_word);
					    				currentCount++;
					    				A.put(word, adjacent_word, currentCount);
					    				if (i != j){
					    					currentCount = A.get(adjacent_word, word);
					    					currentCount++;
					    					A.put(adjacent_word, word, currentCount);
					    				}
					    					
					    			}
				    		}
				    	}
				    	
				    }
				    
				    //Get our rows, write them to a csv
				    ArrayList<Collection<Integer>> rows = new ArrayList<Collection<Integer>>();
				    for (String word : word_set)
				    	rows.add(A.column(word).values());
				    writeCsv(rows, word_set);
				    
				    //Since we're writing a csv, make sure we properly dispose of it after we're done with it.
				    Runtime.getRuntime().addShutdownHook(new Thread()
				    {
				        @Override
				        public void run()
				        {
				        	try {
				        	File file = new File("out.csv");
				        	if (file.delete())
				        		System.out.println("csv successfully cleaned up.");
				        	else
				        		System.out.println("Something has gone awry with csv cleanup");
				        	}
				        	catch (Exception e)
				        	{
				        		//swallow
				        	}
				        }
				    });
				    
				    
				    
				    
				    ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
//				    GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
				    pc.newProject();
				    Workspace workspace = pc.getCurrentWorkspace();
				    ImportController importController = Lookup.getDefault().lookup(ImportController.class);


				  //Import file       
				    Container container;
				    try {
				        File file = new File("out.csv");
				        container = importController.importFile(file);
				        container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);   //Force DIRECTED
				    } catch (Exception ex) {
				        ex.printStackTrace();
				        return;
				    }
				    
				    
				    importController.process(container, new DefaultProcessor(), workspace);

				    
				    //create display applet
				    //Preview configuration
				    PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
				    PreviewModel previewModel = previewController.getModel();
				    if (nodeCheckBox.isSelected())
				    	previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
				    else
				    	previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.FALSE);
				    
				    
				    previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.WHITE));
				    if (curvedEdgeCheckBox.isSelected())
				    	previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.TRUE);
				    else
				    	previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);	
				    
				    previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
				    previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 10f);
				    String colorWindowchoice = colorBox.getSelectedItem().toString();
				    switch (colorWindowchoice)
				    {
				    case "Black":
					   
					    break;
				    case "Dark Gray":
					    previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.DARK_GRAY);
					    break;
				    case "White":
					    previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
					    break;
				    default:	
				    	 previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.BLACK);
				    }

				    previewController.refreshPreview();
				     
				    //New Processing target, get the PApplet
				    ProcessingTarget target = (ProcessingTarget) previewController.getRenderTarget(RenderTarget.PROCESSING_TARGET);
				    PApplet applet = target.getApplet();
				    applet.init();
				    applet.setSize(600, 600);
				    //Refresh the preview and reset the zoom
				    previewController.render(target);
				    target.refresh();
				    target.resetZoom();
//				    frame.dispose();
				    

				    //Change instructions 
				    JLabel instructions = new JLabel("Please enter additional text to generate an adjacency matrix for: ");
			        frame.add(instructions, BorderLayout.NORTH);
				    
				    
//				    frame.removeAll();
				    frame.dispose();
				    frame.setLayout(new BorderLayout());
				    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				    frame.add(applet, BorderLayout.CENTER);
				    frame.pack();
				    frame.validate();
//				    Files.delete(Paths.get("out.csv"));
				    frame.setSize(800, 800);//this actually changes size
				    frame.setVisible(true);
				    
				
			}
		});
	    
	    frame.add(button, BorderLayout.SOUTH);
	    
	    
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
	    
       
		    
	}

	    private static void writeCsv(ArrayList<Collection<Integer>> rows, Set<String> headerTitle) {

	        ICsvListWriter csvWriter = null;
	        try {
	            csvWriter = new CsvListWriter(new FileWriter("out.csv"), 
	                CsvPreference.STANDARD_PREFERENCE);
	            	csvWriter.writeHeader(headerTitle.toArray(new String[headerTitle.size()]));
	            	for (Collection<Integer> row : rows)
	                	csvWriter.write(row.toArray());

	        } catch (IOException e) {
	            e.printStackTrace(); // TODO handle exception properly
	        } finally {
	            try {
	                csvWriter.close();
	            } catch (IOException e) {
	            }
	        }

	    }
		  

}
	


