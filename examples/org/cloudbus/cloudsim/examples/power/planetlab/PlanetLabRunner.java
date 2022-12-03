package org.cloudbus.cloudsim.examples.power.planetlab;

import java.util.Calendar;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.power.Helper;
import org.cloudbus.cloudsim.examples.power.RunnerAbstract;
import org.cloudbus.cloudsim.examples.power.Constants;
/**
 * The example runner for the PlanetLab workload.
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
public class PlanetLabRunner extends RunnerAbstract {
	//private static int Number_of_VMs;
	/**
	 * Instantiates a new planet lab runner.
	 * 
	 * @param enableOutput       the enable output
	 * @param outputToFile       the output to file
	 * @param inputFolder        the input folder
	 * @param outputFolder       the output folder
	 * @param workload           the workload
	 * @param vmAllocationPolicy the vm allocation policy
	 * @param vmSelectionPolicy  the vm selection policy
	 * @param parameter          the parameter
	 */
	public PlanetLabRunner(boolean enableOutput, boolean outputToFile, String inputFolder, String outputFolder,
			String workload, String vmAllocationPolicy, String vmSelectionPolicy, String parameter) {
		super(enableOutput, outputToFile, inputFolder, outputFolder, workload, vmAllocationPolicy, vmSelectionPolicy,
				parameter);
	}

	/*
	 * (non-Javadoc)
	 * Alanzy (To set VMs number in planetlab)
	 * @see
	 * org.cloudbus.cloudsim.examples.power.RunnerAbstract#init(java.lang.String)
	 */
	@Override
	protected void init(String inputFolder) {
		try {
			CloudSim.init(1, Calendar.getInstance(), false);

			broker = Helper.createBroker();
			int brokerId = broker.getId();

			cloudletList = PlanetLabHelper.createCloudletListPlanetLab(brokerId, inputFolder);
			//setNumber_of_VMs(cloudletList.size()); // to send it to the constants class if I need to use it 
			System.out.println(cloudletList.size());
			//System.out.println(getNumber_of_VMs());
		//vmList = Helper.createVmList(brokerId, cloudletList.size());// (Orignial code)
		//hostList = Helper.createHostList(PlanetLabConstants.NUMBER_OF_HOSTS);
		
			//System.out.println(Constants.Number_of_VMs);
		
			if (Constants.Number_of_VMs==0) {
				vmList = Helper.createVmList(brokerId, cloudletList.size());
				//Constants.Number_of_VMs=cloudletList.size();
			//	Constants.Number_of_VMs=100*Constants.getRep_counter();
			}
			else {
				vmList = Helper.createVmList(brokerId, Constants.Number_of_VMs ); // No. of VMs  
			}
		//	
			hostList = Helper.createHostList(PlanetLabConstants.NUMBER_OF_HOSTS);
		
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
			System.exit(0);
		}
	}

	/**
	 * @return the number_of_VMs
	 */
//	public static int getNumber_of_VMs() {
//		return Number_of_VMs;
//	}

	/**
	 * @param number_of_VMs the number_of_VMs to set
	 */
//	public static void setNumber_of_VMs(int number_of_VMs) {
//		PlanetLabRunner.Number_of_VMs = number_of_VMs;
//	}

}
