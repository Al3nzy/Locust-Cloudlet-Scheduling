package org.cloudbus.cloudsim.power;

import java.util.List;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;
import org.cloudbus.cloudsim.util.MathUtil;

public class PowerHostUtilizationHistory_LACE extends PowerHost_included_types_LACE {
	
	/**
	 * Instantiates a new power host utilization history.
	 * 
	 * @param id             the id
	 * @param ramProvisioner the ram provisioner
	 * @param bwProvisioner  the bw provisioner
	 * @param storage        the storage
	 * @param peList         the pe list
	 * @param vmScheduler    the vm scheduler
	 * @param powerModel     the power model
	 */
	public PowerHostUtilizationHistory_LACE(int id, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner,
			long storage, List<? extends Pe> peList, VmScheduler vmScheduler, PowerModel powerModel, String Host_Type) {
		super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler, powerModel, Host_Type);
	}

	/**
	 * Gets the host utilization history.
	 * 
	 * @return the host utilization history
	 */
	protected double[] getUtilizationHistory() {
		double[] utilizationHistory = new double[PowerVm.HISTORY_LENGTH];
		double hostMips = getTotalMips();
		for (PowerVm vm : this.<PowerVm>getVmList()) {
			for (int i = 0; i < vm.getUtilizationHistory().size(); i++) {
				utilizationHistory[i] += vm.getUtilizationHistory().get(i) * vm.getMips() / hostMips;
			}
		}
		return MathUtil.trimZeroTail(utilizationHistory);
	}

}
