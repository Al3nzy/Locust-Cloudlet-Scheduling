package org.cloudbus.cloudsim.examples;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

public class Connectedhost extends MyextenedHost {

	private ArrayList<Connectedhost> neighbours;

	public Connectedhost(int id, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner, long storage,
			List<? extends Pe> peList, VmScheduler vmScheduler) {

		super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler);
		// TODO Auto-generated constructor stub

		neighbours = new ArrayList<Connectedhost>();

	}

	public void addNeighbour(Connectedhost neighbour) {
		neighbours.add(neighbour);
		addMe(neighbour);

	}

	private void addMe(Connectedhost neighbour) {
		neighbour.getNeighbours().add(this);
	}

	public ArrayList<Connectedhost> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(ArrayList<Connectedhost> neighbours) {
		this.neighbours = neighbours;
	}

	public String toString() { // to convert it to string
		return this.getId() + "";
	}

}
