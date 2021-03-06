package uniandes.algorithms.readsanalyzer;

import java.util.HashMap;

import java.util.Set;

import ngsep.sequences.RawRead;
/**
 * Stores abundances information on a list of subsequences of a fixed length k (k-mers)
 * @author Jorge Duitama
 */
public class KmersTable implements RawReadProcessor {

	private int kmerSize;
	private HashMap<String,Integer> kMerSizes = new HashMap<>();
	/**
	 * Creates a new table with the given k-mer size
	 * @param kmerSize length of k-mers stored in this table
	 */
	public KmersTable(int kmerSize) {
		// TODO: Implementar metodo
		this.kmerSize = kmerSize; 
	}

	/**
	 * Identifies k-mers in the given read
	 * @param read object to extract new k-mers
	 */
	public void processRead(RawRead read) {
		String sequence = read.getSequenceString();
		// TODO Implementar metodo. Calcular todos los k-mers del tamanho dado en la constructora
		// y actualizar la abundancia de cada k-mer
		//Search all posible kMers
		for(int x=0; x < sequence.length()-this.kmerSize; x++) {
			String sub1 = sequence.substring(x, x+this.kmerSize);
			if(!this.kMerSizes.containsKey(sub1)) {
				this.kMerSizes.put(sub1, 1);
			}else {
				this.kMerSizes.replace(sub1, this.kMerSizes.get(sub1)+1);
			}
		}
		
	}
	
	/**
	 * List with the different k-mers found up to this point
	 * @return Set<String> set of k-mers
	 */
	public Set<String> getDistinctKmers() {
		// TODO Implementar metodo
		Set<String> keyset = kMerSizes.keySet();
		
		return keyset;
	}
	
	/**
	 * Calculates the current abundance of the given k-mer 
	 * @param kmer sequence of length k
	 * @return int times that the given k-mer have been extracted from given reads
	 */
	public int getAbundance(String kmer) {
		// TODO Implementar metodo
		return kMerSizes.get(kmer);
	}
	
	/**
	 * K-Mers abundance distribution
	 * Calculates the distribution of abundances
	 * @return int [] array where the indexes are abundances and the values are the number of k-mers
	 * observed as many times as the corresponding array index. Position zero should be equal to zero
	 */
	public int [] calculateAbundancesDistribution() {
		// TODO Implementar metodo
		//Find the maximun abundance value 
		int max = 0;
		for(int x: kMerSizes.values()) {
			//System.out.println("La abundancia es: " + x);
			max = (x > max)?x:max;
		}
		
		System.out.println("El valor de Max es: " + max);
		
		int[] a_distribution = new int[max+1];
		a_distribution[0] = 0;
		for(int i= 0; i < max; i++) {
			a_distribution[i] = 0;
		}
		for(int x: kMerSizes.values()) {
			a_distribution[x] = a_distribution[x] + 1;
		}
		System.out.println("El tamaño de las distribuciones es: " + a_distribution.length);
		return a_distribution;
		
	}
}
