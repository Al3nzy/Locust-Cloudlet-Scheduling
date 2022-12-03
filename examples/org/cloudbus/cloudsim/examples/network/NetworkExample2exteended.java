/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.examples.network;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.NetworkTopology;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.network.TopologicalGraph;
import org.cloudbus.cloudsim.network.TopologicalLink;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.util.WorkloadFileReader;

/**
 * A simple example showing how to create two datacenters with one host and a
 * network topology each and run two cloudlets on them.
 */
public class NetworkExample2exteended {
	protected static List<? extends Vm> vmsCreatedList;
	public static int hostno = 10;
	static List<Host> hostList = new ArrayList<Host>();
	/** The cloudlet list. */
	// private static List<Cloudlet> cloudletList;
	// public static List<Cloudlet> cloudletListupdated ;
	private static List<Cloudlet> cloudletList;
///////////////////////////////////////////////////////////////////////////////////

	private static List<Cloudlet> createCloudLets(int userId, int cloudlets) throws FileNotFoundException {
		/** The cloudlet list. */
		List<Cloudlet> cloudletList;
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();
		// Read Cloudlets from workload file in the swf format
		WorkloadFileReader workloadFileReader = new WorkloadFileReader(
				"C:\\Users\\Administrator\\Desktop\\Cloudsim\\cloudsim-3.0.3\\HPC2N-2002-2.2-cln.swf\\HPC2N-2002-2.2-cln _all.swf",
				1);
		// generate cloudlets from workload file
		cloudletList = workloadFileReader.generateWorkload();

		long fileSize = 300;
		long outputSize = 300;

		UtilizationModel utilizationModel = new UtilizationModelFull();

		Cloudlet[] cloudlet = new Cloudlet[cloudlets];
		for (int i = 0; i < cloudlets; i++) {
			cloudlet[i] = new Cloudlet(cloudletList.get(i).getCloudletId(), cloudletList.get(i).getCloudletLength(), 1,
					fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			// setting the owner of these Cloudlets
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);

			System.out.println("#" + i + "  ID " + cloudletList.get(i).getCloudletId() + "   Length:  "
					+ cloudletList.get(i).getCloudletLength() + " PES  " + cloudletList.get(i).getNumberOfPes()
					+ "  file size/fileout  " + fileSize + "  " + outputSize);
		}

		return list;

	}

//////////////////////////////////////////////////////////////////////////////////////////
	/** The vmlist. */
	private static List<Vm> vmlist;

	private static List<Vm> createVM(int userId, int vms) {

		// Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();

		// VM Parameters
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		int mips = 1000;
		long bw = 5000;
		int pesNumber = 2; // number of cpus
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

	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {

		Log.printLine("Starting NetworkExample2...");

		try {
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			// Datacenters are the resource providers in CloudSim. We need at list one of
			// them to run a CloudSim simulation
			Datacenter datacenter0 = createDatacenter("Datacenter_0");
			// Datacenter datacenter1 = createDatacenter("Datacenter_1");

			// Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			// Fourth step: Create VMs and Cloudlets and send them to broker

			vmlist = createVM(brokerId, 10); // creating 20 vms
			cloudletList = createCloudLets(brokerId, 10); // creating 40 cloudlets

			broker.submitVmList(vmlist);
			broker.submitCloudletList(cloudletList);

			// Sixth step: configure network
			// load the network topology file
			// NetworkTopology.buildNetworkTopology("C:\\Users\\moham\\Desktop\\eclipse-jee-photon-R-win32-x86_64\\cloudsim-3.0.3\\cloudsim-3.0.3\\examples\\org\\cloudbus\\cloudsim\\examples\\network\\topology.brite");

			// maps CloudSim entities to BRITE entities
			// Datacenter0 will correspond to BRITE node 0
			// int briteNode=0;
			// NetworkTopology.mapNode(datacenter0.getId(),briteNode);

			// Datacenter1 will correspond to BRITE node 2

			TopologicalGraph graph = new TopologicalGraph();

			for (int i = 0; i < hostno; i++) {
				
			 int Ran = new Random().nextInt(hostno);
	

				NetworkTopology.addLink(hostList.get(i).getId(), hostList.get(Ran).getId(), 10.0, 10);
				graph.addLink(
						new TopologicalLink(hostList.get(i).getId(), hostList.get(Ran).getId(), (float) 10.0, 10));
				System.out
						.println("Source =" + hostList.get(i).getId() + "  Destination = " + hostList.get(Ran).getId());

			}

			// if (result == CloudSimTags.TRUE) {
			// System.out.println("host ID "+ VmList.getById(getVmsCreatedList(),
			// vmId).getHost().getId());
			// }
			// VmList.getById(getVmsCreatedList(), vmId);
			// Broker will correspond to BRITE node 3
			// briteNode=3;
			// NetworkTopology.mapNode(broker.getId(),briteNode);

			// System.out.println(graph);

			// Sixth step: Starts the simulation
			CloudSim.startSimulation();

			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();

			printCloudletList(newList);

			Log.printLine("NetworkExample2 finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	protected int togethostid(SimEvent ev) {
		int[] data = (int[]) ev.getData();
		@SuppressWarnings("unused")
		int datacenterId = data[0];
		int vmId = data[1];
		int result = data[2];
		VmList.getById(getVmsCreatedList(), vmId).getHost().getId();
		return VmList.getById(getVmsCreatedList(), vmId).getHost().getId();
//System.exit(0);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Vm> List<T> getVmsCreatedList() {
		return (List<T>) vmsCreatedList;
	}

	protected <T extends Vm> void setVmsCreatedList(List<T> vmsCreatedList) {
		this.vmsCreatedList = vmsCreatedList;
	}

	private static Datacenter createDatacenter(String name) {

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		// our machine
		List<Host> hostList1 = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		int mips = 150000;

		// 3. Create PEs and add these into a list
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

		// 4. Create Host with its id and list of PEs and add them to the list of
		// machines
		// int hostId=0;
		int ram = 8048; // host memory (MB)
		long storage = 100000000; // host storage
		int bw = 10000000;

		// in this example, the VMAllocatonPolicy in use is Time Shared with priorities.
		// It means that VMs
		// receive time shares accroding to their priority.

		for (int i = 0; i < hostno; i++) {
			Host ch1 = new Host(i, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList,
					new VmSchedulerTimeShared(peList));
			hostList1.add(ch1); // This is our first machine
			hostList = hostList1;
		}

		// This is our machine

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
		double costPerStorage = 0.001; // the cost of using storage in this resource
		double costPerBw = 0.0; // the cost of using bw in this resource
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
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time");

		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				DecimalFormat dft = new DecimalFormat("###.##");
				Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
						+ indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent
						+ dft.format(cloudlet.getExecStartTime()) + indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}

	}
}
