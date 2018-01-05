package matrix;

import java.io.FileReader;
import java.util.Arrays;

import weka.core.*;

public class CompressedMatrix {
	Instances data;

	/**
	 * The matrix.
	 */
	public Triple[] matrix;

	/**
	 *********************
	 * Make a copy.
	 *********************
	 */
	public CompressedMatrix(CompressedMatrix paraMatrix) {
		matrix = new Triple[paraMatrix.matrix.length];
		Triple tempTail, tempCurrentTriple;
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new Triple();
			tempTail = matrix[i];
			
			tempCurrentTriple = paraMatrix.matrix[i].next;
			while (tempCurrentTriple != null) {
				Triple tempNewTriple = new Triple(tempCurrentTriple.column, tempCurrentTriple.weight, null);
				//Now insert
				tempTail.next = tempNewTriple;
				tempTail = tempNewTriple;
				
				tempCurrentTriple = tempCurrentTriple.next;
			}//Of while
		}//Of for i
	}//Of the constructor
	
	/**
	 *********************
	 * Read from a triple file.
	 *********************
	 */
	public CompressedMatrix(String paraFilename) {
		data = null;
		try {
			FileReader fileReader = new FileReader(paraFilename);
			data = new Instances(fileReader);
			fileReader.close();

			System.out.println(data);
		} catch (Exception ee) {
			System.out.println("Error occurred while trying to read \'" + paraFilename + ".\r\n" + ee);
			return;
		} // Of try

		Instance tempLastInstance = data.instance(data.numInstances() - 1);
		int tempNumNodes = (int)tempLastInstance.value(0) + 1;
		System.out.println("There are " + tempNumNodes + " nodes.");
		matrix = new Triple[tempNumNodes];
		Triple[] tails = new Triple[tempNumNodes];

		// Initialize
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new Triple();
			tails[i] = matrix[i];
		} // Of for i
		
		//Now read data
		for (int i = 0; i < data.numInstances(); i++) {
			int tempRow = (int)data.instance(i).value(0);
			int tempColumn = (int)data.instance(i).value(1);
			double tempWeight = data.instance(i).value(2);
			
			Triple tempNewTriple = new Triple(tempColumn, tempWeight, null);
			tails[tempRow].next = tempNewTriple;
			
			//Update the tail
			tails[tempRow] = tempNewTriple;
			System.out.println("I Love Ran"+tails[tempRow]);
		}//Of for i
	}// Of the first constructor

	/**
	 *********************
	 * Construct an empty compressed matrix with the given number of nodes.
	 *********************
	 */
	public CompressedMatrix(int paraNumNodes) {
		data = null;
		matrix = new Triple[paraNumNodes];

		// Initialize
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new Triple();
		} // Of for i
	}//Of the second constructor
	
	/**
	 *********************
	 * Multiply matrices.
	 *********************
	 */
	public static CompressedMatrix multiply(CompressedMatrix paraMatrix1,
			CompressedMatrix paraMatrix2) {
		System.out.println("multiply test 1");
		int tempNumNodes = paraMatrix1.matrix.length;
		
		CompressedMatrix tempTransposedMatrix = transpose(paraMatrix2);
		CompressedMatrix resultMatrix = new CompressedMatrix(tempNumNodes);

		OrderedIntArray tempArray = new OrderedIntArray(10000);
		
		int tempMiddle;
		Triple tempOuterTriple, tempInnerTriple;
		// Step 1. Compute each row of the new matrix
		System.out.println("multiply test 2");
		for (int i = 0; i < paraMatrix1.matrix.length; i++) {
			tempArray.reset();
			// Step 1.1 Compute the array of available nodes
			tempOuterTriple = paraMatrix1.matrix[i].next;
			System.out.println("paraMatrix1.matrix["+i+"].next="+paraMatrix1.matrix[i].next);
			while (tempOuterTriple != null) {
				tempMiddle = tempOuterTriple.column;
				tempInnerTriple = paraMatrix2.matrix[tempMiddle].next;
				System.out.println("\ntempInnerTriple is"+tempInnerTriple);
				while (tempInnerTriple != null) {
					System.out.print("\t" + i + "->" + tempMiddle + "->" + tempInnerTriple.column+"\n");
					tempArray.insert(tempInnerTriple.column);
					tempInnerTriple = tempInnerTriple.next;
				} // Of inner while
				tempOuterTriple = tempOuterTriple.next;
			} // Of while
			//System.out.println("multiply test 2.2");
			System.out.println("\ntempArray is"+tempArray);
			
			// Step 1.2 Compute weights
			Triple tempPointer = resultMatrix.matrix[i];
			for (int j = 0; j < tempArray.size; j ++) {
				//Add one node
				Triple tempNewTriple = new Triple();
				tempNewTriple.column = tempArray.data[j];
				tempNewTriple.weight = Triple.multiply(paraMatrix1.matrix[i], tempTransposedMatrix.matrix[tempNewTriple.column]);
				tempPointer.next = tempNewTriple;
				tempPointer = tempNewTriple;
			}//Of for j
		} // Of for i

		System.out.println("After multiply: " + resultMatrix);
		return resultMatrix;
	}// Of multiply
	
	/**
	 *********************
	 * Add matrices.
	 *********************
	 */
	public static CompressedMatrix add(CompressedMatrix paraMatrix1,
			CompressedMatrix paraMatrix2) {
		int tempNumNodes = paraMatrix1.matrix.length;
		
		CompressedMatrix resultMatrix = new CompressedMatrix(tempNumNodes);
		
		for (int i = 0; i < resultMatrix.matrix.length; i++) {
			resultMatrix.matrix[i].next = Triple.add(paraMatrix1.matrix[i], paraMatrix2.matrix[i]);
		}//Of for i
		
		System.out.println("After addition: " + resultMatrix);
		return resultMatrix;
	}//Of add

	/**
	 *********************
	 * For output.
	 *********************
	 */
	public String toString() {
		String resultString = "";
		Triple tempTriple;
		for (int i = 0; i < matrix.length; i++) {
			resultString += "\r\n" + i + ":";
			tempTriple = matrix[i].next;
			while (tempTriple != null) {
				resultString += "(" + tempTriple.column + "," + tempTriple.weight + "); ";
				tempTriple = tempTriple.next;
			} // Of while
		} // Of for i

		return resultString;
	}// Of toString
	
	/**
	 *********************
	 * Transpose a n*n matrix.
	 *********************
	 */
	public static CompressedMatrix transpose(CompressedMatrix paraMatrix) {
		int tempNumNodes = paraMatrix.matrix.length;
		int tempNumNode=paraMatrix.matrix.length;
		CompressedMatrix resultMatrix = new CompressedMatrix(tempNumNodes);
		CompressedMatrix resulttMatrix= new CompressedMatrix(tempNumNodes);
		Triple[] tempTails = new Triple[tempNumNodes];
		Triple[] tempTailst=new Triple[tempNumNodes];

		// Initialize
		
		for (int i = 0; i < tempNumNodes; i++) {
			tempTails[i] = resultMatrix.matrix[i];
			
		} // Of for i
		//Initialize
		for (int i = 0; i < tempNumNodes; i++) {
			tempTailst[i]=resulttMatrix.matrix[i];
		}// of for i
		//Scan each row and copy
		Triple tempTriple, tempNewTriple;
		for (int i = 0; i < tempNumNodes; i++) {
			tempTriple = paraMatrix.matrix[i].next;
			while (tempTriple != null) {
				//Construct a new triple
				int tempNewRow = tempTriple.column;
				int tempNewColum = i;
				tempNewTriple = new Triple(tempNewColum, tempTriple.weight, null);
				
				//Now insert
				tempTails[tempNewRow].next = tempNewTriple;
				tempTails[tempNewRow] = tempNewTriple;
				
				tempTriple = tempTriple.next;
				
			}//Of while
			
	 	}//Of for i
		System.out.println("aaa"+resultMatrix);
		return resultMatrix;
	}//Of transpose

	/**
	 *********************
	 * Compute the transition probabilities
	 * 
	 * @param paraMatrix1
	 *            The first matrix.
	 *********************
	 */
	public CompressedMatrix computeTransitionProbabilities() {
		CompressedMatrix resultMatrix = new CompressedMatrix(matrix.length);
		double tempRowSum;
		Triple tempTriple;
		//For each node
		for (int i = 0; i < matrix.length; i++) {
			//First scan of the row to obtain row sum
			tempRowSum = 0;
			tempTriple = matrix[i].next;
			while (tempTriple != null) {
				tempRowSum += tempTriple.weight;
				tempTriple = tempTriple.next;
			}//Of while
			
			//Second scan of the row to construct nodes
			tempTriple = matrix[i].next;
			Triple tempTail = resultMatrix.matrix[i];
			while (tempTriple != null) {
				Triple tempNewTriple = new Triple(tempTriple.column, tempTriple.weight/tempRowSum, null);
				//Now append
				tempTail.next = tempNewTriple;
				tempTail = tempNewTriple;
				
				tempTriple = tempTriple.next;
			}//Of while
		}//Of for i
		System.out.println("The resultMatrix is:"+resultMatrix);
		return resultMatrix;
	}//Of computeTransitionProbabilities
	
	/**
	 *********************
	 * Compute the neighborhood similarity
	 * 
	 * @param 
	 *********************
	 */
	public double neighborhoodSimilarity(int paraI, int paraJ, int paraK) {
		double resultValue = 0;
		double tempDistance = Triple.manhattan(matrix[paraI], matrix[paraJ]);
		if (tempDistance > 6) {
			System.out.print("manhattan(" + paraI + ", " + paraJ + ") = " + tempDistance + " Reason: ");
			System.out.println(matrix[paraI].next + " vs. " + matrix[paraJ].next);
		}
		
		resultValue = Math.exp(2 * paraK - tempDistance) - 1;
		
		return resultValue;
	}//Of neighborhoodSimilarity

	/**
	 *********************
	 * The test method.
	 *********************
	 */
	public static void main(String args[]) {
		System.out.println("Testing CompressedMatrix!");
		CompressedMatrix tempMatrix = new CompressedMatrix("D:/workspace/randomwalk/data/example21.arff");
		System.out.println("The matrix is: \r\n" + tempMatrix);
		
		CompressedMatrix tempMatrix2 = CompressedMatrix.transpose(tempMatrix);
		System.out.println("The transposed matrix is: \r\n" + tempMatrix2);

		CompressedMatrix tempMatrix3 = CompressedMatrix.multiply(tempMatrix, tempMatrix);
		System.out.println("The multiplied matrix is: \r\n" + tempMatrix3);

		CompressedMatrix tempMatrix4 = CompressedMatrix.add(tempMatrix, tempMatrix3);
		System.out.println("The added matrix is: \r\n" + tempMatrix4);
		
		CompressedMatrix tempMatrix5 = tempMatrix.computeTransitionProbabilities();
		System.out.println("The probability matrix is: \r\n" + tempMatrix5);
		
	}// Of main
}// Of class CompressedMatrix
