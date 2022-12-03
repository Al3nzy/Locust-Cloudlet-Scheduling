/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
//import org.apache.commons.logging.Log;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.lists.PeAvailableMips;
import org.cloudbus.cloudsim.lists.PeList;

/**
 * The Static Threshold (THR) VM allocation policy.
 * 
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
 * @since CloudSim Toolkit 3.0
 */
public class PowerVmAllocation_LACE extends Locust_GlobalMigration_LACE {

	/** The utilization threshold. */
	private double utilizationThreshold = 0.8;
	private static double alpha=0.9;

	/**
	 * Instantiates a new power vm allocation policy migration mad.
	 * 
	 * @param hostList             the host list
	 * @param vmSelectionPolicy    the vm selection policy
	 * @param utilizationThreshold the utilization threshold
	 */
	public PowerVmAllocation_LACE(List<? extends Host> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy, double utilizationThreshold) {
		super(hostList, vmSelectionPolicy);
		setUtilizationThreshold(utilizationThreshold);
	}

	
	
	/**
	 * Gets the Utilization In Each PE allocated in host.
	 * 
	 * @param peList the pe list
	 * @param id the PE's id
	 * @param vm     the vm
	 * @return the utilization of PE_i
	 */
	public static <T extends Pe> double getUtilizationOfEachPe (List<T> peList, int id,Vm vm) {
		Pe pe = PeList.getById(peList, id);
		double Utilization=0;
		if (pe != null) {
			Utilization = pe.getPeProvisioner().getUtilization();
				return Utilization;
		}
		return -1;
	}	

	/**
	 * Gets the Available MIPS In Each PE allocated in host.
	 * 
	 * @param peList the pe list
	 * @param id the PE's id
	 * @param vm     the vm
	 * @return the available Mips of PE_i
	 */
	public static <T extends Pe> double getAvailableMipsInEachPe (List<T> peList, int id,Vm vm) {
		Pe pe = PeList.getById(peList, id);
		double AvailableMips=0;
		if (pe != null) {
				AvailableMips = pe.getPeProvisioner().getAvailableMips();
				return AvailableMips;
		}
		return -1;
	}	
	
	/**
	 * Verify if all PEs are fully utilized of a host
	 * 
	 * @param peList the pe list
	 * @return true, if all PE's are fully utilized. Otherwise, false
	 */
	public static <T extends Pe> boolean verifyAllPeAreFullyUtilized(List<T> peList) {
	   int i=0; 
		for (Pe pe : peList) {
	        if (pe.getPeProvisioner().getUtilization()>=alpha) {
	        	i++;
	        }     
	    }
		 if (i==peList.size())
	            return true;
	    return false;
	}
	
	/**
	 * Checks if is host over utilized 100% fully utilized
	 * 
	 * I do remove the @override because this method with host, VM only here (alanzy)
	 *  
	 * @param host the host
	 * @return true, if is host over utilized based on LACE algorithm 
	 */
		protected boolean isHostOverUtilized(PowerHost host, Vm vm) {
		boolean isHostOverUtilized=false;
			double UtilizationOfCpu=host.getUtilizationOfCpu();
			double MaxUtilization=host.getMaxUtilization();			
			double UtilizationMips=host.getUtilizationMips();
			double TotalMips= host.getTotalMips();
			double MaxAvailableMips=host.getMaxAvailableMips();		
			double AvailableMips=host.getAvailableMips();//MaxAvailableMips should be
			double AcceptedPercentage = (host.getTotalMips()/host.getNumberOfPes())*alpha;
			int i=0;
			for ( ;i<host.getNumberOfPes(); i++) {
				double AvailableMipsInEachPe=getAvailableMipsInEachPe(host.getPeList(), i, vm);
		double UtilizationOfEachPe=getUtilizationOfEachPe(host.getPeList(), i, vm);
				double hostUtilizationMips = getUtilizationOfCpuMips(host);
				double PEAvailableMips= host.getPeList().get(i).getPeProvisioner().getAvailableMips();
				double PEUtilization= host.getPeList().get(i).getPeProvisioner().getUtilization();
				double PETotalAllocatedMips= host.getPeList().get(i).getPeProvisioner().getTotalAllocatedMips();
				if (MaxAvailableMips==0 || UtilizationOfEachPe>=alpha ) {
			Log.printLine("Host #"+host.getId()+" |  Pe #"+i+ "  is over-utilized");
				}
				else {
					Log.printLine("Host #"+host.getId()+" |  Pe #"+i+ "  is available");
					isHostOverUtilized=false;
					break;
					}
		}
			if(i==(host.getNumberOfPes()))
				isHostOverUtilized=verifyAllPeAreFullyUtilized(host.getPeList());
			
		return isHostOverUtilized;
	}
	

	/**
	 * Gets the utilization of the CPU in MIPS for the current potentially allocated
	 * VMs.
	 *
	 * @param host the host
	 *
	 * @return the utilization of the CPU in MIPS
	 */
	protected double getUtilizationOfCpuMips(PowerHost host) {
		double hostUtilizationMips = 0;
		for (Vm vm2 : host.getVmList()) {
			if (host.getVmsMigratingIn().contains(vm2)) {
				// calculate additional potential CPU usage of a migrating in VM
				hostUtilizationMips += host.getTotalAllocatedMipsForVm(vm2) * 0.9 / 0.1;
			}
			hostUtilizationMips += host.getTotalAllocatedMipsForVm(vm2);
		}
		return hostUtilizationMips;
	}
	
	/**
	 * Gets the power after allocation. We assume that load is balanced between PEs.
	 * The only restriction is: VM's max MIPS < PE's MIPS
	 * 
	 * @param host the host
	 * @param vm   the vm
	 * 
	 * @return the power after allocation
	 */
	protected double getMaxUtilizationAfterAllocation(PowerHost host, Vm vm) {
		double requestedTotalMips = vm.getCurrentRequestedTotalMips();
		double hostUtilizationMips = getUtilizationOfCpuMips(host);
		double hostPotentialUtilizationMips = hostUtilizationMips + requestedTotalMips;
		double pePotentialUtilization = hostPotentialUtilizationMips / host.getTotalMips();
		return pePotentialUtilization;
	}
	
	
	public List<Double> unAllocated_VMslist = new ArrayList<>();{
		
	}
	
	/**
	 * Sets the utilization threshold.
	 * 
	 * @param utilizationThreshold the new utilization threshold
	 */
	protected void setUtilizationThreshold(double utilizationThreshold) {
		this.utilizationThreshold = utilizationThreshold;
	}

	/**
	 * Gets the utilization threshold.
	 * 
	 * @return the utilization threshold
	 */
	protected double getUtilizationThreshold() {
		return utilizationThreshold;
	}

	@Override
	protected boolean isHostOverUtilized(PowerHost host) {
		// TODO Auto-generated method stub
		return false;
	}

}
