package uniandes.algorithms.readsanalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import htsjdk.samtools.util.RuntimeEOFException;
import ngsep.math.Distribution;
import ngsep.sequences.RawRead;

/**
 * Represents an overlap graph for a set of reads taken from a sequence to assemble
 * @author Jorge Duitama
 *
 */
public class OverlapGraph implements RawReadProcessor {

	private int minOverlap;
	
	private Map<String,Integer> readCounts = new HashMap<>();
	private Map<String,ArrayList<ReadOverlap>> overlaps = new HashMap<>();
	
	/**
	 * Creates a new overlap graph with the given minimum overlap
	 * @param minOverlap Minimum overlap
	 */
	public OverlapGraph(int minOverlap) 
	{
		this.minOverlap = minOverlap;
	}

	/**
	 * Adds a new read to the overlap graph
	 * @param read object with the new read
	 */
	public void processRead(RawRead read) 
	{
		String sequence = read.getSequenceString();
		//TODO: Paso 1. Agregar la secuencia al mapa de conteos si no existe.
		//Si ya existe, solo se le suma 1 a su conteo correspondiente y no se deben ejecutar los pasos 2 y 3 
		
		if(!this.readCounts.containsKey(sequence)) {
			readCounts.put(sequence, 1);
			
			ArrayList<ReadOverlap> overlapes = new ArrayList<>();
			//if(!overlaps.isEmpty()) {
			ReadOverlap new_overlap;
			// Crea sobrelapes donde la secuencia nueva sea predecesora
			for(String old: readCounts.keySet()) {
				//sobrelape;
				int overlap_length = getOverlapLength(old, sequence);
				if (overlap_length > 0 && overlap_length != sequence.length()) { 
					new_overlap = new ReadOverlap(sequence, old, overlap_length);
					overlapes.add(new_overlap);
				}
			}
			if (overlapes.isEmpty()) {
				overlaps.put(sequence, overlapes);
			}
			
			// Crea sobrelapes donde la secuencia nueva sea sucesora
			for(String old: overlaps.keySet()) {
				//sobrelape;
				int overlap_length = getOverlapLength(sequence, old);
				if (overlap_length > 0 && overlap_length != sequence.length()) { 
					new_overlap = new ReadOverlap(old, sequence, overlap_length);
					overlaps.get(old).add(new_overlap);
				}
			}
			//}else {
			//	overlaps.put(sequence, null);
			//}
			
		}else {
			this.readCounts.replace(sequence, this.readCounts.get(sequence)+1);
		}
		
		//TODO: Paso 2. Actualizar el mapa de sobrelapes con los sobrelapes en los que la secuencia nueva sea predecesora de una secuencia existente
		//2.1 Crear un ArrayList para guardar las secuencias que tengan como prefijo un sufijo de la nueva secuencia
		//2.2 Recorrer las secuencias existentes para llenar este ArrayList creando los nuevos sobrelapes que se encuentren.
		//2.3 Después del recorrido para llenar la lista, agregar la nueva secuencia con su lista de sucesores al mapa de sobrelapes 
		
		//TODO: Paso 3. Actualizar el mapa de sobrelapes con los sobrelapes en los que la secuencia nueva sea sucesora de una secuencia existente
		// Recorrer el mapa de sobrelapes. Para cada secuencia existente que tenga como sufijo un prefijo de la nueva secuencia
		//se agrega un nuevo sobrelape a la lista de sobrelapes de la secuencia existente
		
		
	}
	/**
	 * Returns the length of the maximum overlap between a suffix of sequence 1 and a prefix of sequence 2
	 * @param sequence1 Sequence to evaluate suffixes
	 * @param sequence2 Sequence to evaluate prefixes
	 * @return int Maximum overlap between a prefix of sequence2 and a suffix of sequence 1
	 */
	private int getOverlapLength(String sequence1, String sequence2) {
		// TODO Implementar metodo
		int max = 0;
		for(int i = 0; i <= sequence1.length() && i <= sequence2.length() ;i++) {
			String sub_new = sequence1.substring(sequence1.length()-i, sequence1.length());
			String sub_old = sequence2.substring(0, i);
			boolean over = sub_old.equals(sub_new);
			
			if(over && max < sub_new.length()) max = sub_new.length();
		}
		return max;
	}

	

	/**
	 * Returns a set of the sequences that have been added to this graph 
	 * @return Set<String> of the different sequences
	 */
	public Set<String> getDistinctSequences() {
		//TODO: Implementar metodo
		Set <String> keyset = readCounts.keySet();
		return keyset;
	}

	/**
	 * Calculates the abundance of the given sequence
	 * @param sequence to search
	 * @return int Times that the given sequence has been added to this graph
	 */
	public int getSequenceAbundance(String sequence) {
		//TODO: Implementar metodo
		return readCounts.get(sequence);
	}
	
