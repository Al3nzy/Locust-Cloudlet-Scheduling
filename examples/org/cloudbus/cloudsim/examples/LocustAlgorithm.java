package org.cloudbus.cloudsim.examples;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker_phase_2;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Makespan;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.RB2B_DCBroker;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.WaitingTime;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.power.Constants;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import java.time.LocalDateTime;   
@SuppressWarnings("unused")
public class LocustAlgorithm {

  //   static public int mips[] = { 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000 };
     // static public int mips[] = {1000,2000,3000};
	// double MIPStotal = IntStream.of(mips).sum(); // to get total MIPS
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;
	private static long min;  
	private static long max;
	private static int minId;
	private static int maxId;
	private static double[] Averagemakespan;
	public static int iteration;
	private static List<Vm> vmlist;
	
		/**
		 * get total VM MIPS in a datacenter.
		 * 
		 * @param $none
		 *            
		 * @pre $none
		 * @post double summation of VMs Mips
		 */
	public double getTotalVmMips() {
		double MIPStot;
		MIPStot = IntStream.of(TaskSchedulingConstants.VM_MIPS).sum();
		return MIPStot;
	}

	private static List<Vm> createVM(int userId, int vms) {

		// Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();
		
		// VM Parameters
	//	long size = 10000; // image size (MB)
	//	int ram = 512; // vm memory (MB)
		// int mips = 1000;
		//int mips[] = {1000,2000,3000,4000,5000,6000,7000,8000,9000,10000};

		// double MIPStotal = IntStream.of(mips).sum(); // to get total MIPS

		// value = mips;
		//	long bw = 1000;
		//	int pesNumber = 1; // number of cpus
		//	String vmm = "Xen"; // VMM name
		
		// create VMs
		Vm[] vm = new Vm[vms];
		for (int i = 0; i < vms; i++) {
			vm[i] = new Vm(
					i, 
					userId, 
					TaskSchedulingConstants.VM_MIPS[i], 
					TaskSchedulingConstants.VM_PES[i], 
					TaskSchedulingConstants.VM_ram, 
					TaskSchedulingConstants.VM_BW, 
					TaskSchedulingConstants.VM_SIZE, 
					TaskSchedulingConstants.vmm, 
					new CloudletSchedulerTimeShared());
					
			System.out.println("VM #[" + i + "]     " +" MIPS = "    + vm[i].getMips());
			list.add(vm[i]);
		}
		return list;
	}

