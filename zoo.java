import java.util.*;
import java.io.*;

/**
----------------------------------
----------------------------------
A program used to classify different
kinds of animals
Author: Christian Davidson
Date: November 6, 2011
Neural Networks CS380
----------------------------------
----------------------------------
*/

public class zoo
{
        public static void main(String[] args)
        {
//Initializing
//variables
		int numAtt = 17;	//Each animal has 17 attributes, not including name
		int numClasses = 7;	//There are 7 different classifications of animals in this data set
		int classIndex = 16;	//Class Index inside the animalInfo array
                String kill = "";	//String used to kill lines, if necessary
                String ans = "p";	//Initial answer to run program
                String inputName = "";
                String outputName = "";
                double[][] weights = new double[0][0];	//Initializing a weights vector so it may be used outside of training
                boolean trained = false;	//Initialiazing testing criteria, must be trained to test
                Scanner kb = new Scanner(System.in);



                System.out.println("Welcome to my animal classifying application.");
                while(ans != "q")
                {
//Printing out menu
//and taking input option
			mainMenu();
                        ans = kb.next();
			System.out.println("");

//User chooses to
//load an input file
//option "1" or "F"
		
			if(ans.equalsIgnoreCase("1") || ans.equalsIgnoreCase("F"))
			{
	//Reading in training
	//file name
				inputName = realFile("Enter the Training Data file name:", "Could not find the file specified, please try again. (R to return to menu)");
	//Only train if the user
	//does not want to go
	//to the main menu
				if(!inputName.equalsIgnoreCase("R"))
				{
	//Calculating total number
	//of inputs
					int numSamples = getNumber(inputName);
	//Reading in data
	//from the input
	//file
					String[][] animalNames = new String[numSamples][1];
					int[][] animalInfo = new int[numSamples][numAtt];
        	                        try
                	                {
                        	                Scanner in = new Scanner(new File(inputName));
                                	        for(int j = 0; j < numSamples; j++)
                                        	{
							animalNames[j][0] = in.next();
							for(int i = 0; i < numAtt; i++)
								animalInfo[j][i] = in.nextInt();
							kill = in.nextLine();
                        	                }
                                	        in.close();
                                	}
                                	catch(FileNotFoundException e)
                                	{
                                        	System.out.println(e.getMessage());
                                        	System.exit(1);
                                	}
                                	catch(NoSuchElementException e)
                                	{
                                        	System.out.println(e.getMessage());
                                        	System.exit(1);
                                	}
					
	//Implementing the
	//LVQ (learning vector quantization)
	//neural network
		//Initializing variables
		//for training
			//Taking in user
			//input for the max
			//number of Epochs
					int maxEpochs; 
                                        do
                                        {
                                        	maxEpochs = getInteger("What is the maximum number of epochs you would like to have? \n (integer greater than 1)");
                                        }while(maxEpochs < 1);
			//Taking in user
			//input for the starting
			//value of alpha
					double alpha;
					System.out.println("");
					do
					{
						try
						{	
							System.out.println("What is the starting value of alpha? \n (number between 0 and 1)");
							alpha = kb.nextDouble();
						}
						catch(InputMismatchException e)
						{
							kill = kb.nextLine();
							alpha = -1;
						}
						if(alpha <= 0 || alpha >= 1)
						{
							System.out.println("");
							System.out.println("Invalid input, please try again.");
						}
					}while(alpha <= 0 || alpha >= 1);
			//Taking in input
			//for weight convergence
			//criterion
					double weightConverge;
                                        System.out.println("");   
                                        do
                                        {
                                                try
                                                {
                                                        System.out.println("What is the threshold for weight convergence? \n (typically a small value between 0 and 1)");
                                                        weightConverge = kb.nextDouble();
                                                }
                                                catch(InputMismatchException e)
                                                {
                                                        kill = kb.nextLine();
                                                        weightConverge = -1;   
                                                }
                                                if(weightConverge <= 0 || weightConverge >= 1)
                                                {
                                                        System.out.println("");
                                                        System.out.println("Invalid input, please try again.");
                                                }
                                        }while(weightConverge <= 0 || weightConverge >= 1);
					double alphaConverge;
                                        System.out.println("");
                                        do
                                        {
                                                try
                                                {
                                                        System.out.println("What is the threshold for alpha convergence? \n (typically a small value between 0 and 1)");
                                                        alphaConverge = kb.nextDouble();
                                                }
                                                catch(InputMismatchException e)
                                                {
                                                        kill = kb.nextLine();
                                                        alphaConverge = -1;
                                                }
                                                if(alphaConverge <= 0 || alphaConverge >= 1)
                                                {
                                                        System.out.println("");
                                                        System.out.println("Invalid input, please try again.");
                                                }  
                                        }while(alphaConverge <= 0 || alphaConverge >= 1);

			//Initializing
			//weights to 0	
					weights = new double[numClasses][numAtt];
					for(int j = 0; j < numClasses; j++)
					{
						for(int i = 0; i < numAtt; i++)
							weights[j][i] = 0;
					}				
			//Taking in user
			//input for how
			//they would like their
			//starting weights
					int howWeights = -1;
					System.out.println("");
					do
					{
						System.out.println("How would you like to get your weights?");
						System.out.println("1 to pick first available class");
						System.out.println("2 to initialize to random values between -0.5 and 0.5");
						System.out.println("3 to initialize weights to 0");
						System.out.println("4 to use Kohonen SOM");
						try
						{
							howWeights = kb.nextInt();
						}
						catch(InputMismatchException e)
						{
							kill = kb.nextLine();
							howWeights = -1;
						}
						if(howWeights != 1 && howWeights != 2 && howWeights != 3 && howWeights != 4)
						{
							System.out.println("");
							System.out.println("Invalid Input, please try again.");
						}
					}while(howWeights != 1 && howWeights != 2 && howWeights != 3 && howWeights != 4);


		//User decides to select
		//first available class
		//for weights
					if(howWeights == 1)
					{
						for(int j = 0; j < numClasses; j++)
						{
							for(int i = 0; i < numSamples; i++)
							{
								if(animalInfo[i][classIndex] == (j + 1))
								{
									for(int k = 0; k < numAtt; k++)
										weights[j][k] = animalInfo[i][k];
									break;
								}
							}
						}	
					}
		//User decides to use
		//random values between
		//-0.5 and 0.5
					else if(howWeights == 2)
					{
						Random generator = new Random();
						for(int j = 0; j < numClasses; j++)
						{
							for(int i = 0; i < numAtt; i++)
							{
								weights[j][i] = generator.nextDouble() - 0.5;
							}
						}
					}
		//Skip option 3, weights
		//are already 0
		//Option 4, User decides to select
		//weights using
		//Kohonen SOM algorithm
					else if(howWeights == 4)
					{
			//Initialize weights by
			//picking a representative
			//vector for each class
                                                for(int j = 0; j < numClasses; j++)
                                                {
                                                        for(int i = 0; i < numSamples; i++)
                                                        {
                                                                if(animalInfo[i][classIndex] == (j + 1))
                                                                { 
                                                                        for(int k = 0; k < numAtt; k++)
                                                                                weights[j][k] = animalInfo[i][k];
                                                                        break;
                                                                }   
                                                        }
                                                }

						double tempAlpha = alpha;
						boolean kohConverged = false;
						int kohEpochs = 0;
						while(kohEpochs < maxEpochs && kohConverged == false && tempAlpha > alphaConverge)
						{
							int max = -1;
							kohEpochs++;
			                                for(int j = 0; j < numSamples; j++)
                                                	{
                                                        	int k = 0;
                                                        	double min = 100000;
								int change = 0;
                        //Calculating the nearest  
                        //weight vector and storing
                        //its index into a variable
                                                        	for(int p = 0; p < numClasses; p++)
                                                        	{
                                                                	double tempMin = 0;
                                                                	for(int i = 0; i < (numAtt - 1); i++)
                                                                        	tempMin += (animalInfo[j][i] - weights[p][i]) * (animalInfo[j][i] - weights[p][i]);
                                                                	if(tempMin < min)
                                                                	{
                                                                        	min = tempMin;
                                                                       		k = p;
									}
								}
				                                for(int i = 0; i < numAtt - 1; i++)
								{
                                                                        weights[k][i] += tempAlpha * ( animalInfo[j][i] - weights[k][i]);
									change += Math.abs(tempAlpha * ( animalInfo[j][i] - weights[k][i]));
								}
								if(change > max)
									max = change; 
							  }
				                          tempAlpha = (tempAlpha * (kohEpochs)) / (kohEpochs + 1);
                                                	  if (max < weightConverge)
                                                        	kohConverged = true;
						} 
						
					}


		//Initializing variables
		//used in training algorithm
					int epochs = 0;
					boolean converged = false;
		//Beginning training algorithm
					while(epochs < maxEpochs && converged == false && alpha > alphaConverge)
					{
						epochs++;
						double max = -1;
						for(int j = 0; j < numSamples; j++)
						{
							int k = 0;
							double min = 100000;
			//Calculating the nearest
			//weight vector and storing
			//its index into a variable
							for(int p = 0; p < numClasses; p++)
							{	
								double tempMin = 0;
								for(int i = 0; i < (numAtt - 1); i++)
									tempMin += Math.abs(animalInfo[j][i] - weights[p][i]);
								if(tempMin < min)
								{
									min = tempMin;
									k = p;
								}
							}
			//If classes are equal update
			//weights accordingly
							float change = 0;
							if(animalInfo[j][classIndex] == weights[k][classIndex])
							{
								for(int i = 0; i < (numAtt - 1); i++)
								{
									weights[k][i] += alpha * ( animalInfo[j][i] - weights[k][i]);								
									change += Math.abs(alpha * (animalInfo[j][i] - weights[k][i]));
								}
							}
			//Classes are not equal
							else
							{
                                                        	for(int i = 0; i < (numAtt - 1); i++)
                                                                {
									weights[k][i] += (-1) * alpha * ( animalInfo[j][i] - weights[k][i]);
									change += Math.abs(alpha * (animalInfo[j][i] - weights[k][i]));
								}
							}
							if(change > max)
								max = change;
						}
			//Update alpha and
			//check weight convergence
						alpha = (alpha * (epochs)) / (epochs + 1); 
						if (max < weightConverge)
							converged = true;
					}					

					System.out.println("");
					System.out.println("Training converged after " + epochs + " epochs.");
					trained = true;				
				}
			}

//User chooses to
//load in saved weights
//option "2" or "L"

                	else if(ans.equalsIgnoreCase("2") || ans.equalsIgnoreCase("L"))
                	{
                                inputName = realFile("Enter the name of the weights file:", "Could not find the file specified, please try again. (R to return to menu)");
	//Only pick weights
	//if user does not choose
	//to return to menu
				if(!inputName.equalsIgnoreCase("R"))
				{
	//Reading in data
	//from the specified weights file
                                	try
   					{
                                       		Scanner in = new Scanner(new File(inputName));
                                        	weights = new double[numClasses][numAtt];
                                        	for(int j = 0; j < numClasses; j++)
                                        	{
                                                	for(int i = 0; i < numAtt; i++)
                                                        	weights[j][i] = in.nextDouble();
                                        	}
                                        	in.close();
                                	}
                                        catch(FileNotFoundException e)
                                        {
                                                System.out.println(e.getMessage());
                                                System.exit(1);
                                        }  
                                        catch(NoSuchElementException e)
                                        {
                                                System.out.println(e.getMessage());
                                                System.exit(1);
                                        }
	//Weights have been loaded in
	//successfully
                                	System.out.println("");
                                	System.out.println("Weights have been successfully loaded from the file: " + inputName);
                                	trained = true;
				}
                	}



//User chooses
//to save current weights
//option "3" or "S"

			else if(ans.equalsIgnoreCase("3") || ans.equalsIgnoreCase("S"))
			{
	//Net has weights
	//available to save
				if(trained == true)
				{
                                        System.out.println("Enter the weights output file name:");
                                        outputName = kb.next();
                                        try
                                        {
		//Saving out weights data to the
		//specified file
                                                PrintWriter out = new PrintWriter(outputName);
                                                for(int j = 0; j < numClasses; j++)
						{
                                                        for(int i = 0; i < numAtt; i++)
                                                        {
                                                                out.print(weights[j][i] + " ");
                                                        }
                                                        out.println("");
                                                }
                                                out.close();
                                        }
                                        catch(IOException e)
                                        {
                                                System.out.println(e);
                                                System.exit(1);
                                        }
		//Weights have been
		//successfully saved to file
                                        System.out.println("");
                                        System.out.println("Weights have been successfully saved into the file: " + outputName);
				}

	//There are no weights
	//to save
				else
					System.out.println("You need to train the network before you can save weights.");
			}

//User chooses
//to test net with a file
//option "4" or "T"

			else if(ans.equalsIgnoreCase("4") || ans.equalsIgnoreCase("T"))
			{
	//User has weights trained
	//or loaded in, so can
	//test the net
				if(trained == true)
				{
		//Reading in input file
	                                inputName = realFile("Enter the name of the input testing file:", "Could not find the file specified, please try again. (R to return to menu)");
		//Only test the net
		//if user does not want
		//to go to main menu
					if(!inputName.equalsIgnoreCase("R"))
					{
			//Reading in output file
						System.out.println("");
						System.out.println("Enter the name where the results should be stored:");
						outputName = kb.next();

						int tempAtt = getNumAtt(inputName);
						testWithFile(inputName, outputName, numAtt, tempAtt, numClasses, weights);


						System.out.println("");
						System.out.println("The net has successfully tested the file: " + inputName);
						System.out.println("and has saved the results to the file: " + outputName);
					}
				}

	//The net has not been
	//trained for testing
				else
					System.out.println("You need to train the network before you can test the network.");
			}

//User chooses
//to test with their input
//option "5" or "U"

			else if(ans.equalsIgnoreCase("5") || ans.equalsIgnoreCase("U"))
			{
	//User has trained the
	//neural network
				if(trained == true)
				{
		//Initializing variables
					String answer = "";
					int option = 0;
					System.out.println("What is the animals name?");
					kill = kb.nextLine();
					String animalName = kb.nextLine();
					int[] userAtt = new int[numAtt - 1];
					for(int i = 0; i < numAtt - 1; i++)
						userAtt[i] = 0;
		//Reading in user input
		//for which attribute they
		//would like to change
					while(!answer.equalsIgnoreCase("Q") && !answer.equalsIgnoreCase("T"))
					{
						animalMenu(animalName, userAtt);
						do
						{
							option = 0;
							answer = "";
							System.out.println("Enter a number to change its variable, T to train, or Q to quit:");
							answer = kb.next();
							if(!answer.equalsIgnoreCase("T") && !answer.equalsIgnoreCase("Q"))
							{
								try
								{
									option = Integer.parseInt(answer);
								}
								catch(NumberFormatException e)
								{
									do
										option = getInteger("Enter an integer value between 1 and 17");
									while(option < 1 || option > 17);
								}
								while(option < 1 || option > 17)
									option = getInteger("Enter an integer value between 1 and 17:");
							}
						}while(!answer.equalsIgnoreCase("Q") && !answer.equalsIgnoreCase("T") &&( option < 1 && option > 17));
			//User has chosen one of
			//the options to change an
			//associated attribute
						if(!answer.equalsIgnoreCase("Q") && !answer.equalsIgnoreCase("T"))
						{
				//Changing the animals name
							if(option == 1)
							{
								System.out.println("");
								System.out.println("What would you like to change the animal name to?");
								kill = kb.nextLine();
								animalName = kb.nextLine();
							}
				//Changing the number of
				//legs an animal has
							else if(option == 14)
							{
								do
								{
									userAtt[option -2] = getInteger("How many legs does the animal have? (integer 0,2,4,5,6,8)");
								}while(userAtt[option - 2] != 0 && userAtt[option - 2] != 2 && userAtt[option - 2] != 4 && userAtt[option - 2] != 5 && userAtt[option - 2] != 6 && userAtt[option - 2] != 8);
							}
				//Changing one of the
				//boolean attributed
							else
							{
								do
									userAtt[option - 2] = getInteger("What would you like to change the value to? (0 if false, 1 if true)");
								while(userAtt[option - 2] != 1 && userAtt[option - 2] != 0);
							}
						}
					}
	//The user has decided to
	//train the net, not quit
					if(answer.equalsIgnoreCase("T"))
					{
						double[] topThree = new double[3];
						int[] topThreeIndex = new int[3];
						for(int i = 0; i < topThree.length; i++)
						{
							topThree[i] = 100000000;
							topThreeIndex[i] = -1;
						}
		//Finding the nearest weight
		//vector, or the nearest class
                                                for(int p = 0; p < numClasses; p++)
                                                {
                                                	double tempMin = 0;
                                                        for(int i = 0; i < (numAtt - 1); i++)
                                                        	tempMin += Math.abs(userAtt[i] - weights[p][i]);
                                                        if(tempMin < topThree[0])
                                                        {
                                                        	topThree[0] = tempMin;
                                                                topThreeIndex[0] = p;
                                                        }
							else if (tempMin < topThree[1])
							{
								topThree[1] = tempMin;
								topThreeIndex[1] = p;
							}
							else if (tempMin < topThree[2])
							{
								topThree[2] = tempMin;
								topThreeIndex[2] = p;
							}
                                                }
		//Printing out results of testing
						System.out.println("");
						System.out.println("Top three choices for the animal: " + animalName);
						System.out.print("Classification:\t");
						for(int i = 0; i < topThree.length; i++)
						{
							if(i > 0)
								System.out.print("\t\t");
							System.out.println(getClass(topThreeIndex[i] + 1));
						}
					}
				}

	//The net has not been
	//trained for testing
				else
					System.out.println("You need to train the network before you can test the network.");
			}

//User chooses
//to quit the net
			else if(ans.equalsIgnoreCase("6") || ans.equalsIgnoreCase("Q"))
			{
				System.out.println("Thank you for using my animal classifying application.");
				System.out.println("");
				System.exit(1);
			}
			else
			{
				System.out.println("You have entered an invalid input, please try again.");
			}
		}
	}




//Checking if a string
//is an integer

