#AdjacencyMatrix

This Swing based application takes user input (generally a lecture, story, etc..), removes commmon words, and then generates an adjacency matrix to show the relationship between certain words. This matrix is then rendered in Gephi, with a variety of options available for the matrix and rendering of the graph itself. The graph can be navigated via mouse dragging to move around the graph and scrolling to zoom in and out. 


![](http://i.imgur.com/YOacr94.png)

Usage: run the jar in the bin directory. There may be some issues with the common_words.txt file not being bundled in with the jar, if that's the case, simply put the text file (included under data directory) in the directory you are running the jar in.  


Libraries used
Guava for HashBasedTable
SuperCSV for managing data (via a CSV file)
Gephi for visualization
