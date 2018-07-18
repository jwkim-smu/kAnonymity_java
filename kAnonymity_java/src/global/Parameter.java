package global;

public class Parameter {

	public static int KValue =  10;
	
	public static int dataSize = 100;

	public static final int totalAttrSize = 7;
	public static String joinAttrListStr = "0";	//
	
	public static final String inputFileName = new String("data/inputFile.txt");
	
	public static  String resizingInputData_T1 = "data/t1_resizingBy_"+dataSize+".txt";
	
	public static final String genTreeFileName = new String("data/gTree.txt");
	public static String resultFileName = "result/"+joinAttrListStr+"_"+dataSize;

	public static final String original_T1 = new String("data/t1_sample.txt");
	
	public static String k_anonymous_tree_T1 = "data/gTree_T1_";
	
	public static String k_member_T1 = "data/k_member_T1_";
	
	public static final String transformed_kmember_T1 = new String("data/k_member_T1_" + KValue + ".txt");
	public static final String transformed_kanonimity_T1 = new String("data/gTree_T1_" + KValue + ".txt");

	public static final String ILmatrix_T1 = new String("data/ILmatrix_T1_"+ KValue + ".txt");
}