	public static boolean isInteger(String s)
	{
		if(s.isEmpty())return false;
    		for (int i = 0; i < s.length(); i++)
		{
        		char c = s.charAt(i);
        		if(!Character.isDigit(c) && c !='-')
	            		return false;
   		}
    		return true;
	}

//Method to get make sure
//input is an integer

	public static int getInteger(String message)
	{
    		Scanner kb = new Scanner(System.in);
    		String temp = "";
		System.out.println("");
    		System.out.println(message);
    		temp = kb.nextLine();
    		while(!isInteger(temp))
		{
			System.out.println("");
        		System.out.println(message);
        		temp = kb.nextLine();
    		}
    		return Integer.parseInt(temp);
	}

//Checking if a file
//is able to open
//or not

	public static String realFile(String message1, String message2)
	{
		Scanner kb = new Scanner(System.in);
        	boolean realFile = true;
		String inputName;
                do
                {
                	realFile = true;
                        System.out.println(message1);
                        inputName = kb.next();
                        try
                        {
                        	Scanner in = new Scanner(new File(inputName));
                                in.close();
                        }
                        catch(FileNotFoundException e)
                        {
                        	if( inputName.equalsIgnoreCase("R"))
                                	break;
                                else
                                {
                                        System.out.println("");
                                        System.out.println(message2);
                                	realFile = false;
                       		}
                	}
        	}while(realFile == false);
		return inputName;
	}

//Method to get the number of
//samples in a file

