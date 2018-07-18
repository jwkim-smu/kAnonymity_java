package anonymity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import global.Parameter;
import global.textUtil;

public class kAnonymity {

	private int KValue;
	private int totalAttrSize = Parameter.totalAttrSize;
	private String genTreeFileName = new String(Parameter.genTreeFileName);

	private String inputFile_T1;

	private HashMap<String, Integer> maxMap = new HashMap<String, Integer>();
	private HashMap<String, ArrayList<Integer>> rangeMap = new HashMap<String, ArrayList<Integer>>();
	private ArrayList<String> projectionList = new ArrayList<String>();
	private ArrayList<Double> projectionSizeList = new ArrayList<Double>();

	private ArrayList<ArrayList> tupleList_T1 = new ArrayList<ArrayList>();

	private ArrayList<String> transfromed_tupleList_T1 = new ArrayList<String>();

	private int IRcnt = 0;
	private double curIR = 0.0;

	public void loadGenTree() {
		try {
			FileInputStream stream = new FileInputStream(this.genTreeFileName);
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader buffer = new BufferedReader(reader);

			while (true) {
				String label = buffer.readLine();
				if (label == null)
					break;

				StringTokenizer st = new StringTokenizer(label, "|");
				String attrName = st.nextElement().toString();
				Integer treeLevel = new Integer(st.nextElement().toString());
				String valueStr = st.nextElement().toString();

				// update min and max
				Integer curMax = this.maxMap.get(attrName);
				if (curMax == null)
					this.maxMap.put(attrName, treeLevel);
				else if (curMax.intValue() < treeLevel.intValue())
					this.maxMap.put(attrName, treeLevel);

				// insert range list
				ArrayList<Integer> tempArr = new ArrayList<Integer>();
				StringTokenizer valueStr_st = new StringTokenizer(valueStr, "_");
				while (valueStr_st.hasMoreTokens()) {
					tempArr.add(new Integer(valueStr_st.nextToken()));
				}

				this.rangeMap.put(attrName + "-" + treeLevel, tempArr);
			}

			// System.out.println(this.maxMap);
			// System.out.println(this.rangeMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private double exIR = 0.0;
	private ArrayList<Integer> fitNode;

	public kAnonymity() {
		this.projectionList.add("age");
		this.projectionList.add("sex");
		this.projectionList.add("surgery");
		this.projectionList.add("length");
		this.projectionList.add("location");
		// this.projectionList.add("a2");
		this.projectionList.add("disease");

		this.projectionSizeList.add(61.0);
		this.projectionSizeList.add(2.0);
		this.projectionSizeList.add(11.0);
		this.projectionSizeList.add(2.0);
		this.projectionSizeList.add(51.0);

	}

	public void loadData(String inputFileName, ArrayList<ArrayList> curTupleList) {
		int line_count = 0;
		try {
			FileInputStream stream = new FileInputStream(inputFileName);
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader buffer = new BufferedReader(reader);

			int curCount = 0;
			while (true) {
				String label = buffer.readLine();
				line_count++;
				if (line_count % 10000 == 0)
					System.out.println(line_count);
				if (label == null)
					break;

				// System.out.println(label);

				ArrayList curTuple = new ArrayList();
				StringTokenizer st = new StringTokenizer(label, "|");

				for (int i = 1; i < this.projectionList.size(); ++i)
					curTuple.add(new Integer(st.nextToken()));
				for (int i = 0; i < totalAttrSize - this.projectionList.size(); i++)
					st.nextToken();

				curTuple.add(new String(st.nextToken())); // disease
				curTupleList.add(curTuple);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean performGeneralization(ArrayList<Integer> curNode, ArrayList<ArrayList> curTupleList,
			ArrayList<String> transfromed_curTupleList) {

		int attrNumber = this.projectionList.size();
		double nodeIR = 0.0;
		boolean stopFlag = true;
		HashMap<String, ArrayList<String>> anonymizedResult = new HashMap<String, ArrayList<String>>();

		for (int i = 0; i < curTupleList.size(); ++i) {
			ArrayList curTuple = curTupleList.get(i);

			// System.out.println(curTuple);
			String tranformedStr = new String();
			for (int k = 0; k < attrNumber - 1; ++k) {

				String attrName = this.projectionList.get(k);
				int treeLevel = curNode.get(k).intValue();
				int curAttrValue = ((Integer) curTuple.get(k)).intValue();

				if (treeLevel > 0) {
					ArrayList<Integer> curRangeList = this.rangeMap.get(attrName + "-" + treeLevel);

					if (curRangeList.size() == 2) {
						tranformedStr = tranformedStr + "|" + curRangeList.get(0) + "_" + curRangeList.get(1);
						nodeIR += 1.0;
					} else {
						for (int m = 0; m < curRangeList.size() - 1; ++m) {
							int curMin = curRangeList.get(m);
							int curMax = curRangeList.get(m + 1);

							if ((curMin <= curAttrValue) && (curAttrValue <= curMax)) {
								tranformedStr = tranformedStr + "|" + curMin + "_" + curMax;
								nodeIR += (curMax - curMin + 1) / this.projectionSizeList.get(k);
								break;
							}
						}
					}
				} else {
					tranformedStr = tranformedStr + "|" + curAttrValue;
					nodeIR += 1 / this.projectionSizeList.get(k);
				}
			}
			// System.out.println(tranformedStr + "_" +
			// ((String)curTuple.get(attrNumber-1)));
			transfromed_curTupleList.add(tranformedStr + "|" + ((String) curTuple.get(attrNumber - 1)));

			ArrayList<String> senAttrList = anonymizedResult.get(tranformedStr);
			if (senAttrList == null) {
				senAttrList = new ArrayList<String>();
				senAttrList.add(((String) curTuple.get(attrNumber - 1)));
				anonymizedResult.put(tranformedStr, senAttrList);
			} else {
				senAttrList.add(((String) curTuple.get(attrNumber - 1)));
				anonymizedResult.put(tranformedStr, senAttrList);
			}
		}

		if (this.IRcnt > 2) {
			this.curIR = 0.0;
			this.IRcnt = 0;
		}
		this.curIR += nodeIR;
		this.IRcnt++;
		// stop condition check
		for (Map.Entry<String, ArrayList<String>> keyEntry : anonymizedResult.entrySet()) {
			ArrayList<String> valueList = keyEntry.getValue();

			if (valueList.size() < this.KValue) {
				stopFlag = false;
				break;
			}
		}

		return stopFlag;
	}

	public void performAnonymity() {

		ArrayList<ArrayList<Integer>> nodeQueue = new ArrayList<ArrayList<Integer>>();
		// JavaRDD<JavaRDD<Integer>> nodeQueue = new JavaRDD<
		HashMap<String, Integer> duplicateList = new HashMap<String, Integer>();

		ArrayList<Integer> initNode = new ArrayList<Integer>();
		// JavaRDD<JavaRDD<Integer> initNode =
		for (int i = 0; i < this.projectionList.size() - 1; ++i)
			initNode.add(new Integer(0));

		// initialize
		nodeQueue.add(initNode);

		int curCount = 0;
		while (nodeQueue.size() > 0) {

			// System.out.println("nodeQueue: " + nodeQueue);
			// System.out.println("duplicateList: " + duplicateList);
			ArrayList<Integer> curNode = nodeQueue.remove(0);
			// Perform anonymization
			if (curCount > 0) {
				this.transfromed_tupleList_T1.clear();

				if ((performGeneralization(curNode, this.tupleList_T1, this.transfromed_tupleList_T1))) {

					if (this.exIR == 0)
						this.exIR = this.curIR;
					if (this.exIR * 2 < this.curIR) {
						// System.out.println("***curIR is too high" + curNode + " : " + this.curIR);
						return;
					}
					System.out.println(curNode + "\t" + this.curIR);
					if (this.curIR <= this.exIR) {
						System.out.println(this.curIR + " <- " + this.exIR);
						this.exIR = this.curIR;
						this.curIR = 0.0;
						this.IRcnt = 0;

						Writer outWriter_T1 = textUtil.createTXTFile(
								Parameter.k_anonymous_tree_T1 + KValue + "_" + Parameter.dataSize + ".txt");

						for (int i = 0; i < this.transfromed_tupleList_T1.size(); ++i)
							textUtil.writeString(outWriter_T1, this.transfromed_tupleList_T1.get(i) + "\n");

						textUtil.saveTXTFile(outWriter_T1);

						this.fitNode = (ArrayList<Integer>) (curNode.clone());

					}
				}

			}

			// add next nodes
			for (int i = 0; i < this.projectionList.size() - 1; ++i) {
				ArrayList<Integer> tempNode = (ArrayList<Integer>) (curNode.clone());

				Integer attrMaxValue = this.maxMap.get(this.projectionList.get(i));
				if (attrMaxValue >= (tempNode.get(i).intValue() + 1)) {

					tempNode.set(i, new Integer(tempNode.get(i).intValue() + 1));

					String tempStr = new String();
					for (int j = 0; j < this.projectionList.size() - 1; ++j)
						tempStr = tempStr + "_" + tempNode.get(j);

					Object tempObj = duplicateList.get(tempStr);
					if (tempObj == null) {
						nodeQueue.add(tempNode);
						duplicateList.put(tempStr, new Integer(0));
					}
				}
			}

			++curCount;
		}
	}

	public String run(int kvalue2, String input_data) {
		this.KValue = kvalue2;
		inputFile_T1 = "data/" + input_data;
		System.out.println("============================================================");
		System.out.println("k    : " + this.KValue);
		System.out.println("============================================================");

		loadGenTree();
		System.out.println("loadGenTree finish!");
		loadData(this.inputFile_T1, this.tupleList_T1);
		System.out.println("loadData finish!");
		// System.out.println(this.tupleList_T1);

		performAnonymity();
		System.out.println("performAnonymity finish!");

		System.out.println(KValue + "\t" + this.fitNode + "\t" + this.exIR);
		return this.KValue + "\t" + this.fitNode.toString() + "\t" + this.exIR + "\n";
	}

	// main part
	public static void main(String[] args) {
		int kvalue = Integer.parseInt(args[0]);
		String input_data = args[1];

		long start = System.currentTimeMillis();
		kAnonymity mykAnonymity = new kAnonymity();
		mykAnonymity.run(kvalue, input_data);
		System.out.println("***** Done ***** ");
		long end = System.currentTimeMillis();

		int total_time = (int) (end - start) / 1000;

		try {

			BufferedWriter out = new BufferedWriter(new FileWriter(kvalue + "_" + input_data));
			String k = Integer.toString(kvalue);
			String time = Integer.toString(total_time);

			out.write(k);
			out.newLine();
			out.write(input_data);
			out.newLine();
			out.write(time);
			out.newLine();

			out.close();
			////////////////////////////////////////////////////////////////
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}

		System.out.println("runtime : " + (end - start) / 1000.0);
	}

}
