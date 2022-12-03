/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.core.predicates.PredicateType;
import org.cloudbus.cloudsim.examples.power.planetlab.excel_printer;

/**
 * PowerDatacenter is a class that enables simulation of power-aware data
 * centers.
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
 * @since CloudSim Toolkit 2.0
 */
@SuppressWarnings("unused")
public class PowerDatacenter extends Datacenter {

	/** The power. */
	private double power;

	/** The disable migrations. */
	private boolean disableMigrations;

	/** The cloudlet submited. */
	private double cloudletSubmitted;

	/** The migration count. */
	private int migrationCount;

	private int time_frame_count;

	private int NoPower_datacenter_visitng;

	private double DataCenter_utilizationAverage_last_timeFrame;

	private List<Double> Hosts_Utilizations_averages_list = new ArrayList<>();
	private List<Double> Hosts_Utilizations_averages_list_4each_5min = new ArrayList<>();
	private List<Double>	Ave_utilization_active_host_only= new ArrayList<>();
	

	private excel_printer print;

	/**
	 * Instantiates a new datacenter.
	 * 
	 * @param name               the name
	 * @param characteristics    the res config
	 * @param schedulingInterval the scheduling interval
	 * @param utilizationBound   the utilization bound
	 * @param vmAllocationPolicy the vm provisioner
	 * @param storageList        the storage list
	 * @throws Exception the exception
	 */
	public PowerDatacenter(String name, DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval)
			throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);

		setPower(0.0);
		setDisableMigrations(false);
		setCloudletSubmitted(-1);
		setMigrationCount(0);
	}

	/**
	 * Updates processing of each cloudlet running in this PowerDatacenter. It is
	 * necessary because Hosts and VirtualMachines are simple objects, not entities.
	 * So, they don't receive events and updating cloudlets inside them must be
	 * called from the outside.
	 * 
	 * @pre $none
	 * @post $none
	 */
	@Override
	protected void updateCloudletProcessing() {
		if (getCloudletSubmitted() == -1 || getCloudletSubmitted() == CloudSim.clock()) {
			CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.VM_DATACENTER_EVENT));
			schedule(getId(), getSchedulingInterval(), CloudSimTags.VM_DATACENTER_EVENT);
			return;
		}
		double currentTime = CloudSim.clock();

		// if some time passed since last processing
		if (currentTime > getLastProcessTime()) {
			System.out.print(currentTime + " ");

			double minTime = updateCloudetProcessingWithoutSchedulingFutureEventsForce();

			if (!isDisableMigrations()) { // here to do optimization for the current allocation (Alanzy)
				List<Map<String, Object>> migrationMap = getVmAllocationPolicy().optimizeAllocation(getVmList());

				if (migrationMap != null) {
					for (Map<String, Object> migrate : migrationMap) { // loop for the list of mapped migration that will be done in the send(,,,)" // size of loop based on the "MigrationMap" and the counter object parameter is migration that will perform as "i" counter and as an object for the next mapping VM to PM (alanzy)
						Vm vm = (Vm) migrate.get("vm");
						PowerHost targetHost = (PowerHost) migrate.get("host");
						PowerHost oldHost = (PowerHost) vm.getHost(); // will take the old host form the vm.getHost() due to unallocated new host for the VM yet!

						if (oldHost == null) {
							Log.formatLine("%.2f: Migration of VM #%d to Host #%d is started", currentTime, vm.getId(),
									targetHost.getId());
						} else {
							Log.formatLine("%.2f: Migration of VM #%d from Host #%d to Host #%d is started",
									currentTime, vm.getId(), oldHost.getId(), targetHost.getId());
						}

						targetHost.addMigratingInVm(vm);
						incrementMigrationCount();

						/** VM migration delay = RAM / bandwidth **/
						// we use BW / 2 to model BW available for migration purposes, the other
						// half of BW is for VM communication
						// around 16 seconds for 1024 MB using 1 Gbit/s network
						send(getId(), vm.getRam() / ((double) targetHost.getBw() / (2 * 8000)), CloudSimTags.VM_MIGRATE,
								migrate); // migration is done in here (Alanzy) 
					}
				}
			}

			// schedules an event to the next time
			if (minTime != Double.MAX_VALUE) {
				CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.VM_DATACENTER_EVENT));
				send(getId(), getSchedulingInterval(), CloudSimTags.VM_DATACENTER_EVENT);
			}

			setLastProcessTime(currentTime);
		}
	}

	/**
	 * Update cloudet processing without scheduling future events.
	 * 
	 * @return the double
	 */
	protected double updateCloudetProcessingWithoutSchedulingFutureEvents() {
		if (CloudSim.clock() > getLastProcessTime()) {
			return updateCloudetProcessingWithoutSchedulingFutureEventsForce();
		}
		return 0;
	}

	/**
	 * Update cloudet processing without scheduling future events.
	 * 
	 * @return the double
	 */
	protected double updateCloudetProcessingWithoutSchedulingFutureEventsForce() {
		double currentTime = CloudSim.clock();
		double minTime = Double.MAX_VALUE;
		double timeDiff = currentTime - getLastProcessTime();
		double timeFrameDatacenterEnergy = 0.0;
		// excel_printer print= new excel_printer();
		Log.printLine("\n\n--------------------------------------------------------------\n\n");
		Log.formatLine("New resource usage for the time frame starting at %.2f:", currentTime);

		for (PowerHost host : this.<PowerHost>getHostList()) {
			Log.printLine();
			// printing();
			double time = host.updateVmsProcessing(currentTime); // inform VMs to update processing
			if (time < minTime) {
				minTime = time;
			}

			Log.formatLine("%.2f: [Host #%d] utilization is %.2f%%", currentTime, host.getId(),
					host.getUtilizationOfCpu() * 100);
			//_____________________________________________________
			// to print to excel sheet Ala'anzy 9/7/2019
//			try {
//				increment_visiting(); // increment_visiting =getNoPower_datacenter_visitng
//				if (currentTime <= 300.1 && getNoPower_datacenter_visitng() == 1) {
//					excel_printer print1 = new excel_printer();
//					try {
//						print1.printer("enzi", getNoPower_datacenter_visitng(), currentTime, host.getId(),
//								host.getUtilizationOfCpu() * 100);
//					} catch (InvalidFormatException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					print = print1;
//					setPrintInExcel(print);
//				} else {
//					print = getPrintInExcel();
//					try {
//						print.printer("enzi", getNoPower_datacenter_visitng(), currentTime, host.getId(),
//								host.getUtilizationOfCpu() * 100);
//					} catch (InvalidFormatException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					setPrintInExcel(print);
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			//________________________________________________________
			// here Ala'anzy calculate the average utilization of each server

		}

		if (timeDiff > 0) {
			Log.formatLine("\nEnergy consumption for the last time frame from %.2f to %.2f:", getLastProcessTime(),
					currentTime);

//			for (PowerHost host : this.<PowerHost>getHostList()) {
//				double previousUtilizationOfCpu = host.getPreviousUtilizationOfCpu();
//				double utilizationOfCpu = host.getUtilizationOfCpu();
//				double timeFrameHostEnergy = host.getEnergyLinearInterpolation(previousUtilizationOfCpu,
//						utilizationOfCpu, timeDiff);
//				timeFrameDatacenterEnergy += timeFrameHostEnergy;
//
//				Log.printLine();
//				Log.formatLine("%.2f: [Host #%d] utilization at %.2f was %.2f%%, now is %.2f%%", currentTime,
//						host.getId(), getLastProcessTime(), previousUtilizationOfCpu * 100, utilizationOfCpu * 100);
//				Log.formatLine("%.2f: [Host #%d] energy is %.2f W*sec", currentTime, host.getId(), timeFrameHostEnergy);
//			}

			
			double time=0;
			double total_of_utilization = 0;
			double Allaverage = 0;
			double utilization_4each_5min = 0;
			double average_4each_5min = 0;
			double Sum_CPU_utilization4active_host=0;
			int j=0;
			double Ave_CPU_utilization4active_host=0;
			List<Double> utilization_at_current_time = new ArrayList<>(); // I need to call it in another class!
			List<Double> utilization_at_current_time_4each_5min = new ArrayList<>(); 
			
			for (PowerHost host : this.<PowerHost>getHostList()) {
				double previousUtilizationOfCpu = host.getPreviousUtilizationOfCpu();
				double utilizationOfCpu = host.getUtilizationOfCpu();
				double timeFrameHostEnergy = host.getEnergyLinearInterpolation(previousUtilizationOfCpu,
						utilizationOfCpu, timeDiff);
				timeFrameDatacenterEnergy += timeFrameHostEnergy;

				Log.printLine();
				Log.formatLine("%.2f: [Host #%d] utilization at %.2f was %.2f%%, now is %.2f%%", currentTime,
						host.getId(), getLastProcessTime(), previousUtilizationOfCpu * 100, utilizationOfCpu * 100);
				Log.formatLine("%.2f: [Host #%d] energy is %.2f W*sec", currentTime, host.getId(), timeFrameHostEnergy);
//				Log.formatLine("%.2f: [Host #%d] Utilization at the current time frame is %.2f%%", currentTime,
//				host.getId(), host.getUtilizationOfCpu() * 100);
				utilization_at_current_time.add(host.getUtilizationOfCpu() * 100); // araylist to save the utilization of all Hosts at the current time frame (all time frames  included for the migration time frame) 
//				System.out.println("\n\n    I am out     " + Math.floorMod((int) currentTime, 3) );
//              System.exit(0);
				time = currentTime;
				DecimalFormat df = new DecimalFormat("#.##");      
				time = Double.valueOf(df.format(time));
				//System.out.println(time);
				time = time % 3; 
				time = Double.valueOf(df.format(time));
				if(time==0.1) { // Ala'anzy 
//					System.out.println("\n\n     "  +"The % mod is   "+ time % 3 );
					utilization_at_current_time_4each_5min.add(host.getUtilizationOfCpu() * 100);
				}
             
			}
			for (int i = 0; i < utilization_at_current_time.size(); i++) {
				total_of_utilization += utilization_at_current_time.get(i); // The average of the current time frame for the data center (for all hosts utilization at the current time)
			}
			Allaverage = total_of_utilization / utilization_at_current_time.size();
			Hosts_Utilizations_averages_list.add(Allaverage);
			setDataCenter_utilizationAverage_last_timeFrame(Allaverage);
			if(time==0.1) {
			for (int i = 0; i < utilization_at_current_time_4each_5min.size(); i++) {
				utilization_4each_5min += utilization_at_current_time_4each_5min.get(i); // The average of the current time frame for the data center (for all hosts utilization at the current time)
				if (utilization_at_current_time_4each_5min.get(i)!=0) {
					Sum_CPU_utilization4active_host+=utilization_at_current_time_4each_5min.get(i);
					j++;
				}
			}
			Ave_CPU_utilization4active_host=Sum_CPU_utilization4active_host/j;
			average_4each_5min=utilization_4each_5min/utilization_at_current_time_4each_5min.size();
			Hosts_Utilizations_averages_list_4each_5min.add(average_4each_5min);
			Ave_utilization_active_host_only.add(Ave_CPU_utilization4active_host);
			setHosts_Utilizations_averages_list_4each_5min(Hosts_Utilizations_averages_list_4each_5min);
			setAve_utilization_active_host_only(Ave_utilization_active_host_only);
			
			}
			
			Log.formatLine("\n%.2f: Data center's energy is %.2f W*sec\n", currentTime, timeFrameDatacenterEnergy);
			
			Log.printLine();
			Log.formatLine("%.2f: The average Utilization for current time frame is %.2f ", currentTime, Allaverage);

			System.out.println("Time frame #" + getTime_frame_count());
			Log.printLine("\n\n____________________________________________\n\n");
			incrementTime_frame_count();
			// printing();
		}

		setPower(getPower() + timeFrameDatacenterEnergy);

		checkCloudletCompletion();

		/** Remove completed VMs **/
		for (PowerHost host : this.<PowerHost>getHostList()) {
			for (Vm vm : host.getCompletedVms()) {
				getVmAllocationPolicy().deallocateHostForVm(vm);
				getVmList().remove(vm);
				Log.printLine("VM #" + vm.getId() + " has been deallocated from host #" + host.getId());
			}
		}

		Log.printLine();

		setLastProcessTime(currentTime);
		return minTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cloudbus.cloudsim.Datacenter#processVmMigrate(org.cloudbus.cloudsim.core.
	 * SimEvent, boolean)
	 */
	@Override
	protected void processVmMigrate(SimEvent ev, boolean ack) {
		updateCloudetProcessingWithoutSchedulingFutureEvents();
		super.processVmMigrate(ev, ack);
		SimEvent event = CloudSim.findFirstDeferred(getId(), new PredicateType(CloudSimTags.VM_MIGRATE));
		if (event == null || event.eventTime() > CloudSim.clock()) {
			updateCloudetProcessingWithoutSchedulingFutureEventsForce();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cloudsim.Datacenter#processCloudletSubmit(cloudsim.core.SimEvent,
	 * boolean)
	 */
	@Override
	protected void processCloudletSubmit(SimEvent ev, boolean ack) {
		super.processCloudletSubmit(ev, ack);
		setCloudletSubmitted(CloudSim.clock());
	}

	/**
	 * Gets the power.
	 * 
	 * @return the power
	 */
	public double getPower() {
		return power;
	}

	/**
	 * Sets the power.
	 * 
	 * @param power the new power
	 */
	protected void setPower(double power) {
		this.power = power;
	}

	/**
	 * Checks if PowerDatacenter is in migration.
	 * 
	 * @return true, if PowerDatacenter is in migration
	 */
	protected boolean isInMigration() {
		boolean result = false;
		for (Vm vm : getVmList()) {
			if (vm.isInMigration()) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * Checks if is disable migrations.
	 * 
	 * @return true, if is disable migrations
	 */
	public boolean isDisableMigrations() {
		return disableMigrations;
	}

	/**
	 * Sets the disable migrations.
	 * 
	 * @param disableMigrations the new disable migrations
	 */
	public void setDisableMigrations(boolean disableMigrations) {
		this.disableMigrations = disableMigrations;
	}

	/**
	 * Checks if is cloudlet submited.
	 * 
	 * @return true, if is cloudlet submited
	 */
	protected double getCloudletSubmitted() {
		return cloudletSubmitted;
	}

	/**
	 * Sets the cloudlet submited.
	 * 
	 * @param cloudletSubmitted the new cloudlet submited
	 */
	protected void setCloudletSubmitted(double cloudletSubmitted) {
		this.cloudletSubmitted = cloudletSubmitted;
	}

	/**
	 * Gets the migration count.
	 * 
	 * @return the migration count
	 */
	public int getMigrationCount() {
		return migrationCount;
	}

	/**
	 * Sets the migration count.
	 * 
	 * @param migrationCount the new migration count
	 */
	protected void setMigrationCount(int migrationCount) {
		this.migrationCount = migrationCount;
	}

	/**
	 * Increment migration count.
	 */
	protected void incrementMigrationCount() {
		setMigrationCount(getMigrationCount() + 1);
	}

	/**
	 * @return the utilization_current_time_frame
	 */
	public double getDataCenter_utilizationAverage_last_timeFrame() {
		return DataCenter_utilizationAverage_last_timeFrame;
	}

	/**
	 * @param utilization_current_time_frame the utilization_current_time_frame to
	 *                                       set
	 */
	public void setDataCenter_utilizationAverage_last_timeFrame(double DataCenter_utilizationAverage_last_timeFrame) {
		this.DataCenter_utilizationAverage_last_timeFrame = DataCenter_utilizationAverage_last_timeFrame;
	}

	protected void incrementTime_frame_count() {
		setTime_frame_count(getTime_frame_count() + 1);
	}

	/**
	 * @return the time_frame_count
	 */
	public int getTime_frame_count() {
		return time_frame_count;
	}

	/**
	 * @param time_frame_count the time_frame_count to set
	 */
	public void setTime_frame_count(int time_frame_count) {
		this.time_frame_count = time_frame_count;
	}

	public List<Double> getHosts_Utilizations_averages_list() {
		return Hosts_Utilizations_averages_list;
	}

	public void setHosts_Utilizations_averages_list(List<Double> hosts_Utilizations_averages_list) {
		Hosts_Utilizations_averages_list = hosts_Utilizations_averages_list;
	}

	/**
	 * @return the noPower_datacenter_visitng
	 */
	public int getNoPower_datacenter_visitng() {
		return NoPower_datacenter_visitng;
	}

	/**
	 * @param noPower_datacenter_visitng the noPower_datacenter_visitng to set
	 */
	public void setNoPower_datacenter_visitng(int noPower_datacenter_visitng) {
		NoPower_datacenter_visitng = noPower_datacenter_visitng;
	}

	protected void increment_visiting() {
		setNoPower_datacenter_visitng(getNoPower_datacenter_visitng() + 1);
	}

	/**
	 * @return the print
	 */
	public excel_printer getPrintInExcel() {
		return print;
	}

	/**
	 * @param print the print to set
	 */
	public void setPrintInExcel(excel_printer print) {
		this.print = print;
	}

	/**
	 * @return the hosts_Utilizations_averages_list_4each_5min
	 */
	public List<Double> getHosts_Utilizations_averages_list_4each_5min() {
		return Hosts_Utilizations_averages_list_4each_5min;
	}

	/**
	 * @param hosts_Utilizations_averages_list_4each_5min the hosts_Utilizations_averages_list_4each_5min to set
	 */
	public void setHosts_Utilizations_averages_list_4each_5min(List<Double> hosts_Utilizations_averages_list_4each_5min) {
		Hosts_Utilizations_averages_list_4each_5min = hosts_Utilizations_averages_list_4each_5min;
	}
	
	/**
	 * @param to get the average utilization for active servers only 
	 * sleep mode servers have neglected (Alanzy)
	 */
	public List<Double> getAve_utilization_active_host_only() {
		return Ave_utilization_active_host_only;
	}

	public void setAve_utilization_active_host_only(List<Double> ave_utilization_active_host_only) {
		Ave_utilization_active_host_only = ave_utilization_active_host_only;
	}
	
}