	public static int getNumber(String inputName)
	{
        	int numSamples = 1;
                try
                {
                	Scanner in = new Scanner(new File(inputName));
                        String tempChar = in.nextLine();
                        while(in.hasNextLine())
                        {
                        	tempChar = in.nextLine();
                                numSamples++;
                        }
                        in.close();
           	}
                catch(FileNotFoundException e)
                {
                	System.out.println(e.getMessage());
                        System.exit(1);
                }
                catch(NoSuchElementException e)
                {
                	System.out.println(e.getMessage());
                        System.exit(1);
                }
		return numSamples;
	}

//Method to get number of integers in the first line of a file
//This allows us to get the number of attributes in a line of a data set

	public static int getNumAtt(String inputName)
	{
		int numAtt = 0;
                try
                {
                        Scanner in = new Scanner(new File(inputName));
                        String tempString = in.nextLine();
			for(int i = 0; i < tempString.length(); i++)
			{
				if(Character.isDigit(tempString.charAt(i)))
					numAtt++;
			}
                        in.close();
                }
                catch(FileNotFoundException e)
                {
                        System.out.println(e.getMessage());
                        System.exit(1);
                }       
                catch(NoSuchElementException e)
                {  
                        System.out.println(e.getMessage());
                        System.exit(1);
                }
                return numAtt; 
        }

//Method to return the name
//of a class instead of the integer
//value

