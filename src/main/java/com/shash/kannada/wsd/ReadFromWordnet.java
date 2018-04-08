package com.shash.kannada.wsd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReadFromWordnet {

	public void readFromWordNet(String polysemyWord, String fileName) {
		HttpURLConnection conn = null;
		BufferedWriter bw = null;
		FileWriter fw = null;
		BufferedReader br = null;
		try {
			URL url = new URL(
					"http://www.cfilt.iitb.ac.in/indowordnet/first?langno=6&queryword="
							+ polysemyWord);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			fw = new FileWriter(
					"C:\\Users\\shashank\\Documents\\inh\\KannadaPolysemyImplementation2\\"
							+ fileName);
			bw = new BufferedWriter(fw);
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			// System.out.println("Output from Server .... \n");

			while ((output = br.readLine()) != null) {
				// System.out.println(output);

				bw.write(output);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}

		}

	}

	public void readFromWordNet(String polysemyWord) {
		HttpURLConnection conn = null;
		BufferedWriter bw = null;
		FileWriter fw = null;
		BufferedReader br = null;
		try {
			URL url = new URL(
					"http://www.cfilt.iitb.ac.in/indowordnet/first?langno=6&queryword="
							+ polysemyWord);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			fw = new FileWriter(
					"C:\\Users\\shashank\\Documents\\inh\\KannadaPolysemyImplementation\\semantic.txt");
			bw = new BufferedWriter(fw);
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			// System.out.println("Output from Server .... \n");

			while ((output = br.readLine()) != null) {
				// System.out.println(output);

				bw.write(output);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}

		}

	}

}
