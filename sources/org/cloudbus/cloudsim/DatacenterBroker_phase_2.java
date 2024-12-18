package org.cloudbus.cloudsim;
/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */



import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.examples.LocustAlgorithm;
import org.cloudbus.cloudsim.examples.TaskSchedulingConstants;
import org.cloudbus.cloudsim.lists.CloudletList;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * DatacentreBroker represents a broker acting on behalf of a user. It hides VM
 * management, as vm creation, sumbission of cloudlets to this VMs and
 * destruction of VMs.
 * 
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class DatacenterBroker_phase_2 extends SimEntity {

	/** The vm list. */
	protected List<? extends Vm> vmList;

	/** The vms created list. */
	protected List<? extends Vm> vmsCreatedList;

	/** The cloudlet list. */
	protected List<? extends Cloudlet> cloudletList;

	/** The cloudlet submitted list. */
	protected List<? extends Cloudlet> cloudletSubmittedList;

	/** The cloudlet received list. */
	protected List<? extends Cloudlet> cloudletReceivedList;

	/** The cloudlets submitted. */
	protected int cloudletsSubmitted;

	/** The vms requested. */
	protected int vmsRequested;

	/** The vms acks. */
	protected int vmsAcks;

	/** The vms destroyed. */
	protected int vmsDestroyed;

	/** The datacenter ids list. */
	protected List<Integer> datacenterIdsList;

	/** The datacenter requested ids list. */
	protected List<Integer> datacenterRequestedIdsList;

	/** The vms to datacenters map. */
	protected Map<Integer, Integer> vmsToDatacentersMap;

	/** The datacenter characteristics list. */
	protected Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList;
	public static double[] upper;
	public static double[] lower;

	/**
	 * Created a new DatacenterBroker object.
	 * 
	 * @param name
	 *            name to be associated with this entity (as required by
	 *            Sim_entity class from simjava package)
	 * @throws Exception
	 *             the exception
	 * @pre name != null
	 * @post $none
	 */
	public DatacenterBroker_phase_2(String name) throws Exception {
		super(name);

		setVmList(new ArrayList<Vm>());
		setVmsCreatedList(new ArrayList<Vm>());
		setCloudletList(new ArrayList<Cloudlet>());
		setCloudletSubmittedList(new ArrayList<Cloudlet>());
		setCloudletReceivedList(new ArrayList<Cloudlet>());

		cloudletsSubmitted = 0;
		setVmsRequested(0);
		setVmsAcks(0);
		setVmsDestroyed(0);

		setDatacenterIdsList(new LinkedList<Integer>());
		setDatacenterRequestedIdsList(new ArrayList<Integer>());
		setVmsToDatacentersMap(new HashMap<Integer, Integer>());
		setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());
	}

	/**
	 * This method is used to send to the broker the list with virtual machines
	 * that must be created.
	 * 
	 * @param list
	 *            the list
	 * @pre list !=null
	 * @post $none
	 */
	public void submitVmList(List<? extends Vm> list) {
		getVmList().addAll(list);
	}

	/**
	 * This method is used to send to the broker the list of cloudlets.
	 * 
	 * @param list
	 *            the list
	 * @pre list !=null
	 * @post $none
	 */
	public void submitCloudletList(List<? extends Cloudlet> list) {
		getCloudletList().addAll(list);
	}

	/**
	 * Specifies that a given cloudlet must run in a specific virtual machine.
	 * 
	 * @param cloudletId
	 *            ID of the cloudlet being bount to a vm
	 * @param vmId
	 *            the vm id
	 * @pre cloudletId > 0
	 * @pre id > 0
	 * @post $none
	 */
	public void bindCloudletToVm(int cloudletId, int vmId) {
		CloudletList.getById(getCloudletList(), cloudletId).setVmId(vmId);
	}

	/**
	 * Processes events available for this Broker.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	@Override
	public void processEvent(SimEvent ev) {
		switch (ev.getTag()) {
		// Resource characteristics request
		case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
			processResourceCharacteristicsRequest(ev);
			break;
		// Resource characteristics answer
		case CloudSimTags.RESOURCE_CHARACTERISTICS:
			processResourceCharacteristics(ev);
			break;
		// VM Creation answer
		case CloudSimTags.VM_CREATE_ACK:
			processVmCreate(ev);
			break;
		// A finished cloudlet returned
		case CloudSimTags.CLOUDLET_RETURN:
			processCloudletReturn(ev);
			break;
		// if the simulation finishes
		case CloudSimTags.END_OF_SIMULATION:
			shutdownEntity();
			break;
		// other unknown tags are processed by this method
		default:
			processOtherEvent(ev);
			break;
		}

	}

	/**
	 * Process the return of a request for the characteristics of a
	 * PowerDatacenter.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processResourceCharacteristics(SimEvent ev) {
		DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
		getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

		if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
			setDatacenterRequestedIdsList(new ArrayList<Integer>());
			createVmsInDatacenter(getDatacenterIdsList().get(0));
		}
	}

	/**
	 * Process a request for the characteristics of a PowerDatacenter.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processResourceCharacteristicsRequest(SimEvent ev) {
		setDatacenterIdsList(CloudSim.getCloudResourceList());
		setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());

		Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloud Resource List received with "
				+ getDatacenterIdsList().size() + " resource(s)");

		for (Integer datacenterId : getDatacenterIdsList()) {
			sendNow(datacenterId, CloudSimTags.RESOURCE_CHARACTERISTICS, getId());
		}
	}

	/**
	 * Process the ack received due to a request for VM creation.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processVmCreate(SimEvent ev) {
		int[] data = (int[]) ev.getData();
		int datacenterId = data[0];
		int vmId = data[1];
		int result = data[2];

		if (result == CloudSimTags.TRUE) {
			getVmsToDatacentersMap().put(vmId, datacenterId);
			getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
			Log.printLine(CloudSim.clock() + ": " + getName() + ": VM #" + vmId + " has been created in Datacenter #"
					+ datacenterId + ", Host #" + VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
		} else {
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Creation of VM #" + vmId + " failed in Datacenter #"
					+ datacenterId);
		}

		incrementVmsAcks();

		// all the requested VMs have been created
		if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
			submitCloudlets();
		} else {
			// all the acks received, but some VMs were not created
			if (getVmsRequested() == getVmsAcks()) {
				// find id of the next datacenter that has not been tried
				for (int nextDatacenterId : getDatacenterIdsList()) {
					if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) {
						createVmsInDatacenter(nextDatacenterId);
						return;
					}
				}

				// all datacenters already queried
				if (getVmsCreatedList().size() > 0) { // if some vm were created
					submitCloudlets();
				} else { // no vms created. abort
					Log.printLine(CloudSim.clock() + ": " + getName()
							+ ": none of the required VMs could be created. Aborting");
					finishExecution();
				}
			}
		}
	}

	/**
	 * Process a cloudlet return event.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processCloudletReturn(SimEvent ev) {
		Cloudlet cloudlet = (Cloudlet) ev.getData();
		getCloudletReceivedList().add(cloudlet);
		Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId() + " received");
		cloudletsSubmitted--;
		if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all
																		// cloudlets
																		// executed
			Log.printLine(CloudSim.clock() + ": " + getName() + ": All Cloudlets executed. Finishing...");
			clearDatacenters();
			finishExecution();
		} else { // some cloudlets haven't finished yet
			if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
				// all the cloudlets sent finished. It means that some bount
				// cloudlet is waiting its VM be created
				clearDatacenters();
				createVmsInDatacenter(0);
			}

		}
	}

	/**
	 * Overrides this method when making a new and different type of Broker.
	 * This method is called by {@link #body()} for incoming unknown tags.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processOtherEvent(SimEvent ev) {
		if (ev == null) {
			Log.printLine(getName() + ".processOtherEvent(): " + "Error - an event is null.");
			return;
		}

		Log.printLine(getName() + ".processOtherEvent(): " + "Error - event unknown by this DatacenterBroker.");
	}

	/**
	 * Create the virtual machines in a datacenter.
	 * 
	 * @param datacenterId
	 *            Id of the chosen PowerDatacenter
	 * @pre $none
	 * @post $none
	 */
	protected void createVmsInDatacenter(int datacenterId) {
		// send as much vms as possible for this datacenter before trying the
		// next one
		int requestedVms = 0;
		String datacenterName = CloudSim.getEntityName(datacenterId);
		for (Vm vm : getVmList()) {
			if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
				Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId() + " in "
						+ datacenterName);
				sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
				requestedVms++;
				////////    8:15 pm 20/05
				vm = VmList.getById(getVmList(), vm.getId());
	         //  vm.setCreationTime(CloudSim.clock());
				
			}
		}

		getDatacenterRequestedIdsList().add(datacenterId);

		setVmsRequested(requestedVms);
		setVmsAcks(0);
	}

	/**
	 * Submit cloudlets to the created VMs.
	 * 
	 * @pre $none
	 * @post $none
	 */
		protected void submitCloudlets() {
		int vmIndex = 0;
		int delay = 0;
		LocustAlgorithm total = new LocustAlgorithm();
		double MIPStotal = total.getTotalVmMips();
		int f[] = TaskSchedulingConstants.VM_MIPS;
		List<Cloudlet> sortList = new ArrayList<Cloudlet>();
		int totalCloudlets = getCloudletList().size();
		@SuppressWarnings("unused")
		int Csize = totalCloudlets; // to get The Cloudlet size
		long Cmin = 10000000; // to get The minimum
		long Cmax = 0; // Maximam
		for (Cloudlet cloudlet : getCloudletList()) {
			sortList.add(cloudlet);
			if (cloudlet.getCloudletLength() > Cmax) {
				Cmax = cloudlet.getCloudletLength();
			}
			if (cloudlet.getCloudletLength() < Cmin) {
				Cmin = cloudlet.getCloudletLength();
			}
		}
		double x = 0;
		double x1 = 0;
		x1 = Cmax - Cmin;
		x = x1 / MIPStotal;
		Log.printLine("Cmin = " + Cmin + "   Cmax  =" + Cmax + "  x= " + x);
		upper = new double[f.length];
		for (int i = 0; i < f.length; i++) {
			upper[i] = Cmin;
			for (int j = 0; j <= i; j++) {
				upper[i] += (f[j] * x);
			}

			System.out.println(" upper[" + i + "]=  " + upper[i]);
		}
		lower = new double[f.length];
		lower[0] = Cmin;
		System.out.println(" Lower[" + 0 + "]=  " + lower[0]);
		for (int i = 1; i < f.length; i++) {
			lower[i] = Cmin;

			for (int j = 0; j < i; j++) {
				lower[i] += (f[j] * x);
			}
			lower[i] += 1;

			System.out.println(" Lower[" + i + "]=  " + lower[i]);
		}
		// shakour
		List<Cloudlet> UpdatedList = new ArrayList<Cloudlet>();
		int vm_size = upper.length;
		for (Cloudlet cloudlet : sortList) {
			for (int i = 0; i < vm_size; i++) {
				if (cloudlet.getCloudletLength() >= lower[i] && cloudlet.getCloudletLength() <= upper[i]) {
					cloudlet.setVmId(i);
					UpdatedList.add(cloudlet);
					i = vm_size;
				}
			}
		}
		

		@SuppressWarnings("unchecked")
		List<Integer>[] vm_tmp = new ArrayList[vm_size];
		for (int i = 0; i < vm_tmp.length; i++) {
			vm_tmp[i] = new ArrayList<>();
			
		}
		double finishing_time=0;
	
		// for (Cloudlet cloudlet : getCloudletList()) {
		for (Cloudlet cloudlet : UpdatedList) {

			Vm vm;

			// if user didn't bind this cloudlet and it has not been executed
			// yet

			if (cloudlet.getVmId() == -1) {
				vm = getVmsCreatedList().get(vmIndex);
			}

			else { // submit to the specific vm
				vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId()); // to get the cloudlet that I wanna submit it
				if (vm == null) { // vm was not created
					Log.printLine(CloudSim.clock() + ": " + getName() + ": Postponing execution of cloudlet "
							+ cloudlet.getCloudletId() + ": bount VM not available");
					continue;
				}
			} /// updating ..
			
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Sending cloudlet " + cloudlet.getCloudletId()
					+ " to VM #" + vm.getId());
			cloudlet.setVmId(vm.getId());
			
			//vm.updateVmProcessing(CloudSim.clock(), null);

			// schedule(getVmsToDatacentersMap().get(vm.getId()), delay,
			// CloudSimTags.CLOUDLET_SUBMIT, cloudlet );
			// // sendNow (getVmsToDatacentersMap().get(vm.getId()),
			// CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
			// cloudletsSubmitted++;
			// vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
			// getCloudletSubmittedList().add(cloudlet);
			// Cloudlet was submitted...checking VM Status
			if (vm != null) {
				//vm.updateVmProcessing(CloudSim.clock(), null);
				double currentCPU = vm.getTotalUtilizationOfCpu(CloudSim.clock());
				// TO-DO -> Use currentCPU to your business rules...
				// This will be done after you send each cloudlet
				System.out.println("Cloudlet: " + cloudlet.getCloudletId() + " - VM: " + vm.getId()
						+ " - Current CPU Usage Percent: " + currentCPU * 100);
				int i=0;
				
				while( i < upper.length )
				{
				
			 /// to check all the VMs if there is any VM Idle >>it will allocate in it
			if (currentCPU == 0)
				   {
				    
					double processing_time=0;
					schedule(getVmsToDatacentersMap().get(vm.getId()), delay, CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
					cloudletsSubmitted++;
					vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
					getCloudletSubmittedList().add(cloudlet);
					i=upper.length;
					processing_time = cloudlet.getCloudletLength() / vm.getMips();
					finishing_time=vm.getCreationTime()+processing_time;
					vm.setCreationTime(finishing_time);
					Log.printLine("#CLOUDLET : "+cloudlet.getCloudletId()+"  SUBMITTED TO #VM "+cloudlet.getVmId()+"  AT "+vm.getCreationTime());
				   }
				if (currentCPU != 0)
				{
					if (cloudlet.getVmId() == upper.length-1) // is it the VM with highest MIPS
				        {
						int j=0;
						
						for ( j=upper.length -1; j>0;j--)
						{
						currentCPU=0;	
						cloudlet.setVmId(cloudlet.getVmId() - 1);
						vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
						currentCPU = vm.getTotalUtilizationOfCpu(CloudSim.clock());
						 Log.printLine(cloudlet.getVmId()+"  vm id < >utilision "+ currentCPU);
						if(currentCPU==0)
						{
				         j=0;
				        }
						if (currentCPU!=0 && j==1)
						{
							i = upper.length;
						}
						}
						
				        }
				
				    else // VM with highest MIPS checked
				        {
				    	 int count=0;
				    	 int Current_VM_ID =cloudlet.getVmId();
				    	 for(int ii=Current_VM_ID;ii<=upper.length-1 ;ii++)
				    	    {
				    		 count++;
				    		 if(cloudlet.getVmId() == upper.length-1)
				    			 {
				    			
				    			 for(int xx=0;xx<Current_VM_ID;)
				    			 	{
				    				 cloudlet.setVmId(xx);
				    				 currentCPU = 0;
				    				 vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
				    				 currentCPU = vm.getTotalUtilizationOfCpu(CloudSim.clock());
				    				 xx++;
				    				 Log.printLine(cloudlet.getVmId()+"  vm id < >utilision "+ currentCPU);
				    				 if(currentCPU ==0)
				    				 		xx=Current_VM_ID;
				    				 	if (xx==Current_VM_ID && currentCPU !=0)
				    				 	{
				    				 		i=upper.length ;
				    				 	}
				    			 	}
				    			 ii=upper.length-1;
				    			 } 
				    		 else
				    		 	{
				    	     currentCPU = 0;	
				    	     cloudlet.setVmId(cloudlet.getVmId()+1); // Select VM with next highest MIPS
				    	     vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
				    	     currentCPU = vm.getTotalUtilizationOfCpu(CloudSim.clock());
				    	     Log.printLine("VM #"+cloudlet.getVmId()+"  utilision = "+ currentCPU*100);
				    	     
				    	     
				    	     if(currentCPU ==0)
				    	     		ii=upper.length-1; 
				    	     	if(currentCPU!=0 && count==upper.length-1)
				    	     	{
				    	     		i=upper.length ;
				    	     	}
				    		 	}	
				        }
						i++;
				}
			}
				}//phase 2 done 
					
				   
					// here the code for phase 3 >> searching for the least finish time 
				   
		//////////////////////////////////////////////Phase 3///////////////////////////////		
				if (currentCPU !=0)
					{
						Log.printLine("VMs are busy >> going to phase 3 ");
						double Min_Waiting_Time = 1000000000;
						List<Integer> co =  new ArrayList<Integer>();
						for (int iii=0; iii<=upper.length-1;iii++)
						{	
							cloudlet.setVmId(iii);
							double processing_time=0;
							processing_time = cloudlet.getCloudletLength() / f[iii];
							vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
							finishing_time= vm.getCreationTime()+processing_time;
							if (finishing_time < Min_Waiting_Time)
                               {
                                Min_Waiting_Time = finishing_time;
  								co.add(iii);
                               }
							Log.printLine("#Vm"+cloudlet.getVmId()+" The minimam finishing time "+Min_Waiting_Time);
						}
					cloudlet.setVmId(co.get(co.size() - 1));
					double processing_time=0;
					processing_time = cloudlet.getCloudletLength() / f[co.get(co.size() - 1)];
					vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
					schedule(getVmsToDatacentersMap().get(vm.getId()), delay, CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
					cloudletsSubmitted++;
					vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
					getCloudletSubmittedList().add(cloudlet);
					i=upper.length;
					finishing_time=vm.getCreationTime()+processing_time;
					vm.setCreationTime(finishing_time);
					Log.printLine("#CLOUDLET : "+cloudlet.getCloudletId()+"  SUBMITTED TO #VM "+cloudlet.getVmId()+"  AT " +vm.getCreationTime());
					
				//Log.printLine(" ________________  " + vm.getCloudletScheduler());
					}
				// long z = vm.getCloudletScheduler().getNextFinishedCloudlet().getCloudletFinishedSoFar(cloudlet.getCloudletId());
				/*
				 * schedule(getVmsToDatacentersMap().get(vm.getId()), delay,
				 * CloudSimTags.CLOUDLET_SUBMIT, cloudlet ); // sendNow
				 * (getVmsToDatacentersMap().get(vm.getId()),
				 * CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
				 * cloudletsSubmitted++; vmIndex = (vmIndex + 1) %
				 * getVmsCreatedList().size();
				 * getCloudletSubmittedList().add(cloudlet);
				 */
			// this.pause(delay); // Here to put the waiting time for each VMs
			// until the cloudlet finish its processing.

			//this.pause(processing_time);
			CloudSim.runClockTick();
			}
			
		
		}

		// remove submitted cloudlets from waiting list
		for (Cloudlet cloudlet : getCloudletSubmittedList()) {
			getCloudletList().remove(cloudlet);
		}
	}

	///////////////////////////////////////////////////////////////////////////
	/**
	 * Destroy the virtual machines running in datacenters.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void clearDatacenters() {
		for (Vm vm : getVmsCreatedList()) {
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Destroying VM #" + vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.VM_DESTROY, vm);
		}

		getVmsCreatedList().clear();
	}

	/**
	 * Send an internal event communicating the end of the simulation.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void finishExecution() {
		sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cloudsim.core.SimEntity#shutdownEntity()
	 */
	@Override
	public void shutdownEntity() {
		Log.printLine(getName() + " is shutting down...");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cloudsim.core.SimEntity#startEntity()
	 */
	@Override
	public void startEntity() {
		Log.printLine(getName() + " is starting...");
		schedule(getId(), 0, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
	}

	/**
	 * Gets the vm list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Vm> List<T> getVmList() {
		return (List<T>) vmList;
	}

	/**
	 * Sets the vm list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param vmList
	 *            the new vm list
	 */
	protected <T extends Vm> void setVmList(List<T> vmList) {
		this.vmList = vmList;
	}

	/**
	 * Gets the cloudlet list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the cloudlet list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletList() {
		return (List<T>) cloudletList;
	}

	/**
	 * Sets the cloudlet list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cloudletList
	 *            the new cloudlet list
	 */
	protected <T extends Cloudlet> void setCloudletList(List<T> cloudletList) {
		this.cloudletList = cloudletList;
	}

	/**
	 * Gets the cloudlet submitted list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the cloudlet submitted list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletSubmittedList() {
		return (List<T>) cloudletSubmittedList;
	}

	/**
	 * Sets the cloudlet submitted list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cloudletSubmittedList
	 *            the new cloudlet submitted list
	 */
	protected <T extends Cloudlet> void setCloudletSubmittedList(List<T> cloudletSubmittedList) {
		this.cloudletSubmittedList = cloudletSubmittedList;
	}

	/**
	 * Gets the cloudlet received list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the cloudlet received list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletReceivedList() {
		return (List<T>) cloudletReceivedList;
	}

	/**
	 * Sets the cloudlet received list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cloudletReceivedList
	 *            the new cloudlet received list
	 */
	protected <T extends Cloudlet> void setCloudletReceivedList(List<T> cloudletReceivedList) {
		this.cloudletReceivedList = cloudletReceivedList;
	}

	/**
	 * Gets the vm list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Vm> List<T> getVmsCreatedList() {
		return (List<T>) vmsCreatedList;
	}

	/**
	 * Sets the vm list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param vmsCreatedList
	 *            the vms created list
	 */
	protected <T extends Vm> void setVmsCreatedList(List<T> vmsCreatedList) {
		this.vmsCreatedList = vmsCreatedList;
	}

	/**
	 * Gets the vms requested.
	 * 
	 * @return the vms requested
	 */
	protected int getVmsRequested() {
		return vmsRequested;
	}

	/**
	 * Sets the vms requested.
	 * 
	 * @param vmsRequested
	 *            the new vms requested
	 */
	protected void setVmsRequested(int vmsRequested) {
		this.vmsRequested = vmsRequested;
	}

	/**
	 * Gets the vms acks.
	 * 
	 * @return the vms acks
	 */
	protected int getVmsAcks() {
		return vmsAcks;
	}

	/**
	 * Sets the vms acks.
	 * 
	 * @param vmsAcks
	 *            the new vms acks
	 */
	protected void setVmsAcks(int vmsAcks) {
		this.vmsAcks = vmsAcks;
	}

	/**
	 * Increment vms acks.
	 */
	protected void incrementVmsAcks() {
		vmsAcks++;
	}

	/**
	 * Gets the vms destroyed.
	 * 
	 * @return the vms destroyed
	 */
	protected int getVmsDestroyed() {
		return vmsDestroyed;
	}

	/**
	 * Sets the vms destroyed.
	 * 
	 * @param vmsDestroyed
	 *            the new vms destroyed
	 */
	protected void setVmsDestroyed(int vmsDestroyed) {
		this.vmsDestroyed = vmsDestroyed;
	}

	/**
	 * Gets the datacenter ids list.
	 * 
	 * @return the datacenter ids list
	 */
	protected List<Integer> getDatacenterIdsList() {
		return datacenterIdsList;
	}

	/**
	 * Sets the datacenter ids list.
	 * 
	 * @param datacenterIdsList
	 *            the new datacenter ids list
	 */
	protected void setDatacenterIdsList(List<Integer> datacenterIdsList) {
		this.datacenterIdsList = datacenterIdsList;
	}

	/**
	 * Gets the vms to datacenters map.
	 * 
	 * @return the vms to datacenters map
	 */
	protected Map<Integer, Integer> getVmsToDatacentersMap() {
		return vmsToDatacentersMap;
	}

	/**
	 * Sets the vms to datacenters map.
	 * 
	 * @param vmsToDatacentersMap
	 *            the vms to datacenters map
	 */
	protected void setVmsToDatacentersMap(Map<Integer, Integer> vmsToDatacentersMap) {
		this.vmsToDatacentersMap = vmsToDatacentersMap;
	}

	/**
	 * Gets the datacenter characteristics list.
	 * 
	 * @return the datacenter characteristics list
	 */
	protected Map<Integer, DatacenterCharacteristics> getDatacenterCharacteristicsList() {
		return datacenterCharacteristicsList;
	}

	/**
	 * Sets the datacenter characteristics list.
	 * 
	 * @param datacenterCharacteristicsList
	 *            the datacenter characteristics list
	 */
	protected void setDatacenterCharacteristicsList(
			Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList) {
		this.datacenterCharacteristicsList = datacenterCharacteristicsList;
	}

	/**
	 * Gets the datacenter requested ids list.
	 * 
	 * @return the datacenter requested ids list
	 */
	protected List<Integer> getDatacenterRequestedIdsList() {
		return datacenterRequestedIdsList;
	}

	/**
	 * Sets the datacenter requested ids list.
	 * 
	 * @param datacenterRequestedIdsList
	 *            the new datacenter requested ids list
	 */
	protected void setDatacenterRequestedIdsList(List<Integer> datacenterRequestedIdsList) {
		this.datacenterRequestedIdsList = datacenterRequestedIdsList;
	}

}
