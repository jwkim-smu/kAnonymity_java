package etc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import global.Parameter;
import global.textUtil;

public class makeInputFile {

	public static void main(String[] args) {
		String inputFileName = Parameter.inputFileName;
		int dataSize = Parameter.dataSize;
		final int NUM_OF_TABLE = 3;
		final int TOTAL_DATA_SIZE = 100;
		String outputFileName_T1 = Parameter.resizingInputData_T1;

		Writer outWriter_T1 = textUtil.createTXTFile(outputFileName_T1);

		try {
			FileInputStream stream = new FileInputStream(inputFileName);
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader buffer = new BufferedReader(reader);
			int cnt = 0;
			for (; cnt < TOTAL_DATA_SIZE * NUM_OF_TABLE; cnt++) {
				String label = buffer.readLine();
				if (label == null)
					break;
				if (cnt < dataSize)
					textUtil.writeString(outWriter_T1, label + "\n");
			}
			textUtil.saveTXTFile(outWriter_T1);

			System.out.println(" done!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
