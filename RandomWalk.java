package randomwalk;

import java.util.Arrays;

import clustering.*;
import matrix.CompressedMatrix;
import matrix.CompressedSymmetricMatrix;
import matrix.Triple;

public class RandomWalk {
	/**
	 *********************
	 * The main algorithm.
	 * 
	 * @param paraMatrix1
	 *            The first matrix.
	 *********************
	 */
	public void randomWalk(String paraFilename, int paraNumRounds, int paraK){
		//Step 1. Read data
		CompressedMatrix tempMatrix = new CompressedMatrix(paraFilename);
		System.out.println("The original matrix is: " + tempMatrix);
		CompressedMatrix tempMultiplexion, tempCombinedTransitionMatrix;

		//Step 2. Run a number of rounds to obtain new matrices
		for (int i = 0; i < paraNumRounds; i++) {
			//Step 2.1 Compute probability matrix
			CompressedMatrix tempProbabilityMatrix = tempMatrix.computeTransitionProbabilities();
			System.out.println("\r\nThe probability matrix is:" + tempProbabilityMatrix);
			//Make a copy
			tempMultiplexion = new CompressedMatrix(tempProbabilityMatrix);
			
			//Step 2.2 Multiply and add
			//Reinitialize
			tempCombinedTransitionMatrix = new CompressedMatrix(tempProbabilityMatrix);
			for (int j = 2; j <= paraK; j++) {
				System.out.println("j = " + j);
				tempMultiplexion = CompressedMatrix.multiply(tempMultiplexion, tempProbabilityMatrix);
				tempCombinedTransitionMatrix = CompressedMatrix.add(tempCombinedTransitionMatrix, tempMultiplexion);
			}//Of for j
			
			System.out.println("Find the error!" + tempMatrix);
			
			//Step 2.3 Distance between adjacent nodes
			for (int j = 0; j < tempMatrix.matrix.length; j++) {
				Triple tempCurrentTriple = tempMatrix.matrix[j].next;
				
				while (tempCurrentTriple != null) {
					//Update the weight
					tempCurrentTriple.weight = tempCombinedTransitionMatrix.neighborhoodSimilarity(j, tempCurrentTriple.column, paraK);
					
					tempCurrentTriple = tempCurrentTriple.next;
				}//Of while
			}//Of for i
		}//Of for i
		
		System.out.println("The new matrix is:" + tempMatrix);
		
		//Step 3. Depth-first clustering and output
	}//Of randomWalk

	public static void main(String args[]){
		System.out.println("Let's randomly walk!");
		//KMeans tempMeans = new KMeans("D:/workplace/randomwalk/data/iris.arff");
		//KMeans tempMeans = new KMeans("D:/workspace/randomwalk/data/iris.arff");
		//Walk tempWalk = new Walk("D:/workspace/randomwalk/data/iris.arff");
		//int[] tempIntArray = {1, 2};
		
		//tempMeans.kMeans(3, KMeans.MANHATTAN);
		//tempMeans.kMeans(3, KMeans.EUCLIDEAN);
		//tempWalk.computeVkS(tempIntArray, 3);
		//double[][] tempMatrix = tempWalk.computeTransitionProbabilities();
		//double[][] tempTransition = tempWalk.computeKStepTransitionProbabilities(100);
		//double[][] tempTransition = tempWalk.computeAtMostKStepTransitionProbabilities(5);
		
		//double[][] tempNewGraph = tempWalk.ngSeparate(3);
		
		//System.out.println(Arrays.deepToString(tempMatrix));
		
		//System.out.println("The new graph is:\r\n" + Arrays.deepToString(tempNewGraph));
		
		//CompressedSymmetricMatrix tempMatrix = new CompressedSymmetricMatrix("D:/workspace/randomwalk/data/iris.arff", 3);
		//CompressedSymmetricMatrix tempMatrix2 = CompressedSymmetricMatrix.multiply(tempMatrix, tempMatrix);
		//CompressedSymmetricMatrix tempMatrix2 = CompressedSymmetricMatrix.weightMatrixToTransitionProbabilityMatrix(tempMatrix);
		
		//System.out.println("The new matrix is: \r\n" + tempMatrix2);
		//System.out.println("The accuracy is: " + tempMeans.computePurity());
		
		new RandomWalk().randomWalk("D:/space/randomwalk1219/randomwalk/data/example21.arff", 2, 3);
	}//Of main
}//Of class RandomWalk
