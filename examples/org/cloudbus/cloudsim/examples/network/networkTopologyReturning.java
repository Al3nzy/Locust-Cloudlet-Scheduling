package org.cloudbus.cloudsim.examples.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.NetworkTopology;
import org.cloudbus.cloudsim.network.DelayMatrix_Float;
import org.cloudbus.cloudsim.network.GraphReaderBrite;
import org.cloudbus.cloudsim.network.TopologicalGraph;
import org.cloudbus.cloudsim.network.TopologicalLink;

public class networkTopologyReturning extends NetworkTopology {

	private static boolean networkEnabled = false;

	public static TopologicalGraph buildNetworkTopologyReturn(String fileName) {
		Log.printLine("Topology file: " + fileName);

		// try to find the file
		GraphReaderBrite reader = new GraphReaderBrite();

		try {
			graph = reader.readGraphFile(fileName);

			map = new HashMap<Integer, Integer>();
			generateMatrices();
		} catch (IOException e) {
			// problwith the file. Does not simulate network
			Log.printLine("Problem in processing BRITE file. Network simulation is disabled. Error: " + e.getMessage());
		}
		return graph;
	}

	private static void generateMatrices() {
		// creates the delay matrix
		delayMatrix = new DelayMatrix_Float(graph, false);

		// creates the bw matrix
		bwMatrix = createBwMatrix(graph, false);

		networkEnabled = true;
	}

	private static double[][] createBwMatrix(TopologicalGraph graph, boolean directed) {
		int nodes = graph.getNumberOfNodes();

		double[][] mtx = new double[nodes][nodes];

		// cleanup matrix
		for (int i = 0; i < nodes; i++) {
			for (int j = 0; j < nodes; j++) {
				mtx[i][j] = 0.0;
			}
		}

		Iterator<TopologicalLink> iter = graph.getLinkIterator();
		while (iter.hasNext()) {
			TopologicalLink edge = iter.next();

			mtx[edge.getSrcNodeID()][edge.getDestNodeID()] = edge.getLinkBw();

			if (!directed) {
				mtx[edge.getDestNodeID()][edge.getSrcNodeID()] = edge.getLinkBw();
			}
		}

		return mtx;
	}
}
