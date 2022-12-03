package org.cloudbus.cloudsim;

import java.text.DecimalFormat;
import java.util.List;

public class WaitingTime {
//	private int vmListSize;
	//private double[] saving ;
	Cloudlet cloudlet;
	
	
	public WaitingTime() {
		System.out.println("I am in WaitingTime constructor");
	}
	public  double AverageWaitingTime(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cldlet=getCloudlet();
		double overall=0;
		DecimalFormat dft = new DecimalFormat("###.##");

		for (int i = 0; i < size; i++) 
		  {
			cldlet = list.get(i);
			overall = (double) overall + cldlet.getWaitingTime();
			 }  
		Log.printLine("Average waiting time = "+dft.format((double)overall/size));
		return  (double)overall / size;
		  }
	
		

	/**
	 * @return the cloudlet
	 */
	public Cloudlet getCloudlet() {
		return cloudlet;
	}
	/**
	 * @param cloudlet the cloudlet to set
	 */
	public void setCloudlet(Cloudlet cloudlet) {
		this.cloudlet = cloudlet;
	}
}
