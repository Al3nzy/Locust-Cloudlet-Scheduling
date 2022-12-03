package org.cloudbus.cloudsim.lists;

import java.util.List;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;

public class PeAvailableMips {
	
	
	

	public static <T extends Pe> Pe getById(List<T> peList, int id) {
		for (Pe pe : peList) {
			if (pe.getId() == id) {
				return pe;
			}
		}
		return null;
	}
	
	
	public static <T extends Pe> double getAvailableMipsInEachPe (List<T> peList, int id,Vm vm) {
		Pe pe = getById(peList, id);
		double AvailableMips=0;
		if (pe != null) {
			for (Pe pe2 : peList) {
				if (pe2.getPeProvisioner().getAllocatedMipsForVm(vm) == null) {
					continue;
				}
				AvailableMips = pe2.getPeProvisioner().getUtilization();
				
			}
			return AvailableMips;
		}
		return -1;
	}	
	
}