	public static String getClass(int classInt)
	{
		if(classInt == 1)
			return("Mammal");
		else if(classInt == 2)
			return("Bird");
		else if(classInt == 3)
			return("Reptile");
		else if(classInt == 4)
			return("Fish");
		else if(classInt == 5)
			return("Amphibian");
		else if(classInt == 6)
			return("Insect");
		else if(classInt == 7)
			return("Invertebrate");
		else
			return("Could not classify");
	}

//Prints out the menu
//that allows the user to classify
//an animal

	public static void animalMenu(String animalName, int[] userAtt)
	{
		System.out.println("");
        	System.out.println("(1)Name: " + animalName);
                System.out.println("(2)Hair: " + userAtt[0] + "\t(3)Feathers: " + userAtt[1] + "\t(4)Eggs: " + userAtt[2] + "\t(5)Milk:" + userAtt[3]);
                System.out.println("(6)Airborne: " + userAtt[4] + "\t(7)Aquatic: " + userAtt[5] + "\t(8)Predator: " + userAtt[6] + "\t(9)Toothed: " + userAtt[7]);                                    
                System.out.println("(10)Backbone: " + userAtt[8] + "\t(11)Breathes: " + userAtt[9] + "\t(12)Venomous: " + userAtt[10] + "\t(13)Fins: " + userAtt[11]);
                System.out.println("(14)Legs: " + userAtt[12] + "\t(15)Tail: " + userAtt[13] + "\t(16)Domestic: " + userAtt[14] + "\t(17)Catsize: " + userAtt[15]);
                System.out.println("");
	}


//Prints out
//the main menu of the
//program

