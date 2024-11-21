package pa9;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

public class GraphImp implements Graph{
	
	protected class Edge implements Comparable<Edge>{
		int u;
		int v;
		Integer weight;

		public Edge(int u, int v, int weight) {
			this.u = u;
			this.v = v;
			this.weight = weight;
			
		}

		@Override
		public int compareTo(Edge o) {
			return weight.compareTo(o.weight);
		}
		
	}
	
	private class Union{
		private class Node{
			Node parent;
		}
		
		Node[] nodes;
		public Union(int n) {
			nodes = new Node[n];
			for (int i=0; i<n; i++) {
				nodes[i] = new Node();
			}
		}
		
		public void Clean(Node node, Node rep) {
			while (node != rep) {
				Node tmp = node;
				node = node.parent;
				tmp.parent = rep;
			}
		}
		
		public boolean inSameTree(int n1, int n2) {
			Node rep1 = nodes[n1];
			Node rep2 = nodes[n2];
			while (rep1.parent != null) 
				rep1 = rep1.parent;
			while (rep2.parent != null) 
				rep2 = rep2.parent;
			Clean(nodes[n1], rep1);
			Clean(nodes[n2], rep2);
			return rep1 == rep2;
		}
		
		public void join(int n1, int n2) {
			Node rep1 = nodes[n1];
			Node rep2 = nodes[n2];
			while (rep1.parent != null)
				rep1 = rep1.parent;
			while (rep2.parent != null)
				rep2 = rep2.parent;
			rep2.parent = rep1;
			Clean(nodes[n1], rep1);
			Clean(nodes[n2], rep1);
		}
	}
	
	ArrayList<ArrayList<Edge>> list; 
	int n;
	
	public GraphImp(int n) {
		list = new ArrayList<ArrayList<Edge>>(n);
		for (int i=0; i<n; i++) 
			list.add(new ArrayList<Edge>());
		this.n = n;
	}

	@Override
	public void addWeightedEdge(int v, int w, int weight) {
		Edge edge = new Edge(v, w, weight);
		list.get(v).add(edge);
	}
	
	public void addUndirected(int v, int w, int weight) {
		list.get(v).add(new Edge(v, w, weight));
		list.get(w).add(new Edge(w, v, weight));
	}

	@Override
	public boolean hasNegativeCycle() {
		int[] costs = new int[n];
		for (int i=0; i<n-1; i++) {
			for (ArrayList<Edge> lis: this.list) {
				for (Edge e: lis) {
					if (costs[e.u] + e.weight < costs[e.v])
						costs[e.v] = costs[e.u] + e.weight;
				}
			}
		}
		
		for (ArrayList<Edge> lis: this.list) {
			for (Edge e: lis) {
				if (costs[e.u] + e.weight < costs[e.v])
					return true;
			}
		}
		
		return false;
	}

	@Override
	public int[] shortestPath(int v) {
		if (this.hasNegativeCycle()){
			int[] res = new int[n];
			Arrays.fill(res, Integer.MAX_VALUE);
			return res;
		}
		
		int[] costs = new int[n];
		Arrays.fill(costs, Integer.MAX_VALUE);
		costs[v] = 0;
		for (int i=0; i<n-1; i++) {
			for (ArrayList<Edge> lis: this.list) {
				for (Edge e: lis) {
					if (costs[e.u] != Integer.MAX_VALUE && costs[e.u] + e.weight < costs[e.v])
						costs[e.v] = costs[e.u] + e.weight;
				}
			}
		}
		
		for (ArrayList<Edge> lis: this.list) {
			for (Edge e: lis) {
				if (costs[e.u] != Integer.MAX_VALUE && costs[e.u] + e.weight < costs[e.v]) {
					int[] res = new int[n];
					Arrays.fill(res, Integer.MAX_VALUE);
					return res;
				}
			}
		}
		
		return costs;
	}
	
	public HashSet<Edge> prim() {
		HashSet<Edge> res = new HashSet<Edge>();
		HashSet<Integer> visited = new HashSet<Integer>();
		PriorityQueue<Edge> q = new PriorityQueue<Edge>();
		
		Edge dummy = new Edge(-1, 0, 0);
		q.add(dummy);
		while (!q.isEmpty()) {
			Edge curr = q.poll();
			if (!visited.contains(curr.v)) {
				visited.add(curr.v);
				res.add(curr);
				for (Edge e: list.get(curr.v)) {
					if (!visited.contains(e.v)) {
						q.add(e);
					}
				}
			}
		}
		res.remove(dummy);
		return res;
		
	}
	
	public HashSet<Edge> kruskal() {
		ArrayList<Edge> edges = new ArrayList<>();
		for (ArrayList<Edge> lis: list) 
			for(Edge e: lis) 
				if (e.u <  e.v)
					edges.add(e);
		Collections.sort(edges);
		HashSet<Edge> res = new HashSet<Edge>();
		Union uni = new Union(n);
		for (Edge e: edges) {
			if(!uni.inSameTree(e.u, e.v)) {
				res.add(e);
				uni.join(e.u, e.v);
			}
		}
		return res;
	}
}
