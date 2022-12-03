package org.cloudbus.cloudsim.examples.power.planetlab;

import java.io.IOException;

import org.cloudbus.cloudsim.examples.power.Constants;

/**
 * A simulation of a heterogeneous power aware data center that only applied DVFS, but no dynamic
 * optimization of the VM allocation. The adjustment of the hosts' power consumption according to
 * their CPU utilization is happening in the PowerDatacenter class.
 * 
 * This example uses a real PlanetLab workload: 20110303.
 * 
 * The remaining configuration parameters are in the Constants and PlanetLabConstants classes.
 * 
 * If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * 
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * 
 * @author Mohammed ALanzy
 * @since Mar 31, 2019
 */
public class Lou_Algo {
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		//	Constants.setRep_counter(1);// if I activate it the number in constants must be defined static (no random or get value will be )
	//	for (Constants.getRep_counter();Constants.getRep_counter()<=Constants.rep_no;Constants.setRep_counter(Constants.getRep_counter()+1))
	//	{
		boolean enableOutput = true;
		boolean outputToFile = false;
		String inputFolder = Lou_Algo.class.getClassLoader().getResource("workload/planetlab").getPath();
		String outputFolder = "output";
		String workload = "20110303"; // PlanetLab workload (No. of Vms for 20110303 is 1052 VMs, i.e. 1052 files inside 20110303. and each file contents an interval of utilization masured every 5 mins for a day (24h) and the day has 288 of 5 mins)
		String vmAllocationPolicy = "LACE"; // 
		String vmSelectionPolicy = "mu";
		String parameter = "0.8";

		new PlanetLabRunner(
				enableOutput,
				outputToFile,
				inputFolder,
				outputFolder,
				workload,
				vmAllocationPolicy,
				vmSelectionPolicy,
				parameter);
		
		//}
	}
}
