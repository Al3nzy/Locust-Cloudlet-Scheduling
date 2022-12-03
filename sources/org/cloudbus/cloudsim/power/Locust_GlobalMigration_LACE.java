package org.cloudbus.cloudsim.power;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.examples.power.Constants;
import org.cloudbus.cloudsim.util.ExecutionTimeMeasurer;

public abstract class Locust_GlobalMigration_LACE extends PowerVmAllocationPolicyMigrationAbstract {

	public Locust_GlobalMigration_LACE(List<? extends Host> hostList, PowerVmSelectionPolicy vmSelectionPolicy) {
		super(hostList, vmSelectionPolicy);
		// TODO Auto-generated constructor stub
		}
		// TODO Auto-generated constructor stub
		
		final Map<Integer, List<Double>> utilizationHistory = new HashMap<Integer, List<Double>>();

		/** The metric history. */
		final Map<Integer, List<Double>> metricHistory = new HashMap<Integer, List<Double>>();

		/** The time history. */
		final Map<Integer, List<Double>> timeHistory = new HashMap<Integer, List<Double>>();
		/** The execution time history vm reallocation. */
		final List<Double> executionTimeHistoryVmReallocation = new LinkedList<Double>();

		/** The execution time history total. */
		final List<Double> executionTimeHistoryTotal = new LinkedList<Double>();

	
		public List<Vm> get_unallocated_VM_list = new ArrayList<>();
		
		
		
		/**
		 * Get the unallocated VMs.
		 * 
		 * @param VM the VM
		 * @return list of unallocated VMs 
		 */
//		public List<Vm> get_unallocated_VM_list(Vm vm) {
//		for (int i=0; i>=Constants.Number_of_VMs;i++)
//			if (vm.getHost()==null)
//				get_unallocated_VM_list.add((Vm) vm);
//				return get_unallocated_VM_list;
//					}
	
		
		/**
		 * Find host for vm.
		 * 
		 * @param vm the vm
		 * @return the excluded Hosts and run a new method for finding hosts without the excluded host
		 */
		@Override
		public PowerHost findHostForVm(Vm vm) {
			Set<Host> excludedHosts = new HashSet<Host>();
				if (vm.getHost() != null) {
				if (vm.getHost().getAvailableMips() < Constants.min_random_cores)
				excludedHosts.add(vm.getHost());
			}
			return findHostForVm(vm, excludedHosts);
		}
		
		/**
		 * Find host for vm.
		 * 
		 * @param vm            the vm
		 * @param excludedHosts the excluded hosts
		 * @return the power host
		 */
		@Override
		public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
			PowerHost allocatedHost = null;
			//PowerHost host;
						//double AvailableMips=host.getAvailableMips();
				for (PowerHost host : this.<PowerHost>getHostList()) {
				if (excludedHosts.contains(host)) {
					continue;
				}
				if (host.isSuitableForVm(vm)) {
					if (getUtilizationOfCpuMips(host) != 0 && isHostOverUtilizedAfterAllocation(host, vm)) {
						continue;
					}
					try {
						//double powerAfterAllocation = getPowerAfterAllocation(host, vm);
						if (host.getAvailableMips()!=0) {
							allocatedHost=host;
							break;// if I removed break code then the last fit host will be selected. in other words, the last Host will get full utilized first (assigned to the VM) can minimize the migration number
						}
						else 
							Log.printLine("Host #"+host.getId()+" is over-utilized");
					} 
					catch (Exception e) {
					}
				}//i++;
			}
			return allocatedHost;
		}
	/**
	 * Checks if is host over utilized after allocation.
	 * 
	 * @param host the host
	 * @param vm   the vm
	 * @return true, if is host over utilized after allocation
	 */
	@Override
	protected boolean isHostOverUtilizedAfterAllocation(PowerHost host, Vm vm) {
		boolean isHostOverUtilizedAfterAllocation = true;
		//Log.printLine("i am in method isHostOverUtilizedAfterAllocation ");
		if (host.vmCreate(vm)) {
			isHostOverUtilizedAfterAllocation = isHostOverUtilized(host, vm);
			host.vmDestroy(vm);
		}
		return isHostOverUtilizedAfterAllocation;
	}
	
	
//		else {	
//			
//			Log.printLine("Host" + host.getId()+" is not fully utilized! ");
//			
//			host.getUtilizationOfCpu();
//			
//		host.getVmList();
//		for (int i =0; i>host.getVmList().size();i++){
//			
//			host.getVmList();
//			
//			host.getVmList();
//		}
//		host.getId();
//		return false;
//			}

	
	protected abstract boolean isHostOverUtilized(PowerHost host, Vm vm);

	/**
	 * get the Under Utilized Hosts
	 * 
	 *  
	 * @return under Utilized Hosts list
	 */
	protected List<PowerHostUtilizationHistory> getUnderUtilizedHosts() {
		Log.printLine("i am in method getUnderUtilizedHosts ");
		//System.out.println(" I am in over utilizeddddd2");
		List<PowerHostUtilizationHistory> underUtilizedHosts = new LinkedList<PowerHostUtilizationHistory>();
		for (PowerHostUtilizationHistory host : this.<PowerHostUtilizationHistory>getHostList()) {
			if (isHostOverUtilized(host)) { // will be checked in PowerVmAllocationPolicyMigrationStaticThreshold class 
			
				if (!isHostOverUtilized(host))
				{
				underUtilizedHosts.add(host);
				Log.printLine("Host #"+host.getId()+ "is Under utilized!");
			}
		}
		}
		return underUtilizedHosts;
	}
	
