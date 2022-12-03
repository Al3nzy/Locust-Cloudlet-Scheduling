package org.cloudbus.cloudsim;

public class Makespan {
	private int vmListSize;
	private double[] saving;

	public Makespan() {
		System.out.println("I am in makspan constructor");
	}


	public  void SavingLastFinishTimeForEachVm( int vmListSize,int vmNumber, double finishtime) {
		setVmListSize(vmListSize);
		double[] saving2 = new double [vmListSize];
		if (getSaving()!=null)
		{
			 saving2 =getSaving();
		}
			
		saving2[vmNumber]=(double)finishtime;
		setSaving (saving2);  
		
	}

public  double overall() {
	double overall=0;
	for(int i=0; i<saving.length; i++){
		overall = (double)overall + saving[i];
    }   
	return (double) overall / saving.length;
   
}
public int getVmListSize() {
	return vmListSize;
}


public void setVmListSize(int vmListSize) {
	this.vmListSize = vmListSize;
}
public double[] getSaving() {
	return saving;
}


public void setSaving(double[] saving) {
	this.saving = saving;
}
}
