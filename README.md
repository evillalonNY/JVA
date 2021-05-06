###  JAVA VIDEO ANALYZER

A Thesis in the Field of Information and Technology.  

Elena Villalon (evillalon@post.harvard.edu)  

for the Degree of Master of Liberal Arts in Extension Studies  

Harvard University, November 2008


The Java Video Analyzer (JVA) is a software application that analyzes the images of multi-frame videos download from the internet with Java Multimedia Technology. The color components of every image in the video are extracted and saved 
in a Mckoi database. The Mckoi database stores the metadata embedded in the stream and the RGB (red, green, and blue) sparse matrices with the frequency counts of pixels. The RGB matrices have rows for every frame in the viodeo and columns spanning the color values from 0 to 255. The RGB mean-frames are 256-length vectors and are calculated by averaging over every column of the RGB matrices. The analysis of the RGB color matrices and their corresponding mean-frames is carried out with statistical software of the projects Colt and Apache and the graphics tools of the AIDA software. Similarity metrics are obtained with the paired t-test, the signed rank test and one-way ANOVA model. The t-test and signed rank test can detect duplicates or related videos quite accurately. Summary measures are applied to the three RGB matrices to find other ststiscally significant frames. The videos can be uniquely identified with three fingerprints that are strings of letters and characters for each color channel. JVA has a Graphical user interface that can play the videos, draw histograms, retrieve the metadata and run statistical tests. Finally, remote clients may interact with the application and server database through the distributed interfaces of the software.   

High-Dimensionality Data Reduction with Java, Computing in Science and Engineering, September/October 2008, Vol. 10, No.5