	/**
	 * Calculates the distribution of abundances
	 * @return int [] array where the indexes are abundances and the values are the number of sequences
	 * observed as many times as the corresponding array index. Position zero should be equal to zero
	 */
	public int[] calculateAbundancesDistribution() {
		//TODO: Implementar metodo
		int max = 0;
		for(int x: readCounts.values()) {
			//System.out.println("La abundancia es: " + x);
			max = (x > max)?x:max;
		}
		
		System.out.println("El valor de Max es: " + max);
		
		int[] a_distribution = new int[max+1];
		a_distribution[0] = 0;
		
		for(int i= 0; i < max; i++) {
			a_distribution[i] = 0;
		}
		for(int x: readCounts.values()) {
			a_distribution[x] = a_distribution[x] + 1;
		}
		System.out.println("El tamaño de las distribuciones es: " + a_distribution.length);
		return a_distribution;
	}
	/**
	 * Calculates the distribution of number of successors
	 * @return int [] array where the indexes are number of successors and the values are the number of 
	 * sequences having as many successors as the corresponding array index.
	 * Sucesor es el que en la secuencia aparece en el sufijo (en el source)
	 */
	public int[] calculateOverlapDistribution() {
		// TODO: Implementar metodo
		int max = 0;
		for(ArrayList<ReadOverlap> x : overlaps.values()) {
			//System.out.println("La abundancia es: " + x);
			max = (x.size() > max)?x.size():max;
		}
		
		int[] a_distribution = new int[max+1];
		a_distribution[0] = 0;
		for(int i= 0; i < max; i++) {
			a_distribution[i] = 0;
		}
		
		for(ArrayList<ReadOverlap> x: overlaps.values()) {
			a_distribution[x.size()] = a_distribution[x.size()] + 1;
		}
		return a_distribution;
	}
	/**
	 * Predicts the leftmost sequence of the final assembly for this overlap graph
	 * @return String Source sequence for the layout path that will be the left most subsequence in the assembly
	 */
	public String getSourceSequence () {
		// TODO Implementar metodo recorriendo las secuencias existentes y buscando una secuencia que no tenga predecesores
		for(String x: overlaps.keySet()) {
			int predecesor = 0;
			for(ReadOverlap y: overlaps.get(x)) {
				if(y.getSourceSequence() == x) {
					predecesor++;
				}
			}
			if(predecesor == 0) {
				return x;
			}
		}
		return null;
	}
	
	/**
	 * Calculates a layout path for this overlap graph
	 * @return ArrayList<ReadOverlap> List of adjacent overlaps. The destination sequence of the overlap in 
	 * position i must be the source sequence of the overlap in position i+1. 
	 */
	public ArrayList<ReadOverlap> getLayoutPath() {
		ArrayList<ReadOverlap> layout = new ArrayList<>();
		HashSet<String> visitedSequences = new HashSet<>(); 
		// TODO Implementar metodo. Comenzar por la secuencia fuente que calcula el método anterior
		// Luego, hacer un ciclo en el que en cada paso se busca la secuencia no visitada que tenga mayor sobrelape con la secuencia actual.
		// Agregar el sobrelape a la lista de respuesta y la secuencia destino al conjunto de secuencias visitadas.
		// Parar cuando no se encuentre una secuencia nueva
		//Buscando primera secuencia
		String firts =  getSourceSequence();
		//Anadir primer secuencia a visitados
		visitedSequences.add(firts);
		
		Iterator it = overlaps.entrySet().iterator();
		Set<String> values = overlaps.keySet();
		while(it.hasNext()) {
			Map.Entry actual = (Map.Entry)it.next();
			if(visitedSequences.contains((String)actual.getKey())){
				continue;
			}else {
				ArrayList<ReadOverlap> sequence = overlaps.get(actual.getKey());
				ReadOverlap buscado = getBetterSequence(sequence);
				//Agregar sobrelape
				layout.add(buscado);
				//Agregar secuencia a la lista de visitados
				visitedSequences.add((String)actual.getKey());
				//Reiniciar recorrido
				it = overlaps.entrySet().iterator();
				values = overlaps.keySet();
			}
		}
		return layout;
	}
	
	public ReadOverlap getBetterSequence(ArrayList<ReadOverlap> lista) {
		int max = 0;
		ReadOverlap buscado = null; 
		for(ReadOverlap overlap: lista) {
			if(max < overlap.getOverlap() ) {
				max = overlap.getOverlap();
				buscado = overlap;
			}
		}
		return buscado;
	}
	
	/**
	 * Predicts an assembly consistent with this overlap graph
	 * @return String assembly explaining the reads and the overlaps in this graph
	 */
	public String getAssembly () {
		ArrayList<ReadOverlap> layout = getLayoutPath();
		StringBuilder assembly = new StringBuilder();
		// TODO Recorrer el layout y ensamblar la secuencia agregando al objeto assembly las bases 
		// adicionales que aporta la región de cada secuencia destino que está a la derecha del sobrelape
		assembly.append(getSourceSequence());
		for(ReadOverlap element: layout) {
			int over = element.getOverlap();
			String dest = element.getSourceSequence();
			String aditional = "";
			//if(dest.length() < over) {
			aditional = dest.substring(over, dest.length());
			//}
			assembly.append(aditional);
		}
		return assembly.toString();
	}
}
