package org.cloudbus.cloudsim.examples.power;

import org.cloudbus.cloudsim.power.models.PowerModel;


import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G4Xeon3040;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G5Xeon3075;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerIbmX3250XeonX3480;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerIbmX3550XeonX5670;
import org.cloudbus.cloudsim.examples.power.planetlab.Lou_Algo;
import org.cloudbus.cloudsim.examples.power.planetlab.PlanetLabHelper;
import org.cloudbus.cloudsim.examples.power.planetlab.PlanetLabRunner;
import org.cloudbus.cloudsim.examples.power.planetlab.ThrMu;
/**
 * If you are using any algorithms, policies or workload included in the power
 * package, please cite the following paper:
 *
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic
 * Algorithms and Adaptive Heuristics for Energy and Performance Efficient
 * Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency
 * and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages:
 * 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 *
 * @author Anton Beloglazov
 * @since Jan 6, 2012
 */
@SuppressWarnings("unused")

public class Constants {
		public static int getRep_counter() {
		return rep_counter;
	}

	public static void setRep_counter(int rep_counter) {
		Constants.rep_counter = rep_counter;
	}

	public static int rep_no=1;//  repeating the algorithm to print it into excel sheet for getting the average of the random VMs (LACE algo.)  
	public static int rep_counter=1; 
	// to save the number of the repetitions 
	public final static String output_power_file_directory="D://number of migration .xls";
	public final static boolean ENABLE_OUTPUT = true;
	public final static boolean OUTPUT_CSV    = false;

	public final static double SCHEDULING_INTERVAL = 300; // 5 min
	// if I reduce the scheduling interval the average will keep same (However the number of migration will be reduced)
	public final static double SIMULATION_LIMIT = 24 * 60 * 60; // 1 day 
	//public final static double SIMULATION_LIMIT = 24 * 20 ; // unknown day 
	public final static int CLOUDLET_LENGTH	= 2500 * (int) SIMULATION_LIMIT; //216,000,000 the length 
	public final static int CLOUDLET_PES	= 1;
	
	
	public final static int  Number_of_Hosts=800; //27 Nov 100 
//	public static int Number_of_VMs=50; // 24Nov 350 // Maximum 1052  based on dataset 20110303
//	public static int Number_of_cloudlets=Number_of_VMs;
	public static int Number_of_VMs; //to set the number based on the workload (as in the original code) (check planetLabRunner class if it's not working)
	public static int Number_of_cloudlets; //to set the number of the cloudlet exacly how it it's based on the workload file
	
	public final static int min_random_cores=500; // either for Vm or host 
	public final static int max_random_cores=2500;
//	
	/*
	 * VM instance types:
	 *   High-Memory Extra Large Instance: 3.25 EC2 Compute Units, 8.55 GB // too much MIPS
	 *   High-CPU Medium Instance: 2.5 EC2 Compute Units, 0.85 GB
	 *   Extra Large Instance: 2 EC2 Compute Units, 3.75 GB
	 *   Small Instance: 1 EC2 Compute Unit, 1.7 GB
	 *   Micro Instance: 0.5 EC2 Compute Unit, 0.633 GB
	 *   We decrease the memory size two times to enable over-subscription
	 *
	 */
		public final static int VM_TYPES	= 4;
	public final static int[] VM_MIPS	= {2500, 2000, 1000, 500}; // fixed types Energy consumption: 121.53 kWh 1722 5072
	//public final static int[] VM_MIPS	= { }; //random purpose 
	public final static int[] VM_PES	= {1, 1, 1, 1 };
	public final static int[] VM_RAM	= { 870,  1740, 1740, 613 };//850,  3750, 1700, 610
	public final static int VM_BW		=100000; // 100 Mbit/s  (VM bandwidth (that defines the VM migration time)).
	public final static int VM_SIZE		= 2500; // 2.5 GB // it is related to the Host storage 

	/*
	 * Host types:
	 *   HP ProLiant ML110 G4 (1 x [Xeon 3040 1860 MHz, 2 cores], 4GB)
	 *   HP ProLiant ML110 G5 (1 x [Xeon 3075 2660 MHz, 2 cores], 4GB)
	 *   We increase the memory size to enable over-subscription (x4)
	 */
	
	public static int HOST_TYPES	 = 2;
	public final static int[] HOST_MIPS	 = {1860,2660};
	//public final static int[] HOST_MIPS	 = {}; // if set randomly (with uncommented the code in Helper.java) 
	public final static int[] HOST_PES	 = {2,2};
	public final static int[] HOST_RAM	 = {4096,4096}; //4GB = 4096 MB, 8GB=8192 MB, 12GB=12288 MB.
	public final static int HOST_BW		 = 1000000; // 1 Gbit/s
	public final static int HOST_STORAGE = 1000000000; // 1 TB

	public final static String[] Host_type_names_for_LACE= {"Weak","Powerful"};
	//public final static String Host_type2_name_for_LACE="Weak";
	
	public final static PowerModel[] HOST_POWER = {
		new PowerModelSpecPowerHpProLiantMl110G4Xeon3040(), //HP ProLiant ML110 G4 (1 x [Xeon 3040 1860 MHz, 2 cores], 4GB)
	//	new PowerModelSpecPowerIbmX3250XeonX3480(), //IBM server x3250 (1 x [Xeon X3480 3067 MHz, 4 cores], 8GB)
	//	new PowerModelSpecPowerIbmX3550XeonX5670(), //IBM server x3550 (2 x [Xeon X5670 2933 MHz, 6 cores], 12GB).
	new PowerModelSpecPowerHpProLiantMl110G5Xeon3075()
	};
}
//PowerModel_TEST_ESWCT_ELMWCT();