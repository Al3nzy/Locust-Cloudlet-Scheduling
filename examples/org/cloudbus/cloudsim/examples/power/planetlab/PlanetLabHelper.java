package org.cloudbus.cloudsim.examples.power.planetlab;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelNull;
import org.cloudbus.cloudsim.UtilizationModelPlanetLabInMemory;
import org.cloudbus.cloudsim.examples.power.Constants;

/**
 * A helper class for the running examples for the PlanetLab workload.
 * 
 * If you are using any algorithms, policies or workload included in the power
 * package please cite the following paper:
 * 
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic
 * Algorithms and Adaptive Heuristics for Energy and Performance Efficient
 * Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency
 * and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages:
 * 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * 
 * @author Anton Beloglazov
 * @since Jan 5, 2012
 */
public class PlanetLabHelper {
private static int cloudlet_number_of_the_workload_file;
	/**
	 * Creates the cloudlet list planet lab.
	 * 
	 * @param brokerId        the broker id
	 * @param inputFolderName the input folder name
	 * @return the list
	 * @throws FileNotFoundException the file not found exception
	 */
	public static List<Cloudlet> createCloudletListPlanetLab(int brokerId, String inputFolderName)
			throws FileNotFoundException {
		List<Cloudlet> list = new ArrayList<Cloudlet>();

		long fileSize = 300;
		long outputSize = 300;
		UtilizationModel utilizationModelNull = new UtilizationModelNull();

		File inputFolder = new File(inputFolderName);
		File[] files = inputFolder.listFiles();
		setCloudlet_number_of_the_workload_file(files.length);
		// 
		// Cloudlet(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, 
		//UtilizationModel utilizationModelCpu, 
		//UtilizationModel utilizationModelRam, 
		//UtilizationModel utilizationModelBw)
	if (Constants.Number_of_cloudlets==0)
		Constants.Number_of_cloudlets=getCloudlet_number_of_the_workload_file();
		for (int i = 0; i < Constants.Number_of_cloudlets; i++) {	// ( Alanzy )
	//	for (int i = 0; i < files.length; i++) {
			Cloudlet cloudlet = null;
			try {
				cloudlet = new Cloudlet(i, Constants.CLOUDLET_LENGTH, Constants.CLOUDLET_PES, fileSize, outputSize,
						new UtilizationModelPlanetLabInMemory(files[i].getAbsolutePath(), //CPU Utilization will be set to the cloudlet utilization
								Constants.SCHEDULING_INTERVAL),
						utilizationModelNull, utilizationModelNull); 
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
			cloudlet.setUserId(brokerId);
			
			//here to schedule the VM to set them in the next code (alanzy)
			cloudlet.setVmId(i);
			list.add(cloudlet);
		}

		return list;
	}
	/**
	 * @return the cloudlet_number_of_the_workload_file
	 */
	public static  int getCloudlet_number_of_the_workload_file() {
		return cloudlet_number_of_the_workload_file;
	}
	/**
	 * @param cloudlet_number_of_the_workload_file the cloudlet_number_of_the_workload_file to set
	 */
	public static  void setCloudlet_number_of_the_workload_file(int cloudlet_number_of_the_workload_file) {
		PlanetLabHelper.cloudlet_number_of_the_workload_file = cloudlet_number_of_the_workload_file;
	}

}