//	private boolean isHostUnderUtilized(PowerHostUtilizationHistory host) {
//		// TODO Auto-generated method stub
//		return false;
//	}

	/**
	 * Optimize allocation of the VMs according to current utilization.
	 * 
	 * @param vmList the vm list
	 * 
	 * @return the array list< hash map< string, object>>
	 */
	@Override
	public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
		ExecutionTimeMeasurer.start("optimizeAllocationTotal");
		ExecutionTimeMeasurer.start("optimizeAllocationHostSelection");
		List<PowerHostUtilizationHistory> under_UtilizedHosts = getUnderUtilizedHosts();
		getExecutionTimeHistoryHostSelection().add(ExecutionTimeMeasurer.end("optimizeAllocationHostSelection"));
		printOverUtilizedHosts(under_UtilizedHosts);
		saveAllocation();
		ExecutionTimeMeasurer.start("optimizeAllocationVmSelection");
		List<? extends Vm> vmsToMigrate = getVmsToMigrateFromHosts(under_UtilizedHosts); // select all VM in the over-utilized host as a VM to be migrated (Alanzy)
		getExecutionTimeHistoryVmSelection().add(ExecutionTimeMeasurer.end("optimizeAllocationVmSelection"));
		Log.printLine("Reallocation of VMs from the over-utilized hosts:");
		ExecutionTimeMeasurer.start("optimizeAllocationVmReallocation");
		List<Map<String, Object>> migrationMap = getNewVmPlacement(vmsToMigrate, new HashSet<Host>(under_UtilizedHosts));
		getExecutionTimeHistoryVmReallocation().add(ExecutionTimeMeasurer.end("optimizeAllocationVmReallocation"));
		Log.printLine();
		migrationMap.addAll(getMigrationMapFromUnderUtilizedHosts(under_UtilizedHosts));
		restoreAllocation();
		getExecutionTimeHistoryTotal().add(ExecutionTimeMeasurer.end("optimizeAllocationTotal"));
		return migrationMap;
	}
	
	
	
	@Override
	protected List<Map<String, Object>> getMigrationMapFromUnderUtilizedHosts(
			List<PowerHostUtilizationHistory> overUtilizedHosts) {
		List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();
		List<PowerHost> switchedOffHosts = getSwitchedOffHosts();

		// over-utilized hosts + hosts that are selected to migrate VMs to from
		// over-utilized hosts
		Set<PowerHost> excludedHostsForFindingUnderUtilizedHost = new HashSet<PowerHost>();
		excludedHostsForFindingUnderUtilizedHost.addAll(overUtilizedHosts);
		excludedHostsForFindingUnderUtilizedHost.addAll(switchedOffHosts);
		excludedHostsForFindingUnderUtilizedHost.addAll(extractHostListFromMigrationMap(migrationMap));

		// over-utilized + under-utilized hosts
		Set<PowerHost> excludedHostsForFindingNewVmPlacement = new HashSet<PowerHost>();
		excludedHostsForFindingNewVmPlacement.addAll(overUtilizedHosts);
		excludedHostsForFindingNewVmPlacement.addAll(switchedOffHosts);

		int numberOfHosts = getHostList().size();

		while (true) {
			if (numberOfHosts == excludedHostsForFindingUnderUtilizedHost.size()) {
				break;
			}

			PowerHost underUtilizedHost = getUnderUtilizedHost(excludedHostsForFindingUnderUtilizedHost);
			if (underUtilizedHost == null) {
				break;
			}

			Log.printLine("Under-utilized host: host #" + underUtilizedHost.getId() + "\n");

			excludedHostsForFindingUnderUtilizedHost.add(underUtilizedHost);
			excludedHostsForFindingNewVmPlacement.add(underUtilizedHost);

			List<? extends Vm> vmsToMigrateFromUnderUtilizedHost = getVmsToMigrateFromUnderUtilizedHost(
					underUtilizedHost);
			if (vmsToMigrateFromUnderUtilizedHost.isEmpty()) {
				continue;
			}
			//Log.printLine("i am in method getMigrationMapFromUnderUtilizedHosts ");
			Log.print("Reallocation of VMs from the under-utilized host: ");
			if (!Log.isDisabled()) {
				for (Vm vm : vmsToMigrateFromUnderUtilizedHost) {
					Log.print(vm.getId() + " ");
				}
			}
			Log.printLine();

			List<Map<String, Object>> newVmPlacement = getNewVmPlacementFromUnderUtilizedHost(
					vmsToMigrateFromUnderUtilizedHost, excludedHostsForFindingNewVmPlacement);

			excludedHostsForFindingUnderUtilizedHost.addAll(extractHostListFromMigrationMap(newVmPlacement));

			migrationMap.addAll(newVmPlacement);
			Log.printLine();
		}

		return migrationMap;
	}

//	@Override
//	protected boolean isHostOverUtilized(PowerHost host) {
//		// TODO Auto-generated method stub
//		return false;
//	}
}