	private static List<Cloudlet> createCloudlet(int userId, int cloudlets) {
		// Creates a container to store Cloudlets
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();
		UtilizationModel utilizationModel = new UtilizationModelFull();
		setMin(Integer.MAX_VALUE);
		setMax(Integer.MIN_VALUE);
		Cloudlet[] cloudlet = new Cloudlet[cloudlets];
		double increasingValue=0.0;
		
		increasingValue=(double)(TaskSchedulingConstants.CLOUDLET_LENGTH_endRandomValue-TaskSchedulingConstants.CLOUDLET_LENGTH_startRandomValue)/(TaskSchedulingConstants.Cloudlet_number-1);
		cloudlet[0] = new Cloudlet(
				0, 
				(long) //TaskSchedulingConstants.CLOUDLET_LENGTH_startRandomValue + randomNo.nextInt(TaskSchedulingConstants.CLOUDLET_LENGTH_endRandomValue), 
				TaskSchedulingConstants.CLOUDLET_LENGTH_startRandomValue, 
				TaskSchedulingConstants.CLOUDLET_PES, 
				TaskSchedulingConstants.fileSize, 
				TaskSchedulingConstants.outputSize,
				utilizationModel, utilizationModel, utilizationModel);
		cloudlet[0].setUserId(userId);
		list.add(cloudlet[0]);
		if (getMax() < cloudlet[0].getCloudletLength()) {
			 setMax(cloudlet[0].getCloudletLength());
			setMaxId(cloudlet[0].getCloudletId());
		}
          
		
		if(getMin() > cloudlet[0].getCloudletLength()) {
       setMin(cloudlet[0].getCloudletLength());
       setMinId(cloudlet[0].getCloudletId());
		}
		System.out.println ("Cloudlet #"+0 +"     length =  " +cloudlet[0].getCloudletLength());
		for (int i = 1; i < cloudlets; i++) {// I did put i=1 because I added i=0 position as fixed value in the previous code.
			Random randomNo = new Random(); // to create Random behaviour for my set of cloudlets
			cloudlet[i] = new Cloudlet(
					i, 
					(long) (//TaskSchedulingConstants.CLOUDLET_LENGTH_startRandomValue + randomNo.nextInt(TaskSchedulingConstants.CLOUDLET_LENGTH_endRandomValue), 
							TaskSchedulingConstants.CLOUDLET_LENGTH_startRandomValue+(increasingValue*i)), 
					TaskSchedulingConstants.CLOUDLET_PES, 
					TaskSchedulingConstants.fileSize, 
					TaskSchedulingConstants.outputSize,
					utilizationModel, utilizationModel, utilizationModel);
			// setting the owner of these Cloudlets
			
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);		
			
			System.out.println ("Cloudlet #"+i +"    Random length =  " +cloudlet[i].getCloudletLength());
			
			if (getMax() < cloudlet[i].getCloudletLength()) {
				 setMax(cloudlet[i].getCloudletLength());
				setMaxId(cloudlet[i].getCloudletId());
			}
	           
			
			if(getMin() > cloudlet[i].getCloudletLength()) {
            setMin(cloudlet[i].getCloudletLength());
            setMinId(cloudlet[i].getCloudletId());
			}
		}
		return list;
	}
	////////////////////////// STATIC METHODS ///////////////////////

	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {
	//	
		for (iteration=0; iteration<TaskSchedulingConstants.Algo_iteration;iteration++ ) {
			Log.printLine("Starting Locust... \n          Iteration No. "+ (iteration+1) );
			if (iteration==0) {
				
				double[] makespan=new double[TaskSchedulingConstants.Algo_iteration];
				setAveragemakespan(makespan);
			}

		try {
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
		//	int num_user = 1; // number of CLOUD users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(TaskSchedulingConstants.num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			// Datacenters are the resource providers in CloudSim. We need at
			// list one of them to run a CloudSim simulation
			Datacenter datacenter0 = createDatacenter("Datacenter_0");
		//	Datacenter datacenter1 = createDatacenter("Datacenter_1");
			//Datacenter datacenter2 = createDatacenter("Datacenter_2");
		//	Datacenter datacenter3 = createDatacenter("Datacenter_3");
		//	Datacenter datacenter4 = createDatacenter("Datacenter_4");
		//	Datacenter datacenter5 = createDatacenter("Datacenter_5");
		//	Datacenter datacenter6 = createDatacenter("Datacenter_6");
		//	Datacenter datacenter7 = createDatacenter("Datacenter_7");
		//	Datacenter datacenter8 = createDatacenter("Datacenter_8");
		//	Datacenter datacenter9 = createDatacenter("Datacenter_9");
			// Third step: Create Broker
			RB2B_DCBroker broker = createBroker();
			int brokerId = broker.getId();

			// Fourth step: Create VMs and Cloudlets and send them to broker
			vmlist = createVM(brokerId, TaskSchedulingConstants.VM_number); // creating  vms
			cloudletList = createCloudlet(brokerId, TaskSchedulingConstants.Cloudlet_number); // creating cloudlets

			broker.submitVmList(vmlist);
			broker.submitCloudletList(cloudletList);

			// Fifth step: Starts the simulation
			double AlgorithmStartTime,AlgorithmEndTime,AlgorithmTime=0;
			//double AlgorithmEndTime=0;
			
			AlgorithmStartTime=System.currentTimeMillis();
			CloudSim.startSimulation();
			@SuppressWarnings("unchecked")
			List<Integer>[] vm_targeted = new ArrayList[vmlist.size()];
			for (int i = 0; i < vm_targeted.length; i++) {
				vm_targeted[i] = new ArrayList<>();
			}
			// TO assign based on the cloudlet to the VMs
			int vm_size = RB2B_DCBroker.upper.length;

			for (Cloudlet cloudlet : cloudletList) {
				for (int i = 0; i < vm_size; i++) {
					if (cloudlet.getCloudletLength() >= RB2B_DCBroker.lower[i]
							&& cloudlet.getCloudletLength() <= RB2B_DCBroker.upper[i]) {
						vm_targeted[i].add(cloudletList.indexOf(cloudlet));
						i = vm_size;
					}
				}
			}
			for (int i = 0; i < vm_targeted.length; i++) {
				System.out.println(" vm_targeted[" + i + "] =  " + vm_targeted[i]);
			}
			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();
					
			AlgorithmEndTime=System.currentTimeMillis();
			AlgorithmTime=AlgorithmEndTime-AlgorithmStartTime;
		
			Log.printLine(" Simulation Starting Time= "+ AlgorithmStartTime +" millisec || "+AlgorithmStartTime/1000+" Sec.");
			Log.printLine(" Simulation Ending Time= "+ AlgorithmEndTime +" millisec || "+AlgorithmEndTime/1000+" Sec.");
			Log.printLine(" Simulation Time= "+ AlgorithmTime +" millisec || "+AlgorithmTime/1000+" Sec.");
			
			printCloudletList(newList);
			
			Log.printLine(" Simulation has been done!");
			Log.printLine();
			Log.printLine("_______________________________________________");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
		}
		
			double overall=0;
			double Makespan=0;
			for(int i=0; i<getAveragemakespan().length; i++){
				overall = overall + Averagemakespan[i];
		    }   
	
			Makespan=overall / Averagemakespan.length;
			Log.printLine( "Average Makespan for "+iteration+" iterations is = "+  Makespan);

		
	}

	private static Datacenter createDatacenter(String name) {

		List<Host> hostList = new ArrayList<Host>();
		for (int i = 0; i < TaskSchedulingConstants.HOST_Number; i++) {
			int hostType = i % Constants.HOST_TYPES;

			List<Pe> peList = new ArrayList<Pe>();
			for (int j = 0; j < TaskSchedulingConstants.HOST_PES[hostType]; j++) {
			
				peList.add(new Pe(j, new PeProvisionerSimple(TaskSchedulingConstants.HOST_MIPS[hostType])));
			}
//			hostList.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList1,
//					new VmSchedulerTimeShared(peList1)));
			hostList.add(new Host(
					i,
					new RamProvisionerSimple(TaskSchedulingConstants.HOST_RAM[hostType]),
					new BwProvisionerSimple(TaskSchedulingConstants.HOST_BW),
					TaskSchedulingConstants.HOST_STORAGE,
					peList,
					new VmSchedulerTimeShared(peList)));
		}
		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store one or more
		// Machines
		
		/*to create it by constant class
		* stop this code form here 
		*
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores. Therefore,
		// should
		// create a list to store these PEs before creating
		// a Machine.
		List<Pe> peList1 = new ArrayList<Pe>();

		int mips = 10000;

		// 3. Create PEs and add these into the list.
		// for a quad-core machine, a list of 4 PEs is required:
		peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store
																// Pe id and
																// MIPS Rating
		peList1.add(new Pe(1, new PeProvisionerSimple(mips)));
		peList1.add(new Pe(2, new PeProvisionerSimple(mips)));
		peList1.add(new Pe(3, new PeProvisionerSimple(mips)));

		// Another list, for a dual-core machine
		List<Pe> peList2 = new ArrayList<Pe>();

		peList2.add(new Pe(0, new PeProvisionerSimple(mips)));
		peList2.add(new Pe(1, new PeProvisionerSimple(mips)));

		// 4. Create Hosts with its id and list of PEs and add them to the list
		// of machines
		int hostId = 0;
		int ram = 4096; // host memory (MB) (IT WAS 2GB,,
		long storage = 1000000; // host storage
		int bw = 10000;

		hostList.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList1,
				new VmSchedulerTimeShared(peList1))); // This is our first
														// machine

		hostId++;

		hostList.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList2,
				new VmSchedulerTimeShared(peList2))); // Second machine

		
		 *  to here 
		 */
		
		// To create a host with a space-shared allocation policy for PEs to
		// VMs:
		// hostList.add(
		// new Host(
		// hostId,
		// new CpuProvisionerSimple(peList1),
		// new RamProvisionerSimple(ram),
		// new BwProvisionerSimple(bw),
		// storage,
		// new VmSchedulerSpaceShared(peList1)
		// )
		// );

		// To create a host with a oportunistic space-shared allocation policy
		// for PEs to VMs:
		// hostList.add(
		// new Host(
		// hostId,
		// new CpuProvisionerSimple(peList1),
		// new RamProvisionerSimple(ram),
		// new BwProvisionerSimple(bw),
		// storage,
		// new VmSchedulerOportunisticSpaceShared(peList1)
		// )
		// );

		// 5. Create a DatacenterCharacteristics object that stores the
		// properties of a data center: architecture, OS, list of
		// Machines, allocation policy: time- or space-shared, time zone
		// and its price (G$/Pe time unit).
//		String arch = "x86"; // system architecture
//		String os = "Linux"; // operating system
//		String vmm = "Xen";
//		double time_zone = 10.0; // time zone this resource located
//		double cost = 3.0; // the cost of using processing in this resource
//		double costPerMem = 0.05; // the cost of using memory in this resource
//		double costPerStorage = 0.1; // the cost of using storage in this
//										// resource
//		double costPerBw = 0.1; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); 
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				TaskSchedulingConstants.arch, 
				TaskSchedulingConstants.os, 
				TaskSchedulingConstants.DC_vmm, 
				hostList, 
				TaskSchedulingConstants.time_zone,
				TaskSchedulingConstants.cost, 
				TaskSchedulingConstants.costPerMem, 
				TaskSchedulingConstants.costPerStorage, 
				TaskSchedulingConstants.costPerBw);
		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datacenter;
	}
	// We strongly encourage users to develop their own broker policies, to
	// submit vms and cloudlets according
	// to the specific rules of the simulated scenario
	private static RB2B_DCBroker createBroker() {

		RB2B_DCBroker broker = null;
		try {
			broker = new RB2B_DCBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}


	/**
	 * Prints the Cloudlet objects
	 * 
	 * @param list
	 *            list of Cloudlets
	 */
	@SuppressWarnings({ })
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;
		Makespan makespan= new Makespan();
		WaitingTime waitingtime= new WaitingTime();
		
		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent
				+ "length" + indent + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		if (list.size()<TaskSchedulingConstants.Cloudlet_number) {
				Log.printLine();
				Log.printLine();
				Log.printLine("||`````````````````````````````````````````````````````````````````||");
				Log.printLine("||  Iteration No. "+(iteration+1)+" has some cloudlets unsuccess!..      ||");
				Log.printLine("||_______________________________________||");
				Log.printLine();
				Log.printLine("        Repeating... ");
				Log.printLine();
				iteration--;	
		}
		else {
			
		  for (int i = 0; i < size; i++) 
		  {
			  cloudlet = list.get(i);
		  Log.print(indent + cloudlet.getCloudletId() + indent + indent);
		  
		  if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS)
		     {
		      Log.print("SUCCESS");
		      Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() + indent +
		    		  cloudlet.getCloudletLength() + indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + 
		    		  dft.format(cloudlet.getExecStartTime())+ indent + indent + indent + dft.format(cloudlet.getFinishTime()));
		      makespan.SavingLastFinishTimeForEachVm( vmlist.size(),cloudlet.getVmId(), cloudlet.getFinishTime());
		      
		     }		  
		  }
		  Averagemakespan[iteration]=makespan.overall();
		  Log.printLine();
		  System.out.println("Number of Cloudlets (Tasks) = " + list.size());
		  System.out.println("Cloudlet range from = "+ getMin()+" - "+ getMax() + "  MIPS   ||   for cloudletId #"+getMinId()+"  and cloudletId #"+getMaxId()+" respectivly." );
		  System.out.println("Number of VMs = " + vmlist.size());
		  waitingtime.AverageWaitingTime(list);
		  System.out.println("Iteration NO. " + (iteration+1)+ " Makespan = "+makespan.overall());
		  
		  Log.printLine();
		try {
			System.out.println(" Kindly checkout  D drive to get the result in excle sheet ");
			FileOutputStream fileOut = new FileOutputStream("D://ResultS_RB2B_2020_2.xls");
			@SuppressWarnings("resource")
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet worksheet = workbook.createSheet("Enizi2020");

			HSSFCellStyle headcellStyle = workbook.createCellStyle();
			HSSFCellStyle content_cellStyle = workbook.createCellStyle();
			
			HSSFRow row1 = worksheet.createRow((short) 0);
			
			HSSFCell cellS1 = row1.createCell((short) 0);
			cellS1.setCellValue("Cloudlet ID");
			cellS1.setCellStyle(headcellStyle);

			HSSFCell cellB1 = row1.createCell((short) 1);
			cellB1.setCellValue("STATUS");
			cellB1.setCellStyle(headcellStyle);

			HSSFCell cellD1 = row1.createCell((short) 2);
			cellD1.setCellValue("Data center ID");
			cellD1.setCellStyle(headcellStyle);

			HSSFCell cellH1 = row1.createCell((short) 3);
			cellH1.setCellValue("VM ID");
			cellH1.setCellStyle(headcellStyle);

			HSSFCell cellv1 = row1.createCell((short) 7);
			cellv1.setCellValue("LENGTH");
			cellv1.setCellStyle(headcellStyle);

			HSSFCell cellE1 = row1.createCell((short) 4);
			cellE1.setCellValue("TIME");
			cellE1.setCellStyle(headcellStyle);

			HSSFCell cellF1 = row1.createCell((short) 5);
			cellF1.setCellValue("START_TIME");
			cellF1.setCellStyle(headcellStyle);

			HSSFCell cellG1 = row1.createCell((short) 6);
			cellG1.setCellValue("FINISH_TIME");
			cellG1.setCellStyle(headcellStyle);

			// DecimalFormat dft = new DecimalFormat("###.##");
			for (int i = 0; i < size; i++) { // loop for printing
				
				cloudlet = list.get(i);
				HSSFRow row2 = worksheet.createRow( cloudlet.getCloudletId() + 1); // Row no.   cloudlet.getCloudletId() //  replacement for i 
				HSSFCell cellA = row2.createCell((short) 0); // Column no.
				
				cellA.setCellValue(cloudlet.getCloudletId());
				cellA.setCellStyle(content_cellStyle);
				if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
					HSSFCell cellA1 = row2.createCell((short) 1); // colume no.
					cellA1.setCellValue("SUCCESS");
					cellA1.setCellStyle(content_cellStyle);
					HSSFCell cellA2 = row2.createCell((short) 2); // colume no.
					cellA2.setCellValue(cloudlet.getResourceId());
					cellA2.setCellStyle(content_cellStyle);
					HSSFCell cellA3 = row2.createCell((short) 3); // colume no.
					cellA3.setCellValue(cloudlet.getVmId());
					cellA3.setCellStyle(content_cellStyle);
					HSSFCell cellA7 = row2.createCell((short) 7); // colume no.
					cellA7.setCellValue(cloudlet.getCloudletLength());
					cellA7.setCellStyle(content_cellStyle);
					HSSFCell cellA4 = row2.createCell((short) 4); // colume no.
					//cellA4.setCellValue(dft.format(cloudlet.getActualCPUTime()));
					cellA4.setCellValue(cloudlet.getActualCPUTime());
					cellA4.setCellStyle(content_cellStyle);
					HSSFCell cellA5 = row2.createCell((short) 5); // colume no.
					//cellA5.setCellValue(dft.format(cloudlet.getExecStartTime()));
					cellA5.setCellValue(cloudlet.getExecStartTime());
					cellA5.setCellStyle(content_cellStyle);
					HSSFCell cellA6 = row2.createCell((short) 6); // colume no.
					//cellA6.setCellValue(dft.format(cloudlet.getFinishTime()));
					cellA6.setCellValue(cloudlet.getFinishTime());
					cellA6.setCellStyle(content_cellStyle);
				}
			}	
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	}
	/**
	 * @return get the minimum cloudlet length
	 */
	public static long getMin() {
		return min;
	}



	/**
	 * @param set the minimum cloudlet length to set
	 */
	public static void setMin(long min) {
		LocustAlgorithm.min = min;
	}

	/**
	 * @return get the maximum cloudlet length
	 */
	public static long getMax() {
		return max;
	}

	/**
	 * @param set the maximum cloudlet length to set
	 */
	public static void setMax(long max) {
		LocustAlgorithm.max = max;
	}

	/**
	 * @return the minId
	 */
	public static int getMinId() {
		return minId;
	}

	/**
	 * @param minId the minId to set
	 */
	public static void setMinId(int minId) {
		LocustAlgorithm.minId = minId;
	}

	/**
	 * @return the maxId
	 */
	public static int getMaxId() {
		return maxId;
	}

	/**
	 * @param maxId the maxId to set
	 */
	public static void setMaxId(int maxId) {
		LocustAlgorithm.maxId = maxId;
	}

	/**
	 * @return the averagemakespan
	 */
	public static double[] getAveragemakespan() {
		return Averagemakespan;
	}

	/**
	 * @param averagemakespan the averagemakespan to set
	 */
	public static void setAveragemakespan(double[] averagemakespan) {
		Averagemakespan = averagemakespan;
	}

}

