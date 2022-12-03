/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.util.List;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

/**
 * This class to add host type name for each server 
 * If host contents 2 types (Powerful and weak) server
 * If host contents 3 types (Powerful, average, and weak) server.
 * 
 * @author Mohammed Ala'anzy
 * @since 2 Dec. 2019
 */
public class PowerHost_included_types_LACE extends PowerHost {
	private String Host_Type;

	public PowerHost_included_types_LACE(int id, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner,
			long storage, List<? extends Pe> peList, VmScheduler vmScheduler, PowerModel powerModel, String Host_Type) {
		super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler, powerModel);
		setHost_Type(Host_Type);
	}

	
	
	
	/**
	 * @return the host_Type
	 */
	public String getHost_Type() {
		return Host_Type;
	}

	/**
	 * @param host_Type the host_Type to set
	 */
	public void setHost_Type(String host_Type) {
		Host_Type = host_Type;
	}
	
}