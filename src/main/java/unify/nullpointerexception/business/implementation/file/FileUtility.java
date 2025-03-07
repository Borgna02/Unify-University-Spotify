package unify.nullpointerexception.business.implementation.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtility {

	public static final String COLUMN_SEPARATOR = ";";

	public static final String[] trim(String[] s) {
		for (int i = 0; i < s.length; i++) {
			s[i] = s[i].trim();
		}
		return s;
	}

	// metodo che restituisce le righe di un file
	public static FileData readAllRows(String filename) throws IOException {
		FileData result = new FileData();

		try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
			List<String[]> rows = new ArrayList<>();

			String row = null;
			while ((row = in.readLine()) != null) {
				rows.add(trim(row.split(COLUMN_SEPARATOR)));
			}

			long counter = (long) rows.size();

			result.setCounter(counter);
			result.setRows(rows);
		}

		return result;
	}

	// metodo che restituisce la riga di un file dato l'id
	public static String readRowById(String fileName, Integer id) throws IOException {
		String row = new String(), result = new String();
		DataInputStream fileReader;

		fileReader = new DataInputStream(new FileInputStream(fileName));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(fileReader))) {
			while ((row = br.readLine()) != null) {
				if (Integer.parseInt(row.split(";")[0]) == (id)) {
					result = row;
				}
			}
		} catch (NumberFormatException e) {

			e.printStackTrace();
		}

		return result;
	}

	// metodo che crea restituisce la posizione del carattere da cercare
	// a partire da una determinata posizione scelta

	public static Integer findPositionCharFromIndex(String text, char character, Integer position) {
		int i = 0, quantityCharacter = 0;
		for (i = 0; i < text.length() && quantityCharacter < position; i++) {
			if ((text.charAt(i)) == character) {
				quantityCharacter++;
			}
		}
		return i;
	}

	// metodo che elimina una riga dato l'id
	public static void deleteRowById(String fileName, int id) {

		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fileReader);
			String row;

			StringBuilder fileContent = new StringBuilder();

			while ((row = br.readLine()) != null) {

				String oldId = row.substring(0, row.indexOf(COLUMN_SEPARATOR));

				if (Integer.parseInt(oldId) != id)
					fileContent.append(row + "\n");

			}

			FileWriter fileWriter = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fileWriter);
			out.write(fileContent.toString());
			out.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// metodo che serve per cancellare in ogni riga del file da una colonna in poi
	// solo se il contenuto è uguale a quello passato per argomento (equalTo)
	public static void deleteFromIndexColumnIfEqual(String fileName, Integer startingColumnIndex, String equalTo) {
		try {
			DataInputStream fileReader = new DataInputStream(new FileInputStream(fileName));
			BufferedReader br = new BufferedReader(new InputStreamReader(fileReader));

			String row;
			String[] columns;

			StringBuilder fileData = new StringBuilder();

			while ((row = br.readLine()) != null) {

				columns = row.split(";");

				for (int i = 0; i < startingColumnIndex; i++)
					fileData.append(columns[i]).append(';');

				for (int i = startingColumnIndex; i < columns.length; i++) {

					if (!columns[i].equals(equalTo)) {

						fileData.append(columns[i]);

						if ((columns.length - i) > 1)
							fileData.append(';');
					} else if ((columns.length - i) == 1)
						fileData.deleteCharAt(fileData.length() - 1);
				}

				fileData.append('\n');

			}

			FileWriter fstreamWrite = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstreamWrite);
			out.append(fileData.toString());
			out.close();
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// metodo che modifica una riga dato l'id
	public static void editRowById(String fileName, int id, String data) {

		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fileReader);
			String row;
			StringBuilder fileContent = new StringBuilder();

			while ((row = br.readLine()) != null) {

				String oldId = row.substring(0, row.indexOf(COLUMN_SEPARATOR));

				if (Integer.parseInt(oldId) == id) {
					fileContent.append(data);
				} else {
					fileContent.append(row + "\n");
				}
			}

			FileWriter fstreamWrite = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstreamWrite);
			out.write(fileContent.toString());
			out.close();
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// metodo che aggiunge una riga al file
	public static void appendRow(String fileName, String data) {

		try {
			FileWriter fileWriter = new FileWriter(fileName, true);
			fileWriter.write(data);
			fileWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// metodo che elimina un file
	public static Boolean deleteFile(String path) {
		File file = new File(path);
		return file.delete();
	}

	// metodo che controlla se una riga è già presente nel file
	public static Boolean isDataAlreadyInARow(String data, Integer columnIndex, String path) {
		Scanner fileReader;
		try {
			fileReader = new Scanner(new File(path));
			while (fileReader.hasNextLine()) {
				if ((fileReader.nextLine().split(";")[columnIndex]).equals(data)) {
					fileReader.close();
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}
}