	public static void mainMenu()
	{
	        System.out.println("");
                System.out.println("Enter one of the following:");
                System.out.println("'1' or 'F' - To load an input file");
                System.out.println("'2' or 'L' - To load in saved weights");
                System.out.println("'3' or 'S' - To save current weights");
                System.out.println("'4' or 'T' - To test the net with a file");
                System.out.println("'5' or 'U' - To test through user input");
        	System.out.println("'6' or 'Q' - To quit the net");
	}

//Testing Algorithm
//used with file input
	public static void testWithFile(String inputName, String outputName, int numAtt, int tempAtt, int numClasses, double[][] weights)
	{
        	try
                {
	//Opening input and
        //output files          
                	Scanner in = new Scanner(new File(inputName));
                        PrintWriter out = new PrintWriter(outputName);
        //Initializing variables
                        String tempName = "";
                        int[] tempInfo = new int[numAtt];
			int numTest = 0;
			int numCorrect = 0;
        //Running testing  
        //algorithm
                        while(in.hasNextLine())
                        {
        //Reading in and
        //testing one sample
        //per iteration
        			tempName = in.next();
                                for(int i = 0; i < numAtt; i++)
                                	tempInfo[i] = in.nextInt();
                                double[] topThree = new double[3];
                                int[] topThreeIndex = new int[3];
                                for(int i = 0; i < topThree.length; i++)
                                {
                                	topThree[i] = 100000000;
                                        topThreeIndex[i] = -1;
                                } 

        //Finding nearest
        //weight vector or class
                                for(int p = 0; p < numClasses; p++)
                                {
                                	double tempMin = 0;
                                        for(int i = 0; i < numAtt - 1; i++)
                                        	tempMin += Math.abs(tempInfo[i] - weights[p][i]);
                                        if(tempMin < topThree[0])
                                        {
                                        	topThree[0] = tempMin;
                                                topThreeIndex[0] = p;
                                        }
                                        else if (tempMin < topThree[1])
                                        {
                                                topThree[1] = tempMin;
                                                topThreeIndex[1] = p;
                                        }
                                        else if (tempMin < topThree[2])
                                        {
                                        	topThree[2] = tempMin;
                            	        	topThreeIndex[2] = p;
                                        }
                                }
         //Printing results to file
                                out.println("Top three choices for the animal: " + tempName);
                                out.print("Classification:\t");
                                for(int i = 0; i < topThree.length; i++)
                                {
                     	        	if(i > 0)
                                   	out.print("\t\t");
                                   	out.print(getClass(topThreeIndex[i] + 1));
					if(tempAtt >= numAtt)
					{
						if(tempInfo[numAtt - 1] == topThreeIndex[i] + 1)
						{
							out.print("\tCORRECT");
							if(i == 0)
								numCorrect++;
						}
					}
					out.println("");
				}
				out.println("");
                        	String kill = in.nextLine();
                		numTest++;
			}
	//Printing out accuracy
	//results to the file
			if(tempAtt >= numAtt)
			{
				out.println("----------------------------------"); 
				out.println("");
				out.println("Number tested: " + numTest); 
				out.println("Number correct: " + numCorrect);
				out.println("Accuracy: " + ((double)numCorrect / numTest));
			}
                in.close();
                out.close();
                }
                catch(FileNotFoundException e)
                {
                        System.out.println(e.getMessage());
                	System.exit(1);
                }
                catch(NoSuchElementException e)
                {
                        System.out.println(e.getMessage());
                	System.exit(1);
        	}  
	}
}
