/**
 * 
 */
package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.examples.TaskSchedulingConstants;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * @author Ala'anzy
 *
 */
public class RB2B_DCBroker extends DatacenterBroker  {
	public static double[] upper;
	public static double[] lower;
	public RB2B_DCBroker(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * get total VM MIPS in a datacenter.
	 * @param $none
	 *            
	 * @pre $none
	 * @post double summation of VMs Mips
	 */
public double getTotalVmMips() {
	double  MIPS_total;
	MIPS_total = IntStream.of(TaskSchedulingConstants.VM_MIPS).sum();
	return MIPS_total;
}

	@Override
	protected void submitCloudlets() {
		int vmIndex = 0;
		int delay = 0;
		//double MIPS_total=0;
		List<Cloudlet> sortList = new ArrayList<Cloudlet>();
		long Cmin = Integer.MAX_VALUE; // to get The minimum
		long Cmax =Integer.MIN_VALUE; // Maximam
		for (Cloudlet cloudlet : getCloudletList()) {
			sortList.add(cloudlet);
			if (cloudlet.getCloudletLength() > Cmax) {
				Cmax = cloudlet.getCloudletLength();
			}
			if (cloudlet.getCloudletLength() < Cmin) {
				Cmin = cloudlet.getCloudletLength();
			}
		}	
	
		double	x = (double)(Cmax - Cmin) / getTotalVmMips();
		Log.printLine();
		Log.printLine("    Cmin = " + Cmin + "          Cmax  = " + Cmax + "            x= " + x);
		upper = new double[TaskSchedulingConstants.VM_MIPS.length];
		
		for (int i = 0; i < TaskSchedulingConstants.VM_MIPS.length; i++) {
			upper[i] = Cmin;
			
			
		for (int j = 0; j <= i; j++) {
				upper[i] += (TaskSchedulingConstants.VM_MIPS[j] * x);
			}

			System.out.println(" upper[" + i + "]=  " + upper[i]);
		}
		
		lower = new double[TaskSchedulingConstants.VM_MIPS.length];
		lower[0] = Cmin;
		
		System.out.println(" Lower[" + 0 + "]=  " + lower[0]);
		for (int i = 1; i < TaskSchedulingConstants.VM_MIPS.length; i++) {
			lower[i] = Cmin;

			for (int j = 0; j < i; j++) {
				lower[i] += (TaskSchedulingConstants.VM_MIPS[j] * x);
			}
			lower[i] += 1;

			System.out.println(" Lower[" + i + "]=  " + lower[i]);
		}
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
		double finishing_time=0;
		// for (Cloudlet cloudlet : getCloudletList()) {
		for (Cloudlet cloudlet : UpdatedList) {
			Vm vm;
			// if user didn't bind this cloudlet and it has not been executed  yet

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
			if (vm != null) {
				//vm.updateVmProcessing(CloudSim.clock(), null);
				double currentCPU =0; 
						currentCPU=	vm.getTotalUtilizationOfCpu(CloudSim.clock());
						
				// TO-DO -> Use currentCPU to your business rules...
				// This will be done after you send each cloudlet
				Log.printLine(" - VM" + vm.getId()	+ "  CPU utilisation %: " + currentCPU * 100);
				
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
					processing_time = (double)cloudlet.getCloudletLength() / vm.getMips();
					finishing_time=vm.getFinishTimeForAllVmList()+processing_time;
					vm.setFinishTimeForAllVmList(finishing_time);
					Log.printLine("     Cloudlet #"+cloudlet.getCloudletId()+"  has been submitted to Vm# "+cloudlet.getVmId());
					Log.printLine();
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
						 Log.printLine(" - Vm"+cloudlet.getVmId()+" utilision is "+ currentCPU*100);
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
				    		 if(cloudlet.getVmId() == upper.length-1) // if the VM reached to the last VM
				    			 {
				    			 for(int xx=Current_VM_ID;xx>0;)
				    			 	{
				    				 cloudlet.setVmId(xx-1);
				    				 currentCPU = 0;
				    				 vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
				    				 currentCPU = vm.getTotalUtilizationOfCpu(CloudSim.clock());
				    				 xx--;
				    				 Log.printLine(" - Vm#"+cloudlet.getVmId()+" utilision is "+ currentCPU*100);
				    				 if(currentCPU ==0)
				    				 		xx=-1;
				    				 	if (xx==0 && currentCPU !=0)
				    				 	{
				    				 		i=upper.length ;
				    				 		xx=-1;
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
				    	     Log.printLine(" - Vm#"+cloudlet.getVmId()+"  utilision is "+ currentCPU*100);
				    	     
				    	     
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
				}
				if (currentCPU !=0)
				{
					Log.printLine("  -- VMs are busy going to get the minimam processing time required in each server ");
					double Min_Waiting_Time = Integer.MAX_VALUE;
					List<Integer> co =  new ArrayList<Integer>();
					for (int iii=0; iii<=upper.length-1;iii++)
					{	
						cloudlet.setVmId(iii);
						double processing_time=0;
						processing_time = (double)cloudlet.getCloudletLength() / TaskSchedulingConstants.VM_MIPS[iii];
						vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
						finishing_time= vm.getFinishTimeForAllVmList()+processing_time;
						if (finishing_time < Min_Waiting_Time)
                           {
                            Min_Waiting_Time = finishing_time;
								co.add(iii);
                           }
						Log.printLine(" - Vm#"+cloudlet.getVmId()+"  The minimam finishing time needed is "+finishing_time);
					}
				cloudlet.setVmId(co.get(co.size() - 1));
				double processing_time=0;
				processing_time = (double)cloudlet.getCloudletLength() / TaskSchedulingConstants.VM_MIPS[co.get(co.size() - 1)];
				vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
				schedule(getVmsToDatacentersMap().get(vm.getId()), delay, CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
				cloudletsSubmitted++;
				vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
				getCloudletSubmittedList().add(cloudlet);
				i=upper.length;
				finishing_time=vm.getFinishTimeForAllVmList()+processing_time;
				vm.setFinishTimeForAllVmList(finishing_time);
				Log.printLine("   Cloudlet #"+cloudlet.getCloudletId()+"  has been submitted to Vm# "+cloudlet.getVmId()+"   of finishing time needed is: " +vm.getFinishTimeForAllVmList());	
			//Log.printLine(" ________________  " + vm.getCloudletScheduler());
				}
				CloudSim.runClockTick();
			}
		}
		// remove submitted cloudlets from waiting list
		for (Cloudlet cloudlet : getCloudletSubmittedList()) {
			getCloudletList().remove(cloudlet);
		}
	}
	}

