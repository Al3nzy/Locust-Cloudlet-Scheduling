/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.HostList;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * An example showing how to create scalable simulations.
 */
public class CloudSimExample6 {

	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
	private static List<Vm> vmlist;

	private static List<Vm> createVM(int userId, int vms) {

		// Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();

		// VM Parameters
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		int mips = 250;
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name

		// create VMs
		Vm[] vm = new Vm[vms];

		for (int i = 0; i < vms; i++) {
			// vm[i] = new Vm(i, userId, mips, pesNumber, ram, bw, size, vmm, new
			// CloudletSchedulerTimeShared());
			// for creating a VM with a space shared scheduling policy for cloudlets:
			vm[i] = new Vm(i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());

			list.add(vm[i]);
		}

		return list;
	}

	private static List<Cloudlet> createCloudlet(int userId, int cloudlets) {
		// Creates a container to store Cloudlets
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

		// cloudlet parameters
		long length = 1000;
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		Cloudlet[] cloudlet = new Cloudlet[cloudlets];

		for (int i = 0; i < cloudlets; i++) {
			cloudlet[i] = new Cloudlet(i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel,
					utilizationModel);
			// setting the owner of these Cloudlets
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);
		}

		return list;
	}

	////////////////////////// STATIC METHODS ///////////////////////

	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {
		Log.printLine("Starting CloudSimExample6...");

		try {
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
			int num_user = 1; // number of grid users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			Datacenter datacenter0 = createDatacenter("Datacenter_0");

			// Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			// Fourth step: Create VMs and Cloudlets and send them to broker
			vmlist = createVM(brokerId, 40); // creating 20 vms
			cloudletList = createCloudlet(brokerId, 1000); // creating 40 cloudlets

			broker.submitVmList(vmlist);
			broker.submitCloudletList(cloudletList);

			// Fifth step: Starts the simulation
			CloudSim.startSimulation();

			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();

			// List<Host> hosts = datacenter0.getHostList();

			for (Host host : datacenter0.getHostList()) {
				System.out.println(
						"Host id = " + host.getId() + ", visited = " + ((MyextenedHost) host).getvistedCount());
			}

			int max = 10;
			int min = 5;
			for (int i =0; i<=10; i++) {
			int Ran = new Random().nextInt((int) ((max - min) + 1)) + min;
			System.out.println("testtttttttttttt=  "+ Ran);
			}
			System.exit(0);
			CloudSim.stopSimulation();

			printCloudletList(newList);

			Log.printLine("CloudSimExample6 finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	private static Datacenter createDatacenter(String name) {

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store one or more
		// Machines
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
		// create a list to store these PEs before creating
		// a Machine.
		List<Pe> peList1 = new ArrayList<Pe>();

		int mips = 1000;

		// 3. Create PEs and add these into the list.
		// for a quad-core machine, a list of 4 PEs is required:
		peList1.add(new Pe(0, new PeProvisionerSimple(100))); // need to store Pe id and MIPS Rating
		peList1.add(new Pe(1, new PeProvisionerSimple(100)));
		peList1.add(new Pe(2, new PeProvisionerSimple(100)));
		peList1.add(new Pe(3, new PeProvisionerSimple(mips)));

		// Another list, for a dual-core machine
		List<Pe> peList2 = new ArrayList<Pe>();

		peList2.add(new Pe(0, new PeProvisionerSimple(mips)));
		peList2.add(new Pe(1, new PeProvisionerSimple(mips)));

		// 4. Create Hosts with its id and list of PEs and add them to the list of
		// machines
		int hostId = 0;
		int ram = 404800; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 1000000; // i DID ADD "00"
		System.out.println("Host #" + hostId);
		System.out.println("HostMIPS : " + HostList.getNumberOfPes(hostList));
		hostList.add(new MyextenedHost(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage,
				peList1, new VmSchedulerTimeShared(peList1))); // This is our first machine

		hostId++;
		System.out.println("Host #" + hostId);
		System.out.println("HostMIPS : " + HostList.getNumberOfPes(hostList));
		// System.exit(0);

		hostList.add(new MyextenedHost(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage,
				peList2, new VmSchedulerTimeShared(peList2))

		); // Second machine

		// To create a host with a space-shared allocation policy for PEs to VMs:
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

		// To create a host with a oportunistic space-shared allocation policy for PEs
		// to VMs:
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
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.1; // the cost of using storage in this resource
		double costPerBw = 0.1; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone,
				cost, costPerMem, costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	// We strongly encourage users to develop their own broker policies, to submit
	// vms and cloudlets according
	// to the specific rules of the simulated scenario
	private static DatacenterBroker createBroker() {

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * 
	 * @param list list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent
				+ "length" + indent + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");

		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");
				Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
						+ indent + cloudlet.getCloudletLength() + indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent + indent
						+ dft.format(cloudlet.getExecStartTime()) + indent + indent + indent
						+ dft.format(cloudlet.getFinishTime()));

			}

		}

		try {
			System.out.println("Suseccfully >> Go to D drive to see the result ");
			FileOutputStream fileOut = new FileOutputStream("D://ResultS_RB2I.xls");
			@SuppressWarnings("resource")
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet worksheet = workbook.createSheet("Enizi");

			HSSFCellStyle headcellStyle = workbook.createCellStyle();
			HSSFCellStyle content_cellStyle = workbook.createCellStyle();
//			headcellStyle.setFillBackgroundColor(Red);
//			headcellStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
//			headcellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//			headcellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//			headcellStyle.setBorderRight(HSSFCellStyle.BORDER_THICK);
//			content_cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

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
				HSSFRow row2 = worksheet.createRow(cloudlet.getCloudletId() + 1); // Row no. cloudlet.getCloudletId() //
																					// replacement for i
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
					// cellA4.setCellValue(dft.format(cloudlet.getActualCPUTime()));
					cellA4.setCellValue(cloudlet.getActualCPUTime());
					cellA4.setCellStyle(content_cellStyle);
					HSSFCell cellA5 = row2.createCell((short) 5); // colume no.
					// cellA5.setCellValue(dft.format(cloudlet.getExecStartTime()));
					cellA5.setCellValue(cloudlet.getExecStartTime());
					cellA5.setCellStyle(content_cellStyle);
					HSSFCell cellA6 = row2.createCell((short) 6); // colume no.
					// cellA6.setCellValue(dft.format(cloudlet.getFinishTime()));
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
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
